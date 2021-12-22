package mandykr.nutrient.repository;

import mandykr.nutrient.entity.SupplementCategory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class SupplementCategoryRepositoryTest {
    @Autowired
    SupplementCategoryRepository categoryRepository;

    SupplementCategory parentCategory;
    SupplementCategory category;

    @BeforeEach
    void setup() {
        parentCategory = new SupplementCategory("오메가369/피쉬오일", 0);
        category = new SupplementCategory("오메가3", 1, parentCategory);
    }

    @AfterEach
    void tearDown() {
        categoryRepository.deleteAll();
    }

    @Test
    @DisplayName("카테고리를 저장한다.")
    void save_category() {
        // given
        // when
        SupplementCategory saveParentCategory = categoryRepository.save(parentCategory);
        SupplementCategory saveCategory = categoryRepository.save(category);

        // then
        assertEquals(parentCategory.getName(), saveParentCategory.getName());
        assertEquals(category.getName(), saveCategory.getName());
        assertEquals(parentCategory.getName(), saveCategory.getParentCategory().getName());

    }

    @Test
    @DisplayName("카테고리 목록을 조회한다.")
    void find_categoryList() {
        // given
        SupplementCategory saveParentCategory = categoryRepository.save(parentCategory);
        SupplementCategory saveCategory = categoryRepository.save(category);

        // when
        List<SupplementCategory> categoryList = categoryRepository.findAll();

        // then
        assertThat(categoryList).size().isGreaterThan(0);
    }

    @Test
    @DisplayName("카테고리의 레벨을 변경한다.")
    void update_level_category() {
        // given
        SupplementCategory saveCategory = categoryRepository.save(category);

        // when
        category.changeLevel(1);

        // then
        assertThatThrownBy(() -> {
            category.changeLevel(-1);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("변경할 레벨은 0 이상의 정수이어야 합니다.");
        assertEquals(category.getLevel(), saveCategory.getLevel());
    }

    @Test
    @DisplayName("카테고리의 부모 카테고리를 변경한다.")
    void update_parent_category() {
        // given
        SupplementCategory saveCategory = categoryRepository.save(category);
        SupplementCategory newParentCategory = new SupplementCategory("오메가369", 1);

        // when
        category.moveToAnotherParent(newParentCategory);

        // then
        assertEquals(newParentCategory, saveCategory.getParentCategory());
        assertEquals(saveCategory.getLevel(), newParentCategory.getLevel() + 1);
    }

    @Test
    @DisplayName("카테고리를 삭제한다.")
    void delete_category() {
        // given
        SupplementCategory saveCategory = categoryRepository.save(category);

        // when
        categoryRepository.deleteById(saveCategory.getId());

        // then
        assertEquals(Optional.empty(), categoryRepository.findById(saveCategory.getId()));
    }
}