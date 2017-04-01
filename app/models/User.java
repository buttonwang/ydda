package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import play.db.jpa.Model;
import play.libs.Codec;
import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;

/**
 * User entity managed
 */
@Entity
@Table(name="AX_User")
public class User extends Model {
	   
    public String name;
    
    public String realName;

    public String password;
    
    public String email;

    public Date   lastLoginTime;
    
    public String tel;
    
    public String sex;
    
    public Date birthday;
     
    public String nativePlace; //籍贯
    
    public String politics; //政治面貌
    
    public String education; //文化程度
        
    public String title; //职称

    public String category; //专业类别  医师、护士、医技人员、其他

    public String job;  //专业技术职务
    
    public Date jobInTime=null;  //专业技术任职时间

    public String qcn;      //资格证书号码 Qualification certificate number

    public String pcn;      //执业证书号码 Practising Certificate Number
        
    public String orderNum;  //排序号
   
    public String imgSrc;    //照片路径
    
    @Transient
    public Integer age; //年龄

   
	@ManyToOne
    public Dept dept;

   // @ManyToOne
   // public Role role;
    public Integer approveLevel = 0;
    
    //赤峰二院新增
    public String nation;                //民族
    public Date   joinPartyTime;         //入党时间
    
    public String baseEducation;        //基础学历
    public String baseGraduated;        //文化程度（何时何院校何专业毕业）
    
    public String highEducation;        //最高学历
    public String highGraduated;        // 文化程度（何时何院校何专业毕业）
    
    public Date   getTitleTime;         //取得职称时间
    public String alreadyTitle;         //已聘职称
    
    public String postGrade;            //岗位等级
    public Date   putRecordTime;        //备案时间
    public String IdentType;            //身份

    public String toString() {
        return "User(" + email + ")";
    }
    
    public void initPassword() {
       // if (this.password == null) this.password = Codec.hexMD5("ydda");
        if (this.password == null) this.password = Codec.hexMD5("123456");
    }
    public void resetPass() {
        this.password = Codec.hexMD5("123456");
    }
    public boolean checkPassword(String password) {
        return this.password.equals(Codec.hexMD5(password));
    }
    	
    public static User findByName(String name) {
        return find("name", name).first();
    }
    public static List findByNameList(String name) {
        return find("name like ?", name).fetch();
    }
    public static User findByRealName(String name) {
        return find("realName", name).first();
    }
    public static List findByRealNameList(String name) {      //用户维护
        return find("realName like ? ", name).fetch();
    }
   
    public static List findByDeptList(String name) {      //用户维护 科室查询
        return find("dept.name like ? ", name).fetch();
    }
    public static User findByRealNameDept(String name,Dept dept) {
        return find("realName=? and dept=? ", name,dept).first();
    }
    public static User findByRealNameDepts(String name,String depts) {
        return find("realName=? and dept.name in ("+depts+")", name).first();
    }
  
    public static User connect(String name, String password) {
        return find("byNameAndPassword", name, password).first();
    }
    
    public void changePassWord(String password) {
		this.password = Codec.hexMD5(password);
	}

    public static String toCheckJson(List<User> model) {
    	return new JSONSerializer()
    	  .transform(new DateTransformer("yyyy-MM-dd"), "birthday")
          .transform(new DateTransformer("yyyy-MM-dd"), "jobInTime")
          .transform(new DateTransformer("yyyy-MM-dd"), "joinPartyTime")
          .transform(new DateTransformer("yyyy-MM-dd"), "getTitleTime")
          .transform(new DateTransformer("yyyy-MM-dd"), "putRecordTime")
    	  .include("id", "name", "realName", "email", "lastLoginTime", "tel", "sex", "birthday", "age",
            "dept.name", "dept.id", "category",
            "politics", "education", "title", "job", "jobDesc", "jobInTime", "qcn", "pcn",
            "nation","joinPartyTime","baseEducation", "orderNum",
            "baseGraduated","highEducation","highGraduated", "getTitleTime","alreadyTitle","postGrade","putRecordTime","IdentType","nativePlace"
                )
    	  .exclude("*")
    	  .serialize(model);
    }
    
    public static String toTransformJson(User model) {
    	return new JSONSerializer()
    	  .transform(new DateTransformer("yyyy-MM-dd"), "birthday")
          .transform(new DateTransformer("yyyy-MM-dd"), "jobInTime")
          .transform(new DateTransformer("yyyy-MM-dd"), "joinPartyTime")
          .transform(new DateTransformer("yyyy-MM-dd"), "getTitleTime")
          .transform(new DateTransformer("yyyy-MM-dd"), "putRecordTime")
    	  .include("id", "name", "realName", "email", "lastLoginTime", "tel", "sex", "birthday", "age",
            "dept.name", "dept.id", "category",
            "politics", "education", "title", "job", "jobDesc", "jobInTime", "qcn", "pcn",
            "nation","joinPartyTime","baseEducation", "orderNum",
            "baseGraduated","highEducation","highGraduated", "getTitleTime","alreadyTitle","postGrade","putRecordTime","IdentType","nativePlace","imgSrc"
                )
    	  .exclude("*")
    	  .serialize(model);
    }

}