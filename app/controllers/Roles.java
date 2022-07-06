package controllers;

import play.*;
import play.mvc.*;
import play.data.*;
import java.util.*;
import javax.persistence.Query;
import models.*;
import play.db.jpa.JPA;
import utils.YUtils;

/**
 * Manage Roles related operations.
 */

public class Roles extends Controller {
  
    public static void index() {
        render();
    }
    public static void warrant() {
        render();
    }
    /**
     * Get Roles json.
     */
    public static void json(int page, int pagesize) {
    	
    	List<Role> roles = Role.all().fetch(page, pagesize);      	 
    	long total = Role.count();
    	
    	HashMap<String,Object> obj = new HashMap<String,Object>();
    	obj.put("Rows", roles);
    	obj.put("Total", total);
    	
    	renderJSON(obj);
    }

    /**
     * Add a Role.
     */
    public static void add(Role role) {        
        
    }

    /**
     * Get Roles json.
     */
    public static void findAll() {    	
    	List<Role> roles = Role.findAll();      	 
    	renderJSON(roles);
    }

    /**
     * Update a Role.
     */
    public static void save(Role role) {       
        role.save();
        renderJSON("");
    }
    
    /**
     * Delete a Role.
     */
    public static void delete(long id) {
        Role.findById(id)._delete();
        renderJSON("");
    }
   
     public static void getRolingByUser(long id,int page,int pagesize){
        
       // String  sql="select "+id+" as roleid,id,name,remark from AX_Module WHERE NOT EXISTS (SELECT 1 FROM AX_RoleModule where module_id=AX_Module.id and role_id="+id+")";                   
        String  sql="select "+id+" as userid,id,name,remark  from AX_Role  WHERE NOT EXISTS (SELECT 1 FROM AX_UserRole where AX_Role.id=AX_UserRole.role_id  and AX_UserRole.user_id="+id+")";
        Query query = JPA.em().createNativeQuery(sql);
        List rolingList = query.getResultList();
        int total = rolingList.size();
        
        HashMap<String,Object> obj = new HashMap<String,Object>();
    	obj.put("Rows",  rolingList);
    	obj.put("Total", total);	
    	renderJSON(obj);
        
    }
    public static void getRoledByUser(long id,int page,int pagesize){
            
        String sql="  select B.user_id AS userid,A.id,A.name,A.remark from AX_Role A inner join AX_UserRole B on (A.id=B.role_id) where B.user_id= "+id;     
        Query query = JPA.em().createNativeQuery(sql);
        List roledList = query.getResultList();
        int total = roledList.size();
        
        HashMap<String,Object> obj = new HashMap<String,Object>();
    	obj.put("Rows",  roledList);
    	obj.put("Total", total);	
    	renderJSON(obj);  
        
    }
    
    public static void addUserRole(long id,long userid){
         Role role=Role.findById(id);
         User user=User.findById(userid);
         UserRole ur=new UserRole(user,role);
         ur.save();
         System.out.println(userid);
         renderJSON("");
    }
    
    public static void delUserRole(long id,long userid){
         UserRole.findByUserRole(id,userid)._delete();
         renderJSON("");
    }


    
}
