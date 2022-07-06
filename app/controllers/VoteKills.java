package controllers;

import play.*;
import play.mvc.*;
import play.data.*;
import java.util.*;
import models.*;

/**
 * Manage VoteKills related operations.
 */

public class VoteKills extends Controller {
    public static void index() {
        render();
    }

    /**
     * Get VoteKills json.
     */
    public static void json(int page, int pagesize) {
    	List<VoteKill> voteKills = VoteKill.all().fetch(page, pagesize);
    	long total = VoteKill.count();
    	HashMap<String,Object> obj = new HashMap<String,Object>();
    	obj.put("Rows", voteKills);
    	obj.put("Total", total);
    	renderJSON(obj);
    }

    /**
     * Add a VoteKill.
     */
    public static void add(VoteKill votekill) {        
        
    }
    
    /**
     * Update a VoteKill.
     */
    public static void save(VoteKill votekill) {       
        votekill.save();
        renderJSON("");
    }
    
    /**
     * Delete a VoteKill.
     */
    public static void delete(long id) {
        VoteKill.findById(id)._delete();
        renderJSON("");
    }
    
    public static void findAll() {
    	List<VoteKill> votekills = VoteKill.findAll();
    	renderJSON(votekills);
    }

   public static void findGoods(long id) {
        VoteKill votekill= VoteKill.findById(id);  
        String goodsstr = "[";
        String goodsVal = votekill.goods==null?"":votekill.goods;
        if (goodsVal.trim() != "") {
           String[] goodsarr = votekill.goods.toString().split("\\|");
           for(int i=0;i<goodsarr.length;i++){
              goodsstr=goodsstr+"{\"id\":"+i+",\"name\":\""+goodsarr[i]+"\"},";
           }
           goodsstr=goodsstr.substring(0,goodsstr.length()-1)+"]"; 
        }  else {
            System.out.println();
            goodsstr = "[]";
        }       
        renderJSON(goodsstr);
    }
}

