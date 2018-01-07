package com.ncl.sindhu.repository.search;

import com.ncl.sindhu.domain.Block;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Block entity.
 */
public interface BlockSearchRepository extends ElasticsearchRepository<Block, Long> {
}
