package com.likelion.moneylog.domain.transaction.controller;

import com.likelion.moneylog.common.response.ApiResponse;
import com.likelion.moneylog.common.response.PageMeta;
import com.likelion.moneylog.domain.transaction.dto.TransactionRequest;
import com.likelion.moneylog.domain.transaction.dto.TransactionResponse;
import com.likelion.moneylog.domain.transaction.service.TransactionService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<ApiResponse<TransactionResponse>> create(@AuthenticationPrincipal Long userId,
            @Valid @RequestBody TransactionRequest req) {
        TransactionResponse created = transactionService.create(userId, req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("거래내역이 등록되었습니다.", created));
    }

    @GetMapping
    public ApiResponse<Map<String, List<TransactionResponse>>> getList(
            @AuthenticationPrincipal Long userId,
            @RequestParam String yearMonth,
            @PageableDefault(size = 20, sort = "transactionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TransactionResponse> page = transactionService.getList(userId, yearMonth, pageable);
        var data = Map.of("transactions", page.getContent());
        return ApiResponse.success("거래내역 목록을 조회했습니다.", data, PageMeta.from(page));
    }

    @GetMapping("/{id}")
    public ApiResponse<TransactionResponse> get(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        return ApiResponse.success("거래내역을 조회했습니다.", transactionService.get(userId, id));
    }

    @PutMapping("/{id}")
    public ApiResponse<TransactionResponse> update(@AuthenticationPrincipal Long userId,
            @PathVariable Long id, @Valid @RequestBody TransactionRequest req) {
        return ApiResponse.success("거래내역이 수정되었습니다.", transactionService.update(userId, id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        transactionService.delete(userId, id);
        return ResponseEntity.noContent().build();
    }
}
