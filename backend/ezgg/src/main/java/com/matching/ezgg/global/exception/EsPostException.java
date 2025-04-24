package com.matching.ezgg.global.exception;

public class EsPostException extends BaseException {
	private static final String MESSAGE = "Elasticsearch Post를 실패했습니다.";

	public EsPostException() {
		super(MESSAGE);
	}

	@Override
	public int getStatusCode() {
		return 400;
	}
}
