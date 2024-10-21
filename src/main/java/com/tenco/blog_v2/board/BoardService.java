package com.tenco.blog_v2.board;

import com.tenco.blog_v2.common.errors.Exception403;
import com.tenco.blog_v2.common.errors.Exception404;
import com.tenco.blog_v2.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // IoC
@RequiredArgsConstructor
public class BoardService {

    private final BoardJPARepository boardJPARepository;

    /**
     * 게시글 작성 서비스
     */
    @Transactional
    public void createBoard (BoardDTO.saveDTO reqDTO, User sessionUser) {
        boardJPARepository.save(reqDTO.toEntity(sessionUser));
    }

    /**
     *  게시글 ID로 조회 서비스
     */
    public Board getBoard(int boardId) {
        return boardJPARepository.findById(boardId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다."));
    }

    /**
     * 게시글 상세보기 서비스, 게시글 주인 여부 확인
     */
    public Board getBoardDetails(int boardId, User sessionUser) {
        // 전략 1.  JPA가 객체간의 관계를 통해 쿼리를 만들고 가지고 있다.
//        Board board = boardJPARepository.findById(boardId)
//                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다."));

        // 전략 2
        // JPQL - JOIN FETCH 사용, 즉 User 엔티티를 한번에 JOIN 처리
        Board board = boardJPARepository.findByIdJoinUser(boardId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다."));

        // 현재 사용자가 게시글을 작성했는지 여부 판별
        boolean isBoardOwner = false;
        if (sessionUser != null) {
            if (sessionUser.getId().equals(board.getUser().getId())) {
                isBoardOwner = true;
            }
        }

        // 코드 추가
        // 내가 작성한 댓글인지 확인하는 것을 구현 해야한다.
        board.getReplies().forEach( reply -> {
            boolean isReplyOwner = false;
            if (sessionUser != null) {
                if (sessionUser.getId().equals(reply.getUser().getId())) {
                    isReplyOwner = true;
                }
            }
            // 객체만 존재하는 필드 - 리플 객체 엔티티 상태값 변경 처리
            reply.setReplyOwner(isReplyOwner);
        });

        board.setBoardOwner(isBoardOwner);
        return board;
    }

    /**
     * 게시글 삭제 서비스
     */
    public void deleteBoard(int boardId, int sessionUserId) {
        // 게시글 존재 여부 확인
        Board board = boardJPARepository.findById(boardId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다."));

        // 권한 처리 - 현재 사용자가 게시글의 주인이 맞는가
        if (sessionUserId != board.getUser().getId()) {
            throw new Exception403("게시글을 삭제할 권한이 없습니다.");
        }

        // 게시글 삭제
        boardJPARepository.deleteById(boardId);
    }

    /**
     * 게시글 수정 서비스
     */
    @Transactional
    public void updateBoard(int boardId, int sessionUserId, BoardDTO.UpdateDTO reqDTO) {
        // 게시글 존재 여부 확인
        Board board = boardJPARepository.findById(boardId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다."));

        // 권한 확인
        if (sessionUserId != board.getUser().getId()) {
            throw new Exception403("게시글을 수정할 권한이 없습니다.");
        }

        // 게시글 수정
        board.setTitle(reqDTO.getTitle());
        board.setContent(reqDTO.getContent());

        // 더티 체킹 처리

    }

    /**
     * 모든 게시글 조회 서비스
     */
    public List<Board> getAllBoards() {
        // 게시글을 ID 기준으로 내림차순으로 정렬해서 조회
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        return boardJPARepository.findAll(sort);
    }
}
