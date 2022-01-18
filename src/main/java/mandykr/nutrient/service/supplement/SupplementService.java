package mandykr.nutrient.service.supplement;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.supplement.SupplementDto;
import mandykr.nutrient.entity.SupplementCategory;
import mandykr.nutrient.entity.supplement.Supplement;
import mandykr.nutrient.repository.SupplementCategoryRepository;
import mandykr.nutrient.repository.supplement.SupplementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class SupplementService {
    private final SupplementRepository supplementRepository;
    private final SupplementCategoryRepository supplementCategoryRepository;

    @Transactional(readOnly = true)
    public SupplementDto getSupplement(long supplementId) {
        Supplement supplement = getSupplementById(supplementId);

        return new SupplementDto(supplement);
    }

    @Transactional(readOnly = true)
    public List<SupplementDto> getSupplementList() {
        return supplementRepository.findAll().stream().map(SupplementDto::new).collect(Collectors.toList());

    }

    public SupplementDto createSupplement(SupplementDto supplementDto, Long categoryId) {
        SupplementCategory supplementCategory = getCategory(categoryId);

        return new SupplementDto(
                supplementRepository.save(
                        Supplement.builder()
                                .name(supplementDto.getName())
                                .prdlstReportNo(supplementDto.getPrdlstReportNo())
                                .ranking(0.0)
                                .supplementCategory(supplementCategory)
                                .deleteFlag(false).build()));
    }



    public SupplementDto updateSupplement(SupplementDto supplementDto, Long categoryId) {
        SupplementCategory supplementCategory = getCategory(categoryId);

        Supplement supplement = getSupplementById(supplementDto.getId());
        supplement.updateNameAndPrdlstAndCategory(supplementDto.getName(), supplementDto.getPrdlstReportNo(), supplementCategory);

        return new SupplementDto(supplement);
    }


    public void deleteSupplement(Long supplementId) {
        Supplement supplement = getSupplementById(supplementId);

        supplement.updateDeleteFlag();
    }

    private Supplement getSupplementById(Long supplementId) {
        return supplementRepository.findById(supplementId).orElseThrow(() -> new IllegalArgumentException("not found Supplement"));
    }

    private SupplementCategory getCategory(Long categoryId) {
        return supplementCategoryRepository.findById(categoryId).orElseThrow(() -> new IllegalArgumentException("not found SupplementCategory"));
    }
}
