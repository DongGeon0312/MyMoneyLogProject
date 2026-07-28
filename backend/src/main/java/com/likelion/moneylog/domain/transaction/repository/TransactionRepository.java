package com.likelion.moneylog.domain.transaction.repository;

import com.likelion.moneylog.domain.transaction.entity.Transaction;
import com.likelion.moneylog.domain.user.entity.User;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // 본인 데이터 + 기간(월) 필터 + 페이징
    Page<Transaction> findByUserAndTransactionDateBetween(
            User user, LocalDate start, LocalDate end, Pageable pageable);
}
