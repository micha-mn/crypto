package com.crypto.analysis.solr.dto;

import java.time.Instant;

public record OhlcDoc(
    String id,
    Instant startTime,
    Double open,
    Double high,
    Double low,
    Double close,
    Double volume,
    Double marketcap
) {}