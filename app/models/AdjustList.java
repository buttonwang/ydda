package models;

import play.*;
import play.data.validation.MaxSize;
import play.db.jpa.*;
import javax.persistence.*;
import java.util.*;

/**
 *  AdjustList managed  附加项目 加分，减分项目
 * score 加分或扣分的分值； award 奖金
 */

@Entity 
@Table(name="YDDA_AdjustList")
public class AdjustList extends Model {
    
    public String title;

    public String remark;

    public Float score;

    public Integer award;
    
    public Integer type = 1;  //1: 奖励  2：惩罚
    
    public Integer target = 0; //1: 科室  2：个人
    
    public Integer syncadjust = 0;  //1：同步奖惩  2：选择奖惩
    
    public Integer syncscore; //同步分值

    public Integer syncaward; //同步奖金
    
    public String orderNum;
    
    public String goods;      //物品
 
    public String toString() {
        return title;
    }

}