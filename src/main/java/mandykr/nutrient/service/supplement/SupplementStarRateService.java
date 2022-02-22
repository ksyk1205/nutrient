package mandykr.nutrient.service.supplement;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.supplement.starRate.SupplementStarRateDto;
import mandykr.nutrient.entity.member.Member;
import mandykr.nutrient.entity.supplement.SupplementStarRate;
import mandykr.nutrient.entity.supplement.Supplement;
import mandykr.nutrient.repository.supplement.SupplementStarRateRepository;
import mandykr.nutrient.repository.supplement.SupplementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupplementStarRateService {

    private final SupplementStarRateRepository starRateRepository;
    private final SupplementRepository supplementRepository;

    @Transactional
    public SupplementStarRateDto createStarRate(Long supplementId, int starNumber, Member member){

        Supplement supplement = getSupplement(supplementId);

        SupplementStarRateDto starRateDto = new SupplementStarRateDto(starRateRepository.save(new SupplementStarRate(starNumber, supplement, member)));

        supplement.insertList(starRateDto.getId(), starNumber);
        //Supplement 테이블 평점 수정
        supplement.updateRanking();

        return starRateDto;
    }

    @Transactional
    public SupplementStarRateDto updateStarRate(Long supplementId, Long starRateId, int starNumber, Member member){

        Supplement supplement = getSupplement(supplementId);

        SupplementStarRate starRate = getSupplementAndMember(member, supplement).orElseThrow(()-> new IllegalArgumentException("not found SupplementStarRate"));
        starRate.updateStarRate(starRateId,starNumber);

        supplement.updateList(starRate.getId(),starNumber);
        //Supplement 테이블 평점 수정
        supplement.updateRanking();

        return new SupplementStarRateDto(starRate);
    }

    @Transactional(readOnly = true)
    public SupplementStarRateDto getStarRateWithMember(Long supplementId, Member member){

        Supplement supplement = getSupplement(supplementId);

        Optional<SupplementStarRate> star = getSupplementAndMember(member, supplement);
        //별점 정보가 없다면 빈객체를
        if(!star.isPresent()){
            return new SupplementStarRateDto();
        }else{//별점 정보가 있다면 조회한 별점 정보를
            return new SupplementStarRateDto(star.get());

        }
    }

    public void  deleteStarRate(Long starRateId, Member member){
        SupplementStarRate memberAndSupplementStarRate = starRateRepository.findByMemberAndId(member, starRateId).orElseThrow(()-> new IllegalArgumentException("not found SupplementStarRate"));

        starRateRepository.deleteById(starRateId);
    }

    private Optional<SupplementStarRate> getSupplementAndMember(Member member, Supplement supplement) {
        return starRateRepository.findBySupplementAndMember(supplement, member);
    }

    private Supplement getSupplement(Long supplementId) {
        //영양제 아이디가 없다면 error
        return supplementRepository.findById(supplementId).orElseThrow(()-> new IllegalArgumentException("not found SupplementId"));
    }
}
