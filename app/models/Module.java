package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

import flexjson.JSONSerializer;

@Entity
@Table(name="AX_Module")
public class Module extends Model implements Comparable{
  
    @ManyToOne
    @JoinColumn(name = "pid")
    public Module parentMod;

    public String name;    //模块名称

    public String webpage;  //模块url

    public Integer level = 0;    //模块层级， 0：菜单级

    public String orderNum;  //排序号

    public String remark;
  
    public String toString() {
        return name;
    }
   
    @Transient
    public long parentId;
    public long getParentId() {
        return (parentMod == null)? 0:parentMod.id;
    }
   
    @Transient
    public boolean isexpand;
    public boolean getIsexpand(){
		return true;
	}
    @Transient
    public boolean ischecked;

    public boolean getIschecked() {
		return this.ischecked;
    }

    public void setIschecked(boolean ischecked) {
		this.ischecked = ischecked;
    }
    
 
    
    /**
     * 是否是根节点.
     * @return true 是跟节点, false 不是跟节点
     */
    public boolean isRoot(){
        return this.parentMod==null?true:false;
    }

    public List<Module> findChild(){
        return Module.find("parentMod=?", this).fetch();
    }

    public static String toJson(Module module){
        return  new JSONSerializer()
        .include("id", "name","webpage", "level","orderNum","remark","parentId","isexpand")
        .exclude("*").serialize(module);
    }
    
    public static String toJson(List<Module> modules){
        if(modules==null || modules.isEmpty()){
            return "";
        }
        return  new JSONSerializer()
        .include("id", "name","webpage", "level","orderNum","remark","parentId")
        .exclude("*").serialize(modules);
    }
    
    /**
	 * 管理员维护菜单，展示菜单树
	 * @return 所有菜单的ligerUi菜单样式json字符串.
	 */
	public static String allModuleJson(List<Module> rootModules) {
		String json = "";
		
		for(Module module : rootModules){
			String menuJson = toJson(module);
			String childJosn = toJson(module.findChild());
			if("".equals(childJosn)){
				menuJson = menuJson.replaceAll("}$",",\"children\":[]},");
			}else{
				menuJson = menuJson.replaceAll("}$",",\"children\":"+childJosn+"},");
			}
			json+=menuJson;
		}
		return "{\"Rows\":["+json.replaceAll(",$","")+"]}";
	}
	
	/**
	 * 选中 role 对应的module
	 * @param rootModules
	 * @param r
	 * @return
	 */
	public static String selectedRoleModule(List<Module> rootModules,Role r){
		String json = "";
		for(Module module : rootModules){
			String menuJson = toJson(module);
			String childJosn = toJson(module.findChild(),r);
			if("".equals(childJosn)){
				menuJson = menuJson.replaceAll("}$",",\"children\":[]},");
			}else{
				menuJson = menuJson.replaceAll("}$",",\"children\":"+childJosn+"},");
			}
			json+=menuJson;
		}
		return "{\"Rows\":["+json.replaceAll(",$","")+"]}";
	}
	
	/**
	 * 如果 role 已绑定菜单，则设置ischecked 为  true
	 * @param modules
	 * @param r
	 * @return
	 */
	public static String toJson(List<Module> modules,Role r){
		if(modules==null || modules.isEmpty()){
			return "";
		}
		List<Module> rms = RoleModule.getModuleByRole(r);

		List<Module> module2 = new ArrayList<Module>();
		for(Module module : modules){
			if(rms.contains(module)){
				module.setIschecked(true);
			}else{
				module.setIschecked(false);
			}
			module2.add(module);
		}
		return  new JSONSerializer()
		.include("id", "name","url", "parentId","ischecked")
		.exclude("*").serialize(module2);
	}
	
	/**
	 * 登录后展示全部菜单树
	 * @param rootModules
	 * @return
	 */
	public static String showModuleByUser(User u){
		//所有菜单树
                //return allModuleJson(Module.findParentPoint()).replace("{\"Rows\":","").replaceAll("}$", "");
		//获得登陆用户的所有角色
                List<Role> roles = Role.findByUser(u);
                //获取登陆用户具有的所有模块
		List<Module> roleHaveModules = Module.findByRoles(roles);
                //把登陆用户具有的所有根模块放在数组rootModules中
		List<Module> rootModules = new ArrayList<Module>();
		for(Module menu : roleHaveModules){
			if(menu.isRoot()){
				rootModules.add(menu);
			}
		}
                //返回显示字符串
		return "["+createShowModuleJson(rootModules,roleHaveModules).replaceAll(",$","")+"]";
	}
	
	/**
	 * 父节点对应的子节点包含于 roleHaveModules 中才展现json
	 * @param rootModules 父节点
	 * @param roleHaveModules 包含的节点
	 * @return json
	 */
	private static String createShowModuleJson(List<Module> rootModules,List<Module> roleHaveModules) {
		
                String json = "";
		for(Module rootModule : rootModules){
			List<Module> rootChilds = Module.find("parentMod=?", rootModule).fetch();
			List<Module> haveChilds = new ArrayList<Module>();
			for(Module child : rootChilds){
				if(roleHaveModules.contains(child)){
					haveChilds.add(child);
				}
			}
			String rootJson = Module.toJson(rootModule);
			String childJson = Module.toJson(haveChilds);
			if("".equals(childJson)){
				rootJson = rootJson.replaceAll("}$",",\"children\":[]},");
			}else{
				rootJson = rootJson.replaceAll("}$",",\"children\":"+childJson+"},");
			}
			json+=rootJson;
			
		}
		return json;
	}

	/**
	 * 是否是跟节点.
	 * @return true 是跟节点, false 不是跟节点
	 */
	/*public boolean isRoot(){
		return this.parentModule==null?true:false;
	}*/
	
	/**
	 * 根据角色获得对应的菜单.
	 * @param rs
	 * @return
	 */
	public static List<Module> findByRoles(List<Role> rs){
		List<RoleModule> rms = new ArrayList<RoleModule>();
		List<Module> menus = new ArrayList<Module>();
		for(Role r : rs) {
			rms.addAll(RoleModule.findByRole(r));
		}
		for(RoleModule rm : rms){
                        if(menus.contains(rm.module)==false){     //模块不能重复
			   menus.add(rm.module);
                        }
		}
	        Collections.sort(menus);
		return menus;
	}
	
	/**
	 * 获得所有父节点
	 * @return
	 */
	public static List<Module> findParentPoint(){
		return Module.find("parentModule.id is null").fetch();
	}
	
        public int compareTo(Object o) {

            if ( Integer.parseInt(this.orderNum)> Integer.parseInt(((Module)o).orderNum)) return 1;
            else if (Integer.parseInt(this.orderNum)< Integer.parseInt(((Module)o).orderNum) ) return -1;

            if ( Integer.parseInt(this.orderNum)> Integer.parseInt(((Module)o).orderNum)) return  1;
            else if (Integer.parseInt(this.orderNum)< Integer.parseInt(((Module)o).orderNum) ) return -1;
            else return 0;
       }
    
   


}