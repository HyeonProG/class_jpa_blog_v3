package com.tenco.blog_v2.user;

import com.tenco.blog_v2.common.errors.Exception400;
import com.tenco.blog_v2.common.errors.Exception401;
import com.tenco.blog_v2.common.errors.Exception404;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service // IoC
@RequiredArgsConstructor
public class UserService {

    // @Autowired
    private final UserJPARepository userJPARepository;

    /**
     * 회원가입 서비스
     */
    @Transactional
    public void signUp(UserDTO.JoinDTO reqDTO) {
        // 1. username <-- 유니크 확인
        Optional<User> userOp = userJPARepository.findByUsername(reqDTO.getUsername());
        if (userOp.isPresent()) {
            throw new Exception400("중복된 이름입니다.");
        }

        // 회원가입
        userJPARepository.save(reqDTO.toEntity());
    }

    /**
     * 로그인 서비스
     */
    public User signIn(UserDTO.LoginDTO reqDTO) {
        User sessionUser = userJPARepository.
                findByUsernameAndPassword(reqDTO.getUsername(), reqDTO.getPassword())
                .orElseThrow(() -> new Exception401("인증되지 않았습니다."));
        return sessionUser;
    }

    /**
     * 회원 정보 조회 서비스
     *
     * @return
     */
    public User readUser(int id) {
        User user = userJPARepository.findById(id)
                .orElseThrow(() -> new Exception404("회원정보를 찾을 수 없습니다."));
        return user;
    }

    /**
     * 회원 정보 수정 서비스
     */
    public User updateUser(int id, UserDTO.UpdateDTO reqDTO) {
        // 사용자 조회 및 예외 처리
        User user = userJPARepository.findById(id)
                .orElseThrow(() -> new Exception404("회원정보를 찾을 수 없습니다."));

        // 사용자 정보 수정
        user.setEmail(reqDTO.getEmail());
        user.setPassword(reqDTO.getPassword());

        // 더티 체킹을 통해 변경사항 자동 반영
        return user;
    }


}
