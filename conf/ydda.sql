/*
MySQL Data Transfer
Source Host: localhost
Source Database: ydda
Target Host: localhost
Target Database: ydda
Date: 2012-4-16 14:09:58
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for adjustlist
-- ----------------------------
CREATE TABLE `adjustlist` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `award` double NOT NULL,
  `orderNum` int(11) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `score` double NOT NULL,
  `syncadjust` int(11) DEFAULT NULL,
  `syncaward` double NOT NULL,
  `syncscore` double NOT NULL,
  `target` int(11) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `checkList_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKEC9AD8ED8FA61D76` (`checkList_id`),
  CONSTRAINT `FKEC9AD8ED8FA61D76` FOREIGN KEY (`checkList_id`) REFERENCES `checklist` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for checklist
-- ----------------------------
CREATE TABLE `checklist` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `orderNum` int(11) DEFAULT NULL,
  `remark` varchar(1000) DEFAULT NULL,
  `score` double NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8F3065A6670A57F2` (`parent_id`),
  CONSTRAINT `FK8F3065A6670A57F2` FOREIGN KEY (`parent_id`) REFERENCES `checklist` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for dept
-- ----------------------------
CREATE TABLE `dept` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for deptcheck
-- ----------------------------
CREATE TABLE `deptcheck` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `checkDate` datetime DEFAULT NULL,
  `checkMan` varchar(255) DEFAULT NULL,
  `reduce` double NOT NULL,
  `score` double NOT NULL,
  `year` int(11) DEFAULT NULL,
  `dept_id` bigint(20) DEFAULT NULL,
  `grade_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4C9139A3AC7A44BE` (`dept_id`),
  KEY `FK4C9139A3AFDB3DB6` (`grade_id`),
  CONSTRAINT `FK4C9139A3AC7A44BE` FOREIGN KEY (`dept_id`) REFERENCES `dept` (`id`),
  CONSTRAINT `FK4C9139A3AFDB3DB6` FOREIGN KEY (`grade_id`) REFERENCES `grade` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for deptchecklist
-- ----------------------------
CREATE TABLE `deptchecklist` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `plus` double NOT NULL,
  `plusCause` varchar(255) DEFAULT NULL,
  `reduce` double NOT NULL,
  `reduceCause` varchar(255) DEFAULT NULL,
  `score` double NOT NULL,
  `checkList_id` bigint(20) DEFAULT NULL,
  `deptCheck_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKC869D4E1D3627416` (`deptCheck_id`),
  KEY `FKC869D4E18FA61D76` (`checkList_id`),
  CONSTRAINT `FKC869D4E18FA61D76` FOREIGN KEY (`checkList_id`) REFERENCES `checklist` (`id`),
  CONSTRAINT `FKC869D4E1D3627416` FOREIGN KEY (`deptCheck_id`) REFERENCES `deptcheck` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for deptnote
-- ----------------------------
CREATE TABLE `deptnote` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `noteDate` datetime DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `adjustList_id` bigint(20) DEFAULT NULL,
  `createdMan_id` bigint(20) DEFAULT NULL,
  `dept_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3C4BDBF7AC7A44BE` (`dept_id`),
  KEY `FK3C4BDBF7DC0167F7` (`createdMan_id`),
  KEY `FK3C4BDBF7837B54DE` (`adjustList_id`),
  CONSTRAINT `FK3C4BDBF7837B54DE` FOREIGN KEY (`adjustList_id`) REFERENCES `adjustlist` (`id`),
  CONSTRAINT `FK3C4BDBF7AC7A44BE` FOREIGN KEY (`dept_id`) REFERENCES `dept` (`id`),
  CONSTRAINT `FK3C4BDBF7DC0167F7` FOREIGN KEY (`createdMan_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for grade
-- ----------------------------
CREATE TABLE `grade` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `lowLimit` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `reduce` int(11) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `upperLimit` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for hospital
-- ----------------------------
CREATE TABLE `hospital` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for project
-- ----------------------------
CREATE TABLE `project` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `folder` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for project_user
-- ----------------------------
CREATE TABLE `project_user` (
  `Project_id` bigint(20) NOT NULL,
  `members_id` bigint(20) NOT NULL,
  KEY `FK41BE2971FFF686F0` (`members_id`),
  KEY `FK41BE2971856AF776` (`Project_id`),
  CONSTRAINT `FK41BE2971856AF776` FOREIGN KEY (`Project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `FK41BE2971FFF686F0` FOREIGN KEY (`members_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for task
-- ----------------------------
CREATE TABLE `task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `done` bit(1) NOT NULL,
  `dueDate` datetime DEFAULT NULL,
  `folder` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `assignedTo_id` bigint(20) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK27A9A5856AF776` (`project_id`),
  KEY `FK27A9A513821140` (`assignedTo_id`),
  CONSTRAINT `FK27A9A513821140` FOREIGN KEY (`assignedTo_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK27A9A5856AF776` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user
-- ----------------------------
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `birthday` datetime DEFAULT NULL,
  `education` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `job` varchar(255) DEFAULT NULL,
  `jobDesc` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `politics` varchar(255) DEFAULT NULL,
  `sex` varchar(255) DEFAULT NULL,
  `tel` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `dept_id` bigint(20) DEFAULT NULL,
  `lastLoginTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK285FEBAC7A44BE` (`dept_id`),
  CONSTRAINT `FK285FEBAC7A44BE` FOREIGN KEY (`dept_id`) REFERENCES `dept` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for usernote
-- ----------------------------
CREATE TABLE `usernote` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `noteDate` datetime DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `createdMan_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKF3F5967DDC0167F7` (`createdMan_id`),
  KEY `FKF3F5967D47140EFE` (`user_id`),
  CONSTRAINT `FKF3F5967D47140EFE` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKF3F5967DDC0167F7` FOREIGN KEY (`createdMan_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for votekill
-- ----------------------------
CREATE TABLE `votekill` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `remark` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `grade_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKA11DE188AFDB3DB6` (`grade_id`),
  CONSTRAINT `FKA11DE188AFDB3DB6` FOREIGN KEY (`grade_id`) REFERENCES `grade` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records 
-- ----------------------------
INSERT INTO `adjustlist` VALUES ('1', '2', '1', '1', '2', '0', '1', '1', '0', '积极参加各种突发事件的抢救、救灾、舍己救人受到赞扬的', '1', null);
INSERT INTO `checklist` VALUES ('8', '1', '', '10', '参加职业道德培训', null);
INSERT INTO `checklist` VALUES ('9', '2', '', '10', '规范服务', null);
INSERT INTO `checklist` VALUES ('10', '3', '', '10', '各类荣誉', null);
INSERT INTO `checklist` VALUES ('11', '4', '', '20', '医疗纠纷投诉', null);
INSERT INTO `checklist` VALUES ('12', '5', '', '10', '违反职业道德受到处罚情况', null);
INSERT INTO `checklist` VALUES ('13', '6', '', '10', '拒收红包及行风检查', null);
INSERT INTO `checklist` VALUES ('14', '7', '', '10', '社会贡献', null);
INSERT INTO `checklist` VALUES ('15', '8', '', '20', '其他反映医德状况材料', null);
INSERT INTO `checklist` VALUES ('16', null, '', '0', '职业道德规范', '8');
INSERT INTO `checklist` VALUES ('17', null, '', '0', '医学伦理知识培训', '8');
INSERT INTO `checklist` VALUES ('18', null, '', '0', '医德医风自学心得', '8');
INSERT INTO `checklist` VALUES ('19', '21', '', '0', '服务态度', '9');
INSERT INTO `checklist` VALUES ('20', '22', '', '0', '文明用语', '9');
INSERT INTO `checklist` VALUES ('21', '23', '', '0', '尊重病人权益', '9');
INSERT INTO `checklist` VALUES ('22', '31', '', '0', '各级奖励', '10');
INSERT INTO `checklist` VALUES ('23', '32', '', '0', '媒体表扬', '10');
INSERT INTO `checklist` VALUES ('24', '33', '含锦旗', '0', '病人表扬', '10');
INSERT INTO `dept` VALUES ('1', 'ssw', '1', '1');
INSERT INTO `grade` VALUES ('1', '95', '优秀', '0', '总评达95分以上且无扣分者，评为优秀.', '200');
INSERT INTO `grade` VALUES ('2', '80', '良好', '5', '80—94分为良好', '94');
INSERT INTO `grade` VALUES ('3', '60', '一般', '20', '70—79且扣分小于20为一般', '79');
INSERT INTO `grade` VALUES ('4', '0', '较差', null, '60分以下为较差。', '59');
INSERT INTO `user` VALUES ('1', null, null, null, null, null, 'admin', '21232f297a57a5a743894a0e4a801fc3', null, null, null, null, null, '2012-04-16 14:05:53');
INSERT INTO `votekill` VALUES ('4', '在医疗服务活动中索要患者及其亲友财物或者牟取其他不正当利益的', '在医疗服务活动中索要患者及其亲友财物或者牟取其他不正当利益的', '4');
INSERT INTO `votekill` VALUES ('5', '', '在临床诊疗活动中，收受药品、医用设备、医用耗材等生产、经营企业或经销人员以各种名义给予的财物或提成的', '4');
INSERT INTO `votekill` VALUES ('6', '违反医疗服务和药品价格政策，多记费、多收费或者私自收取费用，情节严重的', '违反医疗服务和药品价格政策，多记费、多收费或者私自收取费用，情节严重的', '3');
INSERT INTO `votekill` VALUES ('7', '隐匿、伪造或擅自销毁医学文书及有关资料的', '隐匿、伪造或擅自销毁医学文书及有关资料的', '4');
INSERT INTO `votekill` VALUES ('8', '不认真履行职责，导致发生医疗事故或严重医疗差错的；', '不认真履行职责，导致发生医疗事故或严重医疗差错的；', '4');
INSERT INTO `votekill` VALUES ('9', '出具虚假医学证明文件或参与虚假医疗广告宣传和药品医疗器械促销的；', '出具虚假医学证明文件或参与虚假医疗广告宣传和药品医疗器械促销的；', '4');
INSERT INTO `votekill` VALUES ('10', '医疗服务态度恶劣，造成恶劣影响或者严重后果的；', '医疗服务态度恶劣，造成恶劣影响或者严重后果的；', '4');
INSERT INTO `votekill` VALUES ('11', '其他严重违反职业道德和医学伦理道德的情形。', '其他严重违反职业道德和医学伦理道德的情形。', '4');

--调整考评方法和标题一致
update CheckMethod  set checkList_id=checkList_id-3

----------------------------------------------------------------

/*1.表名的修改 2012-12-18*/

sp_rename Adjust,YDDA_Adjust
go
sp_rename AdjustList,YDDA_AdjustList
go
sp_rename Appraise,YDDA_Appraise
go
sp_rename BaseCheckMethod,YDDA_BaseCheckMethod
go
sp_rename BaseCheckMethod,YDDA_BaseCheckMethod
go
sp_rename BaseCheckMethod,YDDA_BaseCheckMethod
go
sp_rename BaseFileCount,YDDA_BaseFileCount
go
sp_rename BaseOneVoteKill,YDDA_BaseOneVoteKill
go
sp_rename BaseTree,YDDA_BaseTree
go
sp_rename CheckLevel,YDDA_CheckLevel
go
sp_rename CheckList,YDDA_CheckList
go
sp_rename CheckMethod,YDDA_CheckMethod
go
sp_rename Dept,YDDA_Dept
go
sp_rename Grade,YDDA_Grade
go
sp_rename Hospital,YDDA_Hospital
go
sp_rename XTD_Module,YDDA_Module
go
sp_rename Note,YDDA_Note
go
sp_rename NoteApprove,YDDA_NoteApprove
go
sp_rename Record,YDDA_Record
go
sp_rename RecordDetail,YDDA_RecordDetail
go
sp_rename Role,YDDA_Role
go
sp_rename XT_RoleModule,YDDA_RoleModule
go
sp_rename xtd_user,YDDA_User
go
sp_rename Vote,YDDA_Vote
go
sp_rename VoteKill,YDDA_VoteKill
go


/*2.增加列字段*/

if (NOT exists ( select * from dbo.syscolumns where name = 'goods' and id in 
(select id from dbo.sysobjects where id = object_id(N'[dbo].[YDDA_AdjustList]') and OBJECTPROPERTY(id, N'IsUserTable') = 1))
) 
alter TABLE YDDA_AdjustList add goods varchar(80)
GO


if (NOT exists ( select * from dbo.syscolumns where name = 'goods' and id in 
(select id from dbo.sysobjects where id = object_id(N'[dbo].[YDDA_Note]') and OBJECTPROPERTY(id, N'IsUserTable') = 1))
) 
alter TABLE YDDA_Note add goods varchar(80)
GO

if (NOT exists ( select * from dbo.syscolumns where name = 'goods' and id in 
(select id from dbo.sysobjects where id = object_id(N'[dbo].[YDDA_VoteKill]') and OBJECTPROPERTY(id, N'IsUserTable') = 1))
) 
alter TABLE YDDA_VoteKill add goods varchar(80)
GO

if (NOT exists ( select * from dbo.syscolumns where name = 'signatory' and id in 
(select id from dbo.sysobjects where id = object_id(N'[dbo].[YDDA_RecordDetail]') and OBJECTPROPERTY(id, N'IsUserTable') = 1))
) 
alter TABLE YDDA_RecordDetail  add signatory varchar(80)
GO



/*3.设置自动导随访、医德医风评价信息*/
 
--基础考评
insert into YDDA_CheckList(orderNum,remark,score,title,parent_id)
values(8,'自动导入',10,'其它平台表扬批评评价',NULL)
go

/*注意: parent_id根据上面一条执行后的id号来写，赤峰数据库为27*/
insert into YDDA_CheckList(orderNum,remark,score,title,parent_id)
values(1,'设置随访平台批评一次扣分规则',1,'随访平台批评评价',27)
go
insert into YDDA_CheckList(orderNum,remark,score,title,parent_id)
values(2,'设置医德医风平台批评一次扣分规则',1,'医德医风平台批评评价',27)
go
--加分项目
insert into YDDA_AdjustList(award,orderNum,remark,score,syncadjust,syncaward,syncscore,target,title,type,goods)
values(NULL,10,	'设置随访表扬评价分数',	1,NULL,NULL,NULL,NULL,'随访平台表扬评价',1)

go
insert into YDDA_AdjustList(award,orderNum,remark,score,syncadjust,syncaward,syncscore,target,title,type,goods)
values(NULL,10,'设置医德医风表扬分数',1,NULL,NULL,NULL,NULL,'医德医风平台表扬评价',	1)
go



