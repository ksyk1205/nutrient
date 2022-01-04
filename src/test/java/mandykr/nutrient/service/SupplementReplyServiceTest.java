package mandykr.nutrient.service;

import mandykr.nutrient.dto.SupplementReplyDto;
import mandykr.nutrient.dto.request.SupplementReplyRequest;
import mandykr.nutrient.entity.Supplement;
import mandykr.nutrient.entity.SupplementReply;
import mandykr.nutrient.repository.SupplementReplyRepository;
import mandykr.nutrient.repository.SupplementRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SupplementReplyServiceTest")
class SupplementReplyServiceTest {

    SupplementReplyRepository supplementReplyRepository = mock(SupplementReplyRepository.class);

    SupplementRepository supplementRepository = mock(SupplementRepository.class);;

    SupplementReplyService supplementReplyService = new SupplementReplyService(supplementReplyRepository,supplementRepository);


    @Test
    public void 영양제_댓글_등록_테스트() throws Exception{
        /**
         *  영양제1
         *          댓글1
         *              댓글2
         *              댓글3
         */
        //given
        Supplement saveSupplement = new Supplement();
        saveSupplement.setId(1L);
        saveSupplement.setName("test1");
        saveSupplement.setRanking(4.2);
        given(supplementRepository.findById(saveSupplement.getId())).willReturn(Optional.ofNullable(saveSupplement));
        SupplementReplyRequest supplementReplyRequest1 = new SupplementReplyRequest();
        supplementReplyRequest1.setContent("testReply1");
        SupplementReplyRequest supplementReplyRequest2 = new SupplementReplyRequest();
        supplementReplyRequest2.setContent("testReply2");
        SupplementReplyRequest supplementReplyRequest3 = new SupplementReplyRequest();
        supplementReplyRequest3.setContent("testReply3");
        SupplementReply insertData = new SupplementReply().builder()
                .content(supplementReplyRequest1.getContent())
                .orders(1)
                .deleteFlag(false)
                .parent(null)
                .supplement(saveSupplement)
                .build();
        given(supplementReplyRepository.save(isA(SupplementReply.class))).willReturn(insertData);

        //when
        SupplementReply saveSupplementReply1 = supplementReplyService.createSupplementReply(saveSupplement.getId(),supplementReplyRequest1).get();
        //then
        verify(supplementReplyRepository, times(1)).save(isA(SupplementReply.class));
        verify(supplementRepository, times(1)).findById(saveSupplement.getId());
    }

    @Test
    public void 영양제_대댓글_등록_테스트() throws Exception{
        /**
         *  영양제1
         *          댓글1
         *              댓글2
         *              댓글3
         */
        //given
        Supplement saveSupplement = new Supplement();
        saveSupplement.setId(1L);
        saveSupplement.setName("test1");
        saveSupplement.setRanking(4.2);
        given(supplementRepository.findById(saveSupplement.getId())).willReturn(Optional.ofNullable(saveSupplement));
        SupplementReplyRequest supplementReplyRequest1 = new SupplementReplyRequest();
        supplementReplyRequest1.setContent("testReply1");
        SupplementReplyRequest supplementReplyRequest2 = new SupplementReplyRequest();
        supplementReplyRequest2.setContent("testReply2");
        SupplementReplyRequest supplementReplyRequest3 = new SupplementReplyRequest();
        supplementReplyRequest3.setContent("testReply3");
        SupplementReply insertSupplement = new SupplementReply().builder()
                .id(1L)
                .content(supplementReplyRequest1.getContent())
                .orders(1)
                .deleteFlag(false)
                .parent(null)
                .supplement(saveSupplement)
                .build();
        given(supplementReplyRepository.save(isA(SupplementReply.class))).willReturn(insertSupplement);
        given(supplementReplyRepository.findById(anyLong())).willReturn(Optional.of(insertSupplement));

        //when
        SupplementReply saveSupplementReply1 = supplementReplyService.createSupplementReply(saveSupplement.getId(),supplementReplyRequest1).get();
        supplementReplyService.createSupplementReply(saveSupplement.getId(),saveSupplementReply1.getId(),supplementReplyRequest2);
        supplementReplyService.createSupplementReply(saveSupplement.getId(),saveSupplementReply1.getId(),supplementReplyRequest3);

        //then
        verify(supplementReplyRepository, times(3)).save(isA(SupplementReply.class));
        verify(supplementRepository, times(3)).findById(saveSupplement.getId());
        verify(supplementReplyRepository, times(2)).findById(saveSupplement.getId());
    }


    @Test
    public void 업데이트_댓글() throws Exception{
        /**
         *  영양제1
         *          댓글1
         */
        //given
        Supplement saveSupplement = new Supplement();
        saveSupplement.setId(1L);
        saveSupplement.setName("test1");
        saveSupplement.setRanking(4.2);
        given(supplementRepository.findById(saveSupplement.getId())).willReturn(Optional.ofNullable(saveSupplement));


        SupplementReplyRequest supplementReplyRequest = new SupplementReplyRequest();
        supplementReplyRequest.setContent("testReply1");
        SupplementReply insertData = new SupplementReply().builder()
                .id(1L)
                .content(supplementReplyRequest.getContent())
                .orders(1)
                .deleteFlag(false)
                .parent(null)
                .supplement(saveSupplement)
                .build();
        given(supplementReplyRepository.save(isA(SupplementReply.class))).willReturn(insertData);
        given(supplementReplyRepository.findById(anyLong())).willReturn(Optional.ofNullable(insertData));

        //when
        supplementReplyService.createSupplementReply(saveSupplement.getId(),supplementReplyRequest).get();

        //when
        supplementReplyRequest.setContent("test1(수정)");
        SupplementReply supplementReply = supplementReplyService.updateSupplementReply(insertData.getId(), supplementReplyRequest).get();

        //then
        Assertions.assertEquals(supplementReplyRequest.getContent(),supplementReply.getContent());
    }

    /**
     *  DEPTH 1 대댓글 없으면 삭제
     * @throws Exception
     */
    @Test
    public void 삭제_댓글_한개() throws Exception{
        /**
         *  영양제1
         *          댓글1
         */
        //given
        Supplement saveSupplement = new Supplement();
        saveSupplement.setId(1L);
        saveSupplement.setName("test1");
        saveSupplement.setRanking(4.2);
        given(supplementRepository.findById(saveSupplement.getId())).willReturn(Optional.ofNullable(saveSupplement));

        SupplementReplyRequest supplementReplyRequest = new SupplementReplyRequest();
        supplementReplyRequest.setContent("testReply1");
        SupplementReply insertData = new SupplementReply().builder()
                .id(1L)
                .content(supplementReplyRequest.getContent())
                .orders(1)
                .deleteFlag(false)
                .parent(null)
                .supplement(saveSupplement)
                .build();
        given(supplementReplyRepository.findById(anyLong())).willReturn(Optional.ofNullable(insertData));

        //when
        supplementReplyRequest.setContent("test1(수정)");
        supplementReplyService.deleteSupplementReply(insertData.getId());

        //then
        verify(supplementReplyRepository, times(1)).findById(insertData.getId());
        verify(supplementReplyRepository, times(1)).deleteById(insertData.getId());
    }

    /**
     *  DEPTH 2 대댓글 삭제
     * @throws Exception
     */
    @Test
    public void 삭제_대댓글_삭제_한개() throws Exception{
        /**
         *  영양제1
         *          댓글1
         *              댓글2
         */
        //given
        Supplement saveSupplement = new Supplement();
        saveSupplement.setId(1L);
        saveSupplement.setName("test1");
        saveSupplement.setRanking(4.2);
        given(supplementRepository.findById(saveSupplement.getId())).willReturn(Optional.ofNullable(saveSupplement));

        SupplementReplyRequest supplementReplyRequest1 = new SupplementReplyRequest();
        supplementReplyRequest1.setContent("testReply1");
        SupplementReply insertData1 = new SupplementReply().builder()
                .id(1L)
                .content(supplementReplyRequest1.getContent())
                .orders(1)
                .deleteFlag(false)
                .parent(null)
                .supplement(saveSupplement)
                .build();
        given(supplementReplyRepository.findById(1L)).willReturn(Optional.ofNullable(insertData1));
        SupplementReplyRequest supplementReplyRequest2 = new SupplementReplyRequest();
        supplementReplyRequest2.setContent("testReply2");
        SupplementReply insertData2 = new SupplementReply().builder()
                .id(2L)
                .content(supplementReplyRequest2.getContent())
                .orders(2)
                .deleteFlag(false)
                .parent(insertData1)
                .supplement(saveSupplement)
                .build();
        given(supplementReplyRepository.findById(2L)).willReturn(Optional.ofNullable(insertData2));
        //when
        supplementReplyService.deleteSupplementReply(insertData2.getId());

        //then
        verify(supplementReplyRepository, times(1)).findById(insertData2.getId());
        verify(supplementReplyRepository, times(1)).deleteById(insertData2.getId());
    }

    /**
     *  DEPTH 2 대댓글
     *  DEPTH 1 댓글 flag
     * @throws Exception
     */
    @Test
    @DisplayName("대댓글있는 댓글 삭제(flag만 변경)")
    public void 삭제_댓글_대댓글존재() throws Exception{
        /**
         *  영양제1
         *          댓글1
         *              댓글2
         */
        //given
        Supplement saveSupplement = new Supplement();
        saveSupplement.setId(1L);
        saveSupplement.setName("test1");
        saveSupplement.setRanking(4.2);
        given(supplementRepository.findById(saveSupplement.getId())).willReturn(Optional.ofNullable(saveSupplement));

        SupplementReplyRequest supplementReplyRequest1 = new SupplementReplyRequest();
        supplementReplyRequest1.setContent("testReply1");
        SupplementReply insertData1 = new SupplementReply().builder()
                .id(1L)
                .content(supplementReplyRequest1.getContent())
                .orders(1)
                .deleteFlag(false)
                .parent(null)
                .supplement(saveSupplement)
                .build();

        SupplementReplyRequest supplementReplyRequest2 = new SupplementReplyRequest();
        supplementReplyRequest2.setContent("testReply2");
        SupplementReply insertData2 = new SupplementReply().builder()
                .id(2L)
                .content(supplementReplyRequest2.getContent())
                .orders(2)
                .deleteFlag(false)
                .supplement(saveSupplement)
                .build();
        given(supplementReplyRepository.findById(2L)).willReturn(Optional.ofNullable(insertData2));
        insertData2.addParents(insertData1);
        given(supplementReplyRepository.findById(1L)).willReturn(Optional.ofNullable(insertData1));
        //when
        supplementReplyService.deleteSupplementReply(insertData1.getId());

        //then
        Assertions.assertTrue(insertData1.getDeleteFlag());
    }

    /**
     * 대댓글을 지웠을때, 부모도 삭제상태이고,대댓글을 마지막이 나였다면 부모도 지울때
     *  DEPTH 2 대댓글 삭제
     *  DEPTH 1 댓글 flag
     *  모두 지움
     * @throws Exception
     */
    @Test
    public void 삭제_댓글_All() throws Exception{
        /**
         *  영양제1
         *          댓글1
         *              댓글2
         */
        //given
        Supplement saveSupplement = new Supplement();
        saveSupplement.setId(1L);
        saveSupplement.setName("test1");
        saveSupplement.setRanking(4.2);
        given(supplementRepository.findById(saveSupplement.getId())).willReturn(Optional.ofNullable(saveSupplement));

        SupplementReplyRequest supplementReplyRequest1 = new SupplementReplyRequest();
        supplementReplyRequest1.setContent("testReply1");
        SupplementReply insertData1 = new SupplementReply().builder()
                .id(1L)
                .content(supplementReplyRequest1.getContent())
                .orders(1)
                .deleteFlag(false)
                .parent(null)
                .supplement(saveSupplement)
                .build();

        SupplementReplyRequest supplementReplyRequest2 = new SupplementReplyRequest();
        supplementReplyRequest2.setContent("testReply2");
        SupplementReply insertData2 = new SupplementReply().builder()
                .id(2L)
                .content(supplementReplyRequest2.getContent())
                .orders(2)
                .deleteFlag(false)
                .supplement(saveSupplement)
                .build();
        given(supplementReplyRepository.findById(2L)).willReturn(Optional.ofNullable(insertData2));
        insertData2.addParents(insertData1);
        given(supplementReplyRepository.findById(1L)).willReturn(Optional.ofNullable(insertData1));
        //when
        supplementReplyService.deleteSupplementReply(insertData1.getId());
        Assertions.assertTrue(insertData1.getDeleteFlag());
        supplementReplyService.deleteSupplementReply(insertData2.getId());

        //then
        verify(supplementReplyRepository, times(2)).findById(anyLong());
        verify(supplementReplyRepository, times(2)).deleteById(anyLong());
    }

}