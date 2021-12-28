package mandykr.nutrient.entity;

import lombok.*;

import javax.persistence.*;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

    @OneToMany(mappedBy = "parent")
    @Builder.Default
    private List<SupplementReply> child = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUPPLEMENT_ID")
    private Supplement supplement;

}
