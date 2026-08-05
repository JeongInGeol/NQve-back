package com.NQve.backend.domain.member.service;

import com.NQve.backend.domain.member.dto.SignupRequest;
import com.NQve.backend.domain.member.dto.SignupResponse;
import com.NQve.backend.domain.member.entity.Member;
import com.NQve.backend.domain.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private static final int MAX_EMAIL_LENGTH = 100;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 64;
    private static final int MIN_NICKNAME_LENGTH = 2;
    private static final int MAX_NICKNAME_LENGTH = 20;

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        validateSignupRequest(request);

        String email = request.getEmail().trim();
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        Member member = new Member(
                email,
                passwordEncoder.encode(request.getPassword()),
                request.getNickname().trim()
        );

        Member savedMember = memberRepository.save(member);
        return new SignupResponse(
                savedMember.getId(),
                savedMember.getEmail(),
                savedMember.getNickname(),
                savedMember.getStatus()
        );
    }

    private void validateSignupRequest(SignupRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("회원가입 요청이 비어 있습니다.");
        }
        if (isBlank(request.getEmail())) {
            throw new IllegalArgumentException("이메일은 필수입니다.");
        }
        String email = request.getEmail().trim();
        if (email.length() > MAX_EMAIL_LENGTH) {
            throw new IllegalArgumentException("이메일은 100자 이하로 입력해주세요.");
        }
        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("올바른 이메일 형식으로 입력해주세요.");
        }

        if (isBlank(request.getPassword())) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
        String password = request.getPassword();
        if (password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("비밀번호는 8자 이상 64자 이하로 입력해주세요.");
        }
        if (password.matches(".*\\s.*")) {
            throw new IllegalArgumentException("비밀번호에는 공백을 사용할 수 없습니다.");
        }
        if (!password.matches(".*[A-Za-z].*")) {
            throw new IllegalArgumentException("비밀번호에는 영문이 포함되어야 합니다.");
        }
        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("비밀번호에는 숫자가 포함되어야 합니다.");
        }

        if (isBlank(request.getNickname())) {
            throw new IllegalArgumentException("닉네임은 필수입니다.");
        }
        String nickname = request.getNickname().trim();
        if (nickname.length() < MIN_NICKNAME_LENGTH || nickname.length() > MAX_NICKNAME_LENGTH) {
            throw new IllegalArgumentException("닉네임은 2자 이상 20자 이하로 입력해주세요.");
        }
        if (!nickname.matches("^[가-힣A-Za-z0-9_]+$")) {
            throw new IllegalArgumentException("닉네임은 한글, 영문, 숫자, _만 사용할 수 있습니다.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
