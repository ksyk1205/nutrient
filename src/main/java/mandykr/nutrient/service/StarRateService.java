package mandykr.nutrient.service;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.StarRateDto;
import mandykr.nutrient.dto.SupplementDto;
import mandykr.nutrient.entity.Member;
import mandykr.nutrient.entity.StarRate;
import mandykr.nutrient.entity.Supplement;
import mandykr.nutrient.repository.StarRateRepository;
import mandykr.nutrient.repository.SupplementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StarRateService {

    private final StarRateRepository starRateRepository;
    private final SupplementRepository supplementRepository;


    /**
     * 별점 등록
     * @param supplementId 영양제 아이디
     * @param starNumber 별점 갯수
     * @param member 회원
     * @return
     */
    @Transactional
    public StarRateDto createStarRate(Long supplementId,int starNumber, Member member){
        //영양제 조회
        Supplement supplement = getSupplement(supplementId);

        //영양제 별점 등록
        StarRateDto starRateDto = new StarRateDto(starRateRepository.save(new StarRate(starNumber,supplement,member)));

        //Supplement 테이블 평점 수정
        updateSupplementRanking(supplement);

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
    public StarRateDto updateStarRate(Long supplementId,Long starRateId,int starNumber, Member member){
        //영양제 조회
        Supplement supplement = getSupplement(supplementId);

        //영양제 별점 수정
        StarRateDto starRateDto = new StarRateDto(starRateRepository.save(new StarRate(starRateId,starNumber,supplement,member)));

        //Supplement 테이블 평점 수정
        updateSupplementRanking(supplement);

        return starRateDto;
    }

    /**
     * 별점 조회(회원 정보와, 영양제번호로)
     * @param supplementId
     * @param member
     * @return
     */
    @Transactional(readOnly = true)
    public StarRateDto getMemberStarRate(Long supplementId, Member member){
        //영양제 조회
        Supplement supplement = getSupplement(supplementId);
        //별점 조회 후 별점이 등록되어있지 않을 경우 후 처리 고민..
        return new StarRateDto(starRateRepository.findBySupplementAndMember(supplement, member).get());
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
    /**
     * 영양제 테이블 평점 수정
     * @param supplement 영양제
     */
    private void updateSupplementRanking(Supplement supplement) {
        List<StarRate> starRateList = starRateRepository.findBySupplement(supplement);
        long sum=0;
        for(StarRate starRate : starRateList){
            sum += starRate.getStarNumber();
        }
        double ranking = (double) sum / starRateList.size();
        supplement.setRanking(ranking);
        supplementRepository.save(supplement);
    }
    //별점 삭제
    public void deleteStarRate(Long id){
        starRateRepository.deleteById(id);
    }

}
