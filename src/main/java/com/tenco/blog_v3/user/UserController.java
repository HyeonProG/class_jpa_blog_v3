package com.tenco.blog_v3.user;

import com.tenco.blog_v3.common.utils.ApiUtil;
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
    public String update(@RequestBody UserRequest.UpdateDTO updateDTO) {
//        User sessionUser = (User) session.getAttribute("sessionUser");
//        if (sessionUser == null) {
//            return "redirect:/login-form";
//        }
//        User updatedUser = userService.updateUser(sessionUser.getId(), reqDTO);
//        // 세션 정보 동기화 처리
//        session.setAttribute("sessionUser", updatedUser);
        return "redirect:/";
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
    public String login() {

        return "";
    }

    @GetMapping("/logout")
    public String logout() {
        session.invalidate(); // 세션을 무효화 (로그아웃)
        return "redirect:/";
    }

}