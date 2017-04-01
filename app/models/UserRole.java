package models;

import java.util.List;

import javax.persistence.*;
import play.db.jpa.Model;

@Entity
@Table(name="AX_UserRole")
public class UserRole extends Model {

	@ManyToOne
	public User user;

	@ManyToOne
	public Role role;
	
	public static List<UserRole> findByUser(User user){
		return UserRole.find("user=?", user).fetch();
	}
	public UserRole(User user, Role role){
		this.user = user;
		this.role = role;
	}
        public static UserRole findByUserRole(long id,long userid) {
		return UserRole.find("role.id=? and user.id=? ", id,userid).first();
        }
        
        public static long getMaxRoleId(User user){
            List<UserRole> userRoles=UserRole.findByUser(user);
            long roleId=2;
            for(UserRole userrole: userRoles){
                 if(userrole.role.id==1){
                     roleId=1;
                     break;
                  }else{
                     roleId=(userrole.role.id>roleId)? userrole.role.id:roleId;
                  }			
            }
            return roleId;
        }
	
}