package controllers;

import play.*;
import play.mvc.*;
import play.data.*;
import java.util.*;
import javax.persistence.Query;
import models.*;
import play.db.jpa.JPA;


/**
 * Manage Depts related operations.
 */

public class Depts extends Controller {
  
    public static void index() {
        render();
    }

    /**
     * Get depts json.
     */
    public static void json(int page, int pagesize) {
    	
    	List<Dept> depts = Dept.all().fetch(page, pagesize);      	 
    	long total = Dept.count();
    	
    	HashMap<String,Object> obj = new HashMap<String,Object>();
    	obj.put("Rows", depts);
    	obj.put("Total", total);
    	
    	renderJSON(obj);
    }

    /**
     * Add a dept.
     */
    public static void add(Dept dept) {        
        
    }
    /**
     * Get depts json.
     */
    public static void findAll(long id) {
        List<Dept> depts = null;
        User user = connectedUser();
        long userRole = UserRole.getMaxRoleId(user);
        String deptstr="";
        if (userRole == 3) {                    //角色为科室医德考评小组,则限制科室列表  
                Query querydepts = JPA.em().createNativeQuery("select name  from YDDA_Dept where id='" + user.dept.id + "'");
                
                List listdept = querydepts.getResultList();
               
                if (listdept != null && listdept.size() >= 1) {
                      for (int i = 0; i < listdept.size(); i++) {
                          deptstr = deptstr + "'" + listdept.get(i).toString() + "',";
                      }
                     deptstr = deptstr.substring(0, (deptstr.length() - 1));

                     depts = Dept.find("name in (" + deptstr + ")").fetch();


               } else {
                     //depts = Dept.find("id=?", user.dept.id).fetch();
                    //如果没有查找到，就查询所有的部门，供选择
                     depts = Dept.findAll();
               }
        } else {
            depts = Dept.findAll();
        }
   
        //HashMap<String,Object> obj = new HashMap<String,Object>();
        renderJSON(depts);
        

    }
     /*
     * 根据名称来查找科室
     */
    public static void findByName(String key){
      List<Dept> lists = Dept.find("name like ?",  "%" + key + "%").fetch();
        renderJSON(lists);
    }
     static User connectedUser() {
        String userId = session.get("logged");
        return (userId==null) ? null : (User)User.findById(Long.parseLong(userId));
    }
    /**
     * Update a dept.
     */
    public static void save(Dept dept) {       
        dept.save();
        renderJSON("");
    }
    
    /**
     * Delete a dept.
     */
    public static void delete(long id) {
        Dept.findById(id)._delete();
        renderJSON("");
    }

}

