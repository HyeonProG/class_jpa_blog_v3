package com.tenco.blog_v2.reply;

import com.tenco.blog_v2.board.Board;
import com.tenco.blog_v2.board.BoardJPARepository;
import com.tenco.blog_v2.common.errors.Exception403;
import com.tenco.blog_v2.common.errors.Exception404;
import com.tenco.blog_v2.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReplyService {

    private final BoardJPARepository boardJPARepository;
    private final ReplyJPARepository replyJPARepository;

    /**
     * 댓글 쓰기
     *
     * @param reqDTO
     * @param sessionUser
     */
    @Transactional
    public void saveReply(ReplyDTO.SaveDTO reqDTO, User sessionUser) {
        // 댓글 작성시 게시글 존재 여부 반드시 확인
        Board board = boardJPARepository.findById(reqDTO.getBoardId())
                .orElseThrow(() -> new Exception404("존재하지 않는 게시글에 댓글 작성을 할 수 없습니다."));

        Reply reply = reqDTO.toEntity(sessionUser, board);
        replyJPARepository.save(reply);
    }

    /**
     * 댓글 삭제
     *
     * @param replyId
     * @param sessionUserId
     * @param boardId
     */
    @Transactional
    public void deleteReply(Integer replyId, Integer sessionUserId, Integer boardId) {
        // 댓글 존재 여부 확인
        Reply reply = replyJPARepository.findById(replyId)
                .orElseThrow(() -> new Exception404("존재하지 않는 댓글은 삭제를 할 수 없습니다."));

        // 권한 처리 확인
        if (!reply.getUser().getId().equals(sessionUserId)) {
            throw new Exception403("댓글 삭제 권한이 없습니다.");
        }

        if (!reply.getBoard().getId().equals(boardId)) {
            throw new Exception403("해당 게시글의 댓글이 아닙니다.");
        }

        replyJPARepository.deleteById(replyId);

    }

}
