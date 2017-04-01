package models;

import play.*;
import play.data.validation.MaxSize;
import play.db.jpa.*;
import javax.persistence.*;
import java.util.*;

/**
 * Grade entity managed by 
 * 考核结果  优秀 良好 一般 较差
 * upperLimit 上限 lowLimit下限 如 80-94分为良好
 * reduce 减分情况 如 减分超过20，总评为差
 */
@Entity 
@Table(name="YDDA_Grade")
public class Grade extends Model {

    public String name;
    
    public Integer upperLimit;

    public Integer lowLimit;

    public Integer reduce;

    public String remark;

    public String toString() {
        return name;
    }
    public static Grade findByGradeName(String name) {
        return find("name", name).first();
    }
}

