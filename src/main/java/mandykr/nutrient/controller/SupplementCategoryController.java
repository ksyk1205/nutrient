package mandykr.nutrient.controller;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.SupplementCategoryDto;
import mandykr.nutrient.service.SupplementCategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static mandykr.nutrient.util.ApiUtils.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SupplementCategoryController {
    private final SupplementCategoryService categoryService;

    @GetMapping("/gategory/{id}")
    public ApiResult<SupplementCategoryDto> getCategory(@PathVariable Long id) {
        return success(
                new SupplementCategoryDto(categoryService.getCategory(id))
        );
    }

    @PutMapping("/categories")
    public ApiResult<List<SupplementCategoryDto>> updateCategorys(@RequestBody List<SupplementCategoryDto> categoryDtoList) {
        categoryService.updateCategoryList(categoryDtoList);

        return success(
                categoryService
                        .getSupplementList()
                        .stream()
                        .map(SupplementCategoryDto::new)
                        .collect(Collectors.toList())
        );
    }

}
