package controllers;

import play.*;
import play.mvc.*;
import play.data.*;
import java.util.*;

import flexjson.JSONSerializer;
import models.*;
import utils.YUtils;


/**
 * Manage Records related operations.
 */

public class Records extends Application {
  
    public static void index() {
        render();
    }

    /**
     * Get Records json.
     */
    public static void json(int page, int pagesize) {    	
    	renderJSON("");
    }


    /**
     * Add a Record.
     */
    public static void add(Record record) {        
        
    }
    
    /**
     * Update a Record.
     */
    public static void save(Record record) {    	
    	//record.updatedDate = new Date();
        record.save();
        renderJSON("");
    }
    
    /**
     * Delete a Record.
     */
    public static void delete(long id) {
        Record.findById(id)._delete();
        renderJSON("");
    }

	public static void simplejson() {
	
	}
	
	/**
     * Approve Record.
     */
    public static void approve(int type) {        
        render();
    }   
    
}

