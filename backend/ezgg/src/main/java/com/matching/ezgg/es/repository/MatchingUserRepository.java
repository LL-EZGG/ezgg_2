package com.matching.ezgg.es.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.matching.ezgg.es.index.MatchingUserES;

@Repository
public interface MatchingUserRepository extends ElasticsearchRepository<MatchingUserES, Long> {
} 
