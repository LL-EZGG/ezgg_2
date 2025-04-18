package com.matching.ezgg.global.exception;

public class MemberInfoException extends BaseException {
	private static final String MESSAGE = "멤버 정보를 찾을 수 없습니다.";

	public MemberInfoException() {
		super(MESSAGE);
	}

	@Override
	public int getStatusCode() {
		return 404;
	}
}
