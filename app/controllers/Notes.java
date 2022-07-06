package controllers;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.digester.annotations.rules.AttributeCallParam;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import play.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.*;

import play.db.jpa.JPA;
import play.libs.Files;
import javax.persistence.Query;
import models.*;
import play.modules.excel.RenderExcel;
import utils.YUtils;
import utils.enums.ApproveLevelEnum;
import utils.enums.CategoryEnum;

/**
 * Manage Notes related operations.
 */
public class Notes extends Application {

    public static void index(int tag) {
        render(tag);
    }

    public static void query(int tag) {
        render(tag);
    }

    /**
     * Get Notes json.
     */
    public static void json(int page, int pagesize) {
        renderJSON("");
    }

    /**
     * Get Notes json.
     */
    public static void checkjson(long id, int page, int pagesize, int tag, String userid, String deptid, String checkYear, int category) {
        User user = connectedUser();
        String rolesql = "";                                        // 增加权限控制，针对科室管理员角色
        long userRole = UserRole.getMaxRoleId(user);
        Dept userdept = user.dept;
        if (userRole == 3) {                                           //角色为科室医德考评小组
            rolesql = rolesql + " and dept.id=" + userdept.id;
        }
        String order_sql = " order by " + Note.defaultSortName();   //增加按时间倒序排序功能
        List<Note> notes = null;
        long total = 0;
        String basesql = "";
        if (category == 1) {
            basesql = "checkList.id=? ";
        }
        if (category == 2) {
            basesql = "adjustList.id=? ";
        }
        if (category == 3) {
            basesql = "voteKill.id=? ";
        }
        if (tag > 0) {
            long userId = Integer.parseInt(userid);
            long deptId = Integer.parseInt(deptid);
            if (userId > 0) {   //选中的为姓名
//                notes = Note.find(basesql + " and user.id=? and noteYear=?" + order_sql, id, userId, checkYear).fetch(page, pagesize);
                notes = Note.find(basesql + " and user.id=? and noteYear=?" + order_sql, id, userId, checkYear).fetch(page, pagesize);//2018-8-10wzx改
                total = Note.count(basesql + "  and user.id=? and noteYear=?", id, userId, checkYear);

            } else if (!(userId == -1 && deptId == -1)) {        //选中为科室
//                notes = Note.find(basesql + " and dept.id=? and noteYear=?" + order_sql, id, deptId, checkYear).fetch(page, pagesize);
                notes = Note.find(basesql + " and dept.id=? and noteYear=?" + order_sql, id, deptId, checkYear).fetch();//2018-8-10wzx改
                total = Note.count(basesql + " and dept.id=? and noteYear=?", id, deptId, checkYear);
            } else {                     // 全院  默认情况下执行，增加权限控制
//                notes = Note.find(basesql + " and noteYear=?" + rolesql + order_sql, id, checkYear).fetch(page, pagesize);
                notes = Note.find(basesql + " and noteYear=?" + rolesql + order_sql, id, checkYear).fetch(); //2018-8-10wzx改
                total = Note.count(basesql + "  and noteYear=? " + rolesql, id, checkYear);
            }
        } else {
//                notes = Note.find(basesql + " and noteYear=?" + rolesql + order_sql, id, checkYear).fetch(page, pagesize);
            notes = Note.find(basesql + " and user=? and noteYear=?" + order_sql, id, user, checkYear).fetch();//2018-8-10wzx改
            total = Note.count(basesql + "  and user=? and noteYear=?", id, user, checkYear);
        }
        String notesStr = Note.toCheckJson(notes);
        String jsonStr = YUtils.ligerGridData(notesStr, total);
        renderJSON(jsonStr);
    }
    /*
     * get all notes
     */

    public static void checkalljson(int page, int pageSize, int tag, String userid, String deptid, String checkYear) {
        User user = connectedUser();
        String order_sql = " order by " + Note.defaultSortName();   //增加按时间倒序排序功能
        List<Note> notes = null;
        long total = 0;
        if (tag > 0) { //除个人填报外
            long userId = Integer.parseInt(userid);
            long deptId = Integer.parseInt(deptid);
            if (UserRole.getMaxRoleId(user) == 3) {
                deptId = user.dept.id;
            }
            if (userId > 0) {                               //个人
//                notes = Note.find(" user.id=? and noteYear=?" + order_sql, userId, checkYear).fetch(page, pageSize);
                notes = Note.find(" user.id=? and noteYear=?" + order_sql, userId, checkYear).fetch();//2018-8-10wzx改
                total = Note.count(" user.id=? and noteYear=?", userId, checkYear);
            } else if (!(userId == -1 && deptId == -1)) {   //科室
//                notes = Note.find(" dept.id=? and noteYear=?" + order_sql, deptId, checkYear).fetch(page, pageSize);
                notes = Note.find(" dept.id=? and noteYear=?" + order_sql, deptId, checkYear).fetch(page, pageSize);//2018-8-10wzx改
                total = Note.count(" dept.id=? and noteYear=?", deptId, checkYear);
            } else {                  //全院
//                notes = Note.find(" noteYear=? " + order_sql, checkYear).fetch(page, pageSize);
                notes = Note.find(" noteYear=? " + order_sql, checkYear).fetch();//2018-8-10wzx改
                total = Note.count(" noteYear=? ", checkYear);
            }
        } else{
//            notes = Note.find(" user=? and noteYear=?" + order_sql, user, checkYear).fetch(page, pageSize);
            notes = Note.find(" user=? and noteYear=?" + order_sql, user, checkYear).fetch();//2018-8-10wzx改
            total = Note.count(" user=? and noteYear=?", user, checkYear);
        }
        String notesStr = Note.toCheckJson(notes);
        String jsonStr = YUtils.ligerGridData(notesStr, total);
        renderJSON(jsonStr);
    }

    /**
     * Add a Note.
     */
    public static void add(Note note) {

    }

    /**
     * Delete a Note.
     */
    public static void delete(long id) {
        long roleid = UserRole.getMaxRoleId(connectedUser());
        Note ynote = (Note) Note.findById(id);
        String re = "";
        if (ynote.createdMan == null || ynote.createdMan.equals("")) {
            if (!(roleid == 1) && !(roleid > 3)) {
                re = "{\"name\":\"没有删除该记录权限！\"}";
            } else {
                ynote._delete();
            }
        } else {
            ynote._delete();
        }
        renderJSON(re);
    }

    public static void simplejson() {

    }

    /**
     * Approve Note.
     */
    public static void approve(int level) {
        render(level);
    }

    /**
     * 根据条件获取档案
     * @param level
     * @param isapprove
     * @param page
     * @param pagesize
     * @param sortname
     * @param sortorder
     */
    public static void approvejson(int level, int isapprove, int page, int pagesize, String sortname, String sortorder) {
        String sql = "approveLevel=? ";
        String order_sql = " order by " + Note.defaultSortName();   //默认按时间倒序排序
        if (isapprove == 1) {                  //已审核
            sql = "approveLevel>=? and approveLevel<7";
        }
        if (sortname != null) {   //按选择的标题排序
            if (sortname.equals("checkTitle")) {
                sortname = "checkList.title";
            }
            order_sql = order_sql.replace(Note.defaultSortName(), sortname) + " " + sortorder;
        }
        if (level == 0 || (level == 1 && isapprove == 1)) {      //如果为科室审核，则限制科室类别
            User user = connectedUser();
            long userRole = UserRole.getMaxRoleId(user);
            Dept userdept = user.dept;
            String deptstr = "";
            if (userRole == 3) {                                  //角色为科室医德考评小组
                Query querydepts = JPA.em().createNativeQuery("select name from YDDA_Dept where director='" + user.realName + "'");
                List listdept = querydepts.getResultList();
                if (listdept != null && listdept.size() > 1) {
                    for (int i = 0; i < listdept.size(); i++) {
                        deptstr = deptstr + "'" + listdept.get(i).toString() + "',";
                    }
                    deptstr = deptstr.substring(0, (deptstr.length() - 1));
                    sql = sql + " and dept.name in(" + deptstr + ")";
                } else {
                    sql = sql + " and dept.id=" + userdept.id;
                }
            }
        }
        List<Note> notes = Note.find(sql + order_sql, level).fetch(page, pagesize);
        long total = Note.count(sql, level);
        String notesStr = Note.toApproveJson(notes);
        String jsonStr = YUtils.ligerGridData(notesStr, total);
        renderJSON(jsonStr);
    }

    public static void approvejsoncheck(int plevel, int pisapprove, String sortname, String sortorder,int type,String name) {
        String sql = "approveLevel=? ";
        String order_sql = " order by " + Note.defaultSortName();   //默认按时间倒序排序
        if (pisapprove == 1) {                  //已审核
            sql = "approveLevel>=? and approveLevel<7 ";
        }
        if(type==1||type==3){
            sql += " and user.realName like ? ";
        }else if(type==2||type==4){
            sql += "  and dept.name like ? ";
        }
        if (sortname != null&&!sortname.equals("")) {   //按选择的标题排序
            if (sortname.equals("checkTitle")) {
                sortname = "checkList.title";
            }
            order_sql = order_sql.replace(Note.defaultSortName(), sortname) + " " + sortorder;
        }
        if (plevel == 0 || (plevel == 1 && pisapprove == 1)) {      //如果为科室审核，则限制科室类别
            User user = connectedUser();
            long userRole = UserRole.getMaxRoleId(user);
            Dept userdept = user.dept;
            String deptstr = "";
            if (userRole == 3) {                                  //角色为科室医德考评小组
                Query querydepts = JPA.em().createNativeQuery("select name from YDDA_Dept where director='" + user.realName + "'");
                List listdept = querydepts.getResultList();
                if (listdept != null && listdept.size() > 1) {
                    for (int i = 0; i < listdept.size(); i++) {
                        deptstr = deptstr + "'" + listdept.get(i).toString() + "',";
                    }
                    deptstr = deptstr.substring(0, (deptstr.length() - 1));
                    sql = sql + " and dept.name in(" + deptstr + ")";
                } else {
                    sql = sql + " and dept.id=" + userdept.id;
                }
            }
        }
        List<Note> notes = Note.find(sql + order_sql, plevel,"%"+name+"%").fetch();
        long total = notes.size();
        String notesStr = Note.toApproveJson(notes);
        String jsonStr = YUtils.ligerGridData(notesStr, total);
        renderJSON(jsonStr);
    }


    /**
     * 审核档案
     * @param id
     */
    public static void approveconfirm(long id) {
        Note note =(Note) Note.findById(id);
        int approveLevel = note.approveLevel + 1;
        note.approveLevel = approveLevel;
        note.save();
        NoteApprove noteApprove = new NoteApprove();
        noteApprove.level = note.approveLevel;
        noteApprove.note = note;
        noteApprove.approveDate = new Date();
        noteApprove.approveMan = connectedUser();
        noteApprove.save();
        renderJSON("");
    }

    /**
     * 退回档案
     * @param note
     * @param comment
     */
    public static void approvefailsave(Note note, String comment) {
        long id = note.id;
        Note samenote =(Note) Note.findById(id);
        int approveLevel = note.approveLevel + 7;
        samenote.approveLevel = approveLevel;
        samenote.approveComment = comment;
        samenote.save();
        NoteApprove noteApprove = new NoteApprove();
        noteApprove.level = approveLevel;
        noteApprove.note = note;
        noteApprove.approveDate = new Date();
        noteApprove.approveMan = connectedUser();
        noteApprove.comment = comment;
        noteApprove.save();
        renderJSON("");
    }

    /**
     *批量审核
     * @param selecteds
     */
    public static void approvemulti(String selecteds) {
        String[] selarrs = selecteds.split(",");
        int length = selarrs.length;
        for (int i = 0;i < length; i++) {
            long id = Integer.parseInt(selarrs[i]);
            Note notes =(Note) Note.findById(id);
            notes.approveLevel = notes.approveLevel + 1;
            System.out.println();
            notes.save();
        }
        renderJSON("");
    }
    /**
     *批量退回
     * @param selecteds
     */
    public static void batchReturn(String selecteds, String comment) {
        String[] selarrs = selecteds.split(",");
        int length = selarrs.length;
        for (int i = 0;i < length; i++) {
            long id = Integer.parseInt(selarrs[i]);
            Note notes =(Note) Note.findById(id);
            notes.approveLevel = notes.approveLevel + 7;
            notes.approveComment = comment;
            notes.save();
            NoteApprove noteApprove = new NoteApprove();
            noteApprove.level =  notes.approveLevel;
            noteApprove.note = notes;
            noteApprove.approveDate = new Date();
            noteApprove.approveMan = connectedUser();
            noteApprove.comment = comment;
            noteApprove.save();
        }
        renderJSON("");
    }


    //动态树得到档案记录
    public static void filejson(int page, int pagesize, int tag, String userid, String deptid, String checkYear) {
        User user = connectedUser();
        List<Note> notes = null;
        long total = 0;
        if (tag > 0) {
            long userId = Integer.parseInt(userid);
            long deptId = Integer.parseInt(deptid);
            if (userId > 0) {
                notes = Note.find(" approveLevel=3 and user.id=? and noteYear=?", userId, checkYear).fetch();
                total = Note.count(" approveLevel=3 and user.id=? and noteYear=?", userId, checkYear);
            } else if (!(userId == -1 && deptId == -1)) {
                notes = Note.find(" approveLevel=3 and dept.id=? and noteYear=? ", deptId, checkYear).fetch();
                total = Note.count(" approveLevel=3 and dept.id=? and noteYear=? ", deptId, checkYear);
            } else {
                notes = Note.find(" approveLevel=3 and noteYear=? ", checkYear).fetch();
                total = Note.count(" approveLevel=3 and noteYear=? ", checkYear);
            }
        } else {
            notes = Note.find(" approveLevel=3 and user=? and noteYear=?", user, checkYear).fetch();
            total = Note.count(" approveLevel=3 and user=? and noteYear=?", user, checkYear);
        }
        String notesStr = Note.toCheckJson(notes);
        String jsonStr = YUtils.ligerGridData(notesStr, total);

        renderJSON(jsonStr);
    }
    //查询已存档档案
    public static void detailfilejson(String deptCombox_val,String userCombox_val, int page, int pagesize,
                                      int tag, String begindate, String enddate, String categoryCombox_val, String title) {

        User currentuser = connectedUser();
        List<Note> notes = null;
        long total = 0;
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.util.Date beginDate = null, endDate = null;
        if ((begindate == null) || (begindate.equals(""))) {
            begindate = "1900-1-1";
        }
        if ((enddate == null) || (enddate.equals(""))) {
            enddate = "2200-1-1";
        }
        try {
            beginDate = sdf.parse(begindate);
            endDate = sdf.parse(enddate);
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
        String basesql = " noteDate between ? and ? ";
        if (userCombox_val != null && !userCombox_val.equals("")) {
            long userId = Integer.parseInt(userCombox_val);
            basesql = basesql + " and user.id=" + userId;
        }
        if (deptCombox_val != null && !deptCombox_val.equals("")) {
            long deptId = Integer.parseInt(deptCombox_val);
            System.out.println(deptId);
            basesql = basesql + " and dept.id=" + deptId;
        }
        if (categoryCombox_val != null && !categoryCombox_val.equals("")) {
            int category = Integer.parseInt(categoryCombox_val);
            basesql = basesql + " and category=" + category;
        }
        if (tag > 0) {
            notes = Note.find(basesql, beginDate, endDate).fetch(page, pagesize);
            total = Note.count(basesql, beginDate, endDate);
        } else {
            notes = Note.find(basesql + " and user=? ", beginDate, endDate, currentuser).fetch(page, pagesize);
            total = Note.count(basesql + " and user=? ", beginDate, endDate, currentuser);
        }
        String notesStr = Note.toCheckJson(notes);
        String jsonStr = YUtils.ligerGridData(notesStr, total);
        renderJSON(jsonStr);
    }

    //根据条件查询已存档档案
    public static void queryNotes(String dept,String realName, int page, int pagesize, int tag,
                                      String beginDate, String endDate, String category,
                                      String approveLevel) {
        User currentuser = connectedUser();
        List<Note> notes = null;
        long total = 0;
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        Date inBeginDate = null;
        Date inEndDate = null;
        if ((beginDate == null) || (beginDate.equals(""))) {
            beginDate = "1900-1-1";
        }
        if ((endDate == null) || (endDate.equals(""))) {
            endDate = "2200-1-1";
        }
        try {
            inBeginDate = sdf.parse(beginDate);
            inEndDate = sdf.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String basesql = " noteDate between ? and ? ";
        if (realName != null && !realName.equals("")) {
            long userId = Integer.parseInt(realName);
            basesql = basesql + " and user.id=" + userId;
        }
        if (dept != null && !dept.equals("")) {
            long deptId = Integer.parseInt(dept);
            basesql = basesql + " and dept.id=" + deptId;
        }
        if (category != null && !category.equals("")) {
            int  category1 = Integer.parseInt(category);
            basesql = basesql + " and category=" + category1;
        }
        if (approveLevel != null && !approveLevel.equals("")) {
            int approve = Integer.parseInt(approveLevel);
            if(approve==7){
                basesql = basesql + " and approveLevel in (" + approve+",8 )";
            }else {
                basesql = basesql + " and approveLevel=" + approve;
            }
        }
        if (tag > 0) {
//            notes = Note.find(basesql, inBeginDate, inEndDate).fetch(page, pagesize);
            notes = Note.find(basesql, inBeginDate, inEndDate).fetch();//2018-8-10wzx改
            total = Note.count(basesql, inBeginDate, inEndDate);
            System.out.println(basesql);
        } else {
//            notes = Note.find(basesql + " and user=? ", inBeginDate, inEndDate, currentuser).fetch(page, pagesize);
            notes = Note.find(basesql + " and user=? ", inBeginDate, inEndDate, currentuser).fetch();//2018-8-10wzx改
            total = Note.count(basesql + " and user=? ", inBeginDate, inEndDate, currentuser);
        }
        String notesStr = Note.toCheckJson(notes);
        String jsonStr = YUtils.ligerGridData(notesStr, total);
        renderJSON(jsonStr);
    }
//根据条件查询已存档档案
public static void exportQueryNotesExcel(String dept,String realName, int tag,
                              String beginDate, String endDate, String category,
                              String approveLevel) {
    User currentuser = connectedUser();
    List<Note> note = null;
    List<Map> notes = new ArrayList<Map>();
    long total = 0;
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
    Date inBeginDate = null;
    Date inEndDate = null;
    if ((beginDate == null) || (beginDate.equals(""))) {
        beginDate = "1900-1-1";
    }
    if ((endDate == null) || (endDate.equals(""))) {
        endDate = "2200-1-1";
    }
    try {
        inBeginDate = sdf.parse(beginDate);
        inEndDate = sdf.parse(endDate);
    } catch (ParseException e) {
        e.printStackTrace();
    }

    String basesql = " noteDate between ? and ? ";
    if (realName != null && !realName.equals("")) {
        long userId = Integer.parseInt(realName);
        basesql = basesql + " and user.id=" + userId;
    }
    if (dept != null && !dept.equals("")) {
        long deptId = Integer.parseInt(dept);
        basesql = basesql + " and dept.id=" + deptId;
    }
    if (category != null && !category.equals("")) {
        int  category1 = Integer.parseInt(category);
        basesql = basesql + " and category=" + category1;
    }
    if (approveLevel != null && !approveLevel.equals("")) {
        int approve = Integer.parseInt(approveLevel);
        if(approve==7){
            basesql = basesql + " and approveLevel in (" + approve+",8 )";
            System.out.println();
        }else {
            basesql = basesql + " and approveLevel=" + approve;
        }
    }
    Map<String,String> map = null;
    if (tag > 0) {
        note = Note.find(basesql, inBeginDate, inEndDate).fetch();
        for (int i =0;i<note.size();i++){
            map = notes2Map(note.get(i));
            map.put("category",CategoryEnum.getCategoryEnumsMap().get(map.get("category")));
            map.put("approveLevel", (String) ApproveLevelEnum.getApproveLevelEnumsMap().get(map.get("approveLevel")));
            map.put("noteDate",map.get("noteDate").substring(0,10));
        notes.add(map);
        }
    } else {
        note = Note.find(basesql + " and user=? ", inBeginDate, inEndDate, currentuser).fetch();
        for (int i =0;i<note.size();i++){
            map = notes2Map(note.get(i));
            map.put("approveLevel", (String) ApproveLevelEnum.getApproveLevelEnumsMap().get(map.get("approveLevel")));
            map.put("category",CategoryEnum.getCategoryEnumsMap().get(map.get("category")));
            map.put("noteDate",map.get("noteDate").substring(0,10));
            notes.add(map);
        }
    }
    request.format = "xls";
    renderArgs.put(RenderExcel.RA_ASYNC, true);
    renderArgs.put(RenderExcel.RA_FILENAME,"档案查询列表"+ ".xls");
     render(notes);
}

    public static void save(Note notes, String userCombox_val, String deptCombox_val, String goodsCombox, int tag) {
        User user = connectedUser();
        long roleId = UserRole.getMaxRoleId(user);


        if (userCombox_val == null) userCombox_val = "";

        String[] names = {userCombox_val};
//        if (roleId > 2) {
            if (userCombox_val.contains(";")) {
                names = userCombox_val.split(";");
            }
//        }
        if (notes.id  != null) {
            notes.save();
        } else {
            for (int i = 0; i < names.length; i++) {
                Note note = new Note();                    //创建一个新的对象，for循环能为每个对象赋值一个id
                note.noteDate = notes.noteDate;            //将形参的值赋给对象
                note.certifyMan = notes.certifyMan;
                note.content = notes.content;
                note.checkList = notes.checkList;
                note.createdDate = notes.createdDate;
                note.approveLevel = notes.approveLevel;
                note.category = notes.category;
                note.score = notes.score;
                note.adjustList = notes.adjustList;
                note.voteKill = notes.voteKill;
                note.money=notes.money;
                if (notes.approveLevel == null) {
                    if (roleId > 3) {                               //医德医风小组填报不必科室审核
                        notes.approveLevel = (int) roleId - 3;
                    } else {
                        notes.approveLevel = 0;
                    }
                }
                if (names[i] != null && !(names[i].equals(""))) {
                    note.user = (User) User.findById(Long.parseLong(names[i]));
                } else {
                    note.user = user;                //个人填报增加人员
                }
                if (deptCombox_val != null && !(deptCombox_val.equals(""))) {
                    note.dept = (Dept) Dept.findById(Long.parseLong(deptCombox_val));
                } else {
                    note.dept = user.dept;             // 个人填报增加科室
                }
                if (goodsCombox != null && !(goodsCombox.equals(""))) {   //增加奖惩实物
                    note.goods = goodsCombox;
                }
                if (note.createdMan == null) {
                    note.createdMan = user;
                }
                if (note.noteDate == null) {
                    note.noteDate = new Date();
                }
                if (notes.createdDate == null) {
                    notes.createdDate = new Date();
                }
                note.updatedDate = new Date();
                //增加档案填报的年份
                String noteYear = null;
                java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy");
                if (note.noteDate == null) {
                    noteYear = df.format(new Date());
                    note.noteYear = noteYear;
                } else {
                    noteYear = df.format(note.noteDate);
                    note.noteYear = noteYear;
                }
                note.save();
            }
        }
        renderJSON("");
    }

    public static void upload(long noteId) {
        int retStatus = (int)noteId;
        render(retStatus);
    }

    //上传附件
    public static void fileUpload(File attachment, long noteId) {
        //修改附件名，以noteId为名
        String fileName = attachment.getName();
        String pictureSuffix = fileName.substring(fileName.lastIndexOf(".")+1);     //获取后缀
        String pName = noteId+"."+ pictureSuffix;
        Files.copy(attachment, Play.getFile("public/notefile/" + pName));
        //保存照片所在的路径
        Note note =(Note) Note.findById(noteId);
        note.fileSrc = "public/notefile/" + pName;
        note.save();
        renderTemplate("Notes/picture.html", 0);
    }

    private static Map<String,String> notes2Map(Note note){
        Map<String,String> map = new HashMap<String,String>();
            try {
                map = BeanUtils.describe(note);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        return map;
    }

    public static int DelNoteBycreatedManId(long id){
        List<Note> result =Note.find(" user.id=?",id).fetch();
        for (int i=0;i<result.size();i++){
            NoteApprove.delete(" note.id",result.get(i).id);
        }
       int b= Note.delete(" user.id=?",id);
        return b;
    }
}
