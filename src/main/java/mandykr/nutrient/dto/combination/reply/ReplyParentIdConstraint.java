package mandykr.nutrient.dto.combination.reply;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ReplyParentIdValidator.class)
@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ReplyParentIdConstraint {
    String message() default "자식 댓글을 생성하는 경우 parentId가 존재해야 합니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
