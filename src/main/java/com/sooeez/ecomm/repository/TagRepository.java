package com.sooeez.ecomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.sooeez.ecomm.domain.Tag;

public interface TagRepository extends JpaRepository<Tag, Long>, JpaSpecificationExecutor<Tag> {

}
