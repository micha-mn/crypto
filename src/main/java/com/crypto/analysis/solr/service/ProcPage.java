package com.crypto.analysis.solr.service;

import java.util.List;
import java.util.Map;
public record ProcPage(List<Map<String, Object>> rows, int totalRecords) {}
