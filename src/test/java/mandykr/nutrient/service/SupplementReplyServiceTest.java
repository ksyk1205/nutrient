package mandykr.nutrient.service;

import mandykr.nutrient.dto.SupplementReplyDto;
import mandykr.nutrient.dto.request.SupplementReplyRequest;
import mandykr.nutrient.entity.Supplement;
import mandykr.nutrient.entity.SupplementReply;
import mandykr.nutrient.repository.SupplementRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.annotation.Persistent;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.NoSuchElementException;

@SpringBootTest
@Transactional
class SupplementReplyServiceTest {

    @Autowired
    SupplementReplyService supplementReplyService;

    @Autowired
    SupplementRepository supplementRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    public void 영양제_등록_테스트() throws Exception{
        /**
         *  영양제1
         *          댓글1
         *              댓글2
         *              댓글3
         *          댓글4
         *              댓글5
         *              댓글6
         *          댓글7
         *  영양제2
         *          댓글8
         *              댓글9
         *              댓글10
         *              댓글11
         *          댓글12
         *             댓글13
         *
         */
        //given
        Supplement supplement1 = new Supplement();
        supplement1.setName("test1");
        Supplement supplement2 = new Supplement();
        supplement2.setName("test2");

        Supplement saveSupplement1 = supplementRepository.save(supplement1);
        Supplement saveSupplement2 = supplementRepository.save(supplement2);

        SupplementReplyRequest supplementReplyRequest1 = new SupplementReplyRequest();
        supplementReplyRequest1.setContent("testReply1");
        SupplementReplyRequest supplementReplyRequest2 = new SupplementReplyRequest();
        supplementReplyRequest2.setContent("testReply2");
        SupplementReplyRequest supplementReplyRequest3 = new SupplementReplyRequest();
        supplementReplyRequest3.setContent("testReply3");
        SupplementReplyRequest supplementReplyRequest4 = new SupplementReplyRequest();
        supplementReplyRequest4.setContent("testReply4");
        SupplementReplyRequest supplementReplyRequest5 = new SupplementReplyRequest();
        supplementReplyRequest5.setContent("testReply5");
        SupplementReplyRequest supplementReplyRequest6 = new SupplementReplyRequest();
        supplementReplyRequest6.setContent("testReply6");
        SupplementReplyRequest supplementReplyRequest7 = new SupplementReplyRequest();
        supplementReplyRequest7.setContent("testReply7");
        SupplementReplyRequest supplementReplyRequest8 = new SupplementReplyRequest();
        supplementReplyRequest8.setContent("testReply8");
        SupplementReplyRequest supplementReplyRequest9 = new SupplementReplyRequest();
        supplementReplyRequest9.setContent("testReply9");
        SupplementReplyRequest supplementReplyRequest10 = new SupplementReplyRequest();
        supplementReplyRequest10.setContent("testReply10");
        SupplementReplyRequest supplementReplyRequest11 = new SupplementReplyRequest();
        supplementReplyRequest11.setContent("testReply11");
        SupplementReplyRequest supplementReplyRequest12 = new SupplementReplyRequest();
        supplementReplyRequest12.setContent("testReply12");
        SupplementReplyRequest supplementReplyRequest13 = new SupplementReplyRequest();
        supplementReplyRequest13.setContent("testReply13");
        //when
        SupplementReply saveSupplementReply1 = supplementReplyService.createSupplementReply(saveSupplement1.getId(),supplementReplyRequest1).get();
        SupplementReply saveSupplementReply2 = supplementReplyService.createSupplementReply(saveSupplement1.getId(),saveSupplementReply1.getId(),supplementReplyRequest2).get();
        SupplementReply saveSupplementReply3 = supplementReplyService.createSupplementReply(saveSupplement1.getId(),saveSupplementReply1.getId(),supplementReplyRequest3).get();

        SupplementReply saveSupplementReply4 = supplementReplyService.createSupplementReply(saveSupplement1.getId(),supplementReplyRequest4).get();
        SupplementReply saveSupplementReply5 = supplementReplyService.createSupplementReply(saveSupplement1.getId(),saveSupplementReply4.getId(),supplementReplyRequest5).get();
        SupplementReply saveSupplementReply6 = supplementReplyService.createSupplementReply(saveSupplement1.getId(),saveSupplementReply4.getId(),supplementReplyRequest6).get();

        SupplementReply saveSupplementReply7 = supplementReplyService.createSupplementReply(saveSupplement1.getId(),supplementReplyRequest7).get();

        SupplementReply saveSupplementReply8 = supplementReplyService.createSupplementReply(saveSupplement2.getId(),supplementReplyRequest8).get();
        SupplementReply saveSupplementReply9 = supplementReplyService.createSupplementReply(saveSupplement2.getId(),saveSupplementReply8.getId(),supplementReplyRequest9).get();
        SupplementReply saveSupplementReply10 = supplementReplyService.createSupplementReply(saveSupplement2.getId(),saveSupplementReply8.getId(),supplementReplyRequest10).get();
        SupplementReply saveSupplementReply11 = supplementReplyService.createSupplementReply(saveSupplement2.getId(),saveSupplementReply8.getId(),supplementReplyRequest11).get();
        SupplementReply saveSupplementReply12 = supplementReplyService.createSupplementReply(saveSupplement2.getId(),supplementReplyRequest12).get();
        SupplementReply saveSupplementReply13 = supplementReplyService.createSupplementReply(saveSupplement2.getId(),saveSupplementReply12.getId(),supplementReplyRequest13).get();


        //then
        Assertions.assertEquals(7,supplementReplyService.getSupplementReplyBySupplement(saveSupplement1.getId()).size());
        Assertions.assertEquals(6,supplementReplyService.getSupplementReplyBySupplement(saveSupplement2.getId()).size());
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

        List<SupplementReply> supplementReplies
                = supplementReplyService.getSupplementReplyList();

        //then
        Assertions.assertEquals(2,supplementReplies.size());
    }

    @Test
    public void 업데이트_댓글() throws Exception{
        /**
         *  영양제1
         *          댓글1
         *              댓글2
         *              댓글3
         *          댓글4
         *              댓글5
         *              댓글6
         *          댓글7
         *  영양제2
         *          댓글8
         *              댓글9
         *              댓글10
         *              댓글11
         *          댓글12
         *             댓글13
         *
         */
        //given
        Supplement supplement1 = new Supplement();
        supplement1.setName("test1");
        Supplement supplement2 = new Supplement();
        supplement2.setName("test2");

        Supplement saveSupplement1 = supplementRepository.save(supplement1);
        Supplement saveSupplement2 = supplementRepository.save(supplement2);

        SupplementReplyRequest supplementReplyRequest1 = new SupplementReplyRequest();
        supplementReplyRequest1.setContent("testReply1");
        SupplementReplyRequest supplementReplyRequest2 = new SupplementReplyRequest();
        supplementReplyRequest2.setContent("testReply2");
        SupplementReplyRequest supplementReplyRequest3 = new SupplementReplyRequest();
        supplementReplyRequest3.setContent("testReply3");
        SupplementReplyRequest supplementReplyRequest4 = new SupplementReplyRequest();
        supplementReplyRequest4.setContent("testReply4");
        SupplementReplyRequest supplementReplyRequest5 = new SupplementReplyRequest();
        supplementReplyRequest5.setContent("testReply5");
        SupplementReplyRequest supplementReplyRequest6 = new SupplementReplyRequest();
        supplementReplyRequest6.setContent("testReply6");
        SupplementReplyRequest supplementReplyRequest7 = new SupplementReplyRequest();
        supplementReplyRequest7.setContent("testReply7");
        SupplementReplyRequest supplementReplyRequest8 = new SupplementReplyRequest();
        supplementReplyRequest8.setContent("testReply8");
        SupplementReplyRequest supplementReplyRequest9 = new SupplementReplyRequest();
        supplementReplyRequest9.setContent("testReply9");
        SupplementReplyRequest supplementReplyRequest10 = new SupplementReplyRequest();
        supplementReplyRequest10.setContent("testReply10");
        SupplementReplyRequest supplementReplyRequest11 = new SupplementReplyRequest();
        supplementReplyRequest11.setContent("testReply11");
        SupplementReplyRequest supplementReplyRequest12 = new SupplementReplyRequest();
        supplementReplyRequest12.setContent("testReply12");
        SupplementReplyRequest supplementReplyRequest13 = new SupplementReplyRequest();
        supplementReplyRequest13.setContent("testReply13");
        //when
        SupplementReply saveSupplementReply1 = supplementReplyService.createSupplementReply(saveSupplement1.getId(),supplementReplyRequest1).get();
        SupplementReply saveSupplementReply2 = supplementReplyService.createSupplementReply(saveSupplement1.getId(),saveSupplementReply1.getId(),supplementReplyRequest2).get();
        SupplementReply saveSupplementReply3 = supplementReplyService.createSupplementReply(saveSupplement1.getId(),saveSupplementReply1.getId(),supplementReplyRequest3).get();

        SupplementReply saveSupplementReply4 = supplementReplyService.createSupplementReply(saveSupplement1.getId(),supplementReplyRequest4).get();
        SupplementReply saveSupplementReply5 = supplementReplyService.createSupplementReply(saveSupplement1.getId(),saveSupplementReply4.getId(),supplementReplyRequest5).get();
        SupplementReply saveSupplementReply6 = supplementReplyService.createSupplementReply(saveSupplement1.getId(),saveSupplementReply4.getId(),supplementReplyRequest6).get();

        SupplementReply saveSupplementReply7 = supplementReplyService.createSupplementReply(saveSupplement1.getId(),supplementReplyRequest7).get();

        SupplementReply saveSupplementReply8 = supplementReplyService.createSupplementReply(saveSupplement2.getId(),supplementReplyRequest8).get();
        SupplementReply saveSupplementReply9 = supplementReplyService.createSupplementReply(saveSupplement2.getId(),saveSupplementReply8.getId(),supplementReplyRequest9).get();
        SupplementReply saveSupplementReply10 = supplementReplyService.createSupplementReply(saveSupplement2.getId(),saveSupplementReply8.getId(),supplementReplyRequest10).get();
        SupplementReply saveSupplementReply11 = supplementReplyService.createSupplementReply(saveSupplement2.getId(),saveSupplementReply8.getId(),supplementReplyRequest11).get();
        SupplementReply saveSupplementReply12 = supplementReplyService.createSupplementReply(saveSupplement2.getId(),supplementReplyRequest12).get();
        SupplementReply saveSupplementReply13 = supplementReplyService.createSupplementReply(saveSupplement2.getId(),saveSupplementReply12.getId(),supplementReplyRequest13).get();


        supplementReplyRequest2.setContent("test2(수정)");
        supplementReplyService.updateSupplementReply(saveSupplementReply2.getId(),supplementReplyRequest2);

        supplementReplyRequest6.setContent("test6(수정)");
        supplementReplyService.updateSupplementReply(saveSupplementReply6.getId(),supplementReplyRequest6);

        //then
        Assertions.assertEquals("test2(수정)",supplementReplyService.getSupplementReply(saveSupplementReply2.getId()).getContent());
        Assertions.assertEquals("test6(수정)",supplementReplyService.getSupplementReply(saveSupplementReply6.getId()).getContent());
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
         *              댓글5
         *              댓글6
         *          댓글7
         *  영양제2
         *          댓글8
         *              댓글9
         *              댓글10
         *              댓글11
         *          댓글12
         *             댓글13
         *
         */
        //given
        Supplement supplement1 = new Supplement();
        supplement1.setName("test1");
        Supplement supplement2 = new Supplement();
        supplement2.setName("test2");

        Supplement saveSupplement1 = supplementRepository.save(supplement1);
        Supplement saveSupplement2 = supplementRepository.save(supplement2);

        SupplementReplyRequest supplementReplyRequest1 = new SupplementReplyRequest();
        supplementReplyRequest1.setContent("testReply1");
        SupplementReplyRequest supplementReplyRequest2 = new SupplementReplyRequest();
        supplementReplyRequest2.setContent("testReply2");
        SupplementReplyRequest supplementReplyRequest3 = new SupplementReplyRequest();
        supplementReplyRequest3.setContent("testReply3");
        SupplementReplyRequest supplementReplyRequest4 = new SupplementReplyRequest();
        supplementReplyRequest4.setContent("testReply4");
        SupplementReplyRequest supplementReplyRequest5 = new SupplementReplyRequest();
        supplementReplyRequest5.setContent("testReply5");
        SupplementReplyRequest supplementReplyRequest6 = new SupplementReplyRequest();
        supplementReplyRequest6.setContent("testReply6");
        SupplementReplyRequest supplementReplyRequest7 = new SupplementReplyRequest();
        supplementReplyRequest7.setContent("testReply7");
        SupplementReplyRequest supplementReplyRequest8 = new SupplementReplyRequest();
        supplementReplyRequest8.setContent("testReply8");
        SupplementReplyRequest supplementReplyRequest9 = new SupplementReplyRequest();
        supplementReplyRequest9.setContent("testReply9");
        SupplementReplyRequest supplementReplyRequest10 = new SupplementReplyRequest();
        supplementReplyRequest10.setContent("testReply10");
        SupplementReplyRequest supplementReplyRequest11 = new SupplementReplyRequest();
        supplementReplyRequest11.setContent("testReply11");
        SupplementReplyRequest supplementReplyRequest12 = new SupplementReplyRequest();
        supplementReplyRequest12.setContent("testReply12");
        SupplementReplyRequest supplementReplyRequest13 = new SupplementReplyRequest();
        supplementReplyRequest13.setContent("testReply13");
        //when
        SupplementReply saveSupplementReply1 = supplementReplyService.createSupplementReply(saveSupplement1.getId(),supplementReplyRequest1).get();
        SupplementReply saveSupplementReply2 = supplementReplyService.createSupplementReply(saveSupplement1.getId(),saveSupplementReply1.getId(),supplementReplyRequest2).get();
        SupplementReply saveSupplementReply3 = supplementReplyService.createSupplementReply(saveSupplement1.getId(),saveSupplementReply1.getId(),supplementReplyRequest3).get();

        SupplementReply saveSupplementReply4 = supplementReplyService.createSupplementReply(saveSupplement1.getId(),supplementReplyRequest4).get();
        SupplementReply saveSupplementReply5 = supplementReplyService.createSupplementReply(saveSupplement1.getId(),saveSupplementReply4.getId(),supplementReplyRequest5).get();
        SupplementReply saveSupplementReply6 = supplementReplyService.createSupplementReply(saveSupplement1.getId(),saveSupplementReply4.getId(),supplementReplyRequest6).get();

        SupplementReply saveSupplementReply7 = supplementReplyService.createSupplementReply(saveSupplement1.getId(),supplementReplyRequest7).get();

        SupplementReply saveSupplementReply8 = supplementReplyService.createSupplementReply(saveSupplement2.getId(),supplementReplyRequest8).get();
        SupplementReply saveSupplementReply9 = supplementReplyService.createSupplementReply(saveSupplement2.getId(),saveSupplementReply8.getId(),supplementReplyRequest9).get();
        SupplementReply saveSupplementReply10 = supplementReplyService.createSupplementReply(saveSupplement2.getId(),saveSupplementReply8.getId(),supplementReplyRequest10).get();
        SupplementReply saveSupplementReply11 = supplementReplyService.createSupplementReply(saveSupplement2.getId(),saveSupplementReply8.getId(),supplementReplyRequest11).get();
        SupplementReply saveSupplementReply12 = supplementReplyService.createSupplementReply(saveSupplement2.getId(),supplementReplyRequest12).get();
        SupplementReply saveSupplementReply13 = supplementReplyService.createSupplementReply(saveSupplement2.getId(),saveSupplementReply12.getId(),supplementReplyRequest13).get();



        supplementReplyService.deleteSupplementReply(saveSupplementReply6.getId());
        supplementReplyService.deleteSupplementReply(saveSupplementReply7.getId());
        supplementReplyService.deleteSupplementReply(saveSupplementReply1.getId());



        //then
        Assertions.assertThrows(EntityNotFoundException.class, () ->supplementReplyService.getSupplementReply(saveSupplementReply6.getId()));
        Assertions.assertThrows(EntityNotFoundException.class, () ->supplementReplyService.getSupplementReply(saveSupplementReply7.getId()));
        Assertions.assertEquals(supplementReplyService.getSupplementReply(saveSupplementReply1.getId()).getDeleteFlag(),true);
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
         *          댓글4
         *              댓글5
         *              댓글6
         *          댓글7
         *  영양제2
         *          댓글8
         *              댓글9
         *              댓글10
         *              댓글11
         *          댓글12
         *             댓글13
         *
         */
        //given
        Supplement supplement1 = new Supplement();
        supplement1.setName("test1");
        Supplement supplement2 = new Supplement();
        supplement2.setName("test2");

        Supplement saveSupplement1 = supplementRepository.save(supplement1);
        Supplement saveSupplement2 = supplementRepository.save(supplement2);

        SupplementReplyRequest supplementReplyRequest1 = new SupplementReplyRequest();
        supplementReplyRequest1.setContent("testReply1");
        SupplementReplyRequest supplementReplyRequest2 = new SupplementReplyRequest();
        supplementReplyRequest2.setContent("testReply2");
        SupplementReplyRequest supplementReplyRequest3 = new SupplementReplyRequest();
        supplementReplyRequest3.setContent("testReply3");
        SupplementReplyRequest supplementReplyRequest4 = new SupplementReplyRequest();
        supplementReplyRequest4.setContent("testReply4");
        SupplementReplyRequest supplementReplyRequest5 = new SupplementReplyRequest();
        supplementReplyRequest5.setContent("testReply5");
        SupplementReplyRequest supplementReplyRequest6 = new SupplementReplyRequest();
        supplementReplyRequest6.setContent("testReply6");
        SupplementReplyRequest supplementReplyRequest7 = new SupplementReplyRequest();
        supplementReplyRequest7.setContent("testReply7");
        SupplementReplyRequest supplementReplyRequest8 = new SupplementReplyRequest();
        supplementReplyRequest8.setContent("testReply8");
        SupplementReplyRequest supplementReplyRequest9 = new SupplementReplyRequest();
        supplementReplyRequest9.setContent("testReply9");
        SupplementReplyRequest supplementReplyRequest10 = new SupplementReplyRequest();
        supplementReplyRequest10.setContent("testReply10");
        SupplementReplyRequest supplementReplyRequest11 = new SupplementReplyRequest();
        supplementReplyRequest11.setContent("testReply11");
        SupplementReplyRequest supplementReplyRequest12 = new SupplementReplyRequest();
        supplementReplyRequest12.setContent("testReply12");
        SupplementReplyRequest supplementReplyRequest13 = new SupplementReplyRequest();
        supplementReplyRequest13.setContent("testReply13");
        //when
        SupplementReply saveSupplementReply1 = supplementReplyService.createSupplementReply(saveSupplement1.getId(),supplementReplyRequest1).get();
        SupplementReply saveSupplementReply2 = supplementReplyService.createSupplementReply(saveSupplement1.getId(),saveSupplementReply1.getId(),supplementReplyRequest2).get();
        SupplementReply saveSupplementReply3 = supplementReplyService.createSupplementReply(saveSupplement1.getId(),saveSupplementReply1.getId(),supplementReplyRequest3).get();

        SupplementReply saveSupplementReply4 = supplementReplyService.createSupplementReply(saveSupplement1.getId(),supplementReplyRequest4).get();
        SupplementReply saveSupplementReply5 = supplementReplyService.createSupplementReply(saveSupplement1.getId(),saveSupplementReply4.getId(),supplementReplyRequest5).get();
        SupplementReply saveSupplementReply6 = supplementReplyService.createSupplementReply(saveSupplement1.getId(),saveSupplementReply4.getId(),supplementReplyRequest6).get();

        SupplementReply saveSupplementReply7 = supplementReplyService.createSupplementReply(saveSupplement1.getId(),supplementReplyRequest7).get();

        SupplementReply saveSupplementReply8 = supplementReplyService.createSupplementReply(saveSupplement2.getId(),supplementReplyRequest8).get();
        SupplementReply saveSupplementReply9 = supplementReplyService.createSupplementReply(saveSupplement2.getId(),saveSupplementReply8.getId(),supplementReplyRequest9).get();
        SupplementReply saveSupplementReply10 = supplementReplyService.createSupplementReply(saveSupplement2.getId(),saveSupplementReply8.getId(),supplementReplyRequest10).get();
        SupplementReply saveSupplementReply11 = supplementReplyService.createSupplementReply(saveSupplement2.getId(),saveSupplementReply8.getId(),supplementReplyRequest11).get();
        SupplementReply saveSupplementReply12 = supplementReplyService.createSupplementReply(saveSupplement2.getId(),supplementReplyRequest12).get();
        SupplementReply saveSupplementReply13 = supplementReplyService.createSupplementReply(saveSupplement2.getId(),saveSupplementReply12.getId(),supplementReplyRequest13).get();



        supplementReplyService.deleteSupplementReply(saveSupplementReply1.getId());


        Assertions.assertEquals(supplementReplyService.getSupplementReply(saveSupplementReply1.getId()).getDeleteFlag(),true);
        supplementReplyService.deleteSupplementReply(saveSupplementReply2.getId());

        supplementReplyService.deleteSupplementReply(saveSupplementReply3.getId());

        //then
        //2,3을 지웠을때 삭제되는지 확인
        Assertions.assertThrows(EntityNotFoundException.class, () ->supplementReplyService.getSupplementReply(saveSupplementReply1.getId()));
        Assertions.assertThrows(EntityNotFoundException.class, () ->supplementReplyService.getSupplementReply(saveSupplementReply2.getId()));
        Assertions.assertThrows(EntityNotFoundException.class, () ->supplementReplyService.getSupplementReply(saveSupplementReply3.getId()));
    }

}