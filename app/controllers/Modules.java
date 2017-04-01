/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
 *
 * @author Administrator
 */
public class Modules
//extends Controller{ 
extends Application {
    public static void index() {
        render();
    }
    public static void warrant() {
        render();
    }
    public static void json(int page, int pagesize) {
    	
    	List<Module> modules = Module.all().fetch(page, pagesize);      	 
    	long total = Module.count();
        String modulestr= Module.toJson(modules);
        String jsonStr = YUtils.ligerGridData(modulestr, total);
        renderJSON(jsonStr);
   }
   /*  public static void json(int page, int pagesize) {
    	
    	List<Role> modules = Module.all().fetch(page, pagesize);      	 
    	long total = Module.count();
    	
    	HashMap<String,Object> obj = new HashMap<String,Object>();
    	obj.put("Rows", modules);
    	obj.put("Total", total); 	
    	renderJSON(obj);
    }*/

    
   public static void save(Module module) {   
        module.save();
        renderJSON("");
    }
   
    /**
     * Delete a Module.
     */
    public static void delete(long id) {
        Module.findById(id)._delete();
        renderJSON("");
    }
    
     public static void getModingByRole(long id,int page,int pagesize){
        // Role role=Role.findById(id);
        // List<RoleModule> rm=RoleModule.findByRole(role);
       // String notesStr = Note.toCheckJson(notes);
       // String jsonStr = YUtils.ligerGridData(notesStr, total);
       // renderJSON(jsonStr);
       // List<Dept> depts = Dept.all().fetch(page, pagesize);      	 
       // long total = Dept.count();
        // long total = rm.size();
    	
    	//HashMap<String,Object> obj = new HashMap<String,Object>();
    	//obj.put("Rows", rm);
    	//obj.put("Total", total);	
    	//renderJSON(obj);
        String  sql="select "+id+" as roleid,id,name,remark from AX_Module WHERE NOT EXISTS (SELECT 1 FROM AX_RoleModule where module_id=AX_Module.id and role_id="+id+")";                   
        Query query = JPA.em().createNativeQuery(sql);
        List Modinglist = query.getResultList();
        int total = Modinglist.size();
        
        HashMap<String,Object> obj = new HashMap<String,Object>();
    	obj.put("Rows", Modinglist);
    	obj.put("Total", total);	
    	renderJSON(obj);
        
    }   
    public static void getModedByRole(long id,int page,int pagesize){
        
        String  sql="select B.role_id AS roleid,A.id,A.name,A.remark from AX_Module A inner join AX_RoleModule B on (A.id=B.module_id) where B.role_id= "+id;
        Query query = JPA.em().createNativeQuery(sql);
        List Modedlist = query.getResultList();
        int total = Modedlist.size();
        
        HashMap<String,Object> obj = new HashMap<String,Object>();
    	obj.put("Rows", Modedlist);
    	obj.put("Total", total);	
    	renderJSON(obj);
        
        
    }
    
    public static void addRoleModule(long id,long roleid){
         Role role=Role.findById(roleid);
         Module module=Module.findById(id);
         RoleModule rm=new RoleModule(role,module);     
         rm.save();
         renderJSON("");
    }
    public static void delRoleModule(long id,long roleid){
         RoleModule.findByRoleMoule(id,roleid)._delete();
         renderJSON("");
    }
    
    public static void showModuleByUser(){
         renderJSON(Module.showModuleByUser(connectedUser()));
    }
}
