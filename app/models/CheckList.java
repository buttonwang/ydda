package models;

import play.*;
import play.data.validation.MaxSize;
import play.db.jpa.*;
import javax.persistence.*;

import flexjson.JSONSerializer;

import java.util.*;

/**
 * CheckList entity managed  考评项目
 */
@Entity 
@Table(name="YDDA_CheckList")
public class CheckList extends Model {

    public String title;

    public float score;

    public String orderNum;
       	
    @MaxSize(1000)
    @Column(name="remark", length=1000)
    public String remark;
    
    @ManyToOne
	@JoinColumn(name = "parent_id")       
	public CheckList parentId;

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
    public List<CheckList> checkLists;
  
    public String toString() {
        return title;
    }
    
    public static String toJson(List<CheckList> checkLists) {
    	return new JSONSerializer()
    	  .include("id",  "title", "score", "parentId", "orderNum", "remark", 
    			   //"checkLists.id", "checkLists.title", "checkLists.score", 
    			   //"checkLists.parentId", "checkLists.orderNum", "checkLists.remark" )
    			  "checkLists.*")
    	  .exclude("*")
    	  .serialize(checkLists);    	
    }
    
    public static String toSimpleJson(List<CheckList> checkLists) {
    	return new JSONSerializer()
    	  .include("id",  "title", "parent")
    	  .exclude("*")
    	  .serialize(checkLists);
    }
    
    public static String toLeafJson(List<CheckList> checkLists) {
    	return new JSONSerializer()
    	  .include("id",  "title", "score",  "orderNum", "parent", "parentTitle")
    	  .exclude("*")
    	  .serialize(checkLists);
    }
    public static List<CheckList> findBaseCheckList() {           
        return find("parentId=null").fetch();
    }
   
}