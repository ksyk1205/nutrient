package mandykr.nutrient.service;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.SupplementCategoryDto;
import mandykr.nutrient.entity.SupplementCategory;
import mandykr.nutrient.repository.SupplementCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SupplementCategoryService {
    private final SupplementCategoryRepository categoryRepository;

//    public void createAndUpdateCategories(List<SupplementCategoryDto> categoryDtoList) {
//        createCategories(categoryDtoList.stream().filter(c -> c.getId() == null).collect(Collectors.toList()));
//        updateCategories(categoryDtoList.stream().filter(c -> c.getId() != null).collect(Collectors.toList()));
//    }

    public List<SupplementCategory> createCategories(List<SupplementCategoryDto> categoryDtoList) {
        return categoryDtoList.stream().map(c -> createCategory(c)).collect(Collectors.toList());
    }

    public SupplementCategory createCategory(SupplementCategoryDto categoryDto) {
        return categoryRepository.save(
                SupplementCategory.toEntity(categoryDto.getId(), categoryDto.getName(), categoryDto.getDepth()));
    }

    public void updateCategories(List<SupplementCategoryDto> categoryDtoList) {
        categoryDtoList.sort(Comparator.comparing(c -> c.getDepth()));
        categoryDtoList.forEach(c -> updateCategory(c));
    }

    public SupplementCategory updateCategory(SupplementCategoryDto categoryDto) {
        SupplementCategory findCategory = getCategory(categoryDto.getId());
        SupplementCategory findParentCategory = getCategory(categoryDto.getParentCategory().getId());

        findCategory.changeParent(
                SupplementCategory.toEntity(
                        findParentCategory.getId(),
                        findParentCategory.getName(),
                        findParentCategory.getDepth()));

        findCategory.rename(categoryDto.getName());
        return findCategory;
    }

    public void deleteCategories(List<SupplementCategoryDto> categoryDtoList) {
        categoryDtoList.forEach(c -> categoryRepository.deleteById(c.getId()));
    }

    @Transactional(readOnly = true)
    public SupplementCategory getCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("not found category: " + id));
    }

    @Transactional(readOnly = true)
    public List<SupplementCategory> getCategoryList() {
        return categoryRepository.findAll();
    }

}
