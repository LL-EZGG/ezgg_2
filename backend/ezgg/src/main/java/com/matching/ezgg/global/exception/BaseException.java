package com.matching.ezgg.global.exception;

import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {

	public BaseException(String message) {
		super(message);
	}

	public abstract int getStatusCode();
}
