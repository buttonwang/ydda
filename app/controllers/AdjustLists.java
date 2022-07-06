package controllers;

import play.*;
import play.mvc.*;
import play.data.*;
import java.util.*;
import models.*;
//import net.sf.json.JSONObject;

/**
 * Manage AdjustLists related operations.
 */

public class AdjustLists extends Controller {
  
    public static void index() {
        render();
    }

    /**
     * Get AdjustLists json.
     */
    public static void json(int page, int pagesize) {

//        List<AdjustList> AdjustLists = AdjustList.find("order by OrderNum").fetch(page, pagesize); // 泸州 修改加分项目序号排序 by  LSY
        List<AdjustList> AdjustLists = AdjustList.find("order by id").fetch(page, pagesize);
    	long total = AdjustList.count();

    	HashMap<String,Object> obj = new HashMap<String,Object>();
    	obj.put("Rows", AdjustLists);
    	obj.put("Total", total);

    	renderJSON(obj);
    }

    /**
     * Add a AdjustList.
     */
    public static void add(AdjustList adjustlist) {        
        
    }
    
    /**
     * Update a AdjustList.
     */
    public static void save(AdjustList adjustlist) {       
        adjustlist.save();
        renderJSON("");
    }
    
    /**
     * Delete a AdjustList.
     */
    public static void delete(long id) {
        AdjustList.findById(id)._delete();
        renderJSON("");
    }
   
    public static void findAdjust(long id) {
         AdjustList adjust= AdjustList.findById(id);  	
         renderJSON(adjust);
    }

    public static void findGoods(long id) {
        AdjustList adjust= AdjustList.findById(id);  
          
        String goodsstr = "[";

        String goodsVal = adjust.goods==null?"":adjust.goods;

        if (goodsVal.trim() != "") {
           String[] goodsarr = adjust.goods.toString().split("\\|");
           for(int i=0;i<goodsarr.length;i++){
              goodsstr=goodsstr+"{\"id\":"+i+",\"name\":\""+goodsarr[i]+"\"},";
           }
           goodsstr=goodsstr.substring(0,goodsstr.length()-1)+"]"; 
        }  else 
        {
            goodsstr = "[]";
        }       
        
        renderJSON(goodsstr);
    }
}

