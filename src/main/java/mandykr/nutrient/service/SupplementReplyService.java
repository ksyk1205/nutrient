package mandykr.nutrient.service;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.SupplementReplyDto;
import mandykr.nutrient.entity.Supplement;
import mandykr.nutrient.entity.SupplementReply;
import mandykr.nutrient.repository.SupplementReplyRepository;

import mandykr.nutrient.repository.SupplementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SupplementReplyService {

    private final SupplementReplyRepository supplementReplyRepository;

    private final SupplementRepository supplementRepository;

    @Transactional
    public Optional<SupplementReply> createSupplementReply(Long supplementId, SupplementReplyDto supplementReply){
        Supplement supplement = supplementRepository.findById(supplementId).get();
        SupplementReply saveSupplementReply =
                SupplementReply.makeSupplementReply(null,supplementReply.getContent(),supplement);
        return Optional.of(supplementReplyRepository.save(saveSupplementReply));
    }


    public List<SupplementReply> getSupplementReplyBySupplement(Long id){
        return supplementReplyRepository.findBySupplementId(id);
    }
    public SupplementReply getSupplementReply(Long id){
        return supplementReplyRepository.findById(id).get();
    }
    public List<SupplementReply> getSupplementReplyList(){
        return supplementReplyRepository.findAll();
    }

    public Optional<SupplementReply> updateSupplementReply(Long id,SupplementReplyDto supplementReplyDto){
        SupplementReply supplementReply = supplementReplyDto.makeSupplementReply(id,supplementReplyDto);
        return Optional.of(supplementReplyRepository.save(supplementReply));
    }
    public void deleteSupplementReply(Long id){
        supplementReplyRepository.deleteById(id);
    }
}
