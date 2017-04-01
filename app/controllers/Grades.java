package controllers;

import play.*;
import play.mvc.*;
import play.data.*;
import java.util.*;
import models.*;

/**
 * Manage Grades related operations.
 */

public class Grades extends Controller {
  
    public static void index() {
        render();
    }

    /**
     * Get Grades json.
     */
    public static void json(int page, int pagesize) {
    	List<Grade> grades = null;
    	if (page==0) {
    		grades = Grade.findAll();
    	} else {    	
    		grades = Grade.all().fetch(page, pagesize);
    	}
    	long total = Grade.count();
    	
    	HashMap<String,Object> obj = new HashMap<String,Object>();
    	obj.put("Rows", grades);
    	obj.put("Total", total);
    	
    	renderJSON(obj);
    }

    /**
     * Add a Grade.
     */
    public static void add(Grade grade) {        
        
    }
    
    /**
     * Update a Grade.
     */
    public static void save(Grade grade) {       
        grade.save();
        renderJSON("");
    }
    
    /**
     * Delete a Grade.
     */
    public static void delete(long id) {
        Grade.findById(id)._delete();
        renderJSON("");
    }
  
}

