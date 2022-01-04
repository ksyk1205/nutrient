package mandykr.nutrient.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mandykr.nutrient.dto.request.SupplementReplyRequest;
import mandykr.nutrient.entity.Supplement;
import mandykr.nutrient.entity.SupplementReply;
import mandykr.nutrient.repository.SupplementReplyRepository;

import mandykr.nutrient.repository.SupplementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SupplementReplyService {

    private final SupplementReplyRepository supplementReplyRepository;

    private final SupplementRepository supplementRepository;

    @Transactional
    public Optional<SupplementReply> createSupplementReply(Long supplementId, SupplementReplyRequest request){
        //첫 댓글
        Supplement supplement = supplementRepository.findById(supplementId)
                .orElseThrow(() -> new EntityNotFoundException("not found Supplement : " + supplementId));

        return Optional.ofNullable(
            supplementReplyRepository.save(
                new SupplementReply().builder()
                .content(request.getContent())
                .orders(1)
                .deleteFlag(false)
                .parent(null)
                .supplement(supplement)
                .build()
            )
        );
    }

    @Transactional
    public Optional<SupplementReply> createSupplementReply(Long supplementId, Long supplementReplyId, SupplementReplyRequest request) {
        //대댓글
        Supplement supplement = supplementRepository.findById(supplementId)
                .orElseThrow(() -> new EntityNotFoundException("not found Supplement : " + supplementId));
        SupplementReply supplementReply = supplementReplyRepository.findById(supplementReplyId)
                .orElseThrow(() -> new EntityNotFoundException("not found SupplementReplyId : " + supplementReplyId));
        SupplementReply saveSupplementReply = new SupplementReply().builder()
                                            .content(request.getContent())
                                            .orders(2)
                                            .deleteFlag(false)
                                            .supplement(supplement)
                                            .build();
        saveSupplementReply.addParents(supplementReply);
        return Optional.ofNullable(supplementReplyRepository.save(saveSupplementReply));
    }

    public List<SupplementReply> getSupplementReplyBySupplement(Long supplementId){
        return supplementReplyRepository.findBySupplement(supplementRepository.findById(supplementId)
                .orElseThrow(() -> new EntityNotFoundException("not found Supplement : " + supplementId)));
    }

    public SupplementReply getSupplementReply(Long supplementReplyId){
        return supplementReplyRepository.findById(supplementReplyId)
                .orElseThrow(() -> new EntityNotFoundException("not found SupplementReply : " + supplementReplyId));
    }
    public List<SupplementReply> getSupplementReplyList(){
        return supplementReplyRepository.findAll();
    }

    public Optional<SupplementReply> updateSupplementReply(Long supplementReplyId,SupplementReplyRequest request){
        //변경감지
        SupplementReply findSupplementReply = supplementReplyRepository.findById(supplementReplyId)
                .orElseThrow(() -> new EntityNotFoundException("not found SupplementReply : " + supplementReplyId));
        findSupplementReply.changeContent(request.getContent());
        return Optional.ofNullable(findSupplementReply);
    }
    @Transactional
    public void deleteSupplementReply(Long supplementReplyId){
        //DEPTH 2 그냥 삭제
        //      대댓글을 지웠을때, 부모도 삭제상태이고,대댓글을 마지막이 나였다면 부모도 지워
        //DEPTH 1 대댓글 여부 확인
        SupplementReply supplementReply = supplementReplyRepository.findById(supplementReplyId)
                .orElseThrow(() -> new EntityNotFoundException("not found SupplementReplyId : " + supplementReplyId));
        if(supplementReply.getOrders() == 2){
            SupplementReply parent = supplementReply.getParent();
            supplementReplyRepository.deleteById(supplementReplyId);
            parent.removeChild(supplementReply);
            if(parent.getDeleteFlag() && parent.getChild().size() == 0){
                supplementReplyRepository.deleteById(parent.getId());
            }
        }else{
            if(supplementReply.getChild().size() == 0){ //대댓글 없다면 지워
                supplementReplyRepository.deleteById(supplementReplyId);
            }else{
                //대댓글 있다면 flag만 변경
                //변경감지 활용
                supplementReply.changeTrueDeleteFlag();
            }
        }

    }
}
