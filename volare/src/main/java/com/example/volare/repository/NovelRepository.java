package com.example.volare.repository;

import com.example.volare.model.Novel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NovelRepository extends JpaRepository<Novel,String> {
}
