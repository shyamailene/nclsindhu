package com.ncl.sindhu.repository;

import com.ncl.sindhu.domain.Flat;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Flat entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FlatRepository extends JpaRepository<Flat, Long> {

}
