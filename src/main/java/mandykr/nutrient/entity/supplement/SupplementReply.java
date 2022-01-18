package mandykr.nutrient.entity.supplement;

import lombok.*;
import mandykr.nutrient.entity.Member;
import mandykr.nutrient.entity.util.BaseTimeEntity;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class SupplementReply extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "SUPPLEMENT_REPLY_ID")
    private Long id;

    private String content;

    private Long groups;

    private Long groupOrder;

    //DB 테이블이 필요~~
    //좋아요 / 싫어요 기능 추가 여부

    //삭제된 내역
    private Boolean deleteFlag;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "PARENT_ID")
    private SupplementReply parent;

    @OneToMany(mappedBy = "parent")
    @Builder.Default
    private List<SupplementReply> child = new ArrayList<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "SUPPLEMENT_ID")
    private Supplement supplement;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    public void changeContent(String content){
        this.content = content;
    }

    public void changeTrueDeleteFlag(){
        this.deleteFlag = true;
    }

    public void addParents(SupplementReply parent){
        this.parent = parent;
        if(!parent.getChild().contains(this)){
            parent.getChild().add(this);
        }
    }

    public void removeChild(SupplementReply supplementReply) {
        child.remove(supplementReply);
    }
}
