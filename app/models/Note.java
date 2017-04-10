package models;

import play.*;
import play.data.validation.MaxSize;
import play.db.jpa.*;
import javax.persistence.*;

import flexjson.JSONSerializer;
import flexjson.transformer.BasicDateTransformer;
import flexjson.transformer.DateTransformer;

import java.util.*;

/**
 * Note managed  档案记事
 * status: 0:新建  1：科室审批   2：院部审批 
 */
@Entity 
@Table(name="YDDA_Note")
public class Note extends Model {
    
    @ManyToOne
    public User user;
    
    @ManyToOne
    public Dept dept;

    @ManyToOne
    public CheckList checkList;

    @ManyToOne
    public AdjustList adjustList;

    @ManyToOne
    public VoteKill voteKill;

    public Date noteDate;   //发生日期
    
    public Float score;   //分数

    public String content;  //事项

    @ManyToOne
    public User createdMan; //记录人

    public Date createdDate = new Date(); //记录时间
    
    public Date updatedDate; //更新时间
    
    public String certifyMan;	//证明人

    public Integer approveLevel = 0;  //审批层级

    public String noteYear;  //年度
   
    public String approveComment;  //审核意见
    
    public String fileSrc;    //照片路径   医德档案记录的文件存证
    
    @Transient
    public String approveLevelName;
    
    public String getApproveLevelName() {
        String approveNameStr="审核失败";
        if(approveLevel != null && !(approveLevel.equals(""))){
    	if (approveLevel==0) return "新建";
    	else if (approveLevel==1) return "科室审核通过";
        else if (approveLevel==2) return "存档";
        else if (approveLevel==7) return "科室审核未通过";
    	else if (approveLevel==8) {          
             Role role=Role.findById(4);
             if(role!=null){
                 approveNameStr=role.name+"审核未通过";
             }        
             return approveNameStr;
        } 
     	else return "";
        }
        else return "";
    }
    
    @Transient
    public String noteTarget;
    
    public String getNoteTarget() {
        //if (user !=null) return user.name;
        if (user !=null) return user.realName;
    	else if (dept != null) return dept.name;
    	else return "";
    }
    
    
    @Transient
    public String checkTitle;
    
    public String getCheckTitle() {     
        if(checkList!=null){
    	     return checkList.title;
        }
        else if(adjustList!=null){
            return adjustList.title;
        }else if(voteKill!=null){
            return voteKill.title;
        } else{
            return "";
        }
    }

    @Transient
    public long checkId;

    public long getCheckId() {
       
        if(checkList!=null){
    	    return checkList.id;
        }else if(adjustList!=null){
            return adjustList.id;
        }else if(voteKill != null){
            return voteKill.id;
        }else
        {
            return 0;
        }
    }

    public String toString() {
        return "";
    }
    
    public static String defaultSortName() {
    	return " noteDate, createdDate desc ";
    }
   
    @Transient
    public long userId;

    public long getUserId() {
        if( user != null) return user.id;
        else return 0;
    }
    @Transient
    public long deptId;

    public long getDeptId() {
        if(dept!=null){
           return dept.id;
        }else{
    	   return -1;
        }
    }

    @Transient
    public String realName;

    public String getRealName() {
    	if(user != null) return user.realName;
    	else return "";
    }
    
    @Transient
    public String deptName;

    public String getDeptName() {
        if(dept!=null){
           return dept.name;
        }else{
    	   return "";
        }
    }
   
    public Integer   category = 1;  //档案分类,默认是基础考评
    //public String  keywords;         //
    public String    goods;         //物品
    
    @Transient
    public String createdManName;

    public String getCreatedManName() {
        if(createdMan!=null){
           return createdMan.realName;
        }else{
    	   return "";
        }
    }
    
    @Transient
    public String categoryName;
    
    public String getCategoryName() {
    	//if (category==1) { return "基础考评"; }
    	if (category==1) { return "减分考评"; }
        else if (category==2) { return "加分项目"; }
        else if (category==3) { return "一票否决"; }
 	
    	else return "";
    }
    
    public static String toCheckJson(List<Note> notes) {
        return new JSONSerializer()    	
    	  .transform(new DateTransformer("yyyy-MM-dd"), "noteDate")
    	  .include("id","noteYear", "noteDate", "content", "fileSrc", "certifyMan","approveLevel", "approveLevelName","checkTitle","checkId","userId","deptId","realName","deptName","category","categoryName","approveComment","score","goods","createdManName")
    	  .exclude("*")
    	  .serialize(notes);
    }
    
    public static String toApproveJson(List<Note> notes) {
    	return new JSONSerializer()
    	  .transform(new DateTransformer("yyyy-MM-dd"), "noteDate")
    	  .include("id", "realName", "checkTitle", "noteDate", "content", "fileSrc", "approveLevel", "approveLevelName","certifyMan","deptName","categoryName","approveComment","score","goods","createdManName")
    	  .exclude("*")
    	  .serialize(notes);
    }

    // 返回一票否决档案
    public static Note findVoteKillNote(long userid, String checkYear) {           
       return find(" user.id=? and noteYear=? and approveLevel=2 and category=3", userid, checkYear).first();
    }
}