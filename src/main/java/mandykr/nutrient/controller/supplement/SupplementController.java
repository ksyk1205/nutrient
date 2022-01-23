package mandykr.nutrient.controller.supplement;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.supplement.*;
import mandykr.nutrient.service.supplement.SupplementService;
import mandykr.nutrient.util.ApiUtils.ApiResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static mandykr.nutrient.util.ApiUtils.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SupplementController {
    private final SupplementService supplementService;

    /**
     * 영양제 조회
     * @return
     */
    @GetMapping("/supplement")
    public ApiResult<List<SupplementSearchResponse>> getSupplementList(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String supplementName){
        SupplementSearch supplementSearch = new SupplementSearch(categoryId, supplementName);
        return success(supplementService.getSupplementList(supplementSearch));
    }

    /**
     * 영양제 단건 조회
     * @param supplementId
     * @return
     */
    @GetMapping("/{supplementId}")
    public ApiResult<SupplementResponseDto> getSupplement(
            @PathVariable Long supplementId){
        return success(supplementService.getSupplement(supplementId));
    }

    /**
     * 영양제 등록
     * @param categoryId
     * @param supplementRequest
     * @return
     */
    @PostMapping("/{categoryId}")
    public ApiResult<SupplementResponseDto> createSupplement(
            @PathVariable Long categoryId,
            @RequestBody @Valid SupplementRequest supplementRequest){
        SupplementRequestDto supplementRequestDto = new SupplementRequestDto(supplementRequest);
        return success(supplementService.createSupplement(supplementRequestDto,categoryId));
    }

    /**
     * 영양제 수정
     * @param categoryId
     * @param supplementId
     * @param supplementRequest
     * @return
     */
    @PutMapping("/{categoryId}/{supplementId}")
    public ApiResult<SupplementResponseDto> updateSupplement(
            @PathVariable Long categoryId,
            @PathVariable Long supplementId,
            @RequestBody @Valid SupplementRequest supplementRequest){
        return success(supplementService.updateSupplement(categoryId,supplementId,new SupplementRequestDto(supplementRequest)));
    }

    /**
     * 영양제 삭제
     * @param supplementId
     */
    @DeleteMapping("/{supplementId}")
    public void deleteSupplement(
            @PathVariable Long supplementId){
        supplementService.deleteSupplement(supplementId);
    }
}
