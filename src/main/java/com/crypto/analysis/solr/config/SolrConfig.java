package com.crypto.analysis.solr.config;

import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Optional;

@Configuration
public class SolrConfig {

  @Bean(destroyMethod = "close")
  CloudSolrClient cloudSolrClient(
      @Value("${solr.zk-hosts}") String zkHosts,
      @Value("${solr.zk-chroot}") String chroot,
      @Value("${solr.collection}") String collection) {

    CloudSolrClient client = new CloudSolrClient.Builder(
        Arrays.asList(zkHosts.split(",")),
        Optional.ofNullable(chroot).filter(s -> !s.isBlank())
    ).build();
    client.setDefaultCollection(collection);
    return client;
  }
}