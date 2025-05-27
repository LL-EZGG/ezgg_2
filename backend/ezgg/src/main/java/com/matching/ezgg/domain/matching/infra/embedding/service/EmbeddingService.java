package com.matching.ezgg.domain.matching.infra.embedding.service;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmbeddingService {
	private final EmbeddingModel embeddingModel;

	public float [] embed(String text) {
		return embeddingModel.embed(text);
	}
}
