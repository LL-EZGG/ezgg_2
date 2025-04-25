package com.matching.ezgg.global.exception;

public class NoTFoundMatchingUserException extends BaseException {
	private static final String MESSAGE = "매칭에 해당하는 대상이 없습니다. .";

	public NoTFoundMatchingUserException() {
		super(MESSAGE);
	}

	@Override
	public int getStatusCode() {
		return 404;
	}
}
