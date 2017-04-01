package models;

import play.*;
import play.data.validation.MaxSize;
import play.db.jpa.*;
import javax.persistence.*;
import java.util.*;

/**
 *  VoteKill managed by Ebean  一票否决
 * 
 */
@Entity 
@Table(name="YDDA_VoteKill")
public class VoteKill extends Model {

    public String title;
    
    public String remark;

    public String orderNum;
    
    public String goods;  //物品
    
    @ManyToOne
    public Grade grade;
    
    public String toString() {
        return title;
    }
    
}