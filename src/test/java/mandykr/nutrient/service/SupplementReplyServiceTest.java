package mandykr.nutrient.service;

import mandykr.nutrient.dto.SupplementReplyDto;
import mandykr.nutrient.entity.Supplement;
import mandykr.nutrient.entity.SupplementReply;
import mandykr.nutrient.repository.SupplementRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@SpringBootTest
@Transactional
class SupplementReplyServiceTest {

    @Autowired
    SupplementReplyService supplementReplyService;

    @Autowired
    SupplementRepository supplementRepository;


    @Test
    //@Rollback(value = false)
    public void 영양제_조회_테스트() throws Exception{
        //given
        Supplement supplement = new Supplement();
        supplement.setName("test");

        SupplementReplyDto supplementReplyDto1 = new SupplementReplyDto();
        supplementReplyDto1.setContent("testReply1");
        SupplementReplyDto supplementReplyDto2 = new SupplementReplyDto();
        supplementReplyDto2.setContent("testReply2");

        //when
        Supplement saveSupplement = supplementRepository.save(supplement);
        supplementReplyService.createSupplementReply(supplement.getId(),supplementReplyDto1);
        supplementReplyService.createSupplementReply(supplement.getId(),supplementReplyDto2);

        List<SupplementReply> supplementReplies
                = supplementReplyService.getSupplementReplyBySupplement(saveSupplement.getId());

        //then
        Assertions.assertEquals(2,supplementReplies.size());
    }

    @Test
    public void 영양제_전체_조회() throws Exception{
        Supplement supplement = new Supplement();
        supplement.setName("test");

        SupplementReplyDto supplementReplyDto1 = new SupplementReplyDto();
        supplementReplyDto1.setContent("testReply1");
        SupplementReplyDto supplementReplyDto2 = new SupplementReplyDto();
        supplementReplyDto2.setContent("testReply2");

        //when
        Supplement saveSupplement = supplementRepository.save(supplement);
        supplementReplyService.createSupplementReply(supplement.getId(),supplementReplyDto1);
        supplementReplyService.createSupplementReply(supplement.getId(),supplementReplyDto2);

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

        SupplementReplyDto supplementReplyDto = new SupplementReplyDto();
        supplementReplyDto.setContent("testReply");

        //when
        Supplement saveSupplement = supplementRepository.save(supplement);
        Long id = supplementReplyService.createSupplementReply(supplement.getId(),supplementReplyDto).get().getId();
        supplementReplyDto.setContent("testReply23");
        supplementReplyService.updateSupplementReply(id,supplementReplyDto);

        //then
        Assertions.assertEquals("testReply23",supplementReplyService.getSupplementReply(id).getContent());
    }
    
    
    @Test
    public void 삭제_댓글() throws Exception{
        //given
        Supplement supplement = new Supplement();
        supplement.setName("test");

        SupplementReplyDto supplementReplyDto = new SupplementReplyDto();
        supplementReplyDto.setContent("testReply");

        //when
        Supplement saveSupplement = supplementRepository.save(supplement);
        Long id = supplementReplyService.createSupplementReply(supplement.getId(),supplementReplyDto).get().getId();

        supplementReplyService.deleteSupplementReply(id);
        //then
        Assertions.assertThrows(NoSuchElementException.class,()->supplementReplyService.getSupplementReply(id));
    }
}