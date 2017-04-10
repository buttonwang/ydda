/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;
import play.*;
import play.mvc.*;
import play.data.*;
import java.util.*;
import models.*;
import utils.YUtils;


/**
 *
 * @author Administrator
 */
public class ApproveJson extends Controller{
    
    //取非根节点数据
    public static void json(long id,int page, int pagesize) {
    	List<Note> noteLists = Note.find("checkList.id=? order by noteDate", id).fetch(page, pagesize);    	 
    	long total = Note.count("checkList.id=? order by noteDate", id);
    	String noteListStr = Note.toApproveJson(noteLists);
    	String jsonStr = YUtils.ligerGridData(noteListStr, total);
    	renderJSON(jsonStr);
    }
    
}
