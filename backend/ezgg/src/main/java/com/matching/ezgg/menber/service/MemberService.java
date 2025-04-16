package com.matching.ezgg.menber.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matching.ezgg.menber.dto.SignupRequest;
import com.matching.ezgg.menber.dto.SignupResponse;
import com.matching.ezgg.menber.entity.Member;
import com.matching.ezgg.menber.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	private final PasswordEncoder passwordEncoder;

	@Transactional
	public SignupResponse signup(SignupRequest signupRequest) {

		String password = passwordEncoder.encode(signupRequest.getPassword());

		Member newMember = Member.builder()
			.memberId(signupRequest.getMemberId())
			.password(password)
			.email(signupRequest.getEmail())
			.riotUsername(signupRequest.getRiotUsername())
			.riotTag(signupRequest.getRiotTag())
			.build();

		memberRepository.save(newMember);

		return SignupResponse.builder()
			.memberId(newMember.getMemberId())
			.email(newMember.getEmail())
			.riotUsername(newMember.getRiotUsername())
			.riotTag(newMember.getRiotTag())
			.build();

	}
}
