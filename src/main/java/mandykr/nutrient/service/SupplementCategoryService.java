package mandykr.nutrient.service;

import mandykr.nutrient.dto.SupplementCategoryDto;
import mandykr.nutrient.entity.SupplementCategory;
import mandykr.nutrient.repository.SupplementCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.List;

@Service
public class SupplementCategoryService {
    private final SupplementCategoryRepository categoryRepository;

    public SupplementCategoryService(SupplementCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public mandykr.nutrient.entity.SupplementCategory getCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("not found category: " + id));
    }

    @Transactional
    public SupplementCategory updateCategory(SupplementCategoryDto categoryDto) {
        SupplementCategory category = getCategory(categoryDto.getId());
        SupplementCategoryDto parentCategoryDto = categoryDto.getParentCategory();

        category.rename(categoryDto.getName());
        category.moveToAnotherParent(
                mandykr.nutrient.entity.SupplementCategory.changeEntity(
                        parentCategoryDto.getId(),
                        parentCategoryDto.getName(),
                        parentCategoryDto.getLevel()));

        return category;
    }

    @Transactional
    public void updateCategoryList(List<SupplementCategoryDto> categoryDtoList) {
        categoryDtoList.sort(Comparator.comparing(cd -> cd.getLevel()));
        categoryDtoList.forEach(cd -> updateCategory(cd));
    }

    @Transactional(readOnly = true)
    public List<SupplementCategory> getSupplementList() {
        return categoryRepository.findAll();
    }

}
