package com.tenco.blog_v3.common.config;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.tenco.blog_v3.common.errors.Exception401;
import com.tenco.blog_v3.common.errors.Exception500;
import com.tenco.blog_v3.common.utils.Define;
import com.tenco.blog_v3.common.utils.JwtUtil;
import com.tenco.blog_v3.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

// IoC 를 안한 상태 이다.
public class LoginInterceptor implements HandlerInterceptor {

    /**
     * 컨트롤러 메서드 호출 전에 실행 되는 메서드 이다.
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler chosen handler to execute, for type and/or instance evaluation
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String jwt = request.getHeader(Define.AUTHORIZATION);
        if (jwt == null || !jwt.startsWith(Define.BEARER)) {
            throw new Exception401("JWT 토큰을 전달해 주세요.");
        }
        jwt = jwt.replace(Define.BEARER, "");
        try {
            User sessionUser = JwtUtil.verify(jwt);
            request.setAttribute(Define.SESSION_USER, sessionUser);
            return true;

        } catch (TokenExpiredException e) {
            throw new Exception401("토큰 만료 시간이 지났습니다. 다시 로그인 하세요.");
        } catch (JWTDecodeException e) {
            throw new Exception401("유효하지 않은 토큰입니다.");
        } catch (Exception e) {
            throw new Exception500("서버 오류 : " + e.getMessage());
        }

    }

    /**
     * 컨트롤러 실행 후, 뷰가 렌더링되기 전에 실행되는 메서드
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    /**
     * 뷰가 렌더링 된 후 실행되는 메서드
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
