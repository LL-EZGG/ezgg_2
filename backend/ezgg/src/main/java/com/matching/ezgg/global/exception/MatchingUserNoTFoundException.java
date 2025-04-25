package com.matching.ezgg.global.exception;

public class MatchingUserNoTFoundException extends BaseException {
	private static final String MESSAGE = "매칭에 해당하는 대상이 없습니다. .";

	public MatchingUserNoTFoundException() {
		super(MESSAGE);
	}

	@Override
	public int getStatusCode() {
		return 404;
	}
}
