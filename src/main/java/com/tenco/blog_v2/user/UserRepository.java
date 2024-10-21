package com.tenco.blog_v2.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Repository
public class UserRepository {

    private final EntityManager em;

    /**
     * 사용자 이름과 비밀번호로 사용자 조회
     * @param username
     * @param password
     * @return 조회된 User 엔티티 없으면 null 반환
     */
    public User findByUsernameAndPassword(String username, String password) {
        TypedQuery<User> jpql
                = em.createQuery(" SELECT u FROM User u WHERE u.username = :username AND u.password = :password ", User.class);
        jpql.setParameter("username", username);
        jpql.setParameter("password", password);
        return jpql.getSingleResult();
    }

    /**
     * 사용자 저장 메서드 (JPA API 사용)
     * @param user
     * @return 저장된 사용자 엔티티
     */
    @Transactional
    public User save(User user) {
        // JPQL 은 INSERT 구문을 직접 지원하지 않는다.
        em.persist(user); // 영속화
        return  user;
    }

    /**
     * 사용자 정보 수정 기능 만들기(더티 체킹 사용)
     */
    @Transactional // 더티 체킹 반영
    public User updateById(int id, String password, String email) {
        // id 값으로 영속성 컨텍스트 정보가 있는지 확인
        User userEntity = findById(id);
        userEntity.setPassword(password);
        userEntity.setEmail(email);

        // 객체 상태값이 변경된 엔티티를 반환
        return userEntity;
    }

    /**
     * 사용자 정보 찾기
     * @param id
     * @return
     */
    public User findById(int id) {
        return em.find(User.class, id);
    }

}