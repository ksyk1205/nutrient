package mandykr.nutrient.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mandykr.nutrient.dto.SupplementReplyDto;
import mandykr.nutrient.dto.request.SupplementReplyRequest;
import mandykr.nutrient.entity.Supplement;
import mandykr.nutrient.entity.SupplementReply;
import mandykr.nutrient.repository.SupplementReplyRepository;

import mandykr.nutrient.repository.SupplementRepository;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
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
        Supplement supplement = supplementRepository.findById(supplementId).get();
        return Optional.of(
                supplementReplyRepository.save(
                        new SupplementReply.Builder(
                                new SupplementReply(
                                        request.getContent()
                                        ,supplement
                                )
                        )
                .build()));
    }

    @Transactional
    public Optional<SupplementReply> createSupplementReply(Long supplementId, Long supplementReplyId, SupplementReplyRequest request) {
        //대댓글
        Supplement supplement = supplementRepository.findById(supplementId).get();
        SupplementReply supplementReply = supplementReplyRepository.findById(supplementReplyId).get();
        return Optional.of(
                supplementReplyRepository.save(
                        new SupplementReply.Builder(
                            new SupplementReply(
                                    request.getContent()
                                    ,supplementReply
                                    ,supplement
                            )
                        )
                .build()));
    }

    public List<SupplementReply> getSupplementReplyBySupplement(Long supplementId){
        return supplementReplyRepository.findBySupplement(supplementRepository.findById(supplementId).get());
    }

    public SupplementReply getSupplementReply(Long supplementReplyId){

        return supplementReplyRepository.findById(supplementReplyId).get();
    }
    public List<SupplementReply> getSupplementReplyList(){
        return supplementReplyRepository.findAll();
    }

    public Optional<SupplementReply> updateSupplementReply(Long supplementReplyId,SupplementReplyRequest supplementReplyRequest){
        SupplementReply findSupplementReply = supplementReplyRepository.findById(supplementReplyId).get();
        return Optional.of(supplementReplyRepository.save(
                new SupplementReply.Builder(
                    new SupplementReply(
                        supplementReplyId,
                        supplementReplyRequest.getContent(),
                        findSupplementReply.getOrders(),
                        findSupplementReply.getDeleteFlag(),
                        findSupplementReply.getParent(),
                        findSupplementReply.getSupplement()
                    )
                )
            .build()));
    }

    public void deleteSupplementReply(Long supplementReplyId){
        Optional<SupplementReply> findSupplementReply
                = supplementReplyRepository.findParent(supplementReplyId);

        if(findSupplementReply.get().getChildren().size() > 0){
            supplementReplyRepository.save(
                    new SupplementReply.Builder(
                            new SupplementReply(
                                    supplementReplyId,
                                    supplementReplyRepository.findById(supplementReplyId).get()
                            )
                    )
            .build());
        }else{
            //삭제 가능한 조상 삭제
            supplementReplyRepository.delete(getDeletableAncestorReply(findSupplementReply.get()));
        }
    }
    private SupplementReply getDeletableAncestorReply(SupplementReply supplementReply) { // 삭제 가능한 조상 댓글을 구함
        SupplementReply parent = supplementReply.getParent(); // 현재 댓글의 부모를 구함
        if(parent != null && parent.getChildren().size() == 1 && parent.getDeleteFlag())
            // 부모가 있고, 부모의 자식이 1개(지금 삭제하는 댓글)이고, 부모의 삭제 상태가 TRUE인 댓글이라면 재귀
            return getDeletableAncestorReply(parent);
        return supplementReply; // 삭제해야하는 댓글 반환
    }

}
