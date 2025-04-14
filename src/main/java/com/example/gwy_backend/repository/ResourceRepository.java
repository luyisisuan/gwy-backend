package com.example.gwy_backend.repository;

import com.example.gwy_backend.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findAllByOrderByDisplayOrderAscCreatedAtDesc();
    List<Resource> findByCategoryOrderByDisplayOrderAscCreatedAtDesc(String category);
}