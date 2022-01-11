package mandykr.nutrient.entity.combination;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombinationReplyTest {

    @DisplayName("댓글 내용으로 유효하지 않은 문자열에 대해 false를 리턴한다.")
    @ParameterizedTest(name = "{index} name={0}")
    @ValueSource(strings = {" "})
    @NullSource
    @EmptySource
    void isValid(String content) {
        CombinationReply combinationReply = CombinationReply
                .builder()
                .content("content")
                .orders(1)
                .build();

        assertFalse(combinationReply.isValid(content));
    }

    @Test
    @DisplayName("댓글 물리 삭제가 가능한 경우 true, 불가능한 경우 false를 리턴한다.")
    void isPossiobleToDeletePhysical() {
        // given
        CombinationReply parentHaveChildren = CombinationReply.builder().content("parentHaveChildren").orders(1L).build();
        parentHaveChildren.addChild(CombinationReply.builder().content("child1_1").orders(2L).parent(parentHaveChildren).build());
        parentHaveChildren.addChild(CombinationReply.builder().content("child1_2").orders(2L).parent(parentHaveChildren).build());

        CombinationReply parentAllDeletedChildren = CombinationReply.builder().content("parentAllDeletedChildren").orders(1L).build();
        parentAllDeletedChildren.addChild(CombinationReply.builder().content("child2_1").orders(2L).parent(parentAllDeletedChildren).deleteFlag(true).build());
        parentAllDeletedChildren.addChild(CombinationReply.builder().content("child2_2").orders(2L).parent(parentAllDeletedChildren).deleteFlag(true).build());

        CombinationReply parentNoChildren = CombinationReply.builder().content("parentNoChildren").orders(1L).build();

        // when
        // then
        assertFalse(parentHaveChildren.isPossiobleToDeletePhysical());
        assertTrue(parentAllDeletedChildren.isPossiobleToDeletePhysical());
        assertTrue(parentNoChildren.isPossiobleToDeletePhysical());
    }
}