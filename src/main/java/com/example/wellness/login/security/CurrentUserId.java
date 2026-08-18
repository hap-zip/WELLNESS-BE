package com.example.wellness.login.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 로그인 토큰(Authorization: Bearer)에서 뽑아낸 사용자 id를 컨트롤러 파라미터로 바로 받기 위한 어노테이션. */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUserId {
}
