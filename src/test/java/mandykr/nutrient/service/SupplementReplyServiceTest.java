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

import javax.persistence.EntityManager;
import java.util.List;
import java.util.NoSuchElementException;

@SpringBootTest
@Transactional
class SupplementReplyServiceTest {

    @Autowired
    SupplementReplyService supplementReplyService;

    @Autowired
    SupplementRepository supplementRepository;

    @Persistent
    EntityManager entityManager;


    @Test
    public void 영양제_조회_테스트() throws Exception{
        //given
        Supplement supplement = new Supplement();
        supplement.setName("test");

        SupplementReplyRequest supplementReplyRequest1 = new SupplementReplyRequest();
        supplementReplyRequest1.setContent("testReply1");

        SupplementReplyRequest supplementReplyRequest2 = new SupplementReplyRequest();
        supplementReplyRequest2.setContent("testReply1-1");

        //when
        Supplement saveSupplement = supplementRepository.save(supplement);
        SupplementReply saveSupplementReply1 = supplementReplyService
                .createSupplementReply(supplement.getId(),supplementReplyRequest1)
                .get();
        SupplementReply saveSupplementReply2 = supplementReplyService
                .createSupplementReply(supplement.getId(),saveSupplementReply1.getId(),supplementReplyRequest2)
                .get();

        List<SupplementReply> supplementReplies
                = supplementReplyService.getSupplementReplyBySupplement(saveSupplement.getId());

        //then
        Assertions.assertEquals(2,supplementReplies.size());
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
        //given
        Supplement supplement = new Supplement();
        supplement.setName("test");

        SupplementReplyRequest supplementReplyRequest1 = new SupplementReplyRequest();
        supplementReplyRequest1.setContent("testReply1");

        SupplementReplyRequest supplementReplyRequest2 = new SupplementReplyRequest();
        supplementReplyRequest2.setContent("testReply1-1");

        SupplementReplyRequest supplementReplyRequest3 = new SupplementReplyRequest();
        supplementReplyRequest3.setContent("testReply2");


        //when
        Supplement saveSupplement = supplementRepository.save(supplement);
        SupplementReply saveSupplementReply1 = supplementReplyService
                .createSupplementReply(supplement.getId(),supplementReplyRequest1)
                .get();
        SupplementReply saveSupplementReply2 = supplementReplyService
                .createSupplementReply(supplement.getId(),saveSupplementReply1.getId(),supplementReplyRequest2)
                .get();
        SupplementReply saveSupplementReply3 = supplementReplyService
                .createSupplementReply(supplement.getId(),supplementReplyRequest3)
                .get();

        supplementReplyRequest2.setContent("test1-1(수정)");
        supplementReplyService.updateSupplementReply(saveSupplementReply2.getId(),supplementReplyRequest2);

        //then
        Assertions.assertEquals("test1-1(수정)",supplementReplyService.getSupplementReply(saveSupplementReply2.getId()).getContent());
    }

    @Test
    public void 삭제_댓글() throws Exception{
        //given
        Supplement supplement = new Supplement();
        supplement.setName("test");

        SupplementReplyRequest supplementReplyRequest1 = new SupplementReplyRequest();
        supplementReplyRequest1.setContent("testReply1");

        SupplementReplyRequest supplementReplyRequest2 = new SupplementReplyRequest();
        supplementReplyRequest2.setContent("testReply1-1");

        SupplementReplyRequest supplementReplyRequest3 = new SupplementReplyRequest();
        supplementReplyRequest3.setContent("testReply1-2");


        //when
        Supplement saveSupplement = supplementRepository.save(supplement);
        SupplementReply saveSupplementReply1 = supplementReplyService
                .createSupplementReply(supplement.getId(),supplementReplyRequest1)
                .get();
        SupplementReply saveSupplementReply2 = supplementReplyService
                .createSupplementReply(supplement.getId(),saveSupplementReply1.getId(),supplementReplyRequest2)
                .get();
        SupplementReply saveSupplementReply3 = supplementReplyService
                .createSupplementReply(supplement.getId(),saveSupplementReply1.getId(),supplementReplyRequest3)
                .get();

        supplementReplyService.deleteSupplementReply(saveSupplementReply1.getId());

        //then
        Assertions.assertThrows(NoSuchElementException.class,()->supplementReplyService.getSupplementReply(saveSupplementReply2.getId()));
        //Assertions.assertEquals(supplementReplyService.getSupplementReply(saveSupplementReply1.getId()).getDeleteFlag(),
          //      false);
    }
}