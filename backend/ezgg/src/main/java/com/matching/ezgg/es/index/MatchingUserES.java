package com.matching.ezgg.es.index;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(indexName = "matching-user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchingUserES {

	@Id
	private Long id;

	@Field(type = FieldType.Object)
	private PreferredPartnerES preferredPartnerES;

	@Field(type = FieldType.Object)
	private MemberInfoES memberInfoES;

	@Field(type = FieldType.Object)
	private RecentTwentyMatchES recentTwentyMatchES;
}
