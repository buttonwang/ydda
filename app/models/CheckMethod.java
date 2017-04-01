package models;

import play.*;
import play.data.validation.MaxSize;
import play.db.jpa.*;
import javax.persistence.*;

import flexjson.JSONSerializer;

import java.util.*;

/**
 * CheckMethod entity managed  考评方法
 */
@Entity 
@Table(name="YDDA_CheckMethod")
public class CheckMethod extends Model {

	@ManyToOne   
	public CheckList checkList;
	    
    public String title;		//考评方法描述

    public Integer score;        //扣分分值
    
    public String formula;		//计算公式，用来扩展。

    public String orderNum;
       	
    @MaxSize(1000)
    @Column(name="remark", length=1000)
    public String remark;
    
    @Transient
    public String checkTitle;
    
    public String getCheckTitle() {
    	return checkList.title;
    }

    @Transient
    public long checkId;

    public long getCheckId() {
    	return checkList.id;
    }
    
    public String toString() {
        return title;
    }
    
    public static String toJson(List<CheckList> checkLists) {
    	return new JSONSerializer()
    	  .include("id",  "title", "score", "formula", "orderNum", "remark")
    	  .exclude("*")
    	  .serialize(checkLists);    	
    }
    
    public static String toSimpleJson(List<CheckList> checkLists) {
    	return new JSONSerializer()
    	  .include("id",  "title")
    	  .exclude("*")
    	  .serialize(checkLists);
    }
    
    public static String toLeafJson(List<CheckList> checkLists) {
    	return new JSONSerializer()
    	  .include("id",  "title", "score",  "orderNum")
    	  .exclude("*")
    	  .serialize(checkLists);
    }
    public static String toCheckJson(List<CheckMethod> checkmethods) {
    	return new JSONSerializer()
    	  .include("id",  "title", "score", "formula", "orderNum", "remark","checkTitle","checkId")
    	  .exclude("*")
    	  .serialize(checkmethods);    	
    }
    
}