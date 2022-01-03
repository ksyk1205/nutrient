package mandykr.nutrient.service;

import mandykr.nutrient.dto.SupplementCategoryDto;
import mandykr.nutrient.entity.SupplementCategory;
import mandykr.nutrient.repository.SupplementCategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SupplementCategoryServiceTest {
    SupplementCategoryRepository categoryRepository = mock(SupplementCategoryRepository.class);
    SupplementCategoryService categoryService = new SupplementCategoryService(categoryRepository);

    @Test
    void 신규_카테고리를_저장한다_단건() {
        // given
        Long categoryId = 2L;

        SupplementCategoryDto categoryDto = SupplementCategoryDto.toCategoryDto(categoryId, "자식 카테고리", 2, 1L);
        SupplementCategory category = SupplementCategory.toEntity(categoryId, "자식 카테고리", 1);

        given(categoryRepository.save(any(SupplementCategory.class))).willReturn(category);

        // when
        SupplementCategory saveCategory = categoryService.createCategory(categoryDto);

        // then
        then(categoryRepository).should(times(1)).save(any(SupplementCategory.class));
        assertEquals(categoryId, saveCategory.getId());
    }

    @Test
    void 카테고리의_이름과_부모를_변경한다_단건() {
        // given
        Long categoryId = 2L;
        Long findParentId = 1L;

        SupplementCategoryDto categoryDto = SupplementCategoryDto.toCategoryDto(categoryId, "자식 카테고리 수정", 2, findParentId);
        SupplementCategory category = SupplementCategory.toEntity(categoryId, "자식 카테고리", 1);
        SupplementCategory findParentCategory = SupplementCategory.toEntity(findParentId, "신규 부모 카테고리", 1);

        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));
        given(categoryRepository.findById(findParentId)).willReturn(Optional.of(findParentCategory));

        // when
        SupplementCategory updateCategory = categoryService.updateCategory(categoryDto);

        // then
        then(categoryRepository).should(times(1)).findById(categoryId);
        then(categoryRepository).should(times(1)).findById(findParentId);
        assertEquals(categoryDto.getName(), updateCategory.getName());
        assertEquals(findParentCategory.getLevel() + 1, updateCategory.getLevel());
        assertEquals(findParentId, updateCategory.getParentCategory().getId());
    }

    @Test
    void 카테고리를_아이디로_조회한다() {
        // given
        Long findId = 2L;
        Long emptyId = 3L;

        SupplementCategory category = SupplementCategory.toEntity(findId, "자식 카테고리", 1);

        given(categoryRepository.findById(findId)).willReturn(Optional.of(category));
        given(categoryRepository.findById(argThat(l -> l != findId))).willReturn(Optional.empty());

        // when
        SupplementCategory findCategory = categoryService.getCategory(findId);

        // then
        then(categoryRepository).should(times(1)).findById(findId);
        assertThatThrownBy(() -> {
            categoryService.getCategory(emptyId);
        }).isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("not found category: " + emptyId);
        assertEquals(findId, findCategory.getId());
    }
}