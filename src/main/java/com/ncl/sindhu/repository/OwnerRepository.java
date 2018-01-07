package com.ncl.sindhu.repository;

import com.ncl.sindhu.domain.Owner;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Owner entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {

}
