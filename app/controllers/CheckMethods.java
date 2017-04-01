/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import play.*;
import play.mvc.*;
import play.data.*;
import java.util.*;

import flexjson.JSONSerializer;
import javax.persistence.Query;
import models.*;
import play.db.jpa.JPA;
import utils.YUtils;

/**
 *
 * @author Administrator
 */
public class CheckMethods extends Application{
     public static void index() {
        render();
    }
 
     
    /**
     * Get CheckMethod json.
     */
    public static void checkjson(long id, int page, int pagesize) {    	
    	List<CheckMethod> checkmethods = CheckMethod.find("checkList.id=? ", id).fetch();    						   
    	long total = CheckMethod.count("checkList.id=? ", id);    
    	String checkmethodStr = CheckMethod.toCheckJson(checkmethods);
    	String jsonStr = YUtils.ligerGridData(checkmethodStr, total);   	
    	renderJSON(jsonStr);
    }
    /**
     * save a CheckMethod.
     */
    public static void save(CheckMethod checkmethod) {  	
        checkmethod.save();
        renderJSON("");
    }
    
    public static void delete(long id) {
        CheckMethod.findById(id)._delete();
        renderJSON("");
    }
    
    public static void findMethod(long id) {
    	//List<CheckMethod> checkmethods = CheckMethod.find("checkList.id=? ", id).fetch(); 
         String sql = "select id,title,score from YDDA_CheckMethod where checkList_id="+id;            
         Query query = JPA.em().createNativeQuery(sql, BaseCheckMethod.class);
         List<BaseCheckMethod> lists = query.getResultList();
         renderJSON(lists);
    }
}
