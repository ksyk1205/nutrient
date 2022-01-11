package mandykr.nutrient.service.combination;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.dto.combination.reply.CombinationReplyDto;
import mandykr.nutrient.entity.combination.Combination;
import mandykr.nutrient.entity.combination.CombinationReply;
import mandykr.nutrient.repository.combination.reply.CombinationReplyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CombinationReplyService {
    private final CombinationReplyRepository replyRepository;

    public Page<CombinationReplyDto> getCombinationReplyByCombination(long combinationId, Pageable pageable) {
        Page<CombinationReply> replyPageResult =
                replyRepository.findByCombinationAndOrders(
                        new Combination(combinationId),
                        CombinationReply.MIN_ORDERS,
                        pageable);
        return replyPageResult.map(CombinationReplyDto::of);
    }

    public Page<CombinationReplyDto> getCombinationReplyByParent(long combinationId, long parentId, Pageable pageable) {
        Page<CombinationReply> replyPageResult =
                replyRepository.findByCombinationAndParent(
                        new Combination(combinationId),
                        new CombinationReply(parentId),
                        pageable);
        return replyPageResult.map(CombinationReplyDto::of);
    }
}
