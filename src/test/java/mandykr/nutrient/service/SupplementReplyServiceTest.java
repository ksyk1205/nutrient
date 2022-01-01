package mandykr.nutrient.service;

import mandykr.nutrient.dto.SupplementReplyDto;
import mandykr.nutrient.dto.request.SupplementReplyRequest;
import mandykr.nutrient.entity.Supplement;
import mandykr.nutrient.entity.SupplementReply;
import mandykr.nutrient.repository.SupplementReplyRepository;
import mandykr.nutrient.repository.SupplementRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@DisplayName("SupplementReplyServiceTest")
class SupplementReplyServiceTest {

    SupplementReplyRepository supplementReplyRepository = mock(SupplementReplyRepository.class);

    SupplementRepository supplementRepository = mock(SupplementRepository.class);;

    SupplementReplyService supplementReplyService = new SupplementReplyService(supplementReplyRepository,supplementRepository);

    Supplement saveSupplement;

    @BeforeEach
    public void setup(){
        saveSupplement = new Supplement();
        saveSupplement.setId(1L);
        saveSupplement.setName("test1");
        saveSupplement.setRanking(4.2);
    }

    @Test
    public void 영양제_등록_테스트() throws Exception{
        /**
         *  영양제1
         *          댓글1
         *              댓글2
         *              댓글3
         */
        //given
        SupplementReplyRequest supplementReplyRequest1 = new SupplementReplyRequest();
        supplementReplyRequest1.setContent("testReply1");
        SupplementReplyRequest supplementReplyRequest2 = new SupplementReplyRequest();
        supplementReplyRequest2.setContent("testReply2");
        SupplementReplyRequest supplementReplyRequest3 = new SupplementReplyRequest();
        supplementReplyRequest3.setContent("testReply3");

        //when
        SupplementReply saveSupplementReply1 = supplementReplyService.createSupplementReply(saveSupplement.getId(),supplementReplyRequest1).get();
        supplementReplyService.createSupplementReply(saveSupplement.getId(),saveSupplementReply1.getId(),supplementReplyRequest2);
        supplementReplyService.createSupplementReply(saveSupplement.getId(),saveSupplementReply1.getId(),supplementReplyRequest3);

        //then
        Assertions.assertEquals(3,supplementReplyService.getSupplementReplyBySupplement(saveSupplement.getId()).size());
    }


    @Test
    public void 영양제_전체_조회() throws Exception{
        Supplement supplement = new Supplement();
        supplement.setName("test");

        SupplementReplyRequest supplementReplyRequest1 = new SupplementReplyRequest();
        supplementReplyRequest1.setContent("testReply1");

        SupplementReplyRequest supplementReplyRequest2 = new SupplementReplyRequest();
        supplementReplyRequest2.setContent("testReply2");

        //when
        Supplement saveSupplement = supplementRepository.save(supplement);
        supplementReplyService.createSupplementReply(supplement.getId(),supplementReplyRequest1);
        supplementReplyService.createSupplementReply(supplement.getId(),supplementReplyRequest2);
        //then
        Assertions.assertEquals(2,supplementReplyService.getSupplementReplyList().size());
    }

    @Test
    public void 업데이트_댓글() throws Exception{
        /**
         *  영양제1
         *          댓글1
         *              댓글2
         *              댓글3
         */
        //given

        SupplementReplyRequest supplementReplyRequest1 = new SupplementReplyRequest();
        supplementReplyRequest1.setContent("testReply1");
        SupplementReplyRequest supplementReplyRequest2 = new SupplementReplyRequest();
        supplementReplyRequest2.setContent("testReply2");
        SupplementReplyRequest supplementReplyRequest3 = new SupplementReplyRequest();
        supplementReplyRequest3.setContent("testReply3");

        //when
        SupplementReply saveSupplementReply1 = supplementReplyService.createSupplementReply(saveSupplement.getId(),supplementReplyRequest1).get();
        SupplementReply saveSupplementReply2 = supplementReplyService.createSupplementReply(saveSupplement.getId(),saveSupplementReply1.getId(),supplementReplyRequest2).get();
        SupplementReply saveSupplementReply3 = supplementReplyService.createSupplementReply(saveSupplement.getId(),saveSupplementReply1.getId(),supplementReplyRequest3).get();

        supplementReplyRequest1.setContent("test1(수정)");
        supplementReplyService.updateSupplementReply(saveSupplementReply1.getId(),supplementReplyRequest1);

        supplementReplyRequest2.setContent("test2(수정)");
        supplementReplyService.updateSupplementReply(saveSupplementReply2.getId(),supplementReplyRequest2);


        //then
        Assertions.assertEquals("test1(수정)",supplementReplyService.getSupplementReply(saveSupplementReply1.getId()).getContent());
        Assertions.assertEquals("test2(수정)",supplementReplyService.getSupplementReply(saveSupplementReply2.getId()).getContent());
    }

    /**
     *  DEPTH 2 그냥 삭제
     *  DEPTH 1 대댓글 여부 확인 후 자식 있으면 flag만 변경, 없으면 삭제
     * @throws Exception
     */
    @Test
    public void 삭제_댓글() throws Exception{
        /**
         *  영양제1
         *          댓글1
         *              댓글2
         *              댓글3
         *          댓글4
         *
         */
        //given

        SupplementReplyRequest supplementReplyRequest1 = new SupplementReplyRequest();
        supplementReplyRequest1.setContent("testReply1");
        SupplementReplyRequest supplementReplyRequest2 = new SupplementReplyRequest();
        supplementReplyRequest2.setContent("testReply2");
        SupplementReplyRequest supplementReplyRequest3 = new SupplementReplyRequest();
        supplementReplyRequest3.setContent("testReply3");
        SupplementReplyRequest supplementReplyRequest4 = new SupplementReplyRequest();
        supplementReplyRequest4.setContent("testReply4");
        //when
        SupplementReply saveSupplementReply1 = supplementReplyService.createSupplementReply(saveSupplement.getId(),supplementReplyRequest1).get();
        SupplementReply saveSupplementReply2 = supplementReplyService.createSupplementReply(saveSupplement.getId(),saveSupplementReply1.getId(),supplementReplyRequest2).get();
        SupplementReply saveSupplementReply3 = supplementReplyService.createSupplementReply(saveSupplement.getId(),saveSupplementReply1.getId(),supplementReplyRequest3).get();

        SupplementReply saveSupplementReply4 = supplementReplyService.createSupplementReply(saveSupplement.getId(),supplementReplyRequest4).get();


        supplementReplyService.deleteSupplementReply(saveSupplementReply1.getId()); //flag만

        supplementReplyService.deleteSupplementReply(saveSupplementReply2.getId()); //대댓글 삭제
        supplementReplyService.deleteSupplementReply(saveSupplementReply4.getId()); //댓글 삭제


        //then
        Assertions.assertThrows(EntityNotFoundException.class, () ->supplementReplyService.getSupplementReply(saveSupplementReply2.getId()));
        Assertions.assertThrows(EntityNotFoundException.class, () ->supplementReplyService.getSupplementReply(saveSupplementReply4.getId()));
        Assertions.assertTrue(supplementReplyService.getSupplementReply(saveSupplementReply1.getId()).getDeleteFlag());
    }
    /**
     *  DEPTH 2 대댓글을 지웠을때, 부모도 삭제상태이고,대댓글을 마지막이 나였다면 부모도 지울때
     * @throws Exception
     */
    @Test
    public void 댓글_삭제_자식만지우기() throws Exception{
        /**
         *  영양제1
         *          댓글1
         *              댓글2
         *              댓글3
         */
        //given
        SupplementReplyRequest supplementReplyRequest1 = new SupplementReplyRequest();
        supplementReplyRequest1.setContent("testReply1");
        SupplementReplyRequest supplementReplyRequest2 = new SupplementReplyRequest();
        supplementReplyRequest2.setContent("testReply2");
        SupplementReplyRequest supplementReplyRequest3 = new SupplementReplyRequest();
        supplementReplyRequest3.setContent("testReply3");


        //when
        SupplementReply saveSupplementReply1 = supplementReplyService.createSupplementReply(saveSupplement.getId(),supplementReplyRequest1).get();
        SupplementReply saveSupplementReply2 = supplementReplyService.createSupplementReply(saveSupplement.getId(),saveSupplementReply1.getId(),supplementReplyRequest2).get();
        SupplementReply saveSupplementReply3 = supplementReplyService.createSupplementReply(saveSupplement.getId(),saveSupplementReply1.getId(),supplementReplyRequest3).get();

        supplementReplyService.deleteSupplementReply(saveSupplementReply1.getId()); // flag true 변경
        Assertions.assertTrue(supplementReplyService.getSupplementReply(saveSupplementReply1.getId()).getDeleteFlag());

        supplementReplyService.deleteSupplementReply(saveSupplementReply2.getId());
        supplementReplyService.deleteSupplementReply(saveSupplementReply3.getId());

        //then
        //2,3을 지웠을때 삭제되는지 확인
        Assertions.assertThrows(EntityNotFoundException.class, () ->supplementReplyService.getSupplementReply(saveSupplementReply1.getId()));
        Assertions.assertThrows(EntityNotFoundException.class, () ->supplementReplyService.getSupplementReply(saveSupplementReply2.getId()));
        Assertions.assertThrows(EntityNotFoundException.class, () ->supplementReplyService.getSupplementReply(saveSupplementReply3.getId()));
    }

}