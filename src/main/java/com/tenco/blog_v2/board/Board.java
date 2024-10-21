package com.tenco.blog_v2.board;

import com.tenco.blog_v2.reply.Reply;
import com.tenco.blog_v2.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Entity
@Table(name = "board_tb")
@Getter
@Setter
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본키 전략 db 위임
    private Integer id;
    private String title;
    @Lob // 대용량 데이터 저장 가능
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // 게시글 작성자 정보
    private User user;

    // created_at 컬럼과 매핑하며, 이 필드는 데이터 저장시 자동으로 설정된다.
    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;

    // 해당 테이블에 컬럼을 만들지 마라!
    // 즉, JPA 메모리상에서만 활용 가능한 필드이다.
    @Transient
    boolean isBoardOwner;

    // 댓글 엔티티를 넣어서 관계를 설정하면 양방향
    // cascade = CascadeType.REMOVE --> 해당 게시글이 삭제되면 종속되어 있는 댓글들을 먼저 삭제해야 한다.
    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Reply> replies = new ArrayList<>(); // 빠른 초기화

    @Builder
    public Board(Integer id, String title, String content, User user, Timestamp createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.user = user;
        this.createdAt = createdAt;
    }

}
