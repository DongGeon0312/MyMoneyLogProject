package com.likelion.moneylog.domain.transaction.repository;

import com.likelion.moneylog.domain.transaction.entity.Transaction;
import com.likelion.moneylog.domain.user.entity.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // 본인 데이터 + 기간(월) 필터 + 페이징
    Page<Transaction> findByUserAndTransactionDateBetween(
            User user, LocalDate start, LocalDate end, Pageable pageable);

    // 통계용: 기간 내 전체 조회 (페이징 없이)
    List<Transaction> findByUserAndTransactionDateBetween(
            User user, LocalDate start, LocalDate end);

    // 단건 조회 시에도 소유자 검증을 함께 (인가의 핵심)
    Optional<Transaction> findByIdAndUser(Long id, User user);
}
