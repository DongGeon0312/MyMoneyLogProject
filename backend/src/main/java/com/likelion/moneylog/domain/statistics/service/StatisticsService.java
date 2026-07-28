package com.likelion.moneylog.domain.statistics.service;

import com.likelion.moneylog.common.exception.CustomException;
import com.likelion.moneylog.common.exception.ErrorCode;
import com.likelion.moneylog.domain.category.entity.CategoryType;
import com.likelion.moneylog.domain.statistics.dto.StatisticsResponse;
import com.likelion.moneylog.domain.statistics.repository.StatisticsRepository;
import com.likelion.moneylog.domain.user.entity.User;
import com.likelion.moneylog.domain.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final StatisticsRepository statisticsRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public StatisticsResponse monthly(Long userId, String yearMonth) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        YearMonth ym = YearMonth.parse(yearMonth);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        long income = 0;
        long expense = 0;
        for (Object[] row : statisticsRepository.sumByType(user, start, end)) {
            CategoryType type = (CategoryType) row[0];
            long sum = (long) row[1];
            if (type == CategoryType.INCOME) {
                income = sum;
            } else {
                expense = sum;
            }
        }

        var byCategory = statisticsRepository.sumExpenseByCategory(user, start, end);
        return StatisticsResponse.of(income, expense, byCategory);
    }
}
