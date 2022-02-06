package mandykr.nutrient.service.combination;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.combination.*;
import mandykr.nutrient.entity.SupplementCombination;
import mandykr.nutrient.entity.combination.Combination;
import mandykr.nutrient.entity.supplement.Supplement;
import mandykr.nutrient.repository.combination.CombinationRepository;
import mandykr.nutrient.repository.combination.SupplementCombinationRepository;
import mandykr.nutrient.repository.supplement.SupplementRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CombinationServiceImpl implements CombinationService{
    private final CombinationRepository combinationRepository;
    private final SupplementCombinationService supplementCombinationService;
    private final SupplementRepository supplementRepository;

    @Override
    public CombinationDetailDto createCombination(CombinationCreateRequest combinationCreateRequest) {
        Combination combination = Combination.builder()
                .caption(combinationCreateRequest.getCaption())
                .build();
        Combination saveCombination = combinationRepository.save(combination);
        //메핑테이블 저장 책임을 영양제 메핑테이블에 전가
        supplementCombinationService.createSupplementCombinations(combinationCreateRequest.getSupplementIds(), saveCombination);

        return combinationRepository.searchByCombination(saveCombination);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CombinationDto> getCombinations(CombinationSearchCondition condition, Pageable pageable) {
        Page<CombinationDto> resultPage = null;

        if (condition.isEmpty()) {
            resultPage = combinationRepository.findWithSupplement(pageable);
        } else {
            resultPage = getCombinationsBySearchCondition(condition, pageable);
        }
        return resultPage;
    }

    private Page<CombinationDto> getCombinationsBySearchCondition(
            CombinationSearchCondition condition, Pageable pageable) {
        Page<CombinationDto> resultPage = null;

        if (condition.getCategoryList().isEmpty()) {
            resultPage = combinationRepository.searchBySupplementList(condition.getSupplementList(), pageable);
        } else {
            resultPage = combinationRepository.searchByCategoryList(condition.getCategoryList(), pageable);
        }
        return resultPage;
    }

    @Override
    public CombinationDetailDto updateCombination(Long id, CombinationUpdateRequest updateRequest) {
        Combination combination = getById(id);
        combination.editCaption(updateRequest.getCaption());

        combination.removeSupplementCombinations(updateRequest.getSupplementIds());
        List<Supplement> supplements = supplementRepository.findAllById(updateRequest.getSupplementIds());
        combination.addSupplementCombinations(supplements);

        return combinationRepository.searchByCombination(combination);
    }

    @Transactional(readOnly = true)
    private Combination getById(Long id) {
        return combinationRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

}
