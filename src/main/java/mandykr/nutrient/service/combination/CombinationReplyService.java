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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CombinationReplyService {
    private final CombinationReplyRepository replyRepository;

    public Page<CombinationReplyDto> getParentReplyByCombination(long combinationId, Pageable pageable) {
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

    public CombinationReply createReply(CombinationReplyCreateFormDto dto) {
        return replyRepository.save(dto.createReply());
    }

    public CombinationReply updateReply(CombinationReplyUpdateFormDto dto) {
        CombinationReply findReply = replyRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("not found category: " + dto.getId()));
        findReply.changeContent(dto.getContent());

        return findReply;
    }
}
