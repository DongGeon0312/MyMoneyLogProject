package com.likelion.moneylog.domain.category.service;

import com.likelion.moneylog.common.exception.CustomException;
import com.likelion.moneylog.common.exception.ErrorCode;
import com.likelion.moneylog.domain.category.dto.CategoryRequest;
import com.likelion.moneylog.domain.category.dto.CategoryResponse;
import com.likelion.moneylog.domain.category.entity.Category;
import com.likelion.moneylog.domain.category.entity.CategoryType;
import com.likelion.moneylog.domain.category.repository.CategoryRepository;
import com.likelion.moneylog.domain.user.entity.User;
import com.likelion.moneylog.domain.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getMyCategories(Long userId) {
        User user = getUser(userId);
        return categoryRepository.findByUser(user).stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @Transactional
    public CategoryResponse create(Long userId, CategoryRequest req) {
        User user = getUser(userId);
        Category saved = categoryRepository.save(
                Category.builder().user(user).name(req.name()).type(req.type()).build());
        return CategoryResponse.from(saved);
    }

    @Transactional
    public CategoryResponse update(Long userId, Long id, CategoryRequest req) {
        Category category = findOwned(userId, id);
        category.update(req.name(), req.type());
        return CategoryResponse.from(category);
    }

    @Transactional
    public void delete(Long userId, Long id) {
        Category category = findOwned(userId, id);
        categoryRepository.delete(category);
    }

    // 회원가입 시 기본 카테고리 자동 시드 (1-8 회원가입 로직에서 호출 예정)
    @Transactional
    public void seedDefaultCategories(User user) {
        if (categoryRepository.existsByUser(user)) {
            return; // 중복 시드 방지
        }
        List<Category> defaults = List.of(
                Category.builder().user(user).name("식비").type(CategoryType.EXPENSE).build(),
                Category.builder().user(user).name("교통").type(CategoryType.EXPENSE).build(),
                Category.builder().user(user).name("주거").type(CategoryType.EXPENSE).build(),
                Category.builder().user(user).name("문화").type(CategoryType.EXPENSE).build(),
                Category.builder().user(user).name("급여").type(CategoryType.INCOME).build(),
                Category.builder().user(user).name("용돈").type(CategoryType.INCOME).build()
        );
        categoryRepository.saveAll(defaults);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private Category findOwned(Long userId, Long id) {
        User user = getUser(userId);
        return categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
    }
}
