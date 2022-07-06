package controllers;

import models.*;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import play.db.jpa.JPA;
import play.modules.excel.RenderExcel;
import utils.YUtils;

import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 考评 Manage Checks related operations.
 */
//@With(ExcelControllerHelper.class)
public class Checks extends Application {

    public static void index(int tag) {
        render(tag);
    }

    public static void resultcount() {
        render();
    }

    //年度考评结果统计(一票否决人员除外)
    public static void resultcountjson(String year, String deptCombox) {
        Calendar cal = Calendar.getInstance();      //默认当前年度
        int yearInt = cal.get(Calendar.YEAR);
        if (year != null && !year.equals("")) {
            yearInt = Integer.valueOf(year);
        }

        String sql= "  select * from("  +
                "  select "+
                "  DT.id as id,"  +yearInt+"   as year, "+
                "  DT.name as dept, "+
                "  ISNULL(A.COUNT ,'') AS excellent, "+
                "  ISNULL(B.COUNT,'') AS well,  "+
                "  ISNULL(C.COUNT,'') AS general,  "+
                "  ISNULL(D.COUNT ,'') AS  shitty, "+
                "  xx.score  "+
                "  from      "+
                "  YDDA_Dept  AS DT   "+
                "  LEFT JOIN ( "+
                "  select  "+
                "  r.year,  "+
                "  d.name , "+
                "  count(*) as COUNT "+
                "  from  "+
                "  YDDA_Record as r, "+
                "  AX_User as u, "+
                "  YDDA_Dept as d "+
                "  where  "+
                "  r.user_id=u.id  "+
                "  and u.dept_id=d.id  "+
                "  and r.grade_id=1   "+
                "  and r.year="  +yearInt+
        "  group by  "+
                "  r.year, "+
                "  d.name  "+
                "  )   as A  ON  DT.name =A.name  "+
                "  LEFT JOIN ( "+
                "  select  "+
                "  r.year,  "+
                "  d.name , "+
                "  count(*) as COUNT  "+
                "  from  "+
                "  YDDA_Record as r, "+
                "  AX_User as u, "+
                "  YDDA_Dept as d  "+
                "  where  "+
                "  r.user_id=u.id  "+
                "  and u.dept_id=d.id  "+
                "  and r.grade_id=2   "+
                "  and r.year="  +yearInt+
        "  group by  "+
                "  r.year, "+
                "  d.name "+
                "  )   as B  ON  DT.name =B.name "+
                "  LEFT JOIN ( "+
                "  select  "+
                "  r.year, "+
                "  d.name, "+
                "  count(*) as COUNT  "+
                "  from  "+
                "  YDDA_Record as r, "+
                "  AX_User as u, "+
                "  YDDA_Dept as d "+
                "  where  "+
                "  r.user_id=u.id  "+
                "  and u.dept_id=d.id  "+
                "  and r.grade_id=3   "+
                "  and r.year="  +yearInt+
        "  group by  "+
                "  r.year, "+
                "  d.name  "+
                "  )   AS C  ON  DT.name =C.name  "+
                "  LEFT JOIN ( "+
                "  select  "+
                "  r.year,  "+
                "  d.name, "+
                "  count(*) as COUNT  "+
                "  from  "+
                "  YDDA_Record as r, "+
                "  AX_User as u, "+
                "  YDDA_Dept as d  "+
                "  where  "+
                "  r.user_id=u.id  "+
                "  and u.dept_id=d.id  "+
                "  and r.grade_id=4   "+
                "  and r.year="  +yearInt+
        "  group by  "+
                "  r.year, "+
                "  d.name  "+
                "  )   AS D  ON  DT.name =D.name  "+
                "  left join ( "+
                "  select "+
                "  r.id,  "+
                "  r.year , "+
                "  d.name , "+
                "  x.realName, "+
                "  x.sex, "+
                "  x.title, "+
                "  g.name  as grade , "+
                "  rd.score as score  "+
                "  from  "+
                "  YDDA_Record as r  "+
                "  left join AX_User as x on r.user_id=x.id   "+
                "  left join YDDA_Dept as d on x.dept_id=d.id  "+
                "  left join YDDA_Grade as g on r.grade_id=g.id  "+
                "  left join ydda_recordDetail as rd on r.id = rd.record_id  "+
                "  where  "+
                "  1=1  "+
                "  and checkLevel_id=3   "+
                "  and r.year="  +yearInt+
        "  )as xx on xx.realName=dt.name "+
                "  where  "+
                "  1=1    "+
                "  union all  "+
                "  select  "+
                "  0 as id, "+ yearInt +"  as 年度, "+
        "  '全院',  "+
                "  ISNULL(A.COUNT ,'') AS 优秀 , "+
                "  ISNULL(B.COUNT,'') AS 良好,  "+
                "  ISNULL(C.COUNT,'') AS 一般, "+
                "  ISNULL(D.COUNT ,'')AS  较差, "+
                "  e.score  from   "+
                "  (select count(*) as COUNT from YDDA_Record as r where  r.grade_id=1   and r.year= "  +yearInt+"  )  as A ,   "+
                "  (select count(*) as COUNT from YDDA_Record as r where  r.grade_id=2   and r.year="  +yearInt+"  )  as B , "+
                "  (select count(*) as COUNT from YDDA_Record as r where  r.grade_id=3   and r.year="  +yearInt+"  )  as C ,  "+
                "  (select count(*) as COUNT from YDDA_Record as r where  r.grade_id=4   and r.year="  +yearInt+"  )  as D , "+
                "  ( "+
                "  select  "+
                "  sum(rd.score) as score  "+
                "  from  "+
                "  YDDA_Record as r  "+
                "  left join AX_User as x on r.user_id=x.id   "+
                "  left join YDDA_Dept as d on x.dept_id=d.id  "+
                "  left join YDDA_Grade as g on r.grade_id=g.id  "+
                "  left join ydda_recordDetail as rd on r.id = rd.record_id  "+
                "  where  "+
                "  1=1   "+
                "  and checkLevel_id=3   "+
                "  and r.year="  +yearInt+
        "  and x.realName in(select name  from YDDA_Dept))e) as rr  ";
        if (deptCombox != null && !deptCombox.equals("")) {        //科室名称
            sql = sql + " WHERE rr.dept='" + deptCombox + "'";
        }else {
            sql= sql +" WHERE 1=1";
        }
        System.out.println("查询语句："+sql);
        Query query = JPA.em().createNativeQuery(sql);
        List filecount = query.getResultList();
        int total = filecount.size();
        HashMap<String, Object> resultGrid = new HashMap<String, Object>();
        resultGrid.put("Rows", filecount);
        resultGrid.put("Total", total);
        renderJSON(resultGrid);
    }

    public static void onevotekilldetail() {
        render();
    }

    public static void onevotekillcount() {
        render();
    }

    //一票否决项目统计表 
    // 由原来的按项目类别、科室分类变成按科室、项目类别分类，并做排序操作  2012-11-23
    public static void onevotekilljson(String year, String deptCombox_val) {
        String wheresql = " and n.approveLevel=2 ";
        Calendar cal = Calendar.getInstance();
        int Year = cal.get(Calendar.YEAR);
        if (year != null && !year.equals("")) {
            Year = Integer.valueOf(year);
            wheresql = wheresql + " and n.noteYear=" + Year;
        }
        System.out.println();
        if (deptCombox_val != null && !deptCombox_val.equals("")) {
            wheresql = wheresql + " and d.name='" + deptCombox_val + "'";
        }
        String sql = " select " +
                    "n.noteYear as 年度," +
                    "d.name as 科室," +
                    "v.id," +
                    "v.title AS 一票否决项目," +
                    "count(*) AS 人次 " +
                "from " +
                    "YDDA_Note  as n ," +
                    "YDDA_VoteKill as v ," +
                    "YDDA_Dept AS d " +
                "where " +
                    "n.voteKill_id=v.id  " +
                    "and n.dept_id=d.id  " + wheresql +
                "  GROUP  BY  " +
                    "n.noteYear," +
                    "d.name," +
                    "v.id," +
                    "v.title " +
                " order  by  " +
                    "d.name," +
                    "v.id ";

        Query query = JPA.em().createNativeQuery(sql);
        List onevotekillcount = query.getResultList();
        int total = onevotekillcount.size();
        HashMap<String, Object> voteGrid = new HashMap<String, Object>();
        voteGrid.put("Rows", onevotekillcount);
        voteGrid.put("Total", total);
        renderJSON(voteGrid);
    }

    /**
     * Get checks json.
     */
    public static void json(int page, int pagesize) {
        List<CheckList> checks = CheckList.find("parentId is null order by OrderNum").fetch(page, pagesize);
        long total = CheckList.count("parentId is null");
        String checkstr = CheckList.toJson(checks);
        String jsonStr = YUtils.ligerGridData(checkstr, total);
        System.out.println();
        renderJSON(jsonStr);
    }

    public static void simplejson() {
        List<CheckList> checks = CheckList.find("order by OrderNum").fetch();
        renderJSON(CheckList.toSimpleJson(checks));
    }

    //取非根节点数据
    public static void leafjson(int page, int pagesize) {
        List<CheckList> checks = CheckList.find(" size(checks) = 0 order by parentId, orderNum").fetch(page, pagesize);
        long total = CheckList.count();
        String checkstr = CheckList.toLeafJson(checks);
        String jsonStr = YUtils.ligerGridData(checkstr, total);
        renderJSON(jsonStr);
    }

    /**
     * Add a CheckList.
     */
    public static void add(CheckList checklist) {
    }

    /**
     * Update a CheckList.
     */
    public static void save(CheckList checklist) {
        checklist.save();
        renderJSON("");
    }

    /**
     * Delete a CheckList.
     */
    public static void delete(long id) {
        CheckList.findById(id)._delete();
        renderJSON("");
    }

    //获取医院信息
    public static void hospital() {
        long id = 1;
        Hospital hp = Hospital.findById(id);
        String hpStr = Hospital.toCheckJson(hp);
        renderJSON(hpStr);
    }

    //获取一票否决信息
    public static void getHaveVoteKill(long UserId, String Year) {
        Note note = Note.findVoteKillNote(UserId, Year);
        renderJSON(note);
    }

    //已有的自我评价信息
    public static void selfhistory(long UserId, int Year) {
        User user = User.findById(UserId);
        Record rd = Record.findByUser(user, Year);
        CheckLevel checkLevel = CheckLevel.findByName("自我评价");
        RecordDetail RD = RecordDetail.findByRecord(rd, checkLevel);
        String rdStr = RecordDetail.toCheckJson(RD);
        System.out.println();
        renderJSON(rdStr);
    }

    public static void depthistory(long UserId, int Year) {
        User user = User.findById(UserId);
        Record rd = Record.findByUser(user, Year);
        CheckLevel checkLevel = CheckLevel.findByName("科室评价");
        RecordDetail RD = RecordDetail.findByRecord(rd, checkLevel);
        System.out.println();
        String rdStr = RecordDetail.toCheckJson(RD);
        renderJSON(rdStr);
    }


    public static void qthistory(long UserId, int Year) {
        User user = User.findById(UserId);
        Record rd = Record.findByUser(user, Year);
        CheckLevel checkLevel = CheckLevel.findByName("其他平台");
        RecordDetail RD = RecordDetail.findByRecord(rd, checkLevel);
        System.out.println();
        String rdStr = RecordDetail.toCheckJson(RD);
        renderJSON(rdStr);
    }

    public static void com_grade_history(long UserId, int Year) {
        User user = User.findById(UserId);
        Record rd = Record.findByUser(user, Year);
        String rdStr = Record.toCheckJson(rd);
        renderJSON(rdStr);
    }

    public static void comhistory(long UserId, int Year) {
        User user = User.findById(UserId);
        Record rd = Record.findByUser(user, Year);
        CheckLevel checkLevel = CheckLevel.findByName("单位评价");
        RecordDetail RD = RecordDetail.findByRecord(rd, checkLevel);
        String rdStr = RecordDetail.toCheckJson(RD);
        System.out.println();
        renderJSON(rdStr);
    }

    public static void complexhistory(long UserId, int Year) {
        User user = User.findById(UserId);
        Record rd = Record.findByUser(user, Year);
        String rdStr = Record.toCheckJson(rd);
        renderJSON(rdStr);
    }

    public static void remarkhistory(long UserId, int Year) {
        User user = User.findById(UserId);
        Record rd = Record.findByUser(user, Year);
        String rdStr = Record.toCheckJson(rd);
        renderJSON(rdStr);
    }

    public static void recordhistory(long UserId, int Year) {
        User user = User.findById(UserId);
        Record rd = Record.findByUser(user, Year);
        String rdStr = Record.toCheckJson(rd);
        renderJSON(rdStr);
    }

    public static void getQitaPingtai(String UserId, String Year) {
        long suifangBY = 0, suifangPP = 0, ydyfBY = 0, ydyfPP = 0;
        String qitaStr = null;
        suifangBY = Note.count("approveLevel=2 and category=2  and certifyMan!='医德医风平台' and createdMan is null");
        suifangPP = Note.count("approveLevel=2 and category=1  and certifyMan!='医德医风平台' and createdMan is null");
        ydyfBY = Note.count("approveLevel=2 and category=2  and certifyMan='医德医风平台'  and createdMan is null");
        System.out.println();
        ydyfPP = Note.count("approveLevel=2 and category=1  and certifyMan='医德医风平台'  and createdMan is null");
        qitaStr = "{\"suifangBY\":" + suifangBY + ",\"suifangPP\":" + suifangPP + ",\"ydyfBY\":"
                + ydyfBY + ",\"ydyfPP\":" + ydyfPP + "}";
        renderJSON(qitaStr);
    }

    //自动计算分数  +自动等级 2012-12-17
    public static void getAutoScore(long UserId, int level, String Year) {
        String initStr = null;
        Long userid = Long.valueOf(UserId);
        //category=1时为基础考评项目分数计算
        //查询出所有的个人基础档案
        //这句程序查出来有问题
        List<Note> notes = Note.find(
//                "category=1 and user.id=? and approveLevel>=? and noteYear=? ",
                "category=1 and user.id=? and approveLevel=? and noteYear=? ",//屏蔽退回档案得分
                userid, 2, Year
        ).fetch();
        int len = notes.size();
        //float initscore = 100;  改为根据设置的分数统计基础分数
        //动态设置基础分数
        float initscore = 0;
        List<CheckList> clist = CheckList.findBaseCheckList();
        for (CheckList c : clist) {
            initscore = initscore + c.score;
        }
        while (len > 0) {
            CheckList cl_first = null;
            CheckList cl = null;
            float score = 0;
            float checklist_score = 0;  //定义考评项目分数
            ArrayList al = new ArrayList();
            for (int i = 0; i < len; i++) {
                Note note = (Note) notes.get(i);
                if (i == 0) {
                    cl_first = note.checkList;   //数列中的第一个考评项目
                    checklist_score = cl_first.score; //考评项目分数赋值
                    if (note.score != null) {
                        score = score + note.score;
                    } else {//第一个项目分数为空了怎么办？
                        score = score + 0;
                        System.out.println();
                    }
                    al.add(i);
                } else {
                    cl = note.checkList;
                    if (cl_first == cl) {    //比较其他档案与第一个是否相同的考评项目，是则累积分数
                        score = score + note.score;
                        al.add(i);
                    }
                }
            }
            /*
             * 下面的操作，循环删除相同考评项目的档案，数列notes会成为一个新的数列
             */
            int len_al = al.size();
            for (int j = 0; j < len_al; j++) {
                Integer indexObj = (Integer) al.get(j);
                int index = indexObj.intValue();
                notes.remove(index - j);
            }
            //判断相同考评项目的档案分数，分数小于项目分数，则减去累积的档案分数，否则减去项目分数
            if (checklist_score + score >= 0) {
                initscore = initscore + score;
            } else {
                initscore = initscore - checklist_score;
                System.out.println();
            }
            len = notes.size(); //重新计算数列长度，直到长度为0
            //循环的去算每一个项目的分数
        }
        //加分项目计算分数
        List<Note> notes_add = Note.find(
//                " category=2 and user.id=? and approveLevel>=? and noteYear=? ",
                " category=2 and user.id=? and approveLevel=? and noteYear=? ",//屏蔽退回档案得分
                userid, 2, Year
        ).fetch();
        int length = notes_add.size();
        for (int k = 0; k < length; k++) {
            Note note = (Note) notes_add.get(k);
            if (note.score != null) {
                initscore = initscore + note.score;
            }
        }
        initStr = String.valueOf(initscore);
        if (level >= 2) {                                       //单位评分 +等级
            long initGrageId = -1;
            Grade youXiuGrade = Grade.findByGradeName("优秀");
            Grade liangHaoGrade = Grade.findByGradeName("良好");
            Grade yiBanGrade = Grade.findByGradeName("一般");
            Grade jiaoChaGrade = Grade.findByGradeName("较差");
            Note note = Note.findVoteKillNote(Long.valueOf(UserId), Year);   //是否含有一票否决信息
            if (note == null) {
                if (initscore >= youXiuGrade.lowLimit) {
                    initGrageId = youXiuGrade.id;
                } else if (initscore >= liangHaoGrade.lowLimit) {
                    initGrageId = liangHaoGrade.id;
                } else if (initscore >= yiBanGrade.lowLimit) {
                    initGrageId = yiBanGrade.id;
                    System.out.println();
                } else {
                    initGrageId = jiaoChaGrade.id;
                }
            } else {
                initGrageId = jiaoChaGrade.id;
            }
            initStr = "{\"initscore\":" + initscore + ",\"initGrageId\":" + initGrageId + "}";
        }
        renderJSON(initStr);
    }
    //保存自我评价
    public static void checkself(Record record, RecordDetail recorddetail, long userID) {
        //区别是添加，还是编辑，取决于record.id是否存在
        User recordUser = User.findById(userID);
        User user = connectedUser();
        CheckLevel checkLevel = CheckLevel.findByName("自我评价");
        Record currentRecord = Record.findByUser(recordUser, record.year);
        if (currentRecord == null) {   //添加档案
            //user调整为档案所属者
            record.user = recordUser;
            record.save();
            recorddetail.record = record;
            recorddetail.user = user;
            recorddetail.checkLevel = checkLevel;
            recorddetail.recordTime = new Date();
            recorddetail.save();
        } else {
            //修改档案              
            RecordDetail RD = RecordDetail.findByRecord(currentRecord, checkLevel);
            if (RD == null) {           //增加自我评价
                System.out.println("");
                recorddetail.record = currentRecord;
                recorddetail.user = user;
                System.out.println();
                recorddetail.checkLevel = checkLevel;
                recorddetail.recordTime = new Date();
                recorddetail.save();
            } else {                   //修改自我评价
                RD.remark = recorddetail.remark;
                RD.recordTime = new Date(); // recorddetail.recordTime;
                RD.user = user;
                RD.score = recorddetail.score;
                RD.signatory = recorddetail.signatory;
                RD.save();
            }
        }
        renderJSON("");
    }
    //保存科室评价
    public static void checkdept(Record record1, RecordDetail recorddetail1, long userID1) {
        System.out.println(userID1);
        User recordUser = User.findById(userID1);
        User user = connectedUser();
        CheckLevel checkLevel = CheckLevel.findByName("科室评价");
        Record currentRecord = Record.findByUser(recordUser, record1.year);
        if (currentRecord == null) {
            record1.user = recordUser;
            record1.save();
            recorddetail1.record = record1;
            recorddetail1.user = user;
            recorddetail1.checkLevel = checkLevel;
            recorddetail1.recordTime = new Date();
            recorddetail1.save();
        } else {
            RecordDetail RD = RecordDetail.findByRecord(currentRecord, checkLevel);
            if (RD == null) {
                recorddetail1.record = currentRecord;
                recorddetail1.user = user;
                recorddetail1.checkLevel = checkLevel;
                recorddetail1.recordTime = new Date();
                recorddetail1.save();
            } else {
                RD.remark = recorddetail1.remark;
                RD.recordTime = new Date();
                RD.user = user;
                RD.score = recorddetail1.score;
                RD.signatory = recorddetail1.signatory;
                System.out.println();
                RD.save();
            }
        }
        renderJSON("");
    }
    //保存单位评价
    public static void checkcom(Record record3, RecordDetail recorddetail3, long gradeCombox_val, long userID3) {
        User recordUser = User.findById(userID3);
        User user = connectedUser();
        CheckLevel checkLevel = CheckLevel.findByName("单位评价");
        Grade grade = Grade.findById(gradeCombox_val);
        Record currentRecord = Record.findByUser(recordUser, record3.year);
        if (currentRecord == null) {
            record3.user = recordUser;
            record3.grade = grade;
            record3.save();
            System.out.println();
            recorddetail3.record = record3;
            recorddetail3.user = user;
            recorddetail3.checkLevel = checkLevel;
            recorddetail3.recordTime = new Date();
            recorddetail3.save();
        } else {
            currentRecord.grade = grade;
            currentRecord.save();
            RecordDetail RD = RecordDetail.findByRecord(currentRecord, checkLevel);
            if (RD == null) {
                recorddetail3.record = currentRecord;
                recorddetail3.user = user;
                recorddetail3.checkLevel = checkLevel;
                recorddetail3.recordTime = new Date();
                System.out.println();
                recorddetail3.save();
            } else {
                RD.recordTime = new Date();
                RD.user = user;
                RD.score = recorddetail3.score;
                RD.save();
            }
        }
        renderJSON("");

    }
    //保存综合评价
    public static void checkcomplex(Record record4, long userID4) {
        User recordUser = User.findById(userID4);
        Record currentRecord = Record.findByUser(recordUser, record4.year);
        if (currentRecord == null) {
            record4.user = recordUser;
            record4.conclusionTime = new Date();
            System.out.println();
            record4.save();
        } else {
            currentRecord.conclusion = record4.conclusion;
            currentRecord.conclusionTime = new Date();
            currentRecord.save();
        }
        renderJSON("");
    }

    //添加综合评价
    //批量处理
    public static void checkcomplexall(Record record4All, String userID4All) {

        String[] users=userID4All.split(";");

        for(int i=0;i<users.length;i++){

            User recordUser = User.findById(Long.valueOf(users[i].trim()));
            Record currentRecord = Record.findByUser(recordUser, record4All.year);

            if (currentRecord == null) {
                Record record = new Record();
                record.user = recordUser;
                record.year=record4All.year;
                record.conclusion = record4All.conclusion;
                record.conclusionTime = new Date();
                record.save();
            } else {
                currentRecord.conclusion = record4All.conclusion;
                currentRecord.conclusionTime = new Date();
                currentRecord.save();
            }
        }
        renderJSON("");
    }
    //保存备注
    public static void checkremark(Record record5, long userID5) {
        User recordUser = User.findById(userID5);
        Record currentRecord = Record.findByUser(recordUser, record5.year);
        if (currentRecord == null) {
            record5.user = recordUser;
            record5.save();
        } else {
            currentRecord.remark = record5.remark;
            currentRecord.save();
        }
        renderJSON("");
    }
    //跳转年度考评结果明细页面
    public static void resultdetail() {
        render();
    }
    //年度考评结果明细
    public static void resultdetailjson(String year, String deptCombox_val,
                                        String gradeCombox_val,String userCombox) {
        //调整  改为全院参与统计（不排除一票否决的信息）
        String sql = "select" +
                        " r.id as id, " +
                        "r.year as year ," +
                        "d.name as dept," +
                        "x.realName as name," +
                        "x.sex as sex," +
                        "x.title as title," +
                        "g.name as grade ," +
                        "rd.score as score, " +
                        "r.isVoteKill as isVoteKill " +
                    "from YDDA_Record as r " +
                        "left join AX_User as x on r.user_id=x.id  " +
                        "left join YDDA_Dept as d on x.dept_id=d.id " +
                        "left join YDDA_Grade as g on r.grade_id=g.id " +
                        "left join ydda_recordDetail as rd on r.id = rd.record_id " +
                    "where  1=1  and checkLevel_id=5 ";

        Calendar cal = Calendar.getInstance();
        int Year = cal.get(Calendar.YEAR);
        if (year != null && !year.equals("")) {
            Year = Integer.valueOf(year);
            sql = sql + " and r.year=" + Year;   //年度
        }
        if (deptCombox_val != null && !deptCombox_val.equals("")) {   //科室名称
            sql = sql + " and x.dept_id='" + deptCombox_val + "'";
        }
        if (userCombox != null && !userCombox.equals("")) {   //科室名称
            sql = sql + " and r.user_id='" + userCombox + "'";
        }
        if (gradeCombox_val != null && !gradeCombox_val.equals("")) {   //等级
            sql = sql + " and r.grade_id='" + gradeCombox_val + "'";
        }
//        System.out.println(sql);
        Query query = JPA.em().createNativeQuery(sql);
        List filedetail = query.getResultList();
        int total = filedetail.size();
        HashMap<String, Object> resultGrid = new HashMap<String, Object>();
        resultGrid.put("Rows", filedetail);
        resultGrid.put("Total", total);
        renderJSON(resultGrid);
    }
    //导出考评结果明细
    public static void exportResultDetailExcel(String year, String dept_id, String grade_id,
                                               String dept,String grade) {
        String excelName = year+"年度";
        if(dept!=null&&!dept.equals("")&&!grade.equals("")&&grade!=null){
            excelName =excelName+ dept+"考评级别-"+grade;
        } else if(dept!=null&&!dept.equals("")) {
            excelName =excelName + dept;
            System.out.println();
        }else if(grade!=null&&!grade.equals("")){
            excelName =excelName +"考评级别-"+ grade;
        }
        String sql = "select " +
                    "r.year as year," +
                    "d.name as dept," +
                    "x.realName as name," +
                    "x.sex as sex," +
                    "x.title as title," +
                    "g.name as grade ," +
                "r.isVoteKill as isVoteKill " +
                "from " +
                    "YDDA_Record as r " +
                    "left join AX_User as x on r.user_id=x.id  " +
                    "left join YDDA_Dept as d on x.dept_id=d.id " +
                    "left join YDDA_Grade as g on r.grade_id=g.id " +
                "where  1=1 ";
        Calendar cal = Calendar.getInstance();
        int Year = cal.get(Calendar.YEAR);
        if (year != null && !year.equals("")) {
            Year = Integer.valueOf(year);
            sql = sql + " and r.year=" + Year;   //年度
        }
        System.out.println();
        if (dept_id != null && !dept_id.equals("")) {   //科室名称
            sql = sql + " and x.dept_id='" + dept_id + "'";
        }
        if (grade_id != null && !grade_id.equals("")) {   //等级
            sql = sql + " and r.grade_id='" + grade_id + "'";
        }
        Query query =JPA.em().createNativeQuery(sql);
        System.out.println("查询语句 ："+sql);
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List lists = query.getResultList();
        request.format = "xls";
        renderArgs.put(RenderExcel.RA_ASYNC, true);
        renderArgs.put(RenderExcel.RA_FILENAME,excelName+"考评结果明细"+ ".xls");
        render(lists);
    }
    //导出Excel年度考评结果统计(一票否决人员除外)
    public static void exportResultCountExcel(String year, String deptCombox) {
        String excelName = year+"年度";
        if(deptCombox!=null&&!deptCombox.equals("")){
            excelName =excelName+ deptCombox;
        }
        Calendar cal = Calendar.getInstance();      //默认当前年度
        int yearInt = cal.get(Calendar.YEAR);
        if (year != null && !year.equals("")) {
            yearInt = Integer.valueOf(year);
        }
        String sql= "  select * from("  +
                "  select "+
                "  DT.id as id,"  +yearInt+"   as year, "+
                "  DT.name as dept, "+
                "  ISNULL(A.COUNT ,'') AS excellent, "+
                "  ISNULL(B.COUNT,'') AS well,  "+
                "  ISNULL(C.COUNT,'') AS general,  "+
                "  ISNULL(D.COUNT ,'') AS  shitty, "+
                "  xx.score  "+
                "  from      "+
                "  YDDA_Dept  AS DT   "+
                "  LEFT JOIN ( "+
                "  select  "+
                "  r.year,  "+
                "  d.name , "+
                "  count(*) as COUNT "+
                "  from  "+
                "  YDDA_Record as r, "+
                "  AX_User as u, "+
                "  YDDA_Dept as d "+
                "  where  "+
                "  r.user_id=u.id  "+
                "  and u.dept_id=d.id  "+
                "  and r.grade_id=1   "+
                "  and r.year="  +yearInt+
                "  group by  "+
                "  r.year, "+
                "  d.name  "+
                "  )   as A  ON  DT.name =A.name  "+
                "  LEFT JOIN ( "+
                "  select  "+
                "  r.year,  "+
                "  d.name , "+
                "  count(*) as COUNT  "+
                "  from  "+
                "  YDDA_Record as r, "+
                "  AX_User as u, "+
                "  YDDA_Dept as d  "+
                "  where  "+
                "  r.user_id=u.id  "+
                "  and u.dept_id=d.id  "+
                "  and r.grade_id=2   "+
                "  and r.year="  +yearInt+
                "  group by  "+
                "  r.year, "+
                "  d.name "+
                "  )   as B  ON  DT.name =B.name "+
                "  LEFT JOIN ( "+
                "  select  "+
                "  r.year, "+
                "  d.name, "+
                "  count(*) as COUNT  "+
                "  from  "+
                "  YDDA_Record as r, "+
                "  AX_User as u, "+
                "  YDDA_Dept as d "+
                "  where  "+
                "  r.user_id=u.id  "+
                "  and u.dept_id=d.id  "+
                "  and r.grade_id=3   "+
                "  and r.year="  +yearInt+
                "  group by  "+
                "  r.year, "+
                "  d.name  "+
                "  )   AS C  ON  DT.name =C.name  "+
                "  LEFT JOIN ( "+
                "  select  "+
                "  r.year,  "+
                "  d.name, "+
                "  count(*) as COUNT  "+
                "  from  "+
                "  YDDA_Record as r, "+
                "  AX_User as u, "+
                "  YDDA_Dept as d  "+
                "  where  "+
                "  r.user_id=u.id  "+
                "  and u.dept_id=d.id  "+
                "  and r.grade_id=4   "+
                "  and r.year="  +yearInt+
                "  group by  "+
                "  r.year, "+
                "  d.name  "+
                "  )   AS D  ON  DT.name =D.name  "+
                "  left join ( "+
                "  select "+
                "  r.id,  "+
                "  r.year , "+
                "  d.name , "+
                "  x.realName, "+
                "  x.sex, "+
                "  x.title, "+
                "  g.name  as grade , "+
                "  rd.score as score  "+
                "  from  "+
                "  YDDA_Record as r  "+
                "  left join AX_User as x on r.user_id=x.id   "+
                "  left join YDDA_Dept as d on x.dept_id=d.id  "+
                "  left join YDDA_Grade as g on r.grade_id=g.id  "+
                "  left join ydda_recordDetail as rd on r.id = rd.record_id  "+
                "  where  "+
                "  1=1  "+
                "  and checkLevel_id=3   "+
                "  and r.year="  +yearInt+
                "  )as xx on xx.realName=dt.name "+
                "  where  "+
                "  1=1    "+
                "  union all  "+
                "  select  "+
                "  0 as id, "+ yearInt +"  as 年度, "+
                "  '全院',  "+
                "  ISNULL(A.COUNT ,'') AS 优秀 , "+
                "  ISNULL(B.COUNT,'') AS 良好,  "+
                "  ISNULL(C.COUNT,'') AS 一般, "+
                "  ISNULL(D.COUNT ,'')AS  较差, "+
                "  e.score  from   "+
                "  (select count(*) as COUNT from YDDA_Record as r where  r.grade_id=1   and r.year= "  +yearInt+"  )  as A ,   "+
                "  (select count(*) as COUNT from YDDA_Record as r where  r.grade_id=2   and r.year="  +yearInt+"  )  as B , "+
                "  (select count(*) as COUNT from YDDA_Record as r where  r.grade_id=3   and r.year="  +yearInt+"  )  as C ,  "+
                "  (select count(*) as COUNT from YDDA_Record as r where  r.grade_id=4   and r.year="  +yearInt+"  )  as D , "+
                "  ( "+
                "  select  "+
                "  sum(rd.score) as score  "+
                "  from  "+
                "  YDDA_Record as r  "+
                "  left join AX_User as x on r.user_id=x.id   "+
                "  left join YDDA_Dept as d on x.dept_id=d.id  "+
                "  left join YDDA_Grade as g on r.grade_id=g.id  "+
                "  left join ydda_recordDetail as rd on r.id = rd.record_id  "+
                "  where  "+
                "  1=1   "+
                "  and checkLevel_id=3   "+
                "  and r.year="  +yearInt+
                "  and x.realName in(select name  from YDDA_Dept))e) as rr  ";
        if (deptCombox != null && !deptCombox.equals("")) {        //科室名称
            sql = sql + " WHERE rr.dept='" + deptCombox + "'";
        }else {
            sql= sql +" WHERE 1=1";
        }
//        System.out.println("导出语句："+sql);
        Query query = JPA.em().createNativeQuery(sql);
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List lists = query.getResultList();
        request.format = "xls";
        renderArgs.put(RenderExcel.RA_ASYNC, true);
        renderArgs.put(RenderExcel.RA_FILENAME,excelName+"考评结果统计"+ ".xls");
        render(lists);
    }

    //export excel 
    public static void exportExcel(long UserId, int Year) {
        User user = User.findById(UserId);
        String jobInTimeS = "";
        if (user.jobInTime != null) {
            java.text.Format formatter = new SimpleDateFormat("yyyy-MM-dd");
            jobInTimeS = formatter.format(user.jobInTime);
        }
        Record rd = Record.findByUser(user, Year);
        Hospital hp = Hospital.findById(1L);
        RecordDetail rd1 = RecordDetail.findByRecordAndCheckLevelName(rd, "自我评价");
        RecordDetail rd2 = RecordDetail.findByRecordAndCheckLevelName(rd, "科室评价");
        RecordDetail rd3 = RecordDetail.findByRecordAndCheckLevelName(rd, "其他平台");
        RecordDetail rd4 = RecordDetail.findByRecordAndCheckLevelName(rd, "单位评价");
        java.text.Format format = new SimpleDateFormat("yyyy年MM月dd日");
        String conclusionTimeS = "年  月  日", rd1_recordTimeS = "年  月  日",
                rd2_recordTimeS = "年  月  日", rd4_recordTimeS = "年  月  日";
        if (rd != null && rd.conclusionTime != null) {

            conclusionTimeS = format.format(rd.conclusionTime);

        }
        if (rd1 != null && rd1.recordTime != null) {
            rd1_recordTimeS = format.format(rd1.recordTime);
        }
        if (rd2 != null && rd2.recordTime != null) {
            rd2_recordTimeS = format.format(rd2.recordTime);
        }
        if (rd4 != null && rd4.recordTime != null) {
            rd4_recordTimeS = format.format(rd4.recordTime);
        }

        long suifangBY = 0, suifangPP = 0, ydyfBY = 0, ydyfPP = 0;
        if (rd3 == null) {
            System.out.println();
            suifangBY =
                    Note.count("approveLevel>=2 and category=2  and certifyMan!='医德医风平台' and createdMan is null");
            suifangPP =
                    Note.count("approveLevel>=2 and category=1  and certifyMan!='医德医风平台' and createdMan is null");
            ydyfBY =
                    Note.count("approveLevel>=2 and category=2  and certifyMan='医德医风平台'  and createdMan is null");
            ydyfPP =
                    Note.count("approveLevel>=2 and category=1  and certifyMan='医德医风平台'  and createdMan is null");
        } else {
            suifangBY = rd3.suifangBY;
            suifangPP = rd3.suifangPP;
            ydyfBY = rd3.ydyfBY;
            ydyfPP = rd3.ydyfPP;
        }
        String isVoteKill = "无";
        if (rd != null && rd.status != null && rd.status == 1 && rd.isVoteKill == true) {
            isVoteKill = "有";
        } else {
            Note note = Note.findVoteKillNote(UserId, String.valueOf(Year));
            isVoteKill = (note == null) ? "无" : "有";
        }
        request.format = "xls";
        renderArgs.put(RenderExcel.RA_ASYNC, true);
        renderArgs.put(RenderExcel.RA_FILENAME, "医德考评表-" + user.realName + ".xls");
        render(user, rd, rd1, rd2, rd3, rd4, hp, jobInTimeS, conclusionTimeS, rd1_recordTimeS, rd2_recordTimeS,
                rd4_recordTimeS, suifangBY, suifangPP, ydyfBY, ydyfPP, isVoteKill);
    }
    //exportExcelAll
    public static void exportExcelAll(int Year) {
        List<User> list = User.all().fetch();
        List li = new ArrayList();
        User user = list.get(0);
        System.out.println(user.realName);
        long UserId = user.id;
        String jobInTimeS = "";
        if (user.jobInTime != null) {
            java.text.Format formatter = new SimpleDateFormat("yyyy-MM-dd");
            jobInTimeS = formatter.format(user.jobInTime);
        }
        Record rd = Record.findByUser(user, Year);
        Hospital hp = Hospital.findById(1L);
        RecordDetail rd1 = RecordDetail.findByRecordAndCheckLevelName(rd, "自我评价");
        RecordDetail rd2 = RecordDetail.findByRecordAndCheckLevelName(rd, "科室评价");
        RecordDetail rd3 = RecordDetail.findByRecordAndCheckLevelName(rd, "其他平台");
        RecordDetail rd4 = RecordDetail.findByRecordAndCheckLevelName(rd, "单位评价");
        java.text.Format format = new SimpleDateFormat("yyyy年MM月dd日");
        String conclusionTimeS = "年  月  日", rd1_recordTimeS = "年  月  日",
                rd2_recordTimeS = "年  月  日", rd4_recordTimeS = "年  月  日";
        if (rd != null && rd.conclusionTime != null) {

            conclusionTimeS = format.format(rd.conclusionTime);

        }
        if (rd1 != null && rd1.recordTime != null) {
            rd1_recordTimeS = format.format(rd1.recordTime);
        }
        if (rd2 != null && rd2.recordTime != null) {
            rd2_recordTimeS = format.format(rd2.recordTime);
        }
        if (rd4 != null && rd4.recordTime != null) {
            rd4_recordTimeS = format.format(rd4.recordTime);
        }

        long suifangBY = 0, suifangPP = 0, ydyfBY = 0, ydyfPP = 0;
        if (rd3 == null) {
            suifangBY =
                    Note.count("approveLevel>=2 and category=2  and certifyMan!='医德医风平台' and createdMan is null");
            suifangPP =
                    Note.count("approveLevel>=2 and category=1  and certifyMan!='医德医风平台' and createdMan is null");
            ydyfBY =
                    Note.count("approveLevel>=2 and category=2  and certifyMan='医德医风平台'  and createdMan is null");
            ydyfPP =
                    Note.count("approveLevel>=2 and category=1  and certifyMan='医德医风平台'  and createdMan is null");
        } else {
            suifangBY = rd3.suifangBY;
            suifangPP = rd3.suifangPP;
            ydyfBY = rd3.ydyfBY;
            ydyfPP = rd3.ydyfPP;
        }
        String isVoteKill = "无";
        if (rd != null && rd.status != null && rd.status == 1 && rd.isVoteKill == true) {
            isVoteKill = "有";
        } else {
            Note note = Note.findVoteKillNote(UserId, String.valueOf(Year));
            isVoteKill = (note == null) ? "无" : "有";
        }
        Object[] obj = new Object[17];
        obj[0] = user;
        obj[1] = rd;
        obj[2] = rd1;
        obj[3] = rd2;
        obj[4] = rd3;
        obj[5] = rd4;
        obj[6] = hp;
        obj[7] = jobInTimeS;
        obj[8] = conclusionTimeS;
        obj[9] = rd1_recordTimeS;
        obj[10] = rd2_recordTimeS;
        obj[11] = rd4_recordTimeS;
        obj[12] = suifangBY;
        obj[13] = suifangPP;
        obj[14] = ydyfBY;
        obj[15] = ydyfPP;
        obj[16] = isVoteKill;
        li.add(user);
        request.format = "xls";
        renderArgs.put(RenderExcel.RA_ASYNC, true);
        renderArgs.put(RenderExcel.RA_FILENAME, "医德考评表-全院所有人员.xls");
        render(li);
    }
    //导出Excel一票否决项目明细
    public static void exportOneVoteKillDetailExcel(String year, String deptCombox_val,String deptName) {
        String excelName = year+"年度";
        String sql = "select" +
                    " n.noteYear as year," +
                    "Convert(varchar(10), n.updatedDate,21) as operationTime," +
                    "d.name as deptName," +
                    "u.realName as userName," +
                    "content as killSubject " +
                "from " +
                    "YDDA_Note as n " +
                    "left join YDDA_Dept as d on n.dept_id=d.id " +
                    "left join AX_User as u on n.user_id = u.id " +
                "where " +
                    "voteKill_id is not NULL  " +
                    "and adjustList_id is NULL " +
                    "and checkList_id is NULL " +
                    "and n.approveLevel=2  " +
                    "and n.category=3 ";
        if(deptName!=null&&!deptName.equals("")){
            excelName =excelName+ deptName;
        }
        System.out.println();
        if (year != null && !year.equals("")) {
            sql = sql + " and noteYear='" + year + "'";
        }
        if (deptCombox_val != null && !deptCombox_val.equals("")) {
            long deptId = Long.parseLong(deptCombox_val);
            sql = sql + " and d.id=" + deptId;
        }
        Query query = JPA.em().createNativeQuery(sql);
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List lists = query.getResultList();
        request.format = "xls";
        renderArgs.put(RenderExcel.RA_ASYNC, true);
        renderArgs.put(RenderExcel.RA_FILENAME,excelName+"一票否决项目明细"+ ".xls");
        render(lists);
    }
    //导出excel一票否决项目统计表
    public static void exportOneVoteKillCountExcel(String year, String deptCombox_val) {
        String excelName = year+"年度";
        if(deptCombox_val!=null&&!deptCombox_val.equals("")){
            excelName =excelName+ deptCombox_val;
        }
        String wheresql = " and n.approveLevel=2    ";
        Calendar cal = Calendar.getInstance();
        int Year = cal.get(Calendar.YEAR);
        if (year != null && !year.equals("")) {
            Year = Integer.valueOf(year);
            wheresql = wheresql + " and n.noteYear=" + Year;
            System.out.println();
        }
        if (deptCombox_val != null && !deptCombox_val.equals("")) {
            wheresql = wheresql + " and d.name='" + deptCombox_val + "'";
        }
        String sql = " select" +
                    " n.noteYear as year," +
                    "d.name as dept," +
                    "v.id," +
                    "V.title AS voteKill," +
                    "count(*) AS persionTime " +
                "from " +
                    "YDDA_Note  as n ," +
                    "YDDA_VoteKill as v ," +
                    "YDDA_Dept AS d " +
                "where " +
                    "n.voteKill_id=v.id  " +
                    "and n.dept_id=d.id  " + wheresql +
                " GROUP  BY  " +
                    "n.noteYear," +
                    "d.name," +
                    "v.id," +
                    "v.title " +
                "order  by  " +
                    "d.name," +
                    "v.id ";
        Query query = JPA.em().createNativeQuery(sql);
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List lists = query.getResultList();
        request.format = "xls";
        renderArgs.put(RenderExcel.RA_ASYNC, true);
        renderArgs.put(RenderExcel.RA_FILENAME,excelName+"一票否决项目统计"+ ".xls");
        render(lists);
    }
    //导出excel表扬信、花篮等统计表
    public static void exportCommendCountExcel(String year, String deptCombox_val,String deptName) {
        String excelName = year+"年度";
        if(deptName!=null&&!deptName.equals("")){
            excelName =excelName+ deptName;
        }
        String yearSQL = "";
        String deptSQL = "";
        System.out.println();
        Calendar cal = Calendar.getInstance();
        int Year = cal.get(Calendar.YEAR);
        if (year != null && !year.equals("")) {
            Year = Integer.valueOf(year);
        }
        yearSQL = " and noteYear=" + Year;
        if (deptCombox_val != null && !deptCombox_val.equals("")) {
            deptSQL = " and d.id=" + deptCombox_val ;
        }
        String sql = "select '" +
                    Year + "' as year," +
                    "d.name as deptName," +
                    "isnull(a.sum,0) as praiseMessage," +
                    "isnull(b.sum,0) as championFlag," +
                    "isnull(c.sum,0) as flowersBasket," +
                    "isnull(e.sum,0) as pressie " +
                "from " +
                    "YDDA_Dept d " +
                    "left join(" +
                        "select " +
                            "noteYear," +
                            "dept_id, " +
                            "COUNT(*) as sum " +
                        "from  " +
                            "YDDA_Note  " +
                        "where   " +
                            "goods='表扬信' " + yearSQL +
                        " group by " +
                            "noteYear," +
                            "dept_id " +
                    ") as a on d.id=a.dept_id " +
                    "left join(" +
                        "select " +
                            "noteYear," +
                            "dept_id, " +
                            "COUNT(*) as sum " +
                        "from  " +
                            "YDDA_Note  " +
                        "where   " +
                            "goods='锦旗'   " + yearSQL +
                        " group by " +
                            "noteYear," +
                            "dept_id " +
                    ") as b on d.id=b.dept_id  " +
                    "left join (" +
                        "select " +
                            "noteYear," +
                            "dept_id, " +
                            "COUNT(*) as sum " +
                        "from  " +
                            "YDDA_Note  " +
                        "where   " +
                            "goods='花篮'   " + yearSQL + " " +
                        "group by" +
                            " noteYear," +
                            " dept_id " +
                    ") as c on d.id=c.dept_id  " +
                    "left join (" +
                        "select " +
                            "noteYear," +
                            "dept_id, " +
                            "COUNT(*) as sum " +
                        "from  " +
                            "YDDA_Note  " +
                        "where   " +
                            "goods='礼品盒' " + yearSQL + " " +
                        "group by " +
                            "noteYear," +
                            "dept_id " +
                    ") as e on d.id=e.dept_id  " +
                "where 1=1 " + deptSQL;
        Query query = JPA.em().createNativeQuery(sql);
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List lists = query.getResultList();
        request.format = "xls";
        renderArgs.put(RenderExcel.RA_ASYNC, true);
        renderArgs.put(RenderExcel.RA_FILENAME,excelName+"表扬信、花篮等统计"+ ".xls");
        render(lists);
    }
    //导出excel表扬信、花篮等一览表
    public static void exportCommendDetailExcel(String year, String deptCombox_val,String deptName) {
        String excelName = year+"年度";
        if(deptName!=null&&!deptName.equals("")){
            excelName =excelName+ deptName;
        }
        String wheresql = " and n.approveLevel=2    ";
        Calendar cal = Calendar.getInstance();
        int Year = cal.get(Calendar.YEAR);
        if (year != null && !year.equals("")) {
            Year = Integer.valueOf(year);
            wheresql = wheresql + " and n.noteYear=" + Year;
        }
        if (deptCombox_val != null && !deptCombox_val.equals("")) {
            wheresql = wheresql + " and d.id=" + deptCombox_val;
            System.out.println();
        }
        String sql =" select  " +
                    "n.noteYear as year," +
                    "Convert(varchar(10)," +
                    " n.updatedDate,21) as operationTime," +
                    "d.name as deptName ," +
                    "u.realName as userName," +
                    "u.sex as sex ," +
                    "u.title as userTitle," +
                    "ad.title as subjectNmae ," +
                    "n.goods as goods " +
                "from " +
                    "YDDA_Note as n " +
                    "left join YDDA_Dept as d on n.dept_id=d.id " +
                    "left join AX_User as u on n.user_id = u.id  " +
                    "left join YDDA_AdjustList as ad on n.adjustList_id= ad.id " +
                "where " +
                    " n.goods is not NULL " +
                    "and n.goods!='拒收红包'  " + wheresql;
        Query query = JPA.em().createNativeQuery(sql);
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List lists = query.getResultList();
        request.format = "xls";
        renderArgs.put(RenderExcel.RA_ASYNC, true);
        renderArgs.put(RenderExcel.RA_FILENAME,excelName+"表扬信、花篮等一览表"+ ".xls");
        render(lists);
    }
    //导出excel收取/拒收红包统计
    public static void exportRedPacketCountExcel(String year, String deptCombox_val,String deptName) {
        System.out.println();
        String excelName = year+"年度";
        if(deptName!=null&&!deptName.equals("")){
            excelName =excelName + deptName;
        }
        String yearSQL = "";
        String deptSQL = "";
        Calendar cal = Calendar.getInstance();
        int Year = cal.get(Calendar.YEAR);
        if (year != null && !year.equals("")) {
            Year = Integer.valueOf(year);
        }
        yearSQL = " and noteYear=" + Year;
        if (deptCombox_val != null && !deptCombox_val.equals("")) {
            deptSQL = " and d.id=" + deptCombox_val;
        }
        String sql = " select  '" +
                     Year + "' as year," +
                    "d.name as deptName," +
                    "isnull(a.sum,0) as yesRedPacket," +
                    "isnull(b.sum,0) as noRedPacket  " +
                "from " +
                    "YDDA_Dept d   " +
                    "left join (" +
                        "select " +
                            "noteYear," +
                            "dept_id, " +
                            "COUNT(*) as sum " +
                        "from  " +
                            "YDDA_Note  " +
                        "where   " +
                            "category=2 " +
                            "and goods='收红包'  " + yearSQL +
                        " group by " +
                            "noteYear," +
                            "dept_id " +
                    ") as a on d.id=a.dept_id   " +
                    "left join (" +
                        "select" +
                            " noteYear," +
                            "dept_id, " +
                            "COUNT(*) as sum " +
                        "from  " +
                            "YDDA_Note  " +
                        "where   " +
                            "category=2 " +
                            "and goods='拒收红包' " + yearSQL +
                        " group by " +
                            "noteYear," +
                            "dept_id " +
                    ") as b on d.id=b.dept_id   " +
                "where 1=1" + deptSQL;
        Query query = JPA.em().createNativeQuery(sql);
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List lists = query.getResultList();
        request.format = "xls";
        renderArgs.put(RenderExcel.RA_ASYNC, true);
        renderArgs.put(RenderExcel.RA_FILENAME,excelName+"收取、拒收红包统计"+ ".xls");
        render(lists);
    }
    //导出excel收取/拒收红包明细
    public static void exportRedPacketDetailExcel(String year, String deptCombox_val,String deptName) {
        String excelName = year+"年度";
        if(deptName!=null&&!deptName.equals("")){
            excelName =excelName+ deptName;
        }
        String wheresql = " and n.approveLevel=2    ";
        Calendar cal = Calendar.getInstance();
        System.out.println();
        int Year = cal.get(Calendar.YEAR);
        if (year != null && !year.equals("")) {
            Year = Integer.valueOf(year);
            wheresql = wheresql + " and n.noteYear=" + Year;
        }
        if (deptCombox_val != null && !deptCombox_val.equals("")) {
            wheresql = wheresql + " and d.name='" + deptCombox_val + "'";
        }
        String sql ="select " +
                    "n.noteYear as year," +
                    "Convert(varchar(10), n.updatedDate,21) as operationTime," +
                    "d.name as deptName ," +
                    "u.realName as userName," +
                    "u.sex as sex ," +
                    "u.title as userTitle," +
                    "n.goods as goods " +
                "from " +
                    "YDDA_Note as n " +
                    "left join YDDA_Dept as d on n.dept_id = d.id " +
                    "left join AX_User as u  on n.user_id = u.id " +
                "where " +
                    "n.category=2 " +
                    "and n.goods ='拒收红包' " +
                    "or n.goods='收红包' ";
        Query query = JPA.em().createNativeQuery(sql);
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List lists = query.getResultList();
        request.format = "xls";
        renderArgs.put(RenderExcel.RA_ASYNC, true);
        renderArgs.put(RenderExcel.RA_FILENAME,excelName+"收取红包明细"+ ".xls");
        render(lists);
    }
    //一票否决项目明细  2012-11-22
    public static void onevotekilldetailjson(String year, String deptCombox_val) {
        List<Note> notes = null;
        long total = 0;

        String sql = "approveLevel=2  and category=3 ";
        if (year != null && !year.equals("")) {
            sql = sql + " and noteYear='" + year + "'";
        }
        if (deptCombox_val != null && !deptCombox_val.equals("")) {
            long deptId = Long.parseLong(deptCombox_val);
            sql = sql + " and dept.id=" + deptId;
        }
        notes = Note.find(sql).fetch();
        total = Note.count(sql);
        System.out.println();
        String notesStr = Note.toCheckJson(notes);
        String jsonStr = YUtils.ligerGridData(notesStr, total);
        renderJSON(jsonStr);
    }
    //跳转表彰情况一览表页面
    public static void commenddetail() {
        render();
    }
    //跳转表彰情况统计页面
    public static void commendcount() {
        render();
    }
    //表彰情况一览表数据  2012-11-22
    public static void commenddetailjson(String year, String deptCombox_val) {
        List<Note> notes = null;
        long total = 0;
        String sql = "category=2 and goods!=null and goods!='拒收红包'";
        if (year != null && !year.equals("")) {
            sql = sql + " and noteYear='" + year + "'";
        }
        if (deptCombox_val != null && !deptCombox_val.equals("")) {
            long deptId = Long.parseLong(deptCombox_val);
            sql = sql + " and dept.id=" + deptId;
        }
        notes = Note.find(sql).fetch();
        total = Note.count(sql);
        String notesStr = Note.toCheckJson(notes);
        String jsonStr = YUtils.ligerGridData(notesStr, total);
        System.out.println();
        renderJSON(jsonStr);
    }
    //表彰情况一览表统计数据  2012-11-23
    public static void commendcountjson(String year, String deptCombox) {
        String yearSQL = "";
        String deptSQL = "";
        Calendar cal = Calendar.getInstance();
        int Year = cal.get(Calendar.YEAR);
        System.out.println();
        if (year != null && !year.equals("")) {
            Year = Integer.valueOf(year);
        }
        yearSQL = " and noteYear=" + Year;
        if (deptCombox != null && !deptCombox.equals("")) {
            deptSQL = " and d.name='" + deptCombox + "'";
        }
        String sql = "select '" +
                        Year + "' as 年度," +
                        "d.name as 科室," +
                        "isnull(a.sum,0) as 表扬信," +
                        "isnull(b.sum,0) as 锦旗," +
                        "isnull(c.sum,0) as 花篮," +
                        "isnull(e.sum,0) as 礼品盒 " +
                "from YDDA_Dept d " +
                        "left join(" +
                            "select " +
                                "noteYear," +
                                "dept_id, " +
                                "COUNT(*) as sum " +
                            "from  " +
                                "YDDA_Note  " +
                            "where   " +
                                "goods='表扬信' " + yearSQL +
                            " group by " +
                                "noteYear," +
                                "dept_id " +
                        ") as a on d.id=a.dept_id " +
                        "left join(" +
                            "select " +
                                "noteYear," +
                                "dept_id, " +
                                "COUNT(*) as sum " +
                            "from  " +
                                "YDDA_Note  " +
                            "where   " +
                                "goods='锦旗'   " + yearSQL +
                            " group by " +
                                "noteYear," +
                                "dept_id " +
                        ") as b on d.id=b.dept_id  " +
                        "left join (" +
                            "select " +
                                "noteYear," +
                                "dept_id, " +
                                "COUNT(*) as sum " +
                            "from  " +
                                "YDDA_Note  " +
                            "where   " +
                                "goods='花篮'   " + yearSQL + " " +
                            "group by" +
                                " noteYear," +
                                " dept_id " +
                        ") as c on d.id=c.dept_id  " +
                        "left join (" +
                            "select " +
                                "noteYear," +
                                "dept_id, " +
                                "COUNT(*) as sum " +
                            "from  " +
                                "YDDA_Note  " +
                            "where   " +
                                "goods='礼品盒' " + yearSQL + " " +
                            "group by " +
                                "noteYear," +
                                "dept_id " +
                        ") as e on d.id=e.dept_id  " +
                "where 1=1 " + deptSQL;

        Query query = JPA.em().createNativeQuery(sql);
        List commendcount = query.getResultList();
        int total = commendcount.size();
        HashMap<String, Object> voteGrid = new HashMap<String, Object>();
        voteGrid.put("Rows", commendcount);
        voteGrid.put("Total", total);
        renderJSON(voteGrid);

    }

    public static void redpacketdetail() {
        render();
    }

    public static void redpacketcount() {
        render();
    }
    // 收取/拒收红包明细
    public static void redpacketdetailjson(String year, String deptCombox_val) {
        List<Note> notes = null;
        long total = 0;
        String sql = " category=2 and goods ='拒收红包' or goods='收红包' ";
        if (year != null && !year.equals("")) {
            sql = sql + " and noteYear='" + year + "'";
        }
        if (deptCombox_val != null && !deptCombox_val.equals("")) {
            long deptId = Long.parseLong(deptCombox_val);
            sql = sql + " and dept.id=" + deptId;
        }
        notes = Note.find(sql).fetch();
        System.out.println();
        total = Note.count(sql);
        String notesStr = Note.toCheckJson(notes);
        String jsonStr = YUtils.ligerGridData(notesStr, total);
        renderJSON(jsonStr);
    }
    // 收取/拒收红包统计
    public static void redpacketcountjson(String year, String deptCombox) {
        String yearSQL = "";
        String deptSQL = "";
        Calendar cal = Calendar.getInstance();
        int Year = cal.get(Calendar.YEAR);
        if (year != null && !year.equals("")) {
            Year = Integer.valueOf(year);
        }
        yearSQL = " and noteYear=" + Year;
        if (deptCombox != null && !deptCombox.equals("")) {
            deptSQL = " and d.name='" + deptCombox + "'";
        }
        String sql = " select  '" +
                    Year + "' as 年度," +
                    "d.name as 科室," +
                    "isnull(a.sum,0) as 收红包," +
                    "isnull(b.sum,0) as 拒收红包  " +
                "from " +
                    "YDDA_Dept d   " +
                    "left join (" +
                        "select " +
                            "noteYear," +
                            "dept_id, " +
                            "COUNT(*) as sum " +
                        "from  " +
                            "YDDA_Note  " +
                        "where   " +
                            "category=2 " +
                            "and goods='收红包'  " + yearSQL +
                        "  group by " +
                            "noteYear," +
                            "dept_id " +
                    ") as a on d.id=a.dept_id   " +
                    "left join (" +
                        "select" +
                            " noteYear," +
                            "dept_id, " +
                            "COUNT(*) as sum " +
                        "from  " +
                            "YDDA_Note  " +
                        "where   " +
                            "category=2 " +
                            "and goods='拒收红包' " + yearSQL +
                        " group by " +
                            "noteYear," +
                            "dept_id " +
                ") as b on d.id=b.dept_id   " +
                "where 1=1" + deptSQL;
        System.out.println(sql);
        Query query = JPA.em().createNativeQuery(sql);
        List redpacketcount = query.getResultList();
        int total = redpacketcount.size();
        HashMap<String, Object> voteGrid = new HashMap<String, Object>();
        voteGrid.put("Rows", redpacketcount);
        voteGrid.put("Total", total);
        renderJSON(voteGrid);
    }

    //计算其它平台得分情况（随访、医德医风）
    public static float getQitaPingtaiScore(String UserId, String Year) {
        Long userid = Long.valueOf(UserId);
        //基础考评项目分数
        List<Note> notes =
                Note.find(
                    " approveLevel>=2  and  category=1  and createdMan is null  and user.id=? and noteYear=? ",
                    userid, Year
                ).fetch();
        int len = notes.size();
        CheckList initcl = CheckList.find("title='其它平台表扬批评评价'").first();
        float initscore = initcl.score;
        float score = 0;
        System.out.println();
        for (int i = 0; i < len; i++) {
            score = score + notes.get(i).score;
        }
        if ((initscore + score) >= 0) {
            initscore = initscore + score;
        }
        //加分项目分数
        List<Note> notes_add = Note.find(
                "approveLevel>=2 and category=2  and createdMan is null and user.id=?  and noteYear=? ",
                userid, Year
        ).fetch();
        int length = notes_add.size();
        for (int k = 0; k < length; k++) {
            Note note = (Note) notes_add.get(k);
            if (note.score != null) {
                initscore = initscore + note.score;
            }
        }
        return initscore;
    }

    // 归档保存
    public static void arsave(
            String UserId, String Year, String suifangBY, String suifangPP,
             String ydyfBY, String ydyfPP, String isVoteKill
    ) {
        User user = User.findById(Long.parseLong(UserId));
        Record record = Record.findByUser(user, Integer.parseInt(Year));
        boolean isvk = ("有".equals(isVoteKill)) ? true : false;
        record.status = 1;
        record.isVoteKill = isvk;
        record.archiveTime = new Date();
        record.archiveMan = connectedUser().realName;
        record.save();
        CheckLevel checkLevel = CheckLevel.findByName("其他平台");
        RecordDetail RD = RecordDetail.findByRecord(record, checkLevel);
        if (RD == null) {
            RecordDetail rdnew = new RecordDetail();
            rdnew.record = record;
            rdnew.checkLevel = checkLevel;
            rdnew.suifangBY = (suifangBY == null || suifangBY.equals("")) ? 0 : Integer.parseInt(suifangBY);
            rdnew.suifangPP = (suifangPP == null || suifangPP.equals("")) ? 0 : Integer.parseInt(suifangPP);
            System.out.println();
            rdnew.ydyfBY = (ydyfBY == null || ydyfBY.equals("")) ? 0 : Integer.parseInt(ydyfBY);
            rdnew.ydyfPP = (ydyfPP == null || ydyfPP.equals("")) ? 0 : Integer.parseInt(ydyfPP);
            rdnew.save();
        } else {
            RD.suifangBY = (suifangBY == null || suifangBY.equals("")) ? 0 : Integer.parseInt(suifangBY);
            RD.suifangPP = (suifangPP == null || suifangPP.equals("")) ? 0 : Integer.parseInt(suifangPP);
            RD.ydyfBY = (ydyfBY == null || ydyfBY.equals("")) ? 0 : Integer.parseInt(ydyfBY);
            System.out.println();
            RD.ydyfPP = (ydyfPP == null || ydyfPP.equals("")) ? 0 : Integer.parseInt(ydyfPP);
            RD.save();
        }
        renderJSON("");
    }

    /**
     * 加载分数
     * @param dept
     * @param realName
     * @param page
     * @param pagesize
     * @param tag
     * @param beginDate
     * @param endDate
     * @param category
     * @param approveLevel
     */
    public static void score(String dept,String realName, int page, int pagesize, int tag,
                                  String beginDate, String endDate, String category,
                                  String approveLevel) {
        String initStr = null;
        User currentuser = connectedUser();
        long userid = currentuser.getId();
        Calendar a=Calendar.getInstance();
        String Year= String.valueOf(a.get(Calendar.YEAR));
        int level=0;
        List<Note> notes = Note.find(
//                "category=1 and user.id=? and approveLevel>=? and noteYear=? ",
                "category=1 and user.id=? and approveLevel=? and noteYear=? ",//屏蔽退回档案得分
                userid, 2, Year
        ).fetch();
        int len = notes.size();
        //float initscore = 100;  改为根据设置的分数统计基础分数
        //动态设置基础分数
        float initscore = 0;
        List<CheckList> clist = CheckList.findBaseCheckList();
        for (CheckList c : clist) {
            initscore = initscore + c.score;
        }
        while (len > 0) {
            CheckList cl_first = null;
            CheckList cl = null;
            float score = 0;
            float checklist_score = 0;  //定义考评项目分数
            ArrayList al = new ArrayList();
            for (int i = 0; i < len; i++) {
                Note note = (Note) notes.get(i);
                if (i == 0) {
                    cl_first = note.checkList;   //数列中的第一个考评项目
                    checklist_score = cl_first.score; //考评项目分数赋值
                    if (note.score != null) {
                        score = score + note.score;
                    } else {//第一个项目分数为空了怎么办？
                        score = score + 0;
                        System.out.println();
                    }
                    al.add(i);
                } else {
                    cl = note.checkList;
                    if (cl_first == cl) {    //比较其他档案与第一个是否相同的考评项目，是则累积分数
                        score = score + note.score;
                        al.add(i);
                    }
                }
            }
            /*
             * 下面的操作，循环删除相同考评项目的档案，数列notes会成为一个新的数列
             */
            int len_al = al.size();
            for (int j = 0; j < len_al; j++) {
                Integer indexObj = (Integer) al.get(j);
                int index = indexObj.intValue();
                notes.remove(index - j);
            }
            //判断相同考评项目的档案分数，分数小于项目分数，则减去累积的档案分数，否则减去项目分数
            if (checklist_score + score >= 0) {
                initscore = initscore + score;
            } else {
                initscore = initscore - checklist_score;
                System.out.println();
            }
            len = notes.size(); //重新计算数列长度，直到长度为0
            //循环的去算每一个项目的分数
        }
        //加分项目计算分数
        List<Note> notes_add = Note.find(
//                " category=2 and user.id=? and approveLevel>=? and noteYear=? ",
                " category=2 and user.id=? and approveLevel=? and noteYear=? ",//屏蔽退回档案得分
                userid, 2, Year
        ).fetch();
        int length = notes_add.size();
        for (int k = 0; k < length; k++) {
            Note note = (Note) notes_add.get(k);
            if (note.score != null) {
                initscore = initscore + note.score;
            }
        }
        initStr = String.valueOf(initscore);
        if (level >= 2) {                                       //单位评分 +等级
            long initGrageId = -1;
            Grade youXiuGrade = Grade.findByGradeName("优秀");
            Grade liangHaoGrade = Grade.findByGradeName("良好");
            Grade yiBanGrade = Grade.findByGradeName("一般");
            Grade jiaoChaGrade = Grade.findByGradeName("较差");
            Note note = Note.findVoteKillNote(Long.valueOf(userid), Year);   //是否含有一票否决信息
            if (note == null) {
                if (initscore >= youXiuGrade.lowLimit) {
                    initGrageId = youXiuGrade.id;
                } else if (initscore >= liangHaoGrade.lowLimit) {
                    initGrageId = liangHaoGrade.id;
                } else if (initscore >= yiBanGrade.lowLimit) {
                    initGrageId = yiBanGrade.id;
                    System.out.println();
                } else {
                    initGrageId = jiaoChaGrade.id;
                }
            } else {
                initGrageId = jiaoChaGrade.id;
            }
            initStr = "{\"initscore\":" + initscore + ",\"initGrageId\":" + initGrageId + "}";
        }
        renderJSON(initStr);
    }

}