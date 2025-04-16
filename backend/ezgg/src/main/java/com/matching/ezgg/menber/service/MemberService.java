package com.matching.ezgg.menber.service;

import org.springframework.stereotype.Service;

import com.matching.ezgg.menber.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

}
