package mandykr.nutrient.entity.combination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mandykr.nutrient.entity.util.BaseTimeEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CombinationReply extends BaseTimeEntity {
    @Transient public static final int MIN_ORDERS = 1;
    @Transient public static final int MAX_ORDERS = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMBINATION_REPLY_ID")
    private Long id;

    @Lob
    private String content;
    private long orders;
    private boolean deleteFlag = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMBINATION_ID")
    private Combination combination;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    private CombinationReply parent;

    @OneToMany(mappedBy = "parent")
    @Builder.Default
    private List<CombinationReply> childList = new ArrayList<>();

    public CombinationReply(Long id) {
        this.id = id;
    }

    public void changeContent(String content) {
        if (isValid(content)) {
            throw new IllegalArgumentException("유효하지 않은 댓글 내용입니다.");
        }
        this.content = content;
    }

    public boolean isValid(String content) {
        return content != null
                && !content.isEmpty()
                && !content.isBlank();
    }

    public void deleteLogical() {
        this.deleteFlag = true;
    }

    public boolean existChildren() {
        return !childList.isEmpty();
    }

    public boolean isPossiobleToDeletePhysical() {
        return !existChildren()
                || wereChildrenDeleted();
    }

    public boolean wereChildrenDeleted() {
        return childList.stream()
                .allMatch(r -> r.deleteFlag);
    }

    public void addChild(CombinationReply child1_1) {
        if (!childList.contains(child1_1)) {
            childList.add(child1_1);
        }
        child1_1.parent = this;
    }
}
