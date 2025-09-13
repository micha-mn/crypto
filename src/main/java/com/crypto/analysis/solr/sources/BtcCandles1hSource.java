package com.crypto.analysis.solr.sources;

import java.sql.Types;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import javax.sql.DataSource;

import lombok.RequiredArgsConstructor;

import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Component;

import com.crypto.analysis.solr.service.ProcPage;
import com.crypto.analysis.solr.service.ProcSource;

import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@RequiredArgsConstructor
public class BtcCandles1hSource implements ProcSource {
  private final DataSource ds;
  private static final java.time.format.DateTimeFormatter SQL_TS =
		    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.S]");

  @Override
  public String name() { 
    return "btc_1h"; 
  }

  @Override
  public String collection() { 
    return "crypto"; 
  }

  @Override
  public ProcPage callPage(Instant from, Instant to, int size, int page) throws Exception {
    try (var c = ds.getConnection();
         var cs = c.prepareCall("{CALL cr_dynamic_result(?,?,?,?,?,?,?,?)}")) {

      cs.setString(1, ts(from));
      cs.setString(2, ts(to));
      cs.setString(3, "cr_btc_high_low");   // your table
      cs.setString(4, "candle");   // your criteria
      cs.setString(5, "1h");       // period
      cs.setInt(6, size);
      cs.setInt(7, page);
      cs.registerOutParameter(8, Types.INTEGER);

      boolean hasResult = cs.execute();
      List<Map<String,Object>> rows = Collections.emptyList();
      if (hasResult) try (ResultSet rs = cs.getResultSet()) { rows = readRows(rs); }

      return new ProcPage(rows, cs.getInt(8));
    }
  }

  @Override
  public SolrInputDocument mapRow(Map<String, Object> r) {
	  Instant start = getStartInstant(r); // 👈 robust key resolver
	  SolrInputDocument d = new SolrInputDocument();

	  d.addField("id", "cr_btc_high_low:1h:" + start); // use your actual table/period
	  d.addField("table_s", "cr_btc_high_low");
	  d.addField("period_s", "1h");
	  d.addField("start_time_dt", java.util.Date.from(start));

	// unpack y -> open/high/low/close
	  double[] ohlc = parseCandleY(r.get("y"));
	  if (ohlc != null && ohlc.length >= 4) {
	    d.addField("open",  ohlc[0]);
	    d.addField("high",  ohlc[1]);
	    d.addField("low",   ohlc[2]);
	    d.addField("close", ohlc[3]);
	  }
	  // volume/marketcap aren’t in the candle branch; only add if present
	  put(d, "refer_date",   r.get("x"));
	  put(d, "volume",    r.get("volume"));
	  put(d, "marketcap", r.get("marketcap"));
	  return d;
}
//parse y which may be a JSON string, byte[], or a List
private static double[] parseCandleY(Object y) {
 if (y == null) return null;
 try {
   if (y instanceof String s) {
     // s like "[84475.14, 84475.14, 83736.26, 84087.08]"
     s = s.trim();
     if (!s.startsWith("[")) throw new IllegalArgumentException("y not JSON array: " + s);
     var parts = s.substring(1, s.length()-1).split("\\s*,\\s*");
     double[] out = new double[parts.length];
     for (int i=0;i<parts.length;i++) out[i] = Double.parseDouble(parts[i]);
     return out;
   }
   if (y instanceof byte[] b) {
     return parseCandleY(new String(b, java.nio.charset.StandardCharsets.UTF_8));
   }
   if (y instanceof java.util.List<?> list) {
     double[] out = new double[list.size()];
     for (int i=0;i<list.size();i++) out[i] = Double.parseDouble(String.valueOf(list.get(i)));
     return out;
   }
 } catch (Exception e) {
   throw new IllegalArgumentException("Failed to parse y=" + y, e);
 }
 throw new IllegalArgumentException("Unsupported y type: " + y.getClass());
}

	//robust timestamp getter: accepts start_time OR x, and String or Timestamp
	private static java.time.Instant getStartInstant(Map<String,Object> r){
	Object v = r.get("start_time");
	if (v == null) v = r.get("x");
	if (v == null) throw new IllegalArgumentException("start_time/x missing. Row keys=" + r.keySet());
	if (v instanceof java.time.Instant i) return i;
	if (v instanceof java.sql.Timestamp ts) return ts.toInstant();
	if (v instanceof String s) {
	var ldt = java.time.LocalDateTime.parse(s.replace('T',' ').substring(0, Math.min(23, s.length())), SQL_TS);
	return ldt.atOffset(java.time.ZoneOffset.UTC).toInstant();
	}
	if (v instanceof java.util.Date d) return d.toInstant();
	throw new IllegalArgumentException("Unsupported ts type: " + v.getClass());
	}


private static Object firstNonNull(Object... vals){
  for (Object v : vals) if (v != null) return v;
  return null;
}
  // ---------- helpers ----------
  private static final DateTimeFormatter TS_FMT =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC);
  private static String ts(Instant i){ return TS_FMT.format(i); }

  private static List<Map<String,Object>> readRows(ResultSet rs) throws SQLException {
    List<Map<String,Object>> out = new ArrayList<>();
    var md = rs.getMetaData(); int n = md.getColumnCount();
    while (rs.next()){
      Map<String,Object> row = new LinkedHashMap<>();
      for (int i=1;i<=n;i++){
        String k = Optional.ofNullable(md.getColumnLabel(i)).orElse(md.getColumnName(i));
        row.put(k, rs.getObject(i));
      }
      out.add(row);
    }
    return out;
  }

  private static Instant toInstant(Object v){
	  if (v == null) throw new IllegalArgumentException("start_time is null");
	  if (v instanceof java.sql.Timestamp ts) return ts.toInstant();
	  if (v instanceof java.util.Date d)      return d.toInstant();
	  if (v instanceof java.time.OffsetDateTime odt) return odt.toInstant();
	  if (v instanceof java.time.LocalDateTime ldt)  return ldt.atOffset(java.time.ZoneOffset.UTC).toInstant();
	  if (v instanceof String s) {
	    // Accept "yyyy-MM-dd HH:mm:ss"
	    if (s.indexOf('T') < 0 && s.length() >= 19) {
	      java.time.LocalDateTime ldt = java.time.LocalDateTime.parse(
	          s.substring(0,19), java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	      return ldt.atOffset(java.time.ZoneOffset.UTC).toInstant();
	    }
	    // Accept ISO strings
	    return java.time.Instant.parse(s.replace(" ", "T") + (s.endsWith("Z") ? "" : "Z"));
	  }
	  throw new IllegalArgumentException("Unsupported datetime type: " + v.getClass());
	}
  private static void put(SolrInputDocument d, String f, Object v){
    if (v != null) d.addField(f, v);
  }
}