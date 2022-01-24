package mandykr.nutrient.service.combination;

import mandykr.nutrient.dto.combination.CombinationCreateDto;
import mandykr.nutrient.dto.combination.CombinationDto;
import mandykr.nutrient.dto.combination.CombinationSearchCondition;
import mandykr.nutrient.entity.Member;
import mandykr.nutrient.entity.combination.Combination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CombinationService {
    Page<CombinationDto> getCombinations(CombinationSearchCondition condition, Pageable pageable);

    CombinationDto createCombination(CombinationCreateDto combinationCreateDto, Member member);
}
