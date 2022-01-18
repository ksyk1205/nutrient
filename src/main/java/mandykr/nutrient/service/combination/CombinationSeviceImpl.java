package mandykr.nutrient.service.combination;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.combination.CombinationDto;
import mandykr.nutrient.dto.combination.CombinationSearchCondition;
import mandykr.nutrient.repository.combination.CombinationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CombinationSeviceImpl implements CombinationService{
    private final CombinationRepository combinationRepository;

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
