package mandykr.nutrient.controller.supplement;

import mandykr.nutrient.dto.supplement.starRate.SupplementStarRateDto;
import mandykr.nutrient.entity.Member;
import mandykr.nutrient.entity.supplement.Supplement;
import mandykr.nutrient.entity.supplement.SupplementStarRate;
import mandykr.nutrient.repository.MemberRepository;
import mandykr.nutrient.service.supplement.SupplementStarRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SupplementStarRateController.class)
@MockBean(JpaMetamodelMappingContext.class)
class SupplementStarRateControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    SupplementStarRateService starRateService;
    @MockBean
    MemberRepository memberRepository;

    Supplement supplement;
    Member member;
    SupplementStarRate supplementStarRate1;
    SupplementStarRate supplementStarRate2;
    SupplementStarRate supplementStarRate3;

    @BeforeEach
    void beforeEach(){
        supplement = Supplement.builder()
                .id(1L)
                .name("testSupplement1")
                .prdlstReportNo("111-123")
                .ranking(0.0)
                .deleteFlag(false).build();

        member = new Member();
        member.setId(1L);
        member.setName("testMemberId1");
        member.setMemberId("memberId1");

        supplementStarRate1 = new SupplementStarRate(1L,3,supplement,member);
        supplementStarRate2 = new SupplementStarRate(2L,4,supplement,member);
        supplementStarRate3 = new SupplementStarRate(3L,5,supplement,member);
    }

    @Test
    void 별점을_등록한다() throws Exception{
        //given

        //when
        when(memberRepository.findById("testMemberId1")).thenReturn(Optional.of(member));
        when(starRateService.createStarRate(supplement.getId(), 3, member))
                .thenReturn(new SupplementStarRateDto(supplementStarRate1));
        ResultActions perform = mockMvc.perform(post("/star-rate/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"starNumber\" : 3}"));

        //then
        perform.andExpect(status().isOk())
                .andExpect(handler().handlerType(SupplementStarRateController.class))
                .andExpect(handler().methodName("createStarRate"))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.response.id", is(supplementStarRate1.getId().intValue())))
                .andExpect(jsonPath("$.response.starNumber", is(supplementStarRate1.getStarNumber())));

    }

    @Test
    @DisplayName("별점을 등록할때 영양제가 존재하지 않으면 에러 발생")
    void 별점을_등록할때_영양제가_없을경우() throws Exception {
        //given


        //when
        when(memberRepository.findById("testMemberId1")).thenReturn(Optional.of(member));
        when(starRateService.createStarRate(supplement.getId(), 3, member))
                .thenThrow(new IllegalArgumentException("not found SupplementId"));
        ResultActions perform = mockMvc.perform(post("/star-rate/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"starNumber\" : 3}"));

        //then
        perform.andExpect(status().is4xxClientError())
                .andExpect(handler().handlerType(SupplementStarRateController.class))
                .andExpect(handler().methodName("createStarRate"))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.message", is("not found SupplementId")))
                .andExpect(jsonPath("$.error.status", is(400)));

    }

    @Test
    void 별점_수정() throws Exception{
        //given

        //when
        when(memberRepository.findById("testMemberId1")).thenReturn(Optional.of(member));
        when(starRateService.updateStarRate(supplement.getId(), supplementStarRate1.getId(), 3, member))
                .thenReturn(new SupplementStarRateDto(supplementStarRate1));
        ResultActions perform = mockMvc.perform(put("/star-rate/1/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"starNumber\" : 3}"));

        //then
        perform.andExpect(status().isOk())
                .andExpect(handler().handlerType(SupplementStarRateController.class))
                .andExpect(handler().methodName("updateStarRate"))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.response.id", is(supplementStarRate1.getId().intValue())))
                .andExpect(jsonPath("$.response.starNumber", is(supplementStarRate1.getStarNumber())));

    }

    @Test
    @DisplayName("별점을 수정할때 영양제가 존재하지 않으면 에러 발생")
    void 별점을_수정할때_영양제가_없을경우() throws Exception {
        //given


        //when
        when(memberRepository.findById("testMemberId1")).thenReturn(Optional.of(member));
        when(starRateService.updateStarRate(supplement.getId(), supplementStarRate1.getId(), 3, member))
                .thenThrow(new IllegalArgumentException("not found SupplementId"));
        ResultActions perform = mockMvc.perform(put("/star-rate/1/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"starNumber\" : 3}"));

        //then
        perform.andExpect(status().is4xxClientError())
                .andExpect(handler().handlerType(SupplementStarRateController.class))
                .andExpect(handler().methodName("updateStarRate"))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.message", is("not found SupplementId")))
                .andExpect(jsonPath("$.error.status", is(400)));

    }

    @Test
    @DisplayName("별점을 수정할때 별점이 존재하지 않으면 에러 발생")
    void 별점을_수정할때_별점이_없을경우() throws Exception {
        //given


        //when
        when(memberRepository.findById("testMemberId1")).thenReturn(Optional.of(member));
        when(starRateService.updateStarRate(supplement.getId(), supplementStarRate1.getId(), 3, member))
                .thenThrow(new IllegalArgumentException("not found SupplementStarRate"));
        ResultActions perform = mockMvc.perform(put("/star-rate/1/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"starNumber\" : 3}"));

        //then
        perform.andExpect(status().is4xxClientError())
                .andExpect(handler().handlerType(SupplementStarRateController.class))
                .andExpect(handler().methodName("updateStarRate"))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.message", is("not found SupplementStarRate")))
                .andExpect(jsonPath("$.error.status", is(400)));

    }


}