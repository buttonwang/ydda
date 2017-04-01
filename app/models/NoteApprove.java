package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name="YDDA_NoteApprove")
public class NoteApprove extends Model {
    
	@ManyToOne
	public Note note;	//医德记事
	
	@ManyToOne
    public User approveMan; //审批人

    public Date approveDate; //审批时间

    public Integer level;  //审批层级
    
    public Integer status = 1; 	//审批状态 1:确认 2：退回
    
    public String comment; 	//审批批注

    @ManyToOne
    public CheckMethod checkMethod;

    public Integer score;
    
}