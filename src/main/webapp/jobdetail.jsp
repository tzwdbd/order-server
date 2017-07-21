<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE HTML >
<html>
	<head>
		<style>
		.textClass {width: 400px;height:26px;line-height:26px;padding:0 10px;border-radius:3px;border:1px solid;}
		.textClass:FOCUS{border-color:#FF4040;}
		.leftTd {width: 200px;vertical-align: middle;text-align: right;}
		td {padding: 5px;border:1px solid;}
		</style>
	
	
		<title>添加/修改任务</title>
	</head>

	<body style="align: center; text-align: center;">
		<h2 >添加新任务</h2>
		<form action="${pageContext.request.contextPath}/job/save" method="post" style="margin:0 auto;text-align:center;">
			<input type="hidden" name="id" value="${sj.id }" /> 
			<input type="hidden" name="jobStatus" value="${sj.jobStatus }" />
			<table style="border: 0;border-collapse:collapse;margin:0 auto;font-size:12px;">
				<tr>
					<td class="leftTd">job_class:</td>
					<td><input class="textClass" type="text" name="jobClass" value="${sj.jobClass }" /></td>
				</tr>
				
				<tr>
					<td class="leftTd">job_method:</td>
					<td><input class="textClass" type="text" name="jobMethod"	value="${sj.jobMethod }" /></td>
				</tr>
				<tr>
					<td class="leftTd">method_args:</td>
					<td><textarea rows="10" class="textClass" name="methodArgs">${sj.methodArgs }</textarea></td>
				</tr>
				<tr>
					<td class="leftTd">job_name:</td>
					<td><input class="textClass" type="text" name="jobName"	value="${sj.jobName }" /></td>
				</tr>
				<tr>
					<td class="leftTd">job_group:</td>
					<td><input class="textClass" type="text" name="jobGroup" value="${sj.jobGroup }" /></td>
				</tr>
				<tr>
					<td class="leftTd">cron_expression:</td>
					<td><input class="textClass" type="text" name="cronExpression" value="${sj.cronExpression }" /></td>
				</tr>
				
				<tr>
					<td class="leftTd">description:</td>
					<td><input class="textClass" type="text" name="description" value="${sj.description }" /></td>
				</tr>
			</table>
			
			<input type="submit" value="提交" style="padding:4px 10px;border: 1px solid;border-radius:2px;margin-top:10px;"/>
		</form>
		
		<div style="text-align: left; margin: 50px;">
			<p >需要修改的一般仅限于<strong>method_args</strong> 和 <strong>cron_expression</strong></p>
			<p style="font-size:12px;border:1px solid;padding:4px;">
				<strong>method_args</strong>代表的是方法的参数，如果job_method为<strong>pushUnErnie	， pushUnAction</strong>，
				则需要有4个参数，分别是String message, String version, String	title, String transmission代表的意思是：需要推送的内容，进行模糊匹配的版本号，android推送的标题，android推送的透传内容，格式如下：message&amp;&amp;version&amp;&amp;title&amp;&amp;transmission
				<br>
				如果job_method为<strong>pushUnUpgrade</strong>，则需要有5个参数，分别是String message, String low, String high, String title, String transmission
				代表的意思是：需要推送的内容，进行模糊匹配的低版本号，进行模糊匹配的高版本号，android推送的标题，android推送的透传内容，格式如下：message&amp;&amp;low&amp;&amp;high&amp;&amp;title&amp;&amp;transmission
			</p>
	
			
			<p style="color:red;"><strong>cron_expression</strong>是用来指定何时执行任务。</p>
			<p style="border-bottom:1px dashed;padding-bottom: 5px;">一个cron表达式有至少6个（也可能是7个）由空格分隔的时间元素。从左至右，这些元素的定义如下：</p>
			<p style="margin:0 auto;font-size:12px;border:1px solid;padding:4px;">
					1．秒（0–59）<br>
					2．分钟（0–59）<br>
					3．小时（0–23） <br>
					4．月份中的日期（1–31）<br>
					5．月份（1–12或JAN–DEC） <br>
					6．星期中的日期（1–7或SUN–SAT） <br>
					7．年份（1970–2099）
			</p>
	
			
			<p>
			<table style="border:1px solid;border-collapse:collapse;font-size:12px;">
				<tbody>
					<tr>
						<td>表 达 式</td>
						<td>意 义</td>
					</tr>
					<tr>
						<td>0 0 10,14,16 * * ?</td>
						<td>每天上午10点，下午2点和下午4点</td>
					</tr>
					<tr>
						<td>0 0,15,30,45 * 1-10 * ?</td>
						<td>每月前10天每隔15分钟</td>
					</tr>
					<tr>
						<td>30 0 0 1 1 ? 2012</td>
						<td>在2012年1月1日午夜过30秒时</td>
					</tr>
					<tr>
						<td>0 0 8-5 ? * MON-FRI</td>
						<td>每个工作日的工作时间</td>
					</tr>
				</tbody>
			</table>
		</div>
	</body>
</html>
