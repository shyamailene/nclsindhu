package com.ncl.sindhu.repository.search;

import com.ncl.sindhu.domain.Flat;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Flat entity.
 */
public interface FlatSearchRepository extends ElasticsearchRepository<Flat, Long> {
}
