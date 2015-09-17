package com.sooeez.ecomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sooeez.ecomm.domain.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {

}
