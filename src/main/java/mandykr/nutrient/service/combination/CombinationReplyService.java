package mandykr.nutrient.service.combination;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.combination.reply.CombinationReplyCreateFormDto;
import mandykr.nutrient.dto.combination.reply.CombinationReplyDto;
import mandykr.nutrient.dto.combination.reply.CombinationReplyUpdateFormDto;
import mandykr.nutrient.entity.combination.Combination;
import mandykr.nutrient.entity.combination.CombinationReply;
import mandykr.nutrient.repository.combination.reply.CombinationReplyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CombinationReplyService {
    private final CombinationReplyRepository replyRepository;

    public Page<CombinationReplyDto> getParentsReplyByCombination(long combinationId, Pageable pageable) {
        Page<CombinationReply> replyPageResult =
                replyRepository.findByCombinationAndOrders(
                        new Combination(combinationId),
                        CombinationReply.PARENT_ORDERS,
                        pageable);
        return replyPageResult.map(CombinationReplyDto::of);
    }

    public Page<CombinationReplyDto> getChildrenReplyByParent(long combinationId, long parentId, Pageable pageable) {
        Page<CombinationReply> replyPageResult =
                replyRepository.findByCombinationAndParent(
                        new Combination(combinationId),
                        new CombinationReply(parentId),
                        pageable);
        return replyPageResult.map(CombinationReplyDto::of);
    }

    public CombinationReplyDto createReply(CombinationReplyCreateFormDto dto) {
        return CombinationReplyDto.of(replyRepository.save(dto.createReplyEntity()));
    }

    public CombinationReplyDto updateReply(CombinationReplyUpdateFormDto dto) {
        CombinationReply findReply = getReply(dto.getId());
        findReply.changeContent(dto.getContent());

        return CombinationReplyDto.of(findReply);
    }

    public Page<CombinationReplyDto> getParentOrChildReplyList(CombinationReplyDto replyDto, Pageable pageable) {
        Page<CombinationReplyDto> resultReplyPage = null;
        CombinationReply reply = CombinationReplyDto.createReplyEntity(replyDto);
        if (reply.isParent()) {
            resultReplyPage = getParentsReplyByCombination(reply.getCombination().getId(), pageable);
        }
        else {
            resultReplyPage = getChildrenReplyByParent(
                    reply.getCombination().getId(), reply.getParent().getId(), pageable);
        }
        return resultReplyPage;
    }

    public void deleteReply(Long replyId) {
        CombinationReply findReply = getReply(replyId);
        if (findReply.isPossiobleToDeletePhysical()) {
            replyRepository.deleteById(replyId);

            replyRepository.deleteAllByIdInBatch(
                    findReply.getChildList().stream()
                            .map(CombinationReply::getId)
                            .collect(Collectors.toList()));
        } else {
            findReply.deleteLogical();
        }
    }

    public CombinationReply getReply(Long replyId) {
        return replyRepository.findById(replyId)
                .orElseThrow(() -> new EntityNotFoundException("not found category: " + replyId));
    }

    public CombinationReplyDto getReplyDto(Long replyId) {
        return CombinationReplyDto.of(getReply(replyId));
    }
}
