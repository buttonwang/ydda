package controllers;


import com.ning.http.client.FilePart;
import java.io.File;
import play.*;
import play.db.jpa.JPA;
import play.mvc.*;
import java.util.*;
import javax.naming.spi.DirStateFactory.Result;
import javax.persistence.Query;
import models.*;
import org.h2.util.IOUtils;
import play.libs.Files;
import play.libs.Images;

public class Application extends Controller {

    @Before 
    static void globals() {
        renderArgs.put("connected", connectedUser());
        User user=connectedUser(); 
        int  year=Calendar.YEAR;
        long roleid=UserRole.getMaxRoleId(user);
       // long   grTongg=0,grBack=0, grNew=0,ksTongg=0,ksBack=0, ksWait=0,ksCheck=0,ksNoCheck=0;
        long myArrnum[]=new long[15];
        String myArrstr[]=new String[4];            
        if(roleid==2){
               myArrnum[0]= Note.count("user=? and approveLevel=2",user);  //（个人）审核通过
               myArrnum[1]= Note.count("user=? and approveLevel>7",user);  //（个人）审核失败
               myArrnum[2]= Note.count("user=? and approveLevel=0",user);  //（个人）新建         
               Record rd = Record.findByUser(user,year);
               CheckLevel  cl1 = CheckLevel.findByName("自我评价");
               CheckLevel  cl2 = CheckLevel.findByName("科室评价");
               CheckLevel  cl3 = CheckLevel.findByName("单位评价");          
               RecordDetail RD1 = RecordDetail.findByRecord(rd, cl1);
               RecordDetail RD2 = RecordDetail.findByRecord(rd, cl2);
               RecordDetail RD3 = RecordDetail.findByRecord(rd, cl3);  
               myArrstr[0]= (RD1==null) ?   "未考评": "已考评";           //个人自我考评情况
               myArrstr[1]= (RD2==null) ?   "未考评": "已考评";           //个人科室考评情况
               myArrstr[2] =(RD3==null) ?   "未考评": "已考评";           //个人单位考评情况
               myArrstr[3]= (rd!=null&&rd.status==1)? "已归档":"未归档";            //个人考评归档情况
        }
        if(roleid==3){
             myArrnum[3]= Note.count( "dept=? and approveLevel=2",user.dept);   //（科室）审核通过
             myArrnum[4]= Note.count( "dept=? and approveLevel>7",user.dept);   //（科室）审核失败
             myArrnum[5]= Note.count( "dept=? and approveLevel=0",user.dept);   // (科室) 等待科室审核
             long deptNum=User.count("dept=?",user.dept);                       //  科室人数
             List<Record> records=Record.find("user.dept=?", user.dept).fetch();
             int n=0;
             CheckLevel  clks = CheckLevel.findByName("科室评价");
             for(Record r:records){
                 RecordDetail rdk=RecordDetail.findByRecord(r, clks);
                 if(rdk!=null){
                      n++;
                 }  
             }
             myArrnum[6]= n;                                                       // 科室考评人数
             myArrnum[7]=deptNum-n;                                                   //还没有科室考评人数  
        }
        if(roleid==4){
             myArrnum[8]=  Note.count("approveLevel=2");  
             myArrnum[9]=  Note.count("approveLevel>7");  
             myArrnum[10]= Note.count("approveLevel=0");                
             long qyNum=User.count()-1;                       // 全院人数，去掉系统管理员
             List<Record> records=Record.findAll();
             int m=0;
             CheckLevel  cldw = CheckLevel.findByName("单位评价");
             for(Record r:records){
                 RecordDetail rdk=RecordDetail.findByRecord(r, cldw);
                 if(rdk!=null){
                      m++;
                 }  
             }
             myArrnum[11]= m;                                //(全院)单位考评人数
             myArrnum[12]= qyNum-m;                          //(全院)还未单位考评人数 
             myArrnum[13]= Record.count("status=1");                                 //(全院)归档人数
             myArrnum[14]= qyNum- myArrnum[13];                              //(全院)还未归档人数
        }
            renderArgs.put("myArrnum", myArrnum);
            renderArgs.put("myArrstr", myArrstr);          
    }
    
    @Before(unless={"login", "authenticate", "logout"})
    static void checkUser() throws Throwable {       
            flash.put("yurl", request.method == "GET" ? request.url : "/");
        if (connectedUser() == null) {            	
        	login();
        } else {     	
        }
    }
        
    public static void index() {
        render();
            
    }
    /*
     * 这个方法是登陆验证
     */
    public static void authenticate(String name, String password) throws Throwable {
		User user = User.findByName(name);
        
        if (!authUser(user, name, password)){        	
        	params.flash();
        	login();
        }
        connect(user);
        redirectToOriginalURL();
    }

    public static void login() {
    	flash.keep("yurl");
        render();
    }

    public static void logout() {
    	flash.success("You've been logged out");
    	session.clear();
    	index();
    }

    private static void connect(User user) {
        session.put("logged", user.id);
        //session.put("role", user.role.id);
        user.lastLoginTime  = new Date();
        user.save();
    }
    
    static User connectedUser() {
        String userId = session.get("logged");
        return (userId==null) ? null : (User)User.findById(Long.parseLong(userId));
    }
    
    static void redirectToOriginalURL() throws Throwable {       
        String url = flash.get("yurl");
        if(url == null) {
            url = "/";
        }
        redirect(url);
    }
    
    public static boolean authUser(User user, String name, String password) {    	
		boolean authentic = true;

		flash.keep("yurl");	
		if (user == null) {
        	flash.error("你的用户名不存在，请重新输入用户名。");
        	authentic = false;
        } else {
        	if (!user.checkPassword(password)) {    	
        		flash.error("你的用户名和密码不符，请再试一次。");
        		authentic =false;
        	}
        }
        if (!authentic) flash.put("name", name);                 
        return authentic;
	}
    
	public static void changePassword(String curPassword, String password, String password2) {
		User user = connectedUser(); 
		if (!user.checkPassword(curPassword)) {
			renderText("当前密码输入错误！");
		} else {
			if (password.length()<6) renderText("密码不能小于6个字符，请输入符合规则的密码！");
            if (password.length()>16) renderText("密码不能大于16个字符，请输入符合规则的密码！");
            
            if (!password.equals(password2)) renderText("两次输入的密码不一致，请重新输入密码！");
            
			user.changePassWord(password);
			user.save();
		}
		renderText("修改密码成功！");
	}
/*
    public static void userTree() {
        User user = connectedUser(); 
        long roleId=UserRole.getMaxRoleId(user);
        Dept userDept=user.dept;
     
        String depts="";      
        String whereSQL_dept="";
        String whereSQL_user="";
        String allsql= " union all  select '0',  '-1',       '全院', 'memeber', '', '','','','','','','',null as  orderNum    ";             
       //根绝用户角色，限制人员树的显示权限，各个医院的角色都有不同，但限制角色主要是针对医生（因为医生其实看不到人员树）和科室主任,其它都不限制
        
        
        if(roleId==3){               //角色为科室主任  
                
                 Query querydepts = JPA.em().createNativeQuery("select name from YDDA_Dept where director='"+user.realName+"'");
                 List  listdept = querydepts.getResultList();
                 if(listdept!=null&&listdept.size()>1){
                     
                             for(int i=0;i<listdept.size();i++){
                                      depts=depts+"'"+listdept.get(i).toString()+"',";
                             }
                             depts=depts.substring(0, (depts.length()-1));
                             whereSQL_user=" and d.name in("+depts+") ";
                             whereSQL_dept=" and name in("+depts+")";                 
                             allsql=""; 
               } else{
                             whereSQL_user=" and d.id="+userDept.id;
                             whereSQL_dept=" and id="+userDept.id;
                             allsql="";                  
               }
                           
        }
        if(roleId==2) {     //角色为医护人员
            whereSQL_user=" and u.id="+user.id;   
            whereSQL_dept=" and id="+userDept.id;
            allsql="";                                  
        }
        //orderNum 用于排序功能的实现
         Query query = JPA.em().createNativeQuery( "select 'd'+cast(id as varchar) as id, '0' as pid, name as text, 'memeber' as icon,'' as deptname, '' as title,'' AS sex,'' as category,''as job,'' as jobInTime,'' as qcn,'' as pcn ,null as  orderNum   from  YDDA_Dept  where 1=1 "+whereSQL_dept
                 +   allsql
                 + " union all    select 'u'+cast(u.id as varchar) as id , 'd'+cast(dept_id as varchar), u.realName, 'myaccount', d.name as deptname ,isnull(title,'') as title,u.sex,u.category,u.job,CONVERT(varchar(12),u.jobInTime,23 ),"
                 + " u.qcn,u.pcn, u.orderNum as  orderNum  from AX_User as u  inner join YDDA_Dept as d on u.dept_id=d.id where u.id > 1  "+whereSQL_user+" order by orderNum",BaseTree.class);
         List<BaseTree> utree = query.getResultList();
         renderJSON(utree);       
    }
    */
        
    

    //加载科室
    public static void deptT() {
        User user = connectedUser(); 
        long roleId=UserRole.getMaxRoleId(user);
        Dept userDept=user.dept;
     
        String depts="";      
        String whereSQL_dept="";
        String whereSQL_user="";
       
        
        
        if(roleId==3){               //角色为科室主任  
                
                 Query querydepts = JPA.em().createNativeQuery("select name from YDDA_Dept where director='"+user.realName+"'");
                 List  listdept = querydepts.getResultList();
                 if(listdept!=null&&listdept.size()>1){
                     
                             for(int i=0;i<listdept.size();i++){
                                      depts=depts+"'"+listdept.get(i).toString()+"',";
                             }
                             depts=depts.substring(0, (depts.length()-1));
                             whereSQL_user=" and d.name in("+depts+") ";
                             whereSQL_dept=" and name in("+depts+")";                 
                           
               } else{
                             whereSQL_user=" and d.id="+userDept.id;
                             whereSQL_dept=" and id="+userDept.id;
                                        
               }
                           
        }
        if(roleId==2) {     //角色为医护人员
            whereSQL_user=" and u.id="+user.id;   
            whereSQL_dept=" and id="+userDept.id;
                                        
        }
        //orderNum 用于排序功能的实现
         Query query = JPA.em().createNativeQuery( "select 'd'+cast(id as varchar) as id, '0' as pid, name as text, 'memeber' as icon,'' as deptname, '' as title,'' AS sex,'' as category,''as job,'' as jobInTime,'' as qcn,'' as pcn ,null as  orderNum   from  YDDA_Dept  where 1=1 "+whereSQL_dept,BaseTree.class);
         List<BaseTree> utree = query.getResultList();
        renderJSON(utree);  
    }
       
        //加载人员
   public static void userT(Long deptId) {
   
       Query query = JPA.em().createNativeQuery("select 'u'+cast(u.id as varchar) as id,'d'+cast(d.id as varchar) as pid,u.realName as text,d.name as deptname,'' as title,u.sex,u.category,u.job,u.jobIntime,u.qcn,u.pcn from AX_User as u inner join YDDA_Dept as d on u.dept_id=d.id  and d.id=" +deptId,BaseTree.class);
       List<BaseTree> utree = query.getResultList();
       renderJSON(utree);      
    }

	
}