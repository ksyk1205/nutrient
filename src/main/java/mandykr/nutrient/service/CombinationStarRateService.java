package mandykr.nutrient.service;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.request.CombineStarRateRequest;
import mandykr.nutrient.entity.Combination;
import mandykr.nutrient.entity.CombinationStarRate;
import mandykr.nutrient.entity.Member;
import mandykr.nutrient.repository.CombinationRepository;
import mandykr.nutrient.repository.CombinationStarRateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Service
@RequiredArgsConstructor
@Transactional
public class CombinationStarRateService {
    private final CombinationStarRateRepository combinationStarRateRepository;
    private final CombinationRepository combinationRepository;


    public Optional<CombinationStarRate> saveCombinationStarRate(Long combinationId, Member member, CombineStarRateRequest request) {
        Combination combination = combinationRepository.findByIdFetch(combinationId)
                .orElseThrow(()->new IllegalArgumentException(combinationId+"의 영양제 조합이 존재하지 않습니다."));
        combinationStarRateRepository.findByCombinationIdAndMember(combinationId, member)
        .ifPresent(m ->{
            throw new IllegalArgumentException(m.getId() + "영양제 별점 조합 존재합니다");
        });
        //영양제 조합 평점 업데이트
        CombinationStarRate saveCombineStarRate = CombinationStarRate.builder()
                .starNumber(request.getStarNumber())
                .member(member)
                .build();
        saveCombineStarRate.addStarRate(combination);
        combination.updateRating();
        return Optional.ofNullable(combinationStarRateRepository.save(saveCombineStarRate));
    }

    public Optional<CombinationStarRate> updateCombinationStarRate(Long combinationId, Long combinationStarRateId, Member member, CombineStarRateRequest request) {
        Combination combination = combinationRepository.findByIdFetch(combinationId)
                .orElseThrow(() -> new IllegalArgumentException(combinationId+"의 영양제 조합이 존재하지 않습니다."));
        CombinationStarRate combinationStarRate = combinationStarRateRepository.findIdAndMemberAndComb(combinationStarRateId, member, combinationId)
                .orElseThrow(() -> new IllegalArgumentException(combinationStarRateId+"의 영양제 조합 별점이 존재하지 않습니다."));
        combinationStarRate.updateStarNumber(request.getStarNumber());
        combination.updateList(combinationStarRate);
        combination.updateRating();
        return Optional.ofNullable(combinationStarRate);
    }


    public Optional<CombinationStarRate> getCombineStarRateByCombine(Long combinationId, Member member) {
        return combinationStarRateRepository.findByCombinationIdAndMember(combinationId, member);
    }

    public void deleteCombinationStarRate(Long combinationStarRateId, Member member) {
        CombinationStarRate combinationStarRate = combinationStarRateRepository.findById(combinationStarRateId)
                .orElseThrow(() -> new IllegalArgumentException(combinationStarRateId + "의 영양제 조합 별점이 존재하지 않습니다."));
        if(combinationStarRate.getMember().getId() == member.getId()){
            combinationStarRateRepository.deleteById(combinationStarRateId);
        }
    }
}
