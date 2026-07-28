package com.likelion.moneylog.global.config;

import com.likelion.moneylog.domain.category.entity.Category;
import com.likelion.moneylog.domain.category.entity.CategoryType;
import com.likelion.moneylog.domain.category.repository.CategoryRepository;
import com.likelion.moneylog.domain.transaction.entity.Transaction;
import com.likelion.moneylog.domain.transaction.repository.TransactionRepository;
import com.likelion.moneylog.domain.user.entity.User;
import com.likelion.moneylog.domain.user.repository.UserRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

// 로컬(H2)에서 엔티티/Repository 동작을 눈으로 확인하기 위한 임시 데이터 주입.
// 2일차 CRUD 구현 후에는 실제 API로 대체하고 이 클래스는 삭제해도 된다.
@Slf4j
@Profile("local")
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public void run(String... args) {
        User user = userRepository.save(User.builder()
                .email("test@moneylog.com")
                .password("TEMP_PASSWORD") // 실제로는 BCrypt 인코딩 (3일차)
                .nickname("머니로그유저")
                .build());

        Category food = categoryRepository.save(Category.builder()
                .user(user).name("식비").type(CategoryType.EXPENSE).build());

        transactionRepository.save(Transaction.builder()
                .user(user).category(food).type(CategoryType.EXPENSE)
                .amount(12000L).description("점심 김치찌개")
                .transactionDate(LocalDate.of(2026, 7, 8))
                .build());

        long count = transactionRepository.count();
        log.info("저장된 거래 수 = {}", count);
        transactionRepository.findAll()
                .forEach(t -> log.info("거래: {}원 / {} / {}",
                        t.getAmount(), t.getCategory().getName(), t.getTransactionDate()));
    }
}
