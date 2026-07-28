package com.likelion.moneylog.domain.transaction.service;

import com.likelion.moneylog.common.exception.CustomException;
import com.likelion.moneylog.common.exception.ErrorCode;
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
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private Category findOwnedCategory(User user, Long categoryId) {
        return categoryRepository.findByIdAndUser(categoryId, user)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    // 존재 여부(404)와 소유권(403)을 구분해서 응답한다 — 인가의 핵심
    private Transaction findOwnedTransaction(User user, Long id) {
        Transaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.TRANSACTION_NOT_FOUND));
        if (!tx.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        return tx;
    }
}
