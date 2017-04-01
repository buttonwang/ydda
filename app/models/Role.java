package models;

import flexjson.JSONSerializer;
import play.*;
import play.data.validation.MaxSize;
import play.db.jpa.*;

import javax.persistence.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import flexjson.JSONSerializer;

import java.util.*;

/**
 * Role entity 
 * 角色  1：系统管理员；2：医护人员；3：科室管理员；4：院部管理员
 */

@Entity 
@Table(name="AX_Role")
public class Role extends Model {

    public String name;

    public String remark;
  
  	// 多对多定义
	@ManyToMany
	@JoinTable(name = "AX_RoleModule", joinColumns = { @JoinColumn(name = "user_id") }, 
		inverseJoinColumns = { @JoinColumn(name = "role_id") })
	@Fetch(FetchMode.SUBSELECT)
	@OrderBy("id ASC")
	public List<Module> modules;

    public static String toJson(List<Role> roles){
    	return new JSONSerializer()
    		.include("id","name","remark")
    		.exclude("*").serialize(roles);
    }
    
    public static String toJson(Role role){
    	return new JSONSerializer()
		.include("id","name","remark")
		.exclude("*").serialize(role);
    }
    
    public static List<Role> findByUser(User u){
    	List<UserRole> urs = UserRole.findByUser(u);
    	List<Role> rs = new ArrayList<Role>();
    	for(UserRole ur : urs){
    		rs.add(ur.role);
    	}
    	return rs;
    }
    public String toString() {
        return name;
    }

}
