package mandykr.nutrient.repository.supplement;

import mandykr.nutrient.entity.SupplementCategory;
import mandykr.nutrient.repository.supplement.SupplementCategoryRepository;
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

    @Test
    @DisplayName("카테고리를 저장한다.")
    void save_category() {
        // given
        SupplementCategory parentCategory = SupplementCategory.builder().name("오메가369/피쉬오일").depth(0).build();
        SupplementCategory category = SupplementCategory.builder().name("오메가3").depth(1).parentCategory(parentCategory).build();

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
        SupplementCategory parentCategory = SupplementCategory.builder().name("오메가369/피쉬오일").depth(0).build();
        SupplementCategory category = SupplementCategory.builder().name("오메가3").depth(1).parentCategory(parentCategory).build();
        SupplementCategory saveParentCategory = categoryRepository.save(parentCategory);
        SupplementCategory saveCategory = categoryRepository.save(category);

        // when
        List<SupplementCategory> categoryList = categoryRepository.findAll();

        // then
        assertThat(categoryList).size().isGreaterThan(0);
    }

    @Test
    @DisplayName("카테고리의 부모를 변경하면 하위 카테고리의 레벨이 변경된다.")
    void update_parent_category() {
        // given
        SupplementCategory category1 = SupplementCategory.builder().name("category1").depth(1).build();
        SupplementCategory category1_1 = SupplementCategory.builder().name("category1_1").depth(2).parentCategory(category1).build();
        SupplementCategory category1_2 = SupplementCategory.builder().name("category1_2").depth(2).parentCategory(category1).build();
        SupplementCategory category1_1_1 = SupplementCategory.builder().name("category1_1_1").depth(3).parentCategory(category1_1).build();
        SupplementCategory category1_1_2 = SupplementCategory.builder().name("category1_1_2").depth(3).parentCategory(category1_1).build();
        SupplementCategory category1_1_1_1 = SupplementCategory.builder().name("category1_1_1_1").depth(4).parentCategory(category1_1_1).build();
        SupplementCategory category1_1_1_2 = SupplementCategory.builder().name("category1_1_1_2").depth(4).parentCategory(category1_1_1).build();
        SupplementCategory category2 = SupplementCategory.builder().name("category2").depth(0).build();

        categoryRepository.save(category1);
        categoryRepository.save(category1_1);
        categoryRepository.save(category1_2);
        categoryRepository.save(category1_1_1);
        categoryRepository.save(category1_1_2);
        categoryRepository.save(category1_1_1_1);
        categoryRepository.save(category1_1_1_2);
        categoryRepository.save(category2);

        // when
        SupplementCategory findCategory = categoryRepository.findById(category1_1.getId()).get();
        findCategory.changeParent(category2);

        // then
        findCategory.getChildCategories().forEach(c -> assertEquals(c.getDepth(), c.getParentCategory().getDepth() -1));
    }

    @Test
    @DisplayName("카테고리를 삭제한다.")
    void delete_category() {
        // given
        SupplementCategory parentCategory = SupplementCategory.builder().name("오메가369/피쉬오일").depth(0).build();
        SupplementCategory category = SupplementCategory.builder().name("오메가3").depth(1).parentCategory(parentCategory).build();
        SupplementCategory saveCategory = categoryRepository.save(category);

        // when
        categoryRepository.deleteById(saveCategory.getId());

        // then
        assertEquals(Optional.empty(), categoryRepository.findById(saveCategory.getId()));
    }
}