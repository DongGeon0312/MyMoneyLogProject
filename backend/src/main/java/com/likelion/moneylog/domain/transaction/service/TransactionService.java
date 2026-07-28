package com.likelion.moneylog.domain.transaction.service;

import com.likelion.moneylog.common.exception.NotFoundException;
import com.likelion.moneylog.domain.category.entity.Category;
import com.likelion.moneylog.domain.category.repository.CategoryRepository;
import com.likelion.moneylog.domain.transaction.dto.TransactionRequest;
import com.likelion.moneylog.domain.transaction.dto.TransactionResponse;
import com.likelion.moneylog.domain.transaction.entity.Transaction;
import com.likelion.moneylog.domain.transaction.repository.TransactionRepository;
import com.likelion.moneylog.domain.user.entity.User;
import com.likelion.moneylog.domain.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public TransactionResponse create(Long userId, TransactionRequest req) {
        User user = getUser(userId);
        Category category = findOwnedCategory(user, req.categoryId());

        Transaction tx = Transaction.builder()
                .user(user)
                .category(category)
                .type(req.type())
                .amount(req.amount())
                .description(req.description())
                .transactionDate(req.transactionDate())
                .build();

        return TransactionResponse.from(transactionRepository.save(tx));
    }

    // 목록 조회 — 이번 달(yearMonth) 필터 + 페이징 (필수 범위). 타입/카테고리 필터는 여유 시 확장.
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getList(Long userId, String yearMonth, Pageable pageable) {
        User user = getUser(userId);
        YearMonth ym = YearMonth.parse(yearMonth); // "2026-07"
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        return transactionRepository
                .findByUserAndTransactionDateBetween(user, start, end, pageable)
                .map(TransactionResponse::from);
    }

    @Transactional(readOnly = true)
    public TransactionResponse get(Long userId, Long id) {
        return TransactionResponse.from(findOwnedTransaction(getUser(userId), id));
    }

    @Transactional
    public TransactionResponse update(Long userId, Long id, TransactionRequest req) {
        User user = getUser(userId);
        Transaction tx = findOwnedTransaction(user, id);
        Category category = findOwnedCategory(user, req.categoryId());

        tx.update(category, req.type(), req.amount(), req.description(), req.transactionDate());
        return TransactionResponse.from(tx);
    }

    @Transactional
    public void delete(Long userId, Long id) {
        User user = getUser(userId);
        transactionRepository.delete(findOwnedTransaction(user, id));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND", "사용자를 찾을 수 없습니다: " + userId));
    }

    private Category findOwnedCategory(User user, Long categoryId) {
        return categoryRepository.findByIdAndUser(categoryId, user)
                .orElseThrow(() -> new NotFoundException("CATEGORY_NOT_FOUND", "존재하지 않는 카테고리입니다: " + categoryId));
    }

    // 본인 소유 거래만 조회하는 공통 헬퍼 (인가의 핵심 — 1-8에서 그대로 재사용)
    private Transaction findOwnedTransaction(User user, Long id) {
        return transactionRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("TRANSACTION_NOT_FOUND", "거래내역을 찾을 수 없습니다: " + id));
    }
}
