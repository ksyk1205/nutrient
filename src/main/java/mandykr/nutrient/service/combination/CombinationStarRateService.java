package mandykr.nutrient.service.combination;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.combination.starRate.CombinationStarRateRequestDto;
import mandykr.nutrient.dto.combination.starRate.CombinationStarRateResponseDto;
import mandykr.nutrient.entity.combination.Combination;
import mandykr.nutrient.entity.combination.CombinationStarRate;
import mandykr.nutrient.entity.Member;
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


    public CombinationStarRateResponseDto createCombinationStarRate(Long combinationId, Member member, CombinationStarRateRequestDto request) {
        Combination combination = combinationRepository.findByIdFetch(combinationId)
                .orElseThrow(()->new IllegalArgumentException(combinationId+"의 영양제 조합이 존재하지 않습니다."));
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
                .map(CombinationStarRateResponseDto::new)
                .get();
    }

    public CombinationStarRateResponseDto updateCombinationStarRate(Long combinationId, Long combinationStarRateId, Member member, CombinationStarRateRequestDto request) {
        Combination combination = combinationRepository.findByIdFetch(combinationId)
                .orElseThrow(() -> new IllegalArgumentException(combinationId+"의 영양제 조합이 존재하지 않습니다."));
        CombinationStarRate combinationStarRate = combinationStarRateRepository.findIdAndMemberAndComb(combinationStarRateId, member, combinationId)
                .orElseThrow(() -> new IllegalArgumentException(combinationStarRateId+"의 영양제 조합 별점이 존재하지 않습니다."));
        combinationStarRate.updateStarNumber(request.getStarNumber());
        combination.updateList(combinationStarRate);
        combination.updateRating();
        return Optional.of(combinationStarRate).map(CombinationStarRateResponseDto::new)
                .get();
    }


    public CombinationStarRateResponseDto getCombinationStarRateByCombination(Long combinationId, Member member) {
        CombinationStarRate combinationStarRate = combinationStarRateRepository.findByCombinationIdAndMember(combinationId, member)
                .orElseGet(() -> CombinationStarRate.builder().build()); //NULL OBJECT 패턴
        return new CombinationStarRateResponseDto(combinationStarRate);
    }

    public boolean deleteCombinationStarRate(Long combinationStarRateId, Member member) {
        CombinationStarRate combinationStarRate = combinationStarRateRepository.findById(combinationStarRateId)
                .orElseThrow(() -> new IllegalArgumentException(combinationStarRateId + "의 영양제 조합 별점이 존재하지 않습니다."));
        if(combinationStarRate.getMember().getId().equals(member.getId())){
            combinationStarRateRepository.deleteById(combinationStarRateId);
            return true;
        }
        throw new IllegalArgumentException(member.getMemberId() + "의 영양제 조합 별점이 존재하지 않습니다.");
    }
}
