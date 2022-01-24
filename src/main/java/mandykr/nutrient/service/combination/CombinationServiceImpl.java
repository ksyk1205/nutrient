package mandykr.nutrient.service.combination;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.combination.CombinationCreateDto;
import mandykr.nutrient.dto.combination.CombinationDto;
import mandykr.nutrient.dto.combination.CombinationSearchCondition;
import mandykr.nutrient.entity.Member;
import mandykr.nutrient.entity.SupplementCombination;
import mandykr.nutrient.entity.combination.Combination;
import mandykr.nutrient.entity.supplement.Supplement;
import mandykr.nutrient.repository.SupplementCombinationRepository;
import mandykr.nutrient.repository.combination.CombinationRepository;
import mandykr.nutrient.repository.supplement.SupplementRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CombinationServiceImpl implements CombinationService{
    private final CombinationRepository combinationRepository;
    private final SupplementRepository supplementRepository;
    private final SupplementCombinationRepository supplementCombinationRepository;


    @Override
    public CombinationDto createCombination(CombinationCreateDto combinationCreateDto, Member member) {
        //영양제 조회
        List<Supplement> supplements = supplementRepository.findAllById(combinationCreateDto.getSupplementIds());
        Combination combination = Combination.builder()
                .caption(combinationCreateDto.getCaption())
                .rating(Combination.ZERO)
                .build();
        //영양제 조합 저장
        Combination saveCombination = combinationRepository.save(combination);
        List<SupplementCombination> supplementCombinations = supplements.stream().map(supplement -> new SupplementCombination(supplement, saveCombination)).collect(Collectors.toList());
        supplementCombinationRepository.saveAll(supplementCombinations);
        return CombinationDto.of(saveCombination);
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
