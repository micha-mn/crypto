package com.crypto.analysis.solr.repositories;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crypto.analysis.solr.domain.Checkpoint;

public interface CheckpointRepo extends JpaRepository<Checkpoint, String> {

	Instant getBysourceName(String name);
}
