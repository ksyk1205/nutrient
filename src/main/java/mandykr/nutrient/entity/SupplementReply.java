package mandykr.nutrient.entity;

import lombok.Getter;
import lombok.Setter;
import mandykr.nutrient.dto.SupplementReplyDto;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
public class SupplementReply {

    @Id
    @GeneratedValue
    @Column(name = "SUPPLEMENT_REPLY_ID")
    private Long id;

    private String content;

    private Long groupId;

    private Long orders;

    @Column(name = "CREATE_AT")
    private LocalDateTime createAt;
    @Column(name = "UPDATE_AT")
    private LocalDateTime updateAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUPPLEMENT_ID")
    private Supplement supplement;


    //@ManyToOne
    //private Member member;

    //==생성 메소드 ==//
    public static SupplementReply makeSupplementReply(Long id,String content,Supplement supplement){
        //뒤에 생성되는곳만 바꾸기 위해서는 여기만 바꾸면 된다.
        SupplementReply supplementReply= new SupplementReply(id,content,supplement);
        return supplementReply;
    }
    //==업데이트 메소드==//



    protected SupplementReply(){

    }
    protected SupplementReply(Long id,String content,Supplement supplement){
        this.id = id;
        this.supplement = supplement;
        this.content = content;
    }

}
