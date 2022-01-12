package mandykr.nutrient.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mandykr.nutrient.dto.SupplementReplyDto;
import mandykr.nutrient.dto.request.SupplementReplyRequest;
import mandykr.nutrient.entity.Member;
import mandykr.nutrient.entity.Supplement;
import mandykr.nutrient.entity.SupplementReply;
import mandykr.nutrient.repository.SupplementReplyRepository;

import mandykr.nutrient.repository.SupplementRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.by;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SupplementReplyService {

    private final SupplementReplyRepository supplementReplyRepository;

    private final SupplementRepository supplementRepository;

    @Transactional
    public SupplementReplyDto createSupplementReply(Long supplementId, Member member, SupplementReplyDto supplementReplyDto){
        //첫 댓글
        Supplement supplement = supplementRepository.findById(supplementId)
                .orElseThrow(() -> new EntityNotFoundException("not found Supplement : " + supplementId));
        Long groups = supplementReplyRepository.findByLastOrderWithParent(supplementId);
        if(groups == null)
            groups = 0L;
        return Optional.ofNullable(
                supplementReplyRepository.save(
                    SupplementReply.builder()
                    .content(supplementReplyDto.getContent())
                    .groups(groups)
                    .groupOrder(1L)
                    .member(member)
                    .deleteFlag(false)
                    .parent(null)
                    .supplement(supplement)
                    .build()
                )
            ).map(SupplementReplyDto::new)
            .get();
    }

    @Transactional
    public SupplementReplyDto createSupplementReply(Long supplementId, Long supplementReplyId, Member member, SupplementReplyDto supplementReplyDto) {
        //대댓글
        Supplement supplement = supplementRepository.findById(supplementId)
                .orElseThrow(() -> new EntityNotFoundException("not found Supplement : " + supplementId));
        SupplementReply supplementReply = supplementReplyRepository.findById(supplementReplyId)
                .orElseThrow(() -> new EntityNotFoundException("not found SupplementReply : " + supplementReplyId));

        Long groupOrder = supplementReplyRepository.findByLastOrderWithChild(supplementId, supplementReplyId);
        if(groupOrder == null)
            groupOrder = 1L;
        SupplementReply saveSupplementReply = supplementReplyRepository.save(SupplementReply.builder()
                                                .content(supplementReplyDto.getContent())
                                                .groups(supplementReply.getGroups())
                                                .groupOrder(groupOrder+1)
                                                .deleteFlag(false)
                                                .supplement(supplement)
                                                .build());
        saveSupplementReply.addParents(supplementReply);
        return Optional.ofNullable(saveSupplementReply).map(SupplementReplyDto::new).get();
    }

    public List<SupplementReplyDto> getSupplementReplyBySupplement(Long supplementId){
        return supplementReplyRepository.findBySupplementAndParentIsNull(supplementRepository.findById(supplementId)
                .orElseThrow(() -> new EntityNotFoundException("not found Supplement : " + supplementId)), by(ASC, "groups", "groupOrder"))
                .stream()
                .map(SupplementReplyDto::new)
                .collect(Collectors.toList());
    }

    public SupplementReply getSupplementReply(Long supplementReplyId){
        return supplementReplyRepository.findById(supplementReplyId)
                .orElseThrow(() -> new EntityNotFoundException("not found SupplementReply : " + supplementReplyId));
    }
    public List<SupplementReplyDto> getSupplementReplyBySupplementWithParent(Long supplementId, Long supplementReplyId){
        return supplementReplyRepository.findBySupplementAndParent(supplementRepository.findById(supplementId)
                .orElseThrow(() -> new EntityNotFoundException("not found Supplement : " + supplementId))
                        , getSupplementReply(supplementReplyId)
                        , by(ASC, "groups", "groupOrder"))
                .stream()
                .map(SupplementReplyDto::new)
                .collect(Collectors.toList());
    }

    public SupplementReplyDto updateSupplementReply(Long supplementReplyId, Member member, SupplementReplyDto supplementReplyDto){
        //변경감지
        SupplementReply findSupplementReply = supplementReplyRepository.findByIdAndMember(supplementReplyId, member)
                .orElseThrow(() -> new EntityNotFoundException("not found SupplementReply : " + supplementReplyId));
        findSupplementReply.changeContent(supplementReplyDto.getContent());
        return Optional.ofNullable(findSupplementReply).map(SupplementReplyDto::new).get();
    }
    @Transactional
    public void deleteSupplementReply(Long supplementReplyId, Member member){
        //DEPTH 2 그냥 삭제
        //      대댓글을 지웠을때, 부모도 삭제상태이고,대댓글을 마지막이 나였다면 부모도 지워
        //DEPTH 1 대댓글 여부 확인
        SupplementReply supplementReply = supplementReplyRepository.findByIdAndMember(supplementReplyId, member)
                .orElseThrow(() -> new EntityNotFoundException("not found SupplementReply : " + supplementReplyId));
        if(supplementReply.getParent() != null){
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
