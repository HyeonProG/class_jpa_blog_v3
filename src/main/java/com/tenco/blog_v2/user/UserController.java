package com.tenco.blog_v2.user;

import com.tenco.blog_v2.common.errors.Exception401;
import com.tenco.blog_v2.common.errors.Exception500;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {

    // DI 처리
    private final UserService userService;
    private final HttpSession session;


    /**
     * 회원정보 수정 페이지
     * 주소설계 : http://localhost:8080/update-form
     *
     * @param request
     * @return 문자열
     * 반환되는 문자열을 뷰 리졸버가 처리하며
     * 머스테치 템플릿 엔진을 통해서 뷰 파일을 렌더링 한다.
     */
    @GetMapping("/update-form")
    public String updateForm(HttpServletRequest request, HttpSession session) {

        //  @SessionAttribute(name = "sessionUser") User sessionUser
        // 해당 어노테이션은 모델에 저장되어 있는 세션 값을 바로 가지고 오는 어노테이션이다.
        // 단, 이녀석 뷰, 템플릿 엔진에서 접근하도록 설계되어 있다. 권장 x

        User sessionUser = (User) session.getAttribute("sessionUser");

        User user = userService.readUser(sessionUser.getId());
        request.setAttribute("user", user);

        return "user/update-form"; // 템플릿 경로 : user/update-form.mustache
    }

    /**
     * 사용자 정보 수정
     *
     * @param reqDTO
     * @return 메인 페이지
     */
    @PostMapping("user/update")
    public String update(@ModelAttribute(name = "updateDTO") UserDTO.UpdateDTO reqDTO) {
        // 세션에서 로그인한 사용자 정보 가져오기
        // 없다면 로그인 페이지로 이동
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login-form";
        }

        // 유효성 검사 생략

        // 조회한 엔티티에 정보 수정
        User updateUser = userService.updateUser(sessionUser.getId(), reqDTO);

        // 세션 정보 동기화 처리
        session.setAttribute("sessionUser", updateUser);

        return "redirect:/";
    }

    /**
     * 회원가입 페이지
     * 주소설계 : http://localhost:8080/join-form
     *
     * @param model
     * @return 문자열
     * 반환되는 문자열을 뷰 리졸버가 처리하며
     * 머스테치 템플릿 엔진을 통해서 뷰 파일을 렌더링 한다.
     */
    @GetMapping("/join-form")
    public String joinForm(Model model) {
        log.info("회원가입 페이지");
        model.addAttribute("name", "회원가입 페이지");
        return "user/join-form"; // 템플릿 경로 : user/join-form.mustache
    }

    /**
     * 회원가입 기능
     *
     * @param reqDTO
     * @return
     */
    @PostMapping("/join")
    public String join(@ModelAttribute(name = "joinDTO") UserDTO.JoinDTO reqDTO) {
        // 유효성 검사 생략
        try {
            userService.signUp(reqDTO);
        } catch (DataIntegrityViolationException e) {
            throw new Exception500("동일한 유저이름이 존재합니다.");
        }

        return "redirect:/login-form";
    }

    /**
     * 로그인 페이지
     * 주소설계 : http://localhost:8080/login-form
     *
     * @param model
     * @return 문자열
     * 반환되는 문자열을 뷰 리졸버가 처리하며
     * 머스테치 템플릿 엔진을 통해서 뷰 파일을 렌더링 한다.
     */
    @GetMapping("/login-form")
    public String loginForm(Model model) {
        log.info("로그인 페이지");
        model.addAttribute("name", "로그인 페이지");
        return "user/login-form"; // 템플릿 경로 : user/login-form.mustache
    }

    /**
     * 자원의 요청은 GET 방식이지만 보안의 이유로 예외적으로 POST 방식으로 사용
     * 로그인 처리 메서드
     * 요청 주소 POST : http://localhost:8080/login
     *
     * @param reqDto
     * @return
     */
    @PostMapping("/login")
    public String login(UserDTO.LoginDTO reqDto) {
        try {
            User sessionUser = userService.signIn(reqDto);
            session.setAttribute("sessionUser", sessionUser);
            return "redirect:/";
        } catch (Exception e) {
            // 로그인 실패
            throw new Exception401("유저이름 또는 비밀번호가 틀렸습니다.");
        }

    }

    @GetMapping("/logout")
    public String logout() {
        session.invalidate(); // 세션을 무효화(로그아웃)
        return "redirect:/";
    }

}
