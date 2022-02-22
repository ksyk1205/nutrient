package mandykr.nutrient.service.combination;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.combination.starRate.CombinationStarRateDto;
import mandykr.nutrient.dto.combination.starRate.request.CombinationStarRateRequest;
import mandykr.nutrient.entity.combination.Combination;
import mandykr.nutrient.entity.combination.CombinationStarRate;
import mandykr.nutrient.entity.member.Member;
import mandykr.nutrient.repository.combination.starrate.CombinationStarRateRepository;
import mandykr.nutrient.repository.combination.CombinationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Service
@RequiredArgsConstructor
@Transactional
public class CombinationStarRateService {
    private final CombinationStarRateRepository combinationStarRateRepository;
    private final CombinationRepository combinationRepository;


    public CombinationStarRateDto createCombinationStarRate(Long combinationId, Member member, CombinationStarRateRequest request) {
        Combination combination = getCombination(combinationId);
        combinationStarRateRepository.findByCombinationIdAndMember(combinationId, member)
        .ifPresent(m ->{
            throw new IllegalArgumentException("등록한 영양제 조합 별점이 존재합니다");
        });
        //영양제 조합 평점 업데이트
        CombinationStarRate saveCombineStarRate = CombinationStarRate.builder()
                .starNumber(request.getStarNumber())
                .member(member)
                .build();
        saveCombineStarRate.addStarRate(combination);
        combination.updateRating();
        return Optional.of(combinationStarRateRepository.save(saveCombineStarRate))
                .map(CombinationStarRateDto::new)
                .get();
    }



    public CombinationStarRateDto updateCombinationStarRate(Long combinationId, Long combinationStarRateId, Member member, CombinationStarRateRequest request) {
        Combination combination = getCombination(combinationId);
        CombinationStarRate combinationStarRate = getCombinationStarRate(combinationId, combinationStarRateId, member);
        combinationStarRate.updateStarNumber(request.getStarNumber());
        combination.updateList(combinationStarRate);
        combination.updateRating();
        return Optional.of(combinationStarRate).map(CombinationStarRateDto::new)
                .get();
    }




    public CombinationStarRateDto getCombinationStarRateByCombination(Long combinationId, Member member) {
        CombinationStarRate combinationStarRate = combinationStarRateRepository.findByCombinationIdAndMember(combinationId, member)
                .orElseGet(() -> CombinationStarRate.builder().build());
        return new CombinationStarRateDto(combinationStarRate);
    }

    public boolean deleteCombinationStarRate(Long combinationStarRateId, Member member) {
        CombinationStarRate combinationStarRate = getCombinationStarRate(combinationStarRateId, member);
        combinationStarRateRepository.deleteById(combinationStarRateId);
        return true;
    }

    private CombinationStarRate getCombinationStarRate(Long combinationStarRateId, Member member) {
        return combinationStarRateRepository.findIdAndMember(combinationStarRateId, member)
                .orElseThrow(() -> new IllegalArgumentException(combinationStarRateId+"의 영양제 조합 별점이 존재하지 않습니다."));
    }


    private CombinationStarRate getCombinationStarRate(Long combinationId, Long combinationStarRateId, Member member) {
        return combinationStarRateRepository.findIdAndMemberAndComb(combinationStarRateId, member, combinationId)
                .orElseThrow(() -> new IllegalArgumentException(combinationStarRateId+"의 영양제 조합 별점이 존재하지 않습니다."));
    }

    private Combination getCombination(Long combinationId) {
        return combinationRepository.findByIdFetch(combinationId)
                .orElseThrow(() -> new IllegalArgumentException(combinationId + "의 영양제 조합이 존재하지 않습니다."));
    }
}
