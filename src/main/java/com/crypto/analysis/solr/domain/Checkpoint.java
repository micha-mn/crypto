package com.crypto.analysis.solr.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "solr_index_checkpoint")
public class Checkpoint {
  @Id
  @Column(name = "source_name")
  private String sourceName;

  @Column(name = "last_indexed_utc")
  private Instant lastIndexedUtc;
}