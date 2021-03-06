// 模糊搜索
	var likename = "";
	$("#likename").val("");
	var question="";
var bpath=getRootPath();
var table=null;
var state=99;//全部题目
var json = {
		elem : '#tablearea99',
		
		
		cols : [ [ {
			type : 'numbers',
				width : '5%'
		}/*, {
			field : '_id',
			width : '10%',
			title : '题目ID'
		}*/, {
			field : 'kcname',
			width : '20%',
			title : '课程'
		},{
			field : 'title',
			width : '43%',
			title : '题目内容',
			style:'display:block;height:auto',
			templet : '#tmdetail'
			
		}, {
			field : 'ctimestr',
			title : '创建时间',
			width : '10%',
			templet : '#ctimestr'
		}, {
			field : 'ct',
			title : '操作',
			width : '20%',
			templet : '#dowork'
			
		}

		 ] ],
		page : true,
		url : bpath + "/message/ajaxTmList.do",
		where : {
			likename : likename,
			merid : merid,
			question:question,
			state:state
		},
		limit : 30
	};
layui.use([ 'form', 'layer', 'jquery', 'table','element' ], function() {
	var form = layui.form, layer = parent.layer === undefined ? layui.layer
			: parent.layer,element = layui.element;
	
	table= layui.table;

	table.render(json);

	$(".search_btn").on("click", function() {
		
		if ($("#likename").val() != null && $("#likename").val().trim() != "") {
			likename = $("#likename").val().trim();
		}
		 if ($("#question").val() != null && $("#question").val().trim() != "") {
				question = $("#question").val().trim();
			}
		json.where.likename = likename;
		json.where.state=state;
		json.where.question=question;
		json.elem="#tablearea"+state;
		table.render(json);
	});
	
	 element.on('tab(docDemoTabBrief)', function(elem){
		   state=Number($(this).attr('data'));
		   if ($("#likename").val() != null && $("#likename").val().trim() != "") {
				likename = $("#likename").val().trim();
			}
		   if ($("#question").val() != null && $("#question").val().trim() != "") {
				question = $("#question").val().trim();
			}
			json.where.likename = likename;
			json.where.state=state;
			json.where.question=question;
			json.elem="#tablearea"+state;
			
			
			table.render(json);
	});
	
	
	
	  table.on('tool(tablearea)', function(obj){
		    var data = obj.data;
		    var id=data._id;
		    if(obj.event === 'detail'){
		        //发放记录，实际领取
		    	orderinfo(id);
		      
		    } else if(obj.event === 'del'){
		    	
		      layer.confirm('关闭该题目吗?', function(index){
		    	  $.ajax({
		    		  type:"post",
		    		  url:bpath+"/message/closeMessage.do",
		    		  data:{id:id},
		    		  dataType:"text",
		    		  success:function(data){
		    			  if(data=="200"){
		    				  obj.del();
		    				  layer.close(index);
		    			  }
		    		  }
		    	  });
		    	  
		       
		        
		        
		        
		      });
		    } else if(obj.event === 'edit'){
		    	
		    	
		    	var url=bpath+"/pages/sjpc/info/sendInfo.html?messid="+id;
		    	addModal(url);
		    }
		  });

});


function updateTMState(id,state){
	 $.ajax({
  		  type:"post",
  		  url:bpath+"/message/updateTMState.do",
  		  data:{id:id,state:state},
  		  dataType:"text",
  		  success:function(data){
  			table.render(json);
			  layer.closeAll();
  		  }
  	  });
   	 
   	  
}

function deleteTm(id){
	layer.confirm('您确定删除该题目吗？', {
		  btn: ['确定','取消'] //按钮
		}, function(){
			$.ajax({
				  type:"post",
				  url:bpath+"/message/deleteTm.do",
				  data:{id:id},
				  dataType:"text",
				  success:function(data){
					table.render(json);
					  layer.closeAll();
				  }
			  });
		});

}



function editTM(id){
	
	

window.location.href=bpath+'/message/editTm.do?id='+id;
}

function addModal(url){
	
	var uu=bpath+'/mcoupon/addCouponModal.do';
	if(url){
		uu=url;
	}

	var index=layer.open({
	        type: 2 //此处以iframe举例
	        ,title: '券模板编辑'
	        ,area: ['900px', '460px']
	        ,shade: 0
	        ,maxmin: false
	        ,offset: [ //为了演示，随机坐标
	        /*   Math.random()*($(window).height()-300)
	          ,Math.random()*($(window).width()-390) */
	          50,$(window).width()/2-390
	        ] 
	        ,content:uu
//	         ,btn: [/* '全部关闭' */] //只是为了演示
	        ,yes: function(){
	          $(that).click(); 
	        }
	        ,btn2: function(){
	          layer.closeAll();
	        }
	        
	        ,zIndex: layer.zIndex //重点1
	        ,success: function(layero){
	          layer.setTop(layero); //重点2
	        },end:function(){
	        	
	        	 location.reload();
	        }
	      });
	
	layer.full(index);
}
