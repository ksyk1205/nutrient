package mandykr.nutrient.service.supplement;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.supplement.*;
import mandykr.nutrient.entity.SupplementCategory;
import mandykr.nutrient.entity.supplement.Supplement;
import mandykr.nutrient.repository.SupplementCategoryRepository;
import mandykr.nutrient.repository.supplement.SupplementRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<SupplementSearchResponse> getSupplementList(SupplementSearchRequest supplementSearch, Pageable pageable) {
        return supplementRepository.searchSupplementList(supplementSearch, pageable);

    }

    public SupplementResponseDto createSupplement(SupplementRequest supplementRequest, Long categoryId) {
        SupplementCategory supplementCategory = getCategory(categoryId);

        return new SupplementResponseDto(
                supplementRepository.save(
                        Supplement.builder()
                                .name(supplementRequest.getName())
                                .prdlstReportNo(supplementRequest.getPrdlstReportNo())
                                .supplementCategory(supplementCategory)
                                .deleteFlag(false).build()));
    }



    public SupplementResponseDto updateSupplement(Long categoryId, Long supplementId, SupplementRequest supplementRequest) {
        SupplementCategory supplementCategory = getCategory(categoryId);

        Supplement supplement = getSupplementById(supplementId);
        supplement.updateNameAndPrdlstAndCategory(supplementRequest.getName(), supplementRequest.getPrdlstReportNo(), supplementCategory);

        return new SupplementResponseDto(supplement);
    }


    public void deleteSupplement(Long supplementId) {
        Supplement supplement = getSupplementById(supplementId);

        supplement.updateDeleteFlag();
    }

    public List<SupplementSearchComboResponse> getSupplementSearchCombo(SupplementSearchComboRequest supplementSearchCombo){
        return supplementRepository.searchSupplementCombo(supplementSearchCombo);
    }

    private Supplement getSupplementById(Long supplementId) {
        return supplementRepository.findById(supplementId).orElseThrow(() -> new IllegalArgumentException("not found Supplement"));
    }

    private SupplementCategory getCategory(Long categoryId) {
        return supplementCategoryRepository.findById(categoryId).orElseThrow(() -> new IllegalArgumentException("not found SupplementCategory"));
    }
}
