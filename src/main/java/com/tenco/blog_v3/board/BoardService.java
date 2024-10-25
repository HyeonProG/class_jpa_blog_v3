package com.tenco.blog_v3.board;

import com.tenco.blog_v3.common.errors.Exception403;
import com.tenco.blog_v3.common.errors.Exception404;
import com.tenco.blog_v3.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


@RequiredArgsConstructor
@Service // IoC 처리
public class BoardService {

    private final BoardJPARepository boardJPARepository;

    /**
     * 새로운 게시글을 작성하여 저장합니다.
     *
     * @param reqDTO 게시글 작성 요청 DTO
     * @param sessionUser 현재 세션에 로그인한 사용자
     */
    @Transactional // 트랜잭션 관리: 데이터베이스 연산이 성공적으로 완료되면 커밋, 실패하면 롤백
    public BoardResponse.DTO createBoard(BoardRequest.SaveDTO reqDTO, User sessionUser){
        // 요청 DTO를 엔티티로 변환하여 저장합니다.
        Board savedBoard = boardJPARepository.save(reqDTO.toEntity(sessionUser));
        return new BoardResponse.DTO(savedBoard);
    }


    /**
     * 게시글 ID로 조회 서비스
     */
    public Board getBoard(int boardId) {
        return boardJPARepository
                .findById(boardId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없어요"));
    }

    /**
     * 게시글 상세 조회 서비스
     * @param boardId 조회할 게시글의 ID
     * @param sessionUser 현재 세션 사용자 정보
     * @return 게시글 상세 정보의 DTO
     */
    // 메서드 종료까지 영속성 컨텍스 즉 connection 열어 있음
    // @Transactional 없는 경우 오류 발생 (LazyInitializationException)
    @Transactional
    public BoardResponse.DetailDTO getBoardDetails(int boardId, User sessionUser) {
        Board board = boardJPARepository.findByIdJoinUser(boardId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다"));

        BoardResponse.DetailDTO boardDetail = new BoardResponse.DetailDTO(board, sessionUser);
        System.out.println(boardDetail.toString());
        return boardDetail;
    }


    /**
     * 게시글 삭제 서비스
     */
    public void deleteBoard(int boardId, int sessionUserId) {
         // 1. 
        Board board = boardJPARepository.findById(boardId).orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다"));

        // 2. 권한 처리 - 현재 사용자가 게시글 주인이 맞는가?
        if(sessionUserId != board.getUser().getId()) {
            throw new Exception403("게시글을 삭제할 권한이 없습니다");
        }

        // 3. 게시글 삭제 하기
        boardJPARepository.deleteById(boardId);
    }

    /**
     * 게시글 수정 서비스
     */
    @Transactional
    public void updateBoard(int boardId, int sessionUserId, BoardRequest.UpdateDTO reqDTO) {
        // 1. 게시글 존재 여부 확인
        Board board = boardJPARepository.findById(boardId).orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다"));
        // 2. 권한 확인
        if(sessionUserId != board.getUser().getId()) {
            throw new Exception403("게시글 수정 권한이 없습니다");
        }
        // 3. 게시글 수정
        board.setTitle(reqDTO.getTitle());
        board.setContent(reqDTO.getContent());
        // 더티 체킹 처리
    }

    /**
     * 모든 게시글 조회 서비스
     */
    // 응답 타입 변경
    public List<BoardResponse.ListDTO> getAllBoards() {
        // 게시글을 ID 기준으로 내림차순으로 정렬해서 조회 해라.
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        List<Board> boards = boardJPARepository.findAll(sort);

        return boards.stream().map(BoardResponse.ListDTO::new).toList();
    }

}




