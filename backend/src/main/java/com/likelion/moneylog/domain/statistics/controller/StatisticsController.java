package com.likelion.moneylog.domain.statistics.controller;

import com.likelion.moneylog.common.response.ApiResponse;
import com.likelion.moneylog.domain.statistics.dto.StatisticsResponse;
import com.likelion.moneylog.domain.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/monthly")
    public ApiResponse<StatisticsResponse> monthly(@AuthenticationPrincipal Long userId,
            @RequestParam String yearMonth) {
        return ApiResponse.success("월별 통계를 조회했습니다.", statisticsService.monthly(userId, yearMonth));
    }
}
