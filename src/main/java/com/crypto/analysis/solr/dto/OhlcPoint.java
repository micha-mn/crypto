package com.crypto.analysis.solr.dto;

import java.time.Instant;

public class OhlcPoint {
	 public Instant x;
	  public double[] y;
	  public OhlcPoint(Instant x, double[] y) {
	    this.x = x;
	    this.y = y;
	  }
	}

