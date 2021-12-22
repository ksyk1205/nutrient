package mandykr.nutrient.service;

import mandykr.nutrient.dto.SupplementCategoryDto;
import mandykr.nutrient.entity.SupplementCategory;
import mandykr.nutrient.repository.SupplementCategoryRepository;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SupplementCategoryServiceTest {
    SupplementCategoryRepository categoryRepository = mock(SupplementCategoryRepository.class);
    SupplementCategoryService categoryService = new SupplementCategoryService(categoryRepository);

    @Test
    void 카테고리_단건_수정() {
        SupplementCategoryDto categoryDto =
                new SupplementCategoryDto(1L, "카테고리1", 1,
                        new SupplementCategoryDto(2L, "카테고리2", 0, null));
        given(categoryRepository.findById(1L)).willReturn(Optional.of(new SupplementCategory("카테고리", 0, null)));

        SupplementCategory category = categoryService.updateCategory(categoryDto);

        then(categoryRepository).should(times(1)).findById(1L);
        assertEquals(categoryDto.getName(), category.getName());
        assertEquals(categoryDto.getLevel(), category.getLevel());
        assertEquals(categoryDto.getParentCategory().getName(), category.getParentCategory().getName());
    }
}