package com.likelion.moneylog.domain.category.repository;

import com.likelion.moneylog.domain.category.entity.Category;
import com.likelion.moneylog.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUser(User user); // 내 카테고리 목록

    Optional<Category> findByIdAndUser(Long id, User user); // 소유자 검증 포함 단건 조회

    boolean existsByUser(User user); // 기본 카테고리 중복 시드 방지
}
