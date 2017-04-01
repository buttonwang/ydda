package models;

import play.*;
import play.data.validation.MaxSize;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

/**
 * CheckLevel entity 
 * 评价层级:  自我评价， 科室评价， 单位评价， 群众评价
 * 群众评价 (不加入流程控制)
 */
@Entity 
@Table(name="YDDA_CheckLevel")
public class CheckLevel extends Model {

    public String name;

    public boolean allowRate = true;    //是否允许评分
    
    public String remark;
  
    public String toString() {
        return name;
    }

    public static CheckLevel findByName(String name) {
        return find("name", name).first();
    }
}
