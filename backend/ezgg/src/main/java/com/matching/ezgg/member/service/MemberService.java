package com.matching.ezgg.member.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matching.ezgg.global.exception.ExistEmailException;
import com.matching.ezgg.global.exception.ExistMemberIdException;
import com.matching.ezgg.global.exception.ExistRiotTagException;
import com.matching.ezgg.global.exception.ExistRiotUsernamException;
import com.matching.ezgg.member.dto.SignupRequest;
import com.matching.ezgg.member.dto.SignupResponse;
import com.matching.ezgg.member.entity.Member;
import com.matching.ezgg.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	private final PasswordEncoder passwordEncoder;

	@Transactional
	public SignupResponse signup(SignupRequest signupRequest) {

		String password = passwordEncoder.encode(signupRequest.getPassword());

		vaildateDuplicateMember(signupRequest);

		Member newMember = Member.builder()
			.memberId(signupRequest.getMemberId())
			.password(password)
			.email(signupRequest.getEmail())
			.riotUsername(signupRequest.getRiotUsername())
			.riotTag(signupRequest.getRiotTag())
			.role("ROLE_USER") // 기본 역할 설정
			.build();

		Member member = memberRepository.save(newMember);

		return SignupResponse.builder()
			.memberId(member.getMemberId())
			.email(member.getEmail())
			.riotUsername(member.getRiotUsername())
			.riotTag(member.getRiotTag())
			.build();
	}

	private void vaildateDuplicateMember(SignupRequest signupRequest) {
		// 이미 존재하는 회원인지 확인
		if (memberRepository.existsByMemberId((signupRequest.getMemberId()))) {
			throw new ExistMemberIdException();
		}

		// 이메일 중복 확인
		if (memberRepository.existsByEmail(signupRequest.getEmail())) {
			throw new ExistEmailException();
		}

		// 소환사명 중복 확인
		if (memberRepository.existsByRiotUsername(signupRequest.getRiotUsername())) {
			throw new ExistRiotUsernamException();
		}

		// 소환사 태그 중복 확인
		if (memberRepository.existsByRiotTag(signupRequest.getRiotTag())) {
			throw new ExistRiotTagException();
		}
	}
}
