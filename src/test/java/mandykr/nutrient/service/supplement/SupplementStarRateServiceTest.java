package mandykr.nutrient.service.supplement;

import mandykr.nutrient.dto.supplement.SupplementResponse;
import mandykr.nutrient.dto.supplement.starRate.SupplementStarRateDto;
import mandykr.nutrient.entity.member.Member;
import mandykr.nutrient.entity.supplement.SupplementStarRate;
import mandykr.nutrient.entity.supplement.Supplement;
import mandykr.nutrient.repository.supplement.SupplementStarRateRepository;
import mandykr.nutrient.repository.supplement.SupplementRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;



import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;


class SupplementStarRateServiceTest {

    private SupplementStarRateRepository starRateRepository = mock(SupplementStarRateRepository.class);
    private SupplementRepository supplementRepository = mock(SupplementRepository.class);

    private SupplementStarRateService starRateService = new SupplementStarRateService(starRateRepository, supplementRepository);

    Supplement supplement;
    SupplementStarRate supplementStarRate;
    Member member;


    @BeforeEach
    void before(){
        supplement = Supplement.builder().id(1L).name("testSupplement").ranking(0.0).build();


        member = Member.builder()
                .name("name1")
                .memberId("test").build();


        supplementStarRate = new SupplementStarRate(3,supplement,member);

        supplement.insertList(supplementStarRate.getId(), supplementStarRate.getStarNumber());
    }

    @Test
    void 별점_등록_성공(){
        //given

        //when
        when(supplementRepository.findById(supplement.getId())).thenReturn(Optional.of(supplement));
        when(starRateRepository.save(isA(SupplementStarRate.class))).thenReturn(supplementStarRate);
        SupplementStarRateDto starRateDto = starRateService.createStarRate(supplement.getId(), supplementStarRate.getStarNumber(), member);

        //then
        assertThat(supplementStarRate.getId()).isEqualTo(starRateDto.getId());
        assertThat(supplement.getRanking()).isEqualTo(3.0);
    }

    @Test
    void 별점_조회(){
        //given

        //when
        when(supplementRepository.findById(supplement.getId())).thenReturn(Optional.of(supplement));
        when(starRateRepository.findBySupplementAndMember(supplement,member)).thenReturn(Optional.of(supplementStarRate));
        SupplementStarRateDto starRateWithMemberDto = starRateService.getStarRateWithMember(supplement.getId(), member);

        //then
        assertThat(supplementStarRate.getId()).isEqualTo(starRateWithMemberDto.getId());
    }
    @Test
    @DisplayName("별점 수정을 위하여 별점 조회 했을때 빈 객체가 반환되는지 확인한다.")
    void 별점_빈객체_조회(){
        //given
        SupplementResponse supplementDto = new SupplementResponse();
        //when
        when(supplementRepository.findById(supplement.getId())).thenReturn(Optional.of(supplement));
        when(starRateRepository.findBySupplementAndMember(supplement,member)).thenReturn(Optional.empty());
        SupplementStarRateDto starRateWithMemberDto = starRateService.getStarRateWithMember(supplement.getId(), member);

        //then
        assertThat(starRateWithMemberDto.getId()).isNull();
    }


    @Test
    void 별점_수정(){
        //given

        //when
        when(supplementRepository.findById(supplement.getId())).thenReturn(Optional.of(supplement));
        when(starRateRepository.findBySupplementAndMember(supplement,member)).thenReturn(Optional.of(supplementStarRate));
        SupplementStarRateDto supplementStarRateDto = starRateService.updateStarRate(supplement.getId(), supplementStarRate.getId(), 2, member);

        //then
        assertThat(supplementStarRate.getId()).isEqualTo(supplementStarRateDto.getId());
        assertThat(supplementStarRate.getStarNumber()).isEqualTo(supplementStarRateDto.getStarNumber());
        assertThat(supplement.getRanking()).isEqualTo(2.0);

    }



    @Test
    void 별점_삭제(){
        //given

        //when
        when(starRateRepository.findByMemberAndId(member,supplementStarRate.getId())).thenReturn(Optional.of(supplementStarRate));
        starRateRepository.deleteById(supplementStarRate.getId());
        //then
        then(starRateRepository).should(times(1)).deleteById(supplementStarRate.getId());
    }
}