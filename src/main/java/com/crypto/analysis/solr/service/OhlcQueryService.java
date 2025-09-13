package com.crypto.analysis.solr.service;

import lombok.RequiredArgsConstructor;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.springframework.stereotype.Service;

import com.crypto.analysis.solr.dto.OhlcDoc;
import com.crypto.analysis.solr.dto.OhlcPoint;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OhlcQueryService {
  private final SolrClient solr;
  private static final String COLLECTION = "crypto";

  public List<OhlcPoint> search(
      String table, String period,
      Instant from, Instant to,
      int page, int size,
      boolean asc
  ) throws Exception {

    SolrQuery q = new SolrQuery("*:*");
    // filters
    q.addFilterQuery("table_s:" + esc(table));
    q.addFilterQuery("period_s:" + esc(period));
    if (from != null || to != null) {
      String lower = (from == null) ? "*" : from.toString();
      String upper = (to   == null) ? "*" : to.toString();
      q.addFilterQuery("start_time_dt:[" + lower + " TO " + upper + "]");
    }

    // fields to return
    q.setFields("id","start_time_dt","open","high","low","close","volume","marketcap","refer_date");

    // sort + pagination
    q.setStart(Math.max(page,0) * Math.max(size,1));
    q.setRows(Math.max(size,1));
    q.addSort("start_time_dt", asc ? SolrQuery.ORDER.asc : SolrQuery.ORDER.desc);

    QueryResponse rsp = solr.query(COLLECTION, q);
    List<OhlcPoint> out = new ArrayList<>();
    for (SolrDocument d : rsp.getResults()) {
        OhlcPoint p = mapResponce(d);
        if (p != null) out.add(p);
      }
    return out;
  }
  private static OhlcPoint mapResponce(SolrDocument d) {
	  // if your date field is named "refer_date", use that instead of "start_time_dt"
	  Date dt = (Date) d.getFirstValue("refer_date");
	  if (dt == null) return null;

	  double open  = toDoubleSafe(d.getFieldValue("open"));
	  double high  = toDoubleSafe(d.getFieldValue("high"));
	  double low   = toDoubleSafe(d.getFieldValue("low"));
	  double close = toDoubleSafe(d.getFieldValue("close"));

	  return new OhlcPoint(dt.toInstant(), new double[]{open, high, low, close});
	}

	// Handles: single Number, List/Collection with first element, or String fallback
	private static double toDoubleSafe(Object raw) {
	  if (raw == null) return Double.NaN;
	  if (raw instanceof java.util.Collection<?>) {
	    java.util.Iterator<?> it = ((java.util.Collection<?>) raw).iterator();
	    raw = it.hasNext() ? it.next() : null;
	    if (raw == null) return Double.NaN;
	  }
	  if (raw instanceof Number) return ((Number) raw).doubleValue();
	  return Double.parseDouble(String.valueOf(raw));
	}
  public OhlcDoc byId(String id) throws Exception {
    SolrQuery q = new SolrQuery("id:" + esc(id));
    q.setRows(1);
    q.setFields("id","start_time_dt","open","high","low","close","volume","marketcap");
    var rsp = solr.query(COLLECTION, q);
    if (rsp.getResults().isEmpty()) return null;
    return map(rsp.getResults().get(0));
  }

  private static String esc(String s){
    // minimal escaping for simple terms; adjust if your values contain spaces/special chars
    return "\"" + s.replace("\"","\\\"") + "\"";
  }

  private static OhlcDoc map(SolrDocument d){
    return new OhlcDoc(
        (String) d.getFieldValue("id"),
        toInstant(d.getFieldValue("start_time_dt")),
        toD(d.getFieldValue("open")),
        toD(d.getFieldValue("high")),
        toD(d.getFieldValue("low")),
        toD(d.getFieldValue("close")),
        toD(d.getFieldValue("volume")),
        toD(d.getFieldValue("marketcap"))
    );
  }

  private static Instant toInstant(Object v){
    if (v == null) return null;
    if (v instanceof Instant i) return i;
    if (v instanceof java.util.Date dt) return dt.toInstant();
    return Instant.parse(String.valueOf(v));
  }

  private static Double toD(Object v){
    return v == null ? null : ((Number) v).doubleValue();
  }
}