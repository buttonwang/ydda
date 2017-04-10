package controllers;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import javax.persistence.Query;
import models.Dept;

import models.Note;
import models.User;
import models.UserRole;
import play.Play;
import play.db.jpa.JPA;
import play.libs.Images;
import play.mvc.Controller;
import utils.YUtils;

/**
 * Manage Users related operations.
 */

public class Users extends Controller {
    public static void index() {
        render();
    }

    /**
     * Get Users json.
     */
    public static void json(int page, int pagesize) {

    	List<User> list = User.all().fetch(page, pagesize);      	 
    	long total = User.count();
    	
    	String notesStr = User.toCheckJson(list);
    	String jsonStr = YUtils.ligerGridData(notesStr, total);
    	
    	renderJSON(jsonStr);
    }

    /**
     * Add a User.
     */
    public static void add(User user) {
        
    }
    
    /**
     * Update a User.
     */
    public static void save(User user) {       //保证用户名唯一
        user.initPassword();
        String message="";
        if(user.id!=null){                         //修改
            User u=User.findByName(user.name);
            if(u!=null){
                 if(u.id!=user.id){
                     message="-1";
                 }
            }
        }else{                                     //增加
            User u=User.findByName(user.name);
            if(u!=null){
                    message="-1";
            }
        }
        if(!"-1".equals(message)){
      
            user.save();
            renderJSON("");
        } else{
            renderJSON("-1");   
        }
    }
    
    /**
     * Delete a User.
     */
    public static void delete(long id) {
        User.findById(id)._delete();
        renderJSON("");
    }
    
    /**
     * Update a User.
     */
    public static int list() {
        List<User> list =User.findAll();        
        return  list.size();
    }
    
  
    
    public static void multiRow(String selecteds) {
        String[] selarr=selecteds.split(",");
        int len=selarr.length;
        for(int i=0;i<len;i++){
            long id=Integer.parseInt(selarr[i]);
            Note note = Note.findById(id);
            note.approveLevel = note.approveLevel + 1;
            note.save();    
        }     
        renderJSON("");
    }

    public static void findAll() {
        List<User> lists = null;
        User user = connectedUser();
        long userRole = UserRole.getMaxRoleId(user);
        String deptstr = "";
        if (userRole == 3) {                                  //角色为科室医德考评小组,则限制科室列表          
                Query querydepts = JPA.em().createNativeQuery("select name  from YDDA_Dept where id='" + user.dept.id + "'");
                List listdept = querydepts.getResultList();
                if(listdept!=null && listdept.size()>=1){
                        for (int i = 0; i < listdept.size(); i++) {
                            deptstr = deptstr + "'" + listdept.get(i).toString() + "',";
                        }
                        deptstr = deptstr.substring(0, (deptstr.length() - 1));
                        lists = User.find("dept.name in (" + deptstr + ")").fetch();
            
                } else {
                       lists = User.find("dept=? ", user.dept).fetch();
                }
            
        } else {
                lists = User.findAll();
        }
        renderJSON(lists);
        
         // List<User> lists = User.findAll(); renderJSON(lists);
         
    }

    static User connectedUser() {
        String userId = session.get("logged");
        return (userId==null) ? null : (User)User.findById(Long.parseLong(userId));
    }
    
    // 根据名字查询医生信息
    public static void queryUser(String name) {

        //查询的时候也要根据角色来限制权限
        User logUser = connectedUser();
        long userRole = UserRole.getMaxRoleId(logUser);
        Dept userDept = logUser.dept;
        String deptstr="";
        User user = null;
        //主要限制医生和科室主任（赤峰二院角色为科室主任）

        if (userRole == 3) {   //角色为科室医德考评小组      
            
                Query querydepts = JPA.em().createNativeQuery("select name from YDDA_Dept where director='" + logUser.realName + "'");
                List listdept = querydepts.getResultList();
                if (listdept != null && listdept.size() > 1) {
                      for (int i = 0; i < listdept.size(); i++) {
                          deptstr = deptstr + "'" + listdept.get(i).toString() + "',";
                      }
                     deptstr = deptstr.substring(0, (deptstr.length() - 1));
                     user = User.findByRealNameDepts(name, deptstr);

               }else{
                      user = User.findByRealNameDept(name, userDept);
                }           
                       
        } else if (userRole == 2) {
            user = logUser;
        } else {
            user = User.findByRealName(name);
        }
        
        if (name.equals("个人考评")) {
            user = logUser;
        }
        
        String userStr = User.toTransformJson(user);
        renderJSON(userStr);
    }

    //人员维护：根据用户名、姓名、科室查询
    public static void queryjson(String name,String type) {
        List<User> list=null;
    	if(name==null || name.equals("")){
           list = User.all().fetch();      
        }else{
            if("1".equals(type)){
                  list = User.findByNameList("%"+name+"%");  
            }else if("2".equals(type)){
                  list = User.findByRealNameList("%"+name+"%"); 
            } else{
                  list = User.findByDeptList("%"+name+"%");  
            }
        }     
        long total = list.size();  	
    	String notesStr = User.toCheckJson(list);    
        String jsonStr = YUtils.ligerGridData(notesStr, total);  	
    	renderJSON(jsonStr);
        
    }
    
    //个人信息修改：查找登陆者
    public static void  findLogin() {
        User user = connectedUser(); 
        String userStr = user.toTransformJson(user);
        renderJSON(userStr);
    }
   
    public static void  findLoginr() {
        User user = connectedUser(); 
        long  roleid=UserRole.getMaxRoleId(user);
        renderJSON(roleid);
    }
    
    // 重置用户密码
    public static void  resetPass(long id){
        User user=User.findById(id);
        user.resetPass();
        user.save();
    }
    
    //上传照片
    public static void imageUpload(File attachment,long userId){         
        User user = connectedUser();
        long  roleid=UserRole.getMaxRoleId(user);
        int retStatus = 1;
        if(attachment != null)
        {
            if(userId != user.id && userId != 0)
            {
                Images.resize(attachment, Play.getFile("public/userspicture/" + attachment.getName()), 100, 130);
           
                //修改照片名，以用户的id为名
                String fileName = attachment.getName();
                String pictureSuffix = fileName.substring(fileName.lastIndexOf(".")+1);     //获取后缀
                String pName = userId+"."+ pictureSuffix;
                Images.resize(attachment,Play.getFile("public/Picture/" + pName),100,130);
                //保存照片所在的路径
                User id = User.findById(userId);
                id.imgSrc = "public/Picture/"+pName;
                id.save();
                System.out.println("hhhhhh");
                renderTemplate("Users/picture.html", retStatus);
            }
            else
            {
                Images.resize(attachment, Play.getFile("public/userspicture/" + attachment.getName()), 90, 90);
           
                 //修改照片名，以用户的id为名
                String fileName = attachment.getName();
                String pictureSuffix = fileName.substring(fileName.lastIndexOf(".")+1);     //获取后缀
                String pName = user.id +"."+ pictureSuffix;
                Images.resize(attachment,Play.getFile("public/Picture/" + pName),100,135);
                //保存照片所在的路径
                user.imgSrc = "public/Picture/"+pName;
                user.save();
                renderTemplate("Users/picture.html", retStatus);
            }
        }
        else
        { 
            renderTemplate("Users/upload.html",0);
        }
    }
    
    public static void upload(long userId)
    {
        int retStatus = (int)userId;
        render(retStatus);
    }
    
    //读出照片所在的路径
    public static void findAllImgSrc(long user_Id)
    {
        Query query = JPA.em().createNativeQuery("select imgSrc from AX_User where id="+user_Id);
        List imgSrc = query.getResultList();
        renderJSON(imgSrc);
    }
     
    public static void findByName(String key)
    {
        List<User> lists = User.find("realName like ?",  "%" + key + "%").fetch();
        renderJSON(lists);
    }

}
