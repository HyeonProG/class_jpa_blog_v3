package com.tenco.blog_v3.board;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.tenco.blog_v3.common.utils.ApiUtil;
import com.tenco.blog_v3.common.utils.Define;
import com.tenco.blog_v3.common.utils.JwtUtil;
import com.tenco.blog_v3.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class BoardController {

    private final BoardService boardService; // BoardService 주입
    private final HttpSession session;


    /**
     * 게시글 수정 처리 메서드
     * 요청 주소: **POST http://localhost:8080/board/{id}/update**
     *
     * @param id        수정할 게시글의 ID
     * @param updateDTO 수정된 데이터를 담은 DTO
     * @return 게시글 상세보기 페이지로 리다이렉트
     */
    @PutMapping("/api/boards/{id}")
    public String update(@PathVariable(name = "id") Integer id, @ModelAttribute(name = "updateDTO") BoardRequest.UpdateDTO updateDTO) {
        // 세션에서 로그인한 사용자 정보 가져오기
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login-form"; // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        }

        // 게시글 수정 서비스 호출
        boardService.updateBoard(id, sessionUser.getId(), updateDTO);

        // 수정 완료 후 게시글 상세보기 페이지로 리다이렉트
        return "redirect:/board/" + id;
    }


    /**
     * 게시글 삭제 처리 메서드
     * 요청 주소: **POST http://localhost:8080/board/{id}/delete**
     *
     * @param id 삭제할 게시글의 ID
     * @return 메인 페이지로 리다이렉트
     */
    @DeleteMapping("/api/boards/{id}")
    public String delete(@PathVariable(name = "id") Integer id) {
        // 세션에서 로그인한 사용자 정보 가져오기
        User sessionUser = (User) session.getAttribute("sessionUser");

        // 세션 유효성 검증
        if (sessionUser == null) {
            return "redirect:/login-form"; // 로그인 페이지로 리다이렉트
        }

        // 게시글 삭제 서비스 호출
        boardService.deleteBoard(id, sessionUser.getId());

        // 메인 페이지로 리다이렉트
        return "redirect:/";
    }


    /**
     * 게시글 작성 처리 메서드
     * @param reqDto
     * @param request
     * @return
     */
    @PostMapping("/api/boards")
    public ResponseEntity<ApiUtil<BoardResponse.DTO>> save(@RequestBody BoardRequest.SaveDTO reqDto, HttpServletRequest request) {

        // 여기서 사용자 정보가 필요하다.
        User sessionUser = (User) request.getAttribute(Define.SESSION_USER);

        // 게시글 작성 서비스 호출
        BoardResponse.DTO resDTO = boardService.createBoard(reqDto, sessionUser);

        return ResponseEntity.ok(new ApiUtil<>(resDTO));
    }

    /**
     * 게시글 상세보기 처리 메서드
     * 요청 주소: **GET http://localhost:8080/board/{id}**
     *
     * @param id      게시글의 ID
     * @param request HTTP 요청 객체
     * @return 게시글 상세보기 페이지 뷰
     */
    @GetMapping("/boards/{id}")
    public ResponseEntity<?> detail(@PathVariable Integer id, HttpServletRequest request) {
        User sessionUser = null;
        // api 경로가 아니기 때문에 jwt 토큰이 있는지 여부 검사
        String authorizationHeader = request.getHeader(Define.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith(Define.BEARER)) {
            // 인증된 사용자 (여기 안에 오면)
            String token = authorizationHeader.replace(Define.BEARER, "");
            try {
                sessionUser = JwtUtil.verify(token);
            } catch (TokenExpiredException e) {
                return ResponseEntity.status(401).body(new ApiUtil<>(401, "토큰 유효시간 만료"));
            } catch (Exception e) {
                return ResponseEntity.status(401).body(new ApiUtil<>(401, "유효하지 않은 토큰입니다."));
            }
        }
        // 게시글 상세보기 로직 호출
        BoardResponse.DetailDTO boardDetail = boardService.getBoardDetails(id, sessionUser);
        return ResponseEntity.ok(new ApiUtil<>(boardDetail));
    }

    // 게시글 전체 조회
    @GetMapping({"/", "/boards"})
    public ResponseEntity<List<BoardResponse.ListDTO>> list() {
        return ResponseEntity.ok(boardService.getAllBoards());
    }

}









