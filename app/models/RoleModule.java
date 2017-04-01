package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.ManyToOne;


import play.db.jpa.Model;

@Entity
@Table(name="AX_RoleModule")
public class RoleModule extends Model {
	
	@ManyToOne
	public Role role;
	
	@ManyToOne
	public Module module;
	
	public RoleModule(){}
	
	public RoleModule(Role role, Module module){
		this.role = role;
		this.module = module;
	}
	
	/**
	 * 
	 * @param role
	 * @param Module
	 * @return true 已存在，false 不存在
	 */
	public static boolean isExist(Role role,Module module){
		RoleModule rm = RoleModule.find("role=? and module=?", role,module).first();
		return rm==null?false:true;
	}

	public static List<RoleModule> findByRole(Role r) {
		return RoleModule.find("role=?", r).fetch();
	}
	
	public static String showRoleModule(){
		List<Role> roles = Role.findAll();
		String roleModuleJson="";
		for(Role r : roles){
			String rJson = Role.toJson(r);
			
			String menuJson = getModuleJsonByRole(r);
			
			if("".equals(menuJson)){
				rJson = rJson.replaceAll("}$", ",\"children\":[]},");
			}else{
				rJson = rJson.replaceAll("}$", ",\"children\":")+menuJson+"},";
			}
			
			roleModuleJson+=rJson;
		}
		return roleModuleJson.replaceAll(",$", "");
	}
	
	/**
	 * 通过role 获得对象菜单
	 * @param r
	 * @return
	 */
	public static String getModuleJsonByRole(Role r){
		List<RoleModule> rms = RoleModule.findByRole(r);
		//区分出跟节点和叶子节点
		List<Module> rootModules = new ArrayList<Module>();
		List<Module> leftModules = new ArrayList<Module>();
		for(RoleModule rm: rms){
			if(rm.module.isRoot()){
				rootModules.add(rm.module);
			}else{
				leftModules.add(rm.module);
			}
		}
		String menuJson = "";
		
		for(Module sm : rootModules){
			menuJson += Module.toJson(sm);
			List<Module> childModules = sm.findChild();
			List<Module> hasChild = new ArrayList<Module>();
			for(Module cs : childModules){
				if(leftModules.contains(cs)){
					hasChild.add(cs);
				}
			}
			menuJson = menuJson.replaceAll("}$", ",\"children\":")+Module.toJson(hasChild)+"}";
		}
		return menuJson;
	}
	
	public static List<Module> getModuleByRole(Role r){
		List<RoleModule> rms = RoleModule.findByRole(r);
		List<Module> menus = new ArrayList<Module>();
		for(RoleModule rm: rms){
			menus.add(rm.module);
		}
		return menus;
	}
       
        public static RoleModule findByRoleMoule(long id,long roleid) {
		return RoleModule.find("module.id=? and role.id=?", id,roleid).first();
	}
}
