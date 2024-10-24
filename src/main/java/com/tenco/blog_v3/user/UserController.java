package com.tenco.blog_v3.user;

import com.tenco.blog_v3.common.errors.Exception401;
import com.tenco.blog_v3.common.errors.Exception403;
import com.tenco.blog_v3.common.utils.ApiUtil;
import com.tenco.blog_v3.common.utils.Define;
import com.tenco.blog_v3.common.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Slf4j
@RestController
public class UserController {

    // DI 처리
    private final UserService userService;
    private final HttpSession session;

    /**
     * 회원 정보 수정
     *
     */
    @PutMapping("/api/users/{id}")
    public ResponseEntity<?> update(@PathVariable(name = "id") Integer id, @RequestBody UserRequest.UpdateDTO reqDTO, HttpServletRequest request) {
        // 헤더에 있는 JWT 토큰을 가져오기
        // 토큰에서 사용자 정보 추출
        // 사용자 정보 수정 로직 사용

        String authorizationHeader = request.getHeader(Define.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith(Define.BEARER)) {
            throw new Exception401("인증 정보가 유효하지 않습니다.");
        }
        
        // Bearer 문자열과 공백 한칸 제거 처리
        String token = authorizationHeader.replace(Define.BEARER, "");
        User sessionUser = JwtUtil.verify(token);
        if (sessionUser == null) {
            throw new Exception401("인증 토큰이 유효하지 않습니다.");
        }

        if (sessionUser.getId() != id) {
            throw new Exception403("해당 사용자를 수정할 권한이 없습니다.");
        }
        
        // 서비스에 사용자 정보 수정 요청
        UserResponse.DTO resDTO = userService.updateUser(id, reqDTO);

        return ResponseEntity.ok(new ApiUtil<>(resDTO));
    }



    // @ResponseBody // 데이터 반환
    @PostMapping("/join")
    public ResponseEntity<ApiUtil<UserResponse.DTO>> join(UserRequest.JoinDTO reqDTO) {
        // 회원가입 서비스는 --> 서비스 객체에게 위임한다.
        UserResponse.DTO resDTO = userService.signUp(reqDTO);
        return ResponseEntity.ok(new ApiUtil<>(resDTO));
    }

    /**
     * 자원에 요청은 GET 방식이지만 보안에 이유로 예외 !
     * 로그인 처리 메서드
     * 요청 주소 POST : http://localhost:8080/login
     *
     * @param reqDto
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<ApiUtil<UserResponse.DTO>> login(@RequestBody UserRequest.LoginDTO reqDto) {

        // 사용자가 던진 값을 받아서 DB 에서 사용자 정보 확인
        String jwt = userService.signIn(reqDto);

        return ResponseEntity.ok().header(Define.AUTHORIZATION, Define.BEARER + jwt)
                .body(new ApiUtil<>(null));
    }

    @GetMapping("/logout")
    public String logout() {
        session.invalidate(); // 세션을 무효화 (로그아웃)
        return "redirect:/";
    }

}