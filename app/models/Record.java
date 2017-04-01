package models;

import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;
import play.*;
import play.data.validation.MaxSize;
import play.db.jpa.*;
import javax.persistence.*;
import java.util.*;

@Entity 
@Table(name="YDDA_Record")
public class Record extends Model {
    
    public Integer year;    //年度

    @ManyToOne
    public Dept dept;
    
    @ManyToOne
    public User user;

    public Integer type;  //1: 个人档案  2:科室档案
    
    public Float score;  //最终得分

    @ManyToOne
    public Grade grade;    //考评结果 
   
    public String  conclusion;  //考评结论

    public Date  conclusionTime; //结论时间

    public String  remark;  //备注
    
    public String archiveMan; //归档人

    public Date archiveTime; //归档时间
    
    public String archiveRemark; //归档备注
    
    public Integer status; 	//0: 新建  1：归档
    
    public Integer checkout; //0: 在档  1：外借
    
    public Integer checkoutRemark; //外借备注
    
    public Blob attachment;  //电子版文件
    
    public boolean isVoteKill=false;  //是否含有一票否决
    
    
    public String toString() {
        return "";
    }
    
    @Transient
    public String gradeId;
    public String getGradeId() {
        if(grade!=null){
           return String.valueOf(grade.id);
        }else{
    	   return "-1";
        }
    }
    
    @Transient
    public String gradeName;
    public String getGradeName() {
        if(grade!=null){
           return grade.name;
        }
        else{
            return null;
        }
    }
    public static Record findByUser(User user,int year) {
       return find("byUserAndYear", user, year).first();
    }
    
    public static String toCheckJson(Record record) {
    	return new JSONSerializer()    	
    	  .transform(new DateTransformer("yyyy-MM-dd"), "conclusionTime")
    	  .include("id", "conclusionTime", "conclusion","remark","gradeId","gradeName","year","score","isVoteKill","status")
    	  .exclude("*")
    	  .serialize(record);
    }
}