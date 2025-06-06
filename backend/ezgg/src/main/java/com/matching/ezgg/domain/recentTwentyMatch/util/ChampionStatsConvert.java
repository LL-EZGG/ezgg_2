package com.matching.ezgg.domain.recentTwentyMatch.util;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matching.ezgg.domain.recentTwentyMatch.entity.model.ChampionStat;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ChampionStatsConvert implements AttributeConverter<Map<String, ChampionStat>, String> {
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(Map<String, ChampionStat> attribute) {
		try {
			return objectMapper.writeValueAsString(attribute);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("JSON 직렬화 실패", e);
		}
	}

	@Override
	public Map<String, ChampionStat> convertToEntityAttribute(String dbData) {
		try {
			TypeReference<Map<String, ChampionStat>> typeRef = new TypeReference<>() {};
			return objectMapper.readValue(dbData, typeRef);
		} catch (IOException e) {
			throw new IllegalArgumentException("Error converting JSON to map", e);
		}
	}
}
