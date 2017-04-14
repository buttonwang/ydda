  var manager, grid;
  var returnUserId;
  $(document).ready(function(){

        var depts = [];
       // var roles = [];        
        var categorys = [{ id: 1, name: '医师' }, { id: 2, name: '护士'},
                         { id: 3, name: '医技人员' }, { id: 4, name: '其他'}];
       
       
       
       var sexs = [{id: '男', name: '男'}, {id: '女', name: '女'}];


       //设置事件
        $("..l-dialog-close").click(function ()
        {
        	$(".error").each(function(){	                  	 
            	$(this).ligerHideTip();	                  	 	                  	 
        	});
        });

        $.getJSON('depts/findAll', function(data) {		 				  
        	  depts = data;
    		});
       
       /*	$.getJSON('roles/findAll', function(data) {		 				  
          	  roles = data;
    		});
       */
      

        function f_initGrid()
        {
            grid = manager = $("#maingrid").ligerGrid({
            columns: [
            
            { display: '排列号', name: 'id', width: 50, type: 'hidden' },
            { display: '用户名', name: 'name', width: 60 },
            { display: '姓名', name: 'realName', width: 60},
            { display: '性别', name: 'sex', width: 50},
            { display: '民族', name: 'nation', width: 50},    //新增1
            { display: '出生日期', name: 'birthday', width: 100},
            { display: '籍贯',    name: 'nativePlace', width: 100},
            { display: '政治面貌', name: 'politics', width: 80},
            { display: '入党时间', name: 'joinPartyTime', width: 100},  //新增2
            { display: '工作时间', name: 'jobInTime', width: 100},
              
            { display: '基础学历', name: 'baseEducation', width: 80},    //新增3
            { display: '何时何院何专业毕业', name: 'baseGraduated', width: 140}, //新增4
            { display: '最高学历', name: 'highEducation', width: 80},  //新增5
            { display: '何时何院何专业毕业', name: 'highGraduated', width: 140},  //新增6
        //    { display: '文化程度', name: 'education', width: 60}, 注释掉原来的
               
            { display: '职称', name: 'title', type: 'string', width: 60}, 
            { display: '取得职称时间', name: 'getTitleTime',  width: 80},  //新增7
            { display: '已聘职称', name: 'alreadyTitle',  width: 60},  //新增8
            
            { display: '科室', name: 'dept', type: 'string', width: 80,
            	render: function (item)
                {
                	if (item.dept) return item.dept.name;
                	return '';
                }
            },
            { display: '职务', name: 'job', type: 'string', type: 'textarea', width: 60},
            { display: '专业类别', name: 'category', type: 'string', type: 'string', width: 120,
              render: function (item)
                {                                        
                  for (var i = 0; i < categorys.length; i++) {
                   if (item.category == categorys[i].id)
                      //if (item.category == categorys[i].name)
                      return categorys[i].name;          
                  };                   
                  return '';
                }
            },
            { display: '岗位等级', name: 'postGrade',  width: 60},             //新增9
            { display: '备案时间', name: 'putRecordTime', width: 100},          //新增10
            { display: '身份', name: 'IdentType', width: 60},                  //新增11
            
           /* { display: '角色', name: 'role', type: 'string', type: 'string', width: 120,
            	render: function (item)
                {
                	if (item.role) return item.role.name;
                	return '';
                }
            },*/
            { display: '排序号', name: 'orderNum', width: 60}    
            ],

            url: "users/json", 
            method: "GET",                       
            width: '99%', pageSize: 50, showTitle: true, checkboxColWidth: 50,
            height:560,
            toolbar: { items: [
                { text: '增加', click: addRow, icon: 'add' },
                { line: true },
                { text: '修改', click: modifyRow, icon: 'modify' },
                { line: true },
                { text: '删除', click: deleteRow, icon: 'delete' },
                { line: true },
                { text: '重置密码', click: resetPassRow, icon: 'settings' },
                { line: true },
                { text: '上传照片', click: uploadPicture, icon:'archives'},
                { line: true }
                ] }
        	});         	
        }
        
        f_initGrid();
           function resetPassRow(){
            var selected = grid.getSelected();
            if (!selected) {            
            	$.messager.show("请选择行",false);
            	return;
            }
            $.ligerDialog.confirm('是否对该用户重置密码？',function (r) { 
                 if(r){
                      $.ajax({
		            	  url: 'user/resetpass/' + selected.id,
		                  type: 'POST',                  
		                  loading: '正在重置密码中...',                  
		                  success: function () {
		                       $.messager.show("重置成功",true);
		                      //grid.loadData();
		                  },
		                  error: function () {
		                       $.messager.show("重置失败",true);
		                  }
		              });
                 }
                
            });
        
      
        }
     
        function uploadPicture()
        {
            var row = manager.getSelectedRow();

            var selected = grid.getSelected();
            if (!selected) 
            {
            	LG.tip("请选择行!");
            	return;
            }
            else
            {
                $.ligerDialog.open({
                    height:200,
                    url: 'upload?userId='+row.id,
                    isResize: true,
                    title: '上传照片',
                    width: null, modal: true  
                }); 
            }    
        }
               
        function addRow()
        {
        	showDetail({}, true);
        }
        
        function modifyRow()
        {        	
            var row = manager.getSelectedRow();
            var selected = grid.getSelected();
            if (!selected) {
            	LG.tip("请选择行!");
            	return;
            }
            var parent = grid.getParent(selected);            
            showDetail(row, false);
        }
        
        function deleteRow()
        {
            var row = manager.getSelectedRow();
            var selected = grid.getSelected();
            if (!selected) {
            	LG.tip("请选择行!");
            	return
            }
            jQuery.ligerDialog.confirm('确定删除吗?', function (confirm) {
            		if (confirm) {
		            		        
		              $.ajax({
		            	  url: 'user/' + selected.id,
		                  type: 'DELETE',                  
		                  loading: '正在删除中...',                  
		                  success: function () {
		                      $.messager.show("删除成功",true);
		                     grid.loadData();
		                  },
		                  error: function (message) {
		                       $.messager.show("删除失败",true);
		                  }
		              });
            		}
            		
            });
        }
 
	    var detailWin = null, currentData = null;
	    var detailForm = $("#detailForm");
	    var v =null;
	    function showDetail(data, isAddNew)
	    {          
                currentData = data;
                currentIsAddNew = isAddNew;
          if (detailWin)
          {
	       fromClear("detailForm");
               detailWin.show();               
          }
          else
          {
	        var from = $("#detailForm").ligerForm({
                inputWidth: 280,
                defaultType:"text",
                fields: [
          			{ display: "id",id:"id",name: "user.id",type: "hidden" },
          			{ display: "用户名",id:"name", name: "user.name", newline: true, type: "text", 
          				validate:{specialCharValidate:false,required:true,minlength:2,maxlength:30}, width: 200},
                { display: "姓名",id:"realName", name: "user.realName", newline: false, type: "text", 
                  validate:{specialCharValidate:false,required:true,minlength:2,maxlength:30}, width: 200},  
          			{ display: "邮箱",id:"email",name: "user.email",type: "text",width: 200, newline: true,
          				validate: {  maxlength: 50,email:true}},
           			{ display: "电话",id:"tel",name: "user.tel", type: "text", width: 200, newline: false, validate: {maxlength: 20}},
          			{ display: "性别", id:"sex", name: "user.sexHide", comboboxName: "sexCombox", type: "select", width: 200, newline: true,
                  options: {data: sexs,  valueField: 'id', textField: 'name', valueFieldID: 'user.sex'} },
          			// 新增
                { display: "民族",id:"nation",name: "user.nation", type: "text" ,width: 200,validate: {maxlength: 20}, newline: false},          		       
                { display: "出生日期", id:"birthday",name: "user.birthday", type: "date",format: "yyyy-MM-dd",width: 200,
          				validate: { required: false}, newline: true},
          			{ display: "政治面貌",id:"politics",name: "user.politics", type: "text" ,width: 200,validate: {maxlength: 50}, newline: false}, 	
                { display: "籍贯",id:"nativePlace",name: "user.nativePlace", type: "text" ,width: 200,validate: {maxlength: 20}, newline: true},        //宁城新增     		       
              
                
                // 新增
                { display: "入党时间", id:"joinPartyTime",name: "user.joinPartyTime", type: "date",format: "yyyy-MM-dd",width: 200,
          			   validate: { required: false}, newline: false},
                { display: "工作时间", id:"jobInTime",name: "user.jobInTime", newline: true,  type: "date",format: "yyyy-MM-dd",width: 200},
          			
                { display: "文化程度",id:"education",name: "user.education", type: "text" ,width: 200,validate: {maxlength: 50}, newline: false},
                              
                 //新增
          			{ display: "基础学历",id:"baseEducation",name: "user.baseEducation", type: "text" ,width: 200,validate: {maxlength: 50}, newline: true},
          			{ display: "院校专业时间",id:"baseGraduated",name: "user.baseGraduated", type: "text" ,width: 200,validate: {maxlength: 50}, newline: false},
          			{ display: "最高学历",id:"highEducation",name: "user.highEducation", type: "text" ,width: 200,validate: {maxlength: 50}, newline: true},
          			{ display: "院校专业时间",id:"highGraduated",name: "user.highGraduated", type: "text" ,width: 200,validate: {maxlength: 50}, newline: false},          			                                
                { display: "职称",id:"title",name: "user.title", type: "text", width: 200, newline: true}, 
          			{ display: "取得职称时间", id:"getTitleTime",name: "user.getTitleTime", type: "date",format: "yyyy-MM-dd",width: 200,
          			             validate: { required: false}, newline: false},
                { display: "已聘职称",id:"alreadyTitle",name: "user.alreadyTitle", type: "text", width: 200, newline: true},           			                               
                { display: "科室", id:"dept",name: "user.deptHide", comboboxName: "deptCombox", newline: false, 
          			            width: 200, type: "select", 
          			            options: {data: depts,  valueField: 'id', textField: 'name',valueFieldID: 'user.dept.id'},
          			            validate: { required: false}}, 
                                                       
                { display: "专业类别", id:"category",name: "user.categoryHide", comboboxName: "categoryCombox", newline: true, 
                                           width: 200, type: "select", 
                                           options: {data: categorys,  valueField: 'id', textField: 'name', valueFieldID: 'user.category'},
                                           validate: { required: false}},
          			{ display: "专业技术职务", id:"job",name: "user.job", newline: false,  type: "text" ,width: 200},                 
          			//{ display: "任职时间", id:"jobInTime",name: "user.jobInTime", newline: true,  type: "date",format: "yyyy-MM-dd",width: 200},
          			{ display: "资格证书号码", id:"qcn",name: "user.qcn", newline: true,  type: "text", width: 200},  
          			{ display: "执业证书号码", id:"pcn",name: "user.pcn", newline: false,  type: "text", width: 200},  
          			{ display: "岗位描述",id:"jobDesc", name: "user.jobDesc", newline: true, width: 200,  type: "hidden"}, 
       
                { display: "岗位等级",id:"postGrade", name: "user.postGrade", newline: true, width: 200,  type: "text"}, 
                { display: "备案时间", id:"putRecordTime",name: "user.putRecordTime", type: "date",format: "yyyy-MM-dd",width: 200,
                                           validate: { required: false}, newline: false},
                { display: "身份",id:"IdentType", name: "user.IdentType", newline: true, width: 200,  type: "text"},                                                   
          			/*{ display: "角色", id:"role",name: "user.roleHide", comboboxName: "roleCombox", newline: false, 
          			          width: 200, type: "select", 
          			          options: {data: roles,  valueField: 'id', textField: 'name', valueFieldID: 'user.role.id'},
          			          validate: { required: true}},*/
                { display: "排序号", id:"orderNum",name: "user.orderNum", newline: false,  type: "text", width: 200}   	  
          			  ],
                   toJSON: JSON2.stringify
	               });
	               
	        	$.metadata.setType("attr", "validate");

	            var v = $("#detailForm").validate({
	            	debug:false,
	                errorPlacement: function (lable, element)
	                {
	                    if (element.hasClass("l-textarea"))
	                    {
	                        element.ligerTip({ content: lable.html(), target: element[0] }); 
	                    }
	                    else if (element.hasClass("l-text-field"))
	                    {
	                        element.parent().ligerTip({ content: lable.html(), target: element[0] });
	                    }
	                    else
	                    {
	                        lable.appendTo(element.parents("td:first").next("td"));
	                    }
	                },

	                success: function (lable)
	                {
	   
	                    lable.ligerHideTip();
	                    lable.remove();
	                },
	               
	             	submitHandler: function ()
	             	{
	            	  $("#detailForm .l-text,.l-textarea").ligerHideTip();
	                     $.ajax({
	                         loading: '正在保存数据中...',
	                         type: 'POST',
	                         url: 'user',                  
	                         data: $("#detailForm").serialize(),
	                         success: function (message)
	                         { 
                                    if(message=="-1"){
                                      // $.messager.show("用户名已存在！",false);  
                                        $.ligerDialog.alert("用户名已存在！");
                                    }else{
	                       	       detailWin.hide();
	                               grid.loadData();
	                               $.messager.show("保存成功",true);
                                   }
	                         },
	                         error: function (message)
	                         {  
	                               $.messager.show("保存失败",false);
                                  
	                         }
	                     });
	         	         return false;
	             	}
	         	});

	         	$("#detailForm").ligerForm();
	         	detailWin = $.ligerDialog.open({
	                  target: $("#detail"),
	                  allowClose:false,
	                  width: 700, height: 500, top: 0,
	                  buttons: [
	                  { text: '确定',type:"submit", onclick: function() { save(); } },
	                  { text: '取消', onclick: function () {
	                  	//隐藏错误冒泡提示信息
	              		  ligerHideTip();
	                  	detailWin.hide(); } }
	                  ]
	             });
          }
          
          if (!isAddNew)
          {    	           	
          	 loadData(currentData);
		   	/*  $("#id").val(currentData.id)
		   	  $("[name$=id]").valname$=name]").val(currentData.name);
			  $("[name$=email]").val(currentData.email);(currentData.id)
		      $("[name$=name]").val(currentData.name);
			  $("[name$=email]").val(currentData.email);
			  $("[name$=tel]").val(currentData.tel);
			  $("[name$=sex]").val(currentData.sex);
			  $("[name$=birthday]").val(currentData.birthday);
			  $("[name$=politics]").val(currentData.politics);
			  $("[name$=education]").val(currentData.education);
			  $("[name$=title]").val(currentData.title);
			 // $("[name$=dept]").val(currentData.dept);
			  $("[name$=job]").val(currentData.job);
			  $("[name$=jobDesc]").val(currentData.jobDesc);
		   // $("#deptCombox").ligerGetComboBoxManager().setValue(currentData.dept.id);
			*/		 
               $("#sexCombox").ligerGetComboBoxManager().setValue(currentData.sex);   
               $("#deptCombox").ligerGetComboBoxManager().setValue(currentData.dept.id);
          	// $("#roleCombox").ligerGetComboBoxManager().setValue(currentData.role.id);                      
               $("#categoryCombox").ligerGetComboBoxManager().setValue(currentData.category);
               $("[name$=name]").focus();
          }

          function save()
          {
        	  $('#detailForm').submit();   				
          }
        }
   
	})
        
        //根据用户名、姓名、科室查询
         function EnterPress(event,type){             //IE8支持此方法
        if(event.keyCode==13){    //回车键作为执行查询操作
            var name="";
            if(type==1){
                name=$("#queryByName").val(); 
            }else if(type==2) {
                name=$("#queryByRealName").val(); 
            } else{
                name=$("#queryByDeptName").val(); 
            }                
             $.ajax({
                        loading: '正在查询中...',
                        type: 'POST',
                        url: '/queryjson',                  
                        data: {name:name,type:type},
                        success: function (jsondata)
                        {
                            if(jsondata!=null){
                                grid.setOptions({ data: jsondata }); 
                                grid.loadData();  
                                
                            } else{
                                LG.tip("查询无结果!");
                            }                  
                           // LG.tip('保存成功!');
                        },
                        error: function (message)
                        {
                            LG.tip(message);                 
                        }
                    });
        }
    }  
       