package mandykr.nutrient.service;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.StarRateDto;
import mandykr.nutrient.entity.StarRate;
import mandykr.nutrient.repository.StarRateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StarRateService {

    private final StarRateRepository starRateRepository;
    //별점 번호로 조회
    public StarRateDto getStarRate(Long id){
        return StarRate.makeStarRateDto(starRateRepository.findById(id).get());
    }
    //전체리스트 조회
    public List<StarRateDto> getStarRateList(){
        List<StarRate> list = starRateRepository.findAll();
        List<StarRateDto> tempList = new ArrayList<>();
        for(int i=0; i<list.size(); i++){
            tempList.add(StarRate.makeStarRateDto(list.get(i)));
        }
        return tempList;
    }
    //별점 입력
    public StarRateDto createStarRate(StarRateDto starRatedto){
        StarRateDto starRateDto = StarRate.makeStarRateDto(starRateRepository.save(StarRateDto.makeStarRate(starRatedto)));
        return starRateDto;
    }
    //별점 삭제
    public void deleteStarRate(Long id){
        starRateRepository.deleteById(id);
    }
    //아이디로 별점 수정
    public StarRateDto updateStarRate(Long id,Long starNumber){
        StarRate starRate = starRateRepository.findById(id).get();
        starRate.setStarNumber(starNumber);
        return StarRate.makeStarRateDto(starRateRepository.save(starRate));
    }

}
