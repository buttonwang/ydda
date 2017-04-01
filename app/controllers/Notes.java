package controllers;

import play.*;
import play.mvc.*;
import play.data.*;
import java.util.*;

import flexjson.JSONSerializer;
import javax.persistence.Query;
import models.*;
import play.db.jpa.JPA;
import utils.YUtils;

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
        long userRole = UserRole.getMaxRoleId(user);;
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
                notes = Note.find(basesql + " and user.id=? and noteYear=?" + order_sql, id, userId, checkYear).fetch(page, pagesize);
                total = Note.count(basesql + "  and user.id=? and noteYear=?", id, userId, checkYear);
            } else if (!(userId == -1 && deptId == -1)) {        //选中为科室
                notes = Note.find(basesql + " and dept.id=? and noteYear=?" + order_sql, id, deptId, checkYear).fetch(page, pagesize);
                total = Note.count(basesql + " and dept.id=? and noteYear=?", id, deptId, checkYear);
            } else {                     // 全院  默认情况下执行，增加权限控制
                notes = Note.find(basesql + " and noteYear=?" + rolesql + order_sql, id, checkYear).fetch(page, pagesize);
                total = Note.count(basesql + "  and noteYear=? " + rolesql, id, checkYear);
            }
        } else {
            notes = Note.find(basesql + " and user=? and noteYear=?" + order_sql, id, user, checkYear).fetch(page, pagesize);
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
        //除个人填报外
        if (tag > 0) {
            long userId = Integer.parseInt(userid);
            long deptId = Integer.parseInt(deptid);
            // if( user.role.id.intValue()==3){
            if (UserRole.getMaxRoleId(user) == 3) {
                deptId = user.dept.id;
            }
            if (userId > 0) {                               //个人
                notes = Note.find(" user.id=? and noteYear=?" + order_sql, userId, checkYear).fetch(page, pageSize);
                total = Note.count(" user.id=? and noteYear=?", userId, checkYear);
            } else if (!(userId == -1 && deptId == -1)) {   //科室
                notes = Note.find(" dept.id=? and noteYear=?" + order_sql, deptId, checkYear).fetch(page, pageSize);
                total = Note.count(" dept.id=? and noteYear=?", deptId, checkYear);
            } else {                  //全院      
                notes = Note.find(" noteYear=? " + order_sql, checkYear).fetch(page, pageSize);
                total = Note.count(" noteYear=? ", checkYear);
            }

        } else {
            notes = Note.find(" user=? and noteYear=?" + order_sql, user, checkYear).fetch(page, pageSize);
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
     * Update a Note.
     */
    /*
     * public static void save(Note note, String userCombox_val, String
     * deptCombox_val,String goodsCombox) { /* if (note.approveLevel == null) {
     * note.approveLevel = 0; } User user=connectedUser(); long
     * userId=UserRole.getMaxRoleId(user); if (note.approveLevel == null) {
     *
     * if(userId>3){ //医德医风小组填报不必科室审核 note.approveLevel=(int)userId-3; }else{
     * note.approveLevel = 0; } } //if (note.user== null) note.user =
     * connectedUser(); if (userCombox_val != null &&
     * !(userCombox_val.equals(""))) { //int
     * user_id=Integer.parseInt(userCombox_val); note.user = (User)
     * User.findById(Long.parseLong(userCombox_val)); } else { note.user = user;
     * //个人填报增加人员 } if (deptCombox_val != null && !(deptCombox_val.equals("")))
     * { //int user_id=Integer.parseInt(userCombox_val); note.dept = (Dept)
     * Dept.findById(Long.parseLong(deptCombox_val)); } else{
     * note.dept=user.dept; // 个人填报增加科室 } if (goodsCombox != null &&
     * !(goodsCombox.equals(""))) { //增加奖惩实物 note.goods=goodsCombox; } if
     * (note.createdMan == null) { note.createdMan = user; } if (note.noteDate
     * == null) { note.noteDate = new Date(); } if (note.createdDate == null) {
     * note.createdDate = new Date(); } note.updatedDate = new Date();
     * //增加档案填报的年份 String noteYear = null; java.text.SimpleDateFormat df = new
     * java.text.SimpleDateFormat("yyyy"); if (note.noteDate == null) { noteYear
     * = df.format(new Date()); note.noteYear = noteYear; } else { noteYear =
     * df.format(note.noteDate); note.noteYear = noteYear; }
     *
     * note.save(); renderJSON(""); }
     */
    /**
     * Delete a Note.
     */
    public static void delete(long id) {
        long roleid = UserRole.getMaxRoleId(connectedUser());
        Note ynote = Note.findById(id);
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
        //long total=Note.count("approveLevel=?", level);
        long total = Note.count(sql, level);
        String notesStr = Note.toApproveJson(notes);
        String jsonStr = YUtils.ligerGridData(notesStr, total);

        renderJSON(jsonStr);
    }

    public static void approveconfirm(long id) {
        Note note = Note.findById(id);
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

    public static void approvefailsave(Note note, String comment) {
        long id = note.id;
        Note samenote = Note.findById(id);
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

    public static void approvemulti(String selecteds) {
        String[] selarr = selecteds.split(",");
        int len = selarr.length;
        for (int i = 0; i < len; i++) {
            long id = Integer.parseInt(selarr[i]);
            Note note = Note.findById(id);
            note.approveLevel = note.approveLevel + 1;
            note.save();
        }
        renderJSON("");
    }

    //动态树得到档案记录
    public static void filejson(int page, int pagesize, int tag, String userid, String deptid, String checkYear) {
        //List<Note> notes = Note.find("checkList.id=? order by noteDate", id).fetch();    						   
        //long total = Note.count("checkList.id=? order by noteDate", id);
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
    public static void detailfilejson(String deptCombox_val,String userCombox_val, int page, int pagesize, int tag, String begindate, String enddate, String categoryCombox_val, String title) {

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
        //String basesql = "approveLevel=3  and noteDate between ? and ? ";
        //String basesql = "approveLevel=2  and noteDate between ? and ? ";
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

    public static void save(Note notes, String userCombox_val, String deptCombox_val, String goodsCombox, int tag) {
        User user = connectedUser();
        long userId = UserRole.getMaxRoleId(user);
        String[] names = {userCombox_val};
        if (userId != 2) {
            if (userCombox_val.contains(";")) {
                names = userCombox_val.split(";");
            }
        }
       if(notes.id  != null){
              System.out.println("ddd");
              notes.save();
        }else{
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

            if (notes.approveLevel == null) {
                if (userId > 3) {                               //医德医风小组填报不必科室审核
                    notes.approveLevel = (int) userId - 3;
                } else {
                    notes.approveLevel = 0;
                }
            }
            //if (note.user== null)  note.user = connectedUser();
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
}
