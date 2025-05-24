package com.matching.ezgg.domain.review.controller;

import org.springframework.web.bind.annotation.RestController;

import com.matching.ezgg.domain.review.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

}
