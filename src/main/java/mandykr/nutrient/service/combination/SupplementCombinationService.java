package mandykr.nutrient.service.combination;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.entity.SupplementCombination;
import mandykr.nutrient.entity.combination.Combination;
import mandykr.nutrient.entity.supplement.Supplement;
import mandykr.nutrient.repository.combination.SupplementCombinationRepository;
import mandykr.nutrient.repository.supplement.SupplementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SupplementCombinationService {
    private final SupplementRepository supplementRepository;
    private final SupplementCombinationRepository supplementCombinationRepository;


    public void createSupplementCombinations(List<Long> supplementIds, Combination combination) {
        //영양제 조회
        List<Supplement> supplements = supplementRepository.findAllById(supplementIds);
        //영양제 조합 저장
        List<SupplementCombination> supplementCombinations =
                supplements.stream().map(supplement -> new SupplementCombination(supplement, combination)).collect(Collectors.toList());
        supplementCombinationRepository.saveAll(supplementCombinations);
    }


}
