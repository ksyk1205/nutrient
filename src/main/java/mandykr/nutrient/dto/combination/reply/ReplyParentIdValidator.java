package mandykr.nutrient.dto.combination.reply;

import mandykr.nutrient.entity.combination.CombinationReply;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ReplyParentIdValidator implements ConstraintValidator<ReplyParentIdConstraint, CombinationReplyFormDto> {

    @Override
    public void initialize(ReplyParentIdConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(CombinationReplyFormDto value, ConstraintValidatorContext context) {
        if (value.getOrders() == CombinationReply.CHILD_ORDERS) {
            return value.getParentId() != null;
        }
        return true;
    }
}
