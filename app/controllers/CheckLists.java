package controllers;

import play.*;
import play.mvc.*;
import play.data.*;
import java.util.*;

import flexjson.JSONSerializer;
import models.*;
import utils.YUtils;


/**
 * Manage CheckLists related operations.
 */

public class CheckLists extends Application {
  
    public static void index() {
        render();
    }

    /**
     * Get CheckLists json.
     */
    public static void json(int page, int pagesize) {
    	
    	List<CheckList> checkLists = CheckList.find("parentId is null order by OrderNum")
    								 .fetch(page, pagesize);      	 
    	long total = CheckList.count("parentId is null");
    	
    	//GSON Used has circular reference error changed use flexgson 
    	//HashMap<String, Object> obj = new HashMap<String,Object>();
    	//obj.put("Rows",  checkLists);
    	//obj.put("Total", total);

    	String checkListStr = CheckList.toJson(checkLists);
    	String jsonStr = YUtils.ligerGridData(checkListStr, total);
    	renderJSON(jsonStr);
    }
    
    public static void simplejson() {
    	List<CheckList> checkLists = CheckList.find("order by OrderNum").fetch();     	 
    	renderJSON(CheckList.toSimpleJson(checkLists));
    }
    
    //取非根节点数据
    public static void leafjson(int page, int pagesize) {
    	List<CheckList> checkLists = CheckList.find(" size(checkLists) = 0  order by parentId, orderNum")
    								 .fetch(page, pagesize);    	 
    	long total = CheckList.count();

    	String checkListStr = CheckList.toLeafJson(checkLists);
    	String jsonStr = YUtils.ligerGridData(checkListStr, total);
    	renderJSON(jsonStr);
    }
    

    /**
     * Add a CheckList.
     */
    public static void add(CheckList checklist) {        
        
    }
    
    /**
     * Update a CheckList.
     */
    public static void save(CheckList checklist) {       
        checklist.save();
        renderJSON("");
    }
    
    /**
     * Delete a CheckList.
     */
    public static void delete(long id) {
        CheckList.findById(id)._delete();
        renderJSON("");
    }
  
}

