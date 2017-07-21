<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE HTML>

<html>
	<head>
		<style type="text/css">
		   table,th,td{ margin:0px; padding:0px; line-height:18px; color:#000; font-size:12px;}
		   .mytable th{  background:#BCE774; text-align:center; font-weight:normal; width:150px;  padding:6px;}
		   .mytable td{background:#ECFBD4; padding:3px;  }
		   .mytable th,.mytable td{border-top:1px solid #e9e9e9;border-left:1px solid #e9e9e9;text-align:center;}
		   .mytable{border-bottom:1px solid #e9e9e9;border-right:1px solid #e9e9e9;}
		   .fun_btn{height:30px;line-height:30px;border:1px solid;text-align:center;font-size:12px;padding:4px 10px;border-radius:3px;cursor:pointer;}
			td{white-space:nowrap;}
			td a{text-decoration:none;background:rgba(100,100,100,.5);padding:2px 4px;border-radius:2px;}
		</style>
		
		<script type="text/javascript"  src="${pageContext.request.contextPath}/js/jquery-1.6.3.js"></script>
		<script>
			var path = "${pageContext.request.contextPath}";
		
			function deleteJob(id) {
				if (window.confirm("确定要删除吗？")) {
		            $.ajax({//请求二级和三级类目,替换下拉框中的内容
		                url: path + "/job/delete?id=" + id,
		                type:"post",
		                error:function(){//alert("请求失败!");
		                },
		                success:function(data){
		                    var resp = $.parseJSON(data);
		                    if(resp.success){
		                        alert("删除成功");
		                    }else{
		                        alert(resp.message);
		                    }
		                }
		            });
				}
			}
		
		    function startNow(id) {
		        if (window.confirm("确定要立即执行？")) {
		            $.ajax({//请求二级和三级类目,替换下拉框中的内容
		                url: path + "/job/startNow?id=" + id,
		                type:"post",
		                error:function(){//alert("请求失败!");
		                },
		                success:function(data){
		                    var resp = $.parseJSON(data);
		                    if(resp.success){
		                        alert("删除成功");
		                    }else{
		                        alert(resp.message);
		                    }
		                }
		            });
		        }
		    }
			function add() {
				location = path + "/jobdetail.jsp";
			}
			function startAll() {
				location = path + "/job/startAll";
			}
			function stopAll() {
				location = path + "/job/stopAll";
			}
		
		</script>
		<title>任务管理</title>
	</head>
	
	<body style="text-align:center;">
		<h1>任务管理</h1>
		<div>
			<a onclick="javascript:add();" class="fun_btn">添加任务</a>
			<!-- <a onclick="javascript:startAll();" class="fun_btn">启动所有任务</a> -->
			<!-- <a onclick="javascript:stopAll();" class="fun_btn">停止所有任务</a>-->		
		</div>
		<table class="mytable" style="width:100%;">
			<thead>
				<tr>
					<th>id</th>
					<th>job_class</th>
					<th>job_method</th>
					<th>method_args</th>
					<th>job_name</th>
					<th>job_group</th>
					<th>job_status</th>
					<th>cron_expression</th>
					<th>description</th>
					<th style="width:400px;">action</th>
				</tr>
			</thead>
			
			<tbody>
				<c:forEach var="item" items="${list}" varStatus="status">  
					<tr>
						<td>${item.id }</td>
						<td>${item.jobClass }</td>
						<td>${item.jobMethod }</td>
						<td>${item.methodArgs }</td>
						<td>${item.jobName }</td>
						<td>${item.jobGroup }</td>
						<td><c:if test="${item.jobStatus == '0'}">运行中</c:if><c:if test="${item.jobStatus == '1'}">停止中</c:if></td>
						<td>${item.cronExpression }</td>
						<td>${item.description }</td>
						<td >
	                        <a href="javascript:startNow('${item.id}')">立即执行</a> |
	                        <a href="${pageContext.request.contextPath}/job/update?id=${item.id}">修改</a> |
							<c:if test="${item.jobStatus == '0'}"><a href="${pageContext.request.contextPath}/job/stop?id=${item.id}">停止</a> |</c:if>
							<c:if test="${item.jobStatus == '1'}"><a href="${pageContext.request.contextPath}/job/start?id=${item.id}">启动</a> |</c:if>
							<a href="javascript:deleteJob('${item.id }')">删除</a>
						</td>
				</tr>
				</c:forEach>  
			</tbody>
		</table>
	</body>
</html>
