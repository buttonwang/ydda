package models;

import play.*;
import play.data.validation.MaxSize;
import play.db.jpa.*;
import javax.persistence.*;

import flexjson.JSONSerializer;

import java.util.*;

/**
 * SupervisionList entity managed  权利监控项目
 */
@Entity 
@Table(name="YDDA_SupervisionList")
public class SupervisionList extends Model {

    public String title;

    public float score;

    @MaxSize(3000)
    public String evaluationCriteria;  //考评标准

    @MaxSize(1000)
    public String evaluationMethod;   //考评办法

    @MaxSize(100)
    public String evaluationObjects;  //考评对象

    @MaxSize(3000)
    public String ratingCriteria;     //评分标准

    public String orderNum;
       	
    @MaxSize(1000)
    @Column(name="remark", length=1000)
    public String remark;
    
    @ManyToOne
	@JoinColumn(name = "parent_id")       
	public SupervisionList parentId;

    @Transient
    public long parent;

    public long getParent() {
        return (parentId == null)? 0:parentId.id;
    }
    
    @Transient
    public String parentTitle;

    public String getParentTitle() {
        return (parentId == null)? "":parentId.title;
    }
    
    @OneToMany(mappedBy = "parentId")
    public List<SupervisionList> supervisionLists;
  
    public String toString() {
        return title;
    }
    
    public static String toJson(List<SupervisionList> supervisionLists) {
    	return new JSONSerializer()
    	  .include("id",  "title", "score", "parentId", "orderNum", "remark", 
    			   "evaluationCriteria", "evaluationMethod", "ratingCriteria",
    			   "supervisionLists.*")
    	  .exclude("*")
    	  .serialize(supervisionLists);    	
    }
    
    public static String toSimpleJson(List<SupervisionList> supervisionLists) {
    	return new JSONSerializer()
    	  .include("id",  "title", "parent")
    	  .exclude("*")
    	  .serialize(supervisionLists);
    }
    
    public static String toLeafJson(List<SupervisionList> supervisionLists) {
    	return new JSONSerializer()
    	  .include("id",  "title", "score",  "orderNum", "parent", "parentTitle")
    	  .exclude("*")
    	  .serialize(supervisionLists);
    }

    public static List<SupervisionList> findBaseCheckList() {           
        return find("parentId=null").fetch();
    }
   
}