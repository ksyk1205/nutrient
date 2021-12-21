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
        return Optional.of(
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
        return Optional.of(
            supplementReplyRepository.save(
                new SupplementReply().builder()
                    .content(request.getContent())
                    .orders(supplementReply.getOrders()+1)
                    .deleteFlag(false)
                    .parent(supplementReply)
                    .supplement(supplement)
                    .build()
            )
        );
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

    public Optional<SupplementReply> updateSupplementReply(Long supplementReplyId,SupplementReplyRequest request){
        //SupplementReply findSupplementReply = supplementReplyRepository.findById(supplementReplyId).get();
        return Optional.of(
            supplementReplyRepository.save(
                new SupplementReply().builder()
                .id(supplementReplyId)
                .content(request.getContent())
                .build()
            )
        );
    }

    public void deleteSupplementReply(Long supplementReplyId){
        List<SupplementReply> findSupplementReply
                = supplementReplyRepository.findParent(supplementReplyId);
        if(findSupplementReply.size() > 0){
            supplementReplyRepository.save(
                new SupplementReply().builder()
                .id(supplementReplyId)
                .deleteFlag(true)
                .build()
            );
        }else{
            supplementReplyRepository.deleteById(supplementReplyId);
        }
    }
}
