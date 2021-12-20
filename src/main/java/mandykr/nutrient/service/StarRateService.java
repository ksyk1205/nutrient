package mandykr.nutrient.service;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.StarRateDto;
import mandykr.nutrient.dto.SupplementDto;
import mandykr.nutrient.entity.StarRate;
import mandykr.nutrient.entity.Supplement;
import mandykr.nutrient.repository.StarRateRepository;
import mandykr.nutrient.repository.SupplementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StarRateService {

    private final StarRateRepository starRateRepository;
    private final SupplementRepository supplementRepository;

    /**
     * 별점 등록
     * @param supplementId //영양제 아이디
     * @param starNumber //별점 갯수
     * @return
     */
    @Transactional
    public StarRateDto createStarRate(Long supplementId,Long starNumber){
        //영양제 아이디가 없다면 error
        Supplement supplement = supplementRepository.findById(supplementId).get();
        if(supplement.getId() == null){
            throw new IllegalArgumentException("없는 영양제 입니다.");
        }

        //영양제 별점 등록
        StarRateDto starRateDto = new StarRateDto(starRateRepository.save(new StarRate(starNumber,supplement)));

        //영양제 테이블 평점 수정
        List<StarRate> starRateList = starRateRepository.findBySupplement(supplement);
        long sum=0;
        for(StarRate starRate : starRateList){
            sum += starRate.getStarNumber();
        }
        double ranking = (double) sum / starRateList.size();
        supplement.setRanking(ranking);
        supplementRepository.save(supplement);

        return starRateDto;
    }


    /**
     *
     * @param supplementId 영양제 아이디
     * @param starRateId  별점 아이디
     * @param starNumber 별점 갯수
     * @return
     */
    @Transactional
    public StarRateDto updateStarRate(Long supplementId,Long starRateId,Long starNumber){
        //영양제 아이디가 없다면 error
        Supplement supplement = supplementRepository.findById(supplementId).get();
        if(supplement.getId() == null){
            throw new IllegalArgumentException("없는 영양제 입니다.");
        }

        //영양제 별점 수정
        StarRateDto starRateDto = new StarRateDto(starRateRepository.save(new StarRate(starRateId,starNumber,supplement)));

        //영양제 테이블 평점 수정
        List<StarRate> starRateList = starRateRepository.findBySupplement(supplement);
        long sum=0;
        for(StarRate starRate : starRateList){
            sum += starRate.getStarNumber();
        }
        double ranking = (double) sum / starRateList.size();
        supplement.setRanking(ranking);
        supplementRepository.save(supplement);

        return starRateDto;
    }
    //별점 삭제
    public void deleteStarRate(Long id){
        starRateRepository.deleteById(id);
    }

}
