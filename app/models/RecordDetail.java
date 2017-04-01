package models;

import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;
import play.*;
import play.data.validation.MaxSize;
import play.db.jpa.*;
import javax.persistence.*;
import java.util.*;

/**
 * RecordDetail  考评情况
 * 
 */
@Entity 
@Table(name="YDDA_RecordDetail")
public class RecordDetail extends Model {
    
    @ManyToOne
    public Record record;

    @ManyToOne
    public User user;      //评价人

    @ManyToOne
    public CheckLevel checkLevel;   //评价等级 

    public Float score;  //得分

    public Float plus;   //加分
            
    public Float reduce; //扣分
    
    public String plusCause;  //加分理由
    
    public String reduceCause; //扣分理由

    public String remark; //备注

    public Date recordTime;  //评价时间

   // public Integer agreeNumber;   //同意人数

   // public Integer notAgreeNumber;//不同意人数

   // public String  notAgreeCause; //不同意原因
    
    public String  signatory;    //签名人
    
    public Integer suifangBY;      //随访表扬次数
    
    public Integer suifangPP;      //随访批评次数
    
    public Integer ydyfBY;         //医德医风触摸屏表扬次数
    
    public Integer ydyfPP;         //医德医风触摸屏批评次数
    
    public String toString() {
        return "";
    }

    public static List<RecordDetail> findByRecordList(Record record,CheckLevel checkLevel) { 
        return find("byRecordAndCheckLevel", record,checkLevel).first();
    }

    public static RecordDetail findByRecord(Record record, CheckLevel checkLevel) {
        return find("byRecordAndCheckLevel", record,checkLevel).first();
    }
    
    public static RecordDetail findByRecordAndCheckLevelName(Record record, String checkLevelName) {
        CheckLevel checkLevel = CheckLevel.findByName(checkLevelName);
        return find("byRecordAndCheckLevel", record, checkLevel).first();
    }

    public static String toCheckJson(RecordDetail  model) {
    	return new JSONSerializer()    	
    	  .transform(new DateTransformer("yyyy-MM-dd"), "recordTime")
    	//  .include("id", "score", "remark", "recordTime","agreeNumber","notAgreeNumber","notAgreeCause","signatory")
          .include("id", "score", "remark", "recordTime","suifangBY","suifangPP","ydyfBY","ydyfPP","signatory")
    	  .exclude("*")
    	  .serialize(model);
    }

}