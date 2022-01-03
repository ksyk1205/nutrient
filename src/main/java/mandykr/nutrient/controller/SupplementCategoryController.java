package mandykr.nutrient.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.SupplementCategoryDto;
import mandykr.nutrient.service.SupplementCategoryService;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

import static mandykr.nutrient.util.ApiUtils.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SupplementCategoryController {
    private final SupplementCategoryService categoryService;

    @PostMapping("/categories")
    public ApiResult<List<SupplementCategoryDto>> createAndUpdateCategories(@RequestBody List<SupplementCategoryRequest> categoryRequestList) {
        categoryService.createAndUpdateCategories(toCategoryDtoList(categoryRequestList));

        return getCategoryDtoList();
    }

    @GetMapping("/category/{id}")
    public ApiResult<SupplementCategoryDto> getCategory(@PathVariable Long id) {
        return success(
                SupplementCategoryDto.toCategoryDto(categoryService.getCategory(id))
        );
    }

    @GetMapping("/categories")
    public ApiResult<List<SupplementCategoryDto>> getCategories() {
        return getCategoryDtoList();
    }

    @PutMapping("/categories")
    public ApiResult<List<SupplementCategoryDto>> updateCategories(@RequestBody List<SupplementCategoryRequest> categoryRequestList) {
        categoryService.updateCategories(toCategoryDtoList(categoryRequestList));
        return getCategoryDtoList();
    }

    private List<SupplementCategoryDto> toCategoryDtoList(List<SupplementCategoryRequest> categoryRequestList) {
        return categoryRequestList.stream().map(r -> toCategoryDto(r)).collect(Collectors.toList());
    }

    private SupplementCategoryDto toCategoryDto(SupplementCategoryRequest categoryRequest) {
        return SupplementCategoryDto.toCategoryDto(
                categoryRequest.getId(),
                categoryRequest.getName(),
                categoryRequest.getLevel(),
                categoryRequest.getParentId());
    }

    private ApiResult<List<SupplementCategoryDto>> getCategoryDtoList() {
        return success(
                categoryService
                        .getCategoryList()
                        .stream()
                        .map(SupplementCategoryDto::toCategoryDto)
                        .collect(Collectors.toList())
        );
    }

    @Data
    private static class SupplementCategoryRequest {
        private Long id;
        @NotEmpty
        private String name;
        private int level;
        private Long parentId;
    }
}
