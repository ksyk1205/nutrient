package mandykr.nutrient.service.combination;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.combination.CombinationCreateRequest;
import mandykr.nutrient.dto.combination.CombinationDetailDto;
import mandykr.nutrient.dto.combination.CombinationDto;
import mandykr.nutrient.dto.combination.CombinationSearchCondition;
import mandykr.nutrient.entity.combination.Combination;
import mandykr.nutrient.repository.combination.CombinationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CombinationServiceImpl implements CombinationService{
    private final CombinationRepository combinationRepository;
    private final SupplementCombinationService supplementCombinationService;


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

}
