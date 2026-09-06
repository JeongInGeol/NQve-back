package com.NQve.backend.domain.member.controller;

import com.NQve.backend.domain.member.dto.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import com.NQve.backend.domain.member.dto.SignupRequest;
import com.NQve.backend.domain.member.dto.SignupResponse;
import com.NQve.backend.domain.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final MemberService memberService;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository contexts;
    private final SessionAuthenticationStrategy sessions;

    public AuthController(MemberService memberService, AuthenticationManager authenticationManager,
            SecurityContextRepository contexts, SessionAuthenticationStrategy sessions) {
        this.memberService = memberService;
        this.authenticationManager = authenticationManager;
        this.contexts = contexts;
        this.sessions = sessions;
    }

    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken token) {
        return token;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest body,
            HttpServletRequest request, HttpServletResponse response) {
        if (body.email() == null || body.email().isBlank() || body.email().trim().length() > 100
                || body.password() == null || body.password().isBlank() || body.password().length() > 64) {
            return loginFailed();
        }

        try {
            // 1. DB 회원 조회와 암호화된 비밀번호 검증
            var authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(body.email().trim(), body.password()));
            // 2. 로그인 전 세션 ID를 교체
            sessions.onAuthentication(authentication, request, response);
            // 3. 인증 결과를 세션에 저장해서 다음 요청에서도 로그인 상태를 읽게 해요.
            var context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            contexts.saveContext(context, request, response);
            return ResponseEntity.noContent().build();
        } catch (AuthenticationException exception) {
            return loginFailed();
        }
    }

    private ResponseEntity<Map<String, String>> loginFailed() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "이메일 또는 비밀번호를 확인해주세요."));
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.signup(request));
    }
}
