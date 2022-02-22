package mandykr.nutrient.entity.combination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mandykr.nutrient.entity.member.Member;
import mandykr.nutrient.entity.util.BaseTimeEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CombinationReply extends BaseTimeEntity {
    @Transient public static final int PARENT_ORDERS = 1;
    @Transient public static final int CHILD_ORDERS = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMBINATION_REPLY_ID")
    private Long id;

    @Lob
    private String content;
    private long orders;
    private boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMBINATION_ID")
    private Combination combination;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    private CombinationReply parent;

    @OneToMany(mappedBy = "parent")
    @Builder.Default
    private List<CombinationReply> childList = new ArrayList<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    public CombinationReply(Long id) {
        this.id = id;
    }

    public boolean isParent() {
        return orders == PARENT_ORDERS;
    }

    public boolean isChild() {
        return orders == CHILD_ORDERS;
    }

    public void changeContent(String content) {
        if (!isValid(content)) {
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
        this.deleted = true;
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
                .allMatch(r -> r.deleted);
    }

    public void addChild(CombinationReply child) {
        if (!childList.contains(child)) {
            childList.add(child);
        }
        child.parent = this;
    }
}
