package com.matching.ezgg.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matching.ezgg.domain.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
