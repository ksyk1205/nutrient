package mandykr.nutrient.service.supplement;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.supplement.SupplementStarRateDto;
import mandykr.nutrient.entity.Member;
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


    /**
     * 별점 등록
     * @param supplementId 영양제 아이디
     * @param starNumber 별점 갯수
     * @param member 회원
     * @return
     */
    @Transactional
    public SupplementStarRateDto createStarRate(Long supplementId, int starNumber, Member member){
        //영양제 조회
        Supplement supplement = getSupplement(supplementId);

        //영양제 별점 등록
        SupplementStarRateDto starRateDto = new SupplementStarRateDto(starRateRepository.save(new SupplementStarRate(starNumber,supplement,member)));

        supplement.insertList(starRateDto.getId(),starNumber);
        //Supplement 테이블 평점 수정
        supplement.updateRanking();

        return starRateDto;
    }

    /**
     * 별점 수정
     * @param supplementId 영양제 아이디
     * @param starRateId  별점 아이디
     * @param starNumber 별점 갯수
     * @param member 회원
     * @return
     */
    @Transactional
    public SupplementStarRateDto updateStarRate(Long supplementId, Long starRateId, int starNumber, Member member){
        //영양제 조회
        Supplement supplement = getSupplement(supplementId);

        //영양제 별점 수정
        SupplementStarRateDto starRateDto = new SupplementStarRateDto(starRateRepository.save(new SupplementStarRate(starRateId,starNumber,supplement,member)));

        supplement.updateList(starRateDto.getId(),starNumber);
        //Supplement 테이블 평점 수정
        supplement.updateRanking();

        return starRateDto;
    }

    /**
     * 별점 조회(회원 정보와, 영양제번호로)
     * @param supplementId
     * @param member
     * @return
     */
    @Transactional(readOnly = true)
    public SupplementStarRateDto getStarRateWithMember(Long supplementId, Member member){
        //영양제 조회
        Supplement supplement = getSupplement(supplementId);

        Optional<SupplementStarRate> star = starRateRepository.findBySupplementAndMember(supplement,member);
        //별점 정보가 없다면 빈객체를
        if(!star.isPresent()){
            return new SupplementStarRateDto();
        }else{//별점 정보가 있다면 조회한 별점 정보를
            return new SupplementStarRateDto(star.get());
        }
    }

    /**
     * 별점 삭제
     * @param starRateId 별점 아이디
     */
    public void  deleteStarRate(Long starRateId){
        //별점 삭제
        starRateRepository.deleteById(starRateId);
    }

    /**
     * 영양제 조회
     * @param supplementId 영양제 아이디
     * @return
     */
    private Supplement getSupplement(Long supplementId) {
        //영양제 아이디가 없다면 error
        return supplementRepository.findById(supplementId).orElseThrow(()-> new IllegalArgumentException("not found SupplementId"));
    }
}
