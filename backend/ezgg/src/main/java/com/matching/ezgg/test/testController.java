package com.matching.ezgg.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matching.ezgg.global.exception.TestException;

@RestController
public class testController {

	@GetMapping("/test")
	public void test() {
		throw new TestException();
	}
}
