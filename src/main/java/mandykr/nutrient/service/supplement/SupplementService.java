package mandykr.nutrient.service.supplement;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.supplement.*;
import mandykr.nutrient.entity.SupplementCategory;
import mandykr.nutrient.entity.supplement.Supplement;
import mandykr.nutrient.repository.SupplementCategoryRepository;
import mandykr.nutrient.repository.supplement.SupplementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class SupplementService {
    private final SupplementRepository supplementRepository;
    private final SupplementCategoryRepository supplementCategoryRepository;

    private final static Double ZERO = 0.0;

    @Transactional(readOnly = true)
    public SupplementResponseDto getSupplement(long supplementId) {
        Supplement supplement = getSupplementById(supplementId);

        return new SupplementResponseDto(supplement);
    }

    @Transactional(readOnly = true)
    public List<SupplementSearchResponse> getSupplementList(SupplementSearch supplementSearch) {
        return supplementRepository.searchSupplementList(supplementSearch);

    }

    public SupplementResponseDto createSupplement(SupplementRequestDto supplementRequestDto, Long categoryId) {
        SupplementCategory supplementCategory = getCategory(categoryId);

        return new SupplementResponseDto(
                supplementRepository.save(
                        Supplement.builder()
                                .name(supplementRequestDto.getName())
                                .prdlstReportNo(supplementRequestDto.getPrdlstReportNo())
                                .ranking(ZERO)
                                .supplementCategory(supplementCategory)
                                .deleteFlag(false).build()));
    }



    public SupplementResponseDto updateSupplement(Long categoryId, Long supplementId, SupplementRequestDto supplementRequestDto) {
        SupplementCategory supplementCategory = getCategory(categoryId);

        Supplement supplement = getSupplementById(supplementId);
        supplement.updateNameAndPrdlstAndCategory(supplementRequestDto, supplementCategory);

        return new SupplementResponseDto(supplement);
    }


    public void deleteSupplement(Long supplementId) {
        Supplement supplement = getSupplementById(supplementId);

        supplement.updateDeleteFlag();
    }

    public List<SupplementSearchComboResponse> getSearchCombo(SupplementSearchCombo supplementSearchCombo){
        return supplementRepository.searchCombo(supplementSearchCombo);
    }

    private Supplement getSupplementById(Long supplementId) {
        return supplementRepository.findById(supplementId).orElseThrow(() -> new IllegalArgumentException("not found Supplement"));
    }

    private SupplementCategory getCategory(Long categoryId) {
        return supplementCategoryRepository.findById(categoryId).orElseThrow(() -> new IllegalArgumentException("not found SupplementCategory"));
    }
}
