package mandykr.nutrient.entity.combination;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;

class CombinationReplyTest {

    @DisplayName("문자열에 대한 예외처리를 확인한다.")
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
}