package mandykr.nutrient.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Entity
@Getter
@NoArgsConstructor
public class SupplementReply {

    @Id
    @GeneratedValue
    @Column(name = "SUPPLEMENT_REPLY_ID")
    private Long id;

    private String content;

    private int orders;


    //DB 테이블이 필요~~
    //좋아요 / 싫어요 기능 추가 여부

    //삭제된 내역
    private Boolean deleteFlag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    private SupplementReply parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUPPLEMENT_ID")
    private Supplement supplement;

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<SupplementReply> children = new ArrayList<>();



    public SupplementReply(String content, SupplementReply parent,Supplement supplement) {
        //대댓글 생성
        this(null,content, parent.getOrders()+1,false, parent,supplement);
    }
    public SupplementReply(String content,Supplement supplement) {
        //처음 생성
        this(null,content, 1,false, null,supplement);
    }
    public SupplementReply(Long id,SupplementReply supplementReply) {
        //삭제 댓글
        this(id,supplementReply.getContent(), supplementReply.getOrders(),true, supplementReply.getParent(),supplementReply.getSupplement());
    }

    public SupplementReply(Long id, String content, int orders, boolean deleteFlag, SupplementReply parent,Supplement supplement) {
        //로직 검증
        //validation
        //contents는 비어있으면 안된다.
        /*
        checkArgument(
                isEmpty(content) || content.length() <= 1000,
                "contents length must be less than 1000 characters"
        );
        */
        this.id = id;
        this.content = content;
        this.orders = orders;
        this.deleteFlag = deleteFlag;
        this.parent = parent;
        this.supplement = supplement;
    }


    static public class Builder {
        private Long id;
        private String content;
        private int orders;
        private boolean deleteFlag;
        private SupplementReply parent;
        private Supplement supplement;

        public Builder(SupplementReply supplementReply) {
            this.id = supplementReply.id;
            this.content = supplementReply.content;
            this.orders = supplementReply.orders;
            this.deleteFlag = supplementReply.deleteFlag;
            this.parent = supplementReply.parent;
            this.supplement = supplementReply.supplement;
        }
        public SupplementReply build(){
            return new SupplementReply(
              id,
              content,
              orders,
                    deleteFlag,
                    parent,
              supplement
            );
        }
    }
}
