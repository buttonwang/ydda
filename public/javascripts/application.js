 
  $(function(){
  			$.extend($.validator.messages, {
			specialCharValidate : "请不要输入特殊字符"
			});
		$.extend($.validator.classRuleSettings, {
			specialCharValidate:{specialCharValidate:true}
			});
		$.validator.addMethod("specialCharValidate", function(value, element) {
			var pattern = new RegExp("[`~!@%#$^&*()=|{}':;',　\\[\\]<>/? \\.；：%……+￥（）【】‘”“'。，、？]");
			return this.optional(element)||!pattern.test(value) ;
			}, $.format($.validator.messages["specialCharValidate"]));  
      

	 

	});
	//form自动加载数据			
    loadData = function(data){
      	
          for(var attr in data){
            
              if(typeof(data[attr])=='function'){                    
                continue;
              }
              var $input = $(":input[id='"+attr+"']");
              var type = $input.attr("type");                
              if(type=="checkbox" ||type=="radio"){
                    
                  var avalues = data[attr].split(",");
                    
                  for(var v=0; v<avalues.length;v++){
                    $input.each(function(i,n){
                        var value = $(n).val();                        
                        if(value == avalues[v]){                        
                          $(n).attr("checked","checked");
                        }
                    });
                }
              }else{
                $input.val(data[attr]);
              }
                
            } 
      
      }
      /**隐藏冒泡提示*/
      ligerHideTip=function(){
      	
          	 $(".error").each(function(){
	                  	 
	                  	 	$(this).ligerHideTip();
	                  	 	
	                  	 	
	                  	 });
      }
       /**表单清空*/
      fromClear=function(from){
       $("#"+from).find(':input').each(  
                function(){  
                    switch(this.type){  
                        case 'passsword':  
                        case 'select-multiple':  
                        case 'select-one':  
                        case 'text':  
                         case 'hidden':  
                        case 'textarea':  
                            $(this).val('');  
                            break;  
                        case 'checkbox':  
                        case 'radio':  
                            this.checked = false;  
                    }  
                }     
            );  
      
      }