<%@page import="com.mysql.fabric.xmlrpc.base.Array"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page isELIgnored="false"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.sql.Time"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="org.json.JSONObject"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%
	ArrayList<?> holidayList = (ArrayList<?>) request
			.getAttribute("holidays");
	ArrayList<Time> inTime = (ArrayList<Time>) request
			.getAttribute("inTime");
	ArrayList<Time> outTime = (ArrayList<Time>) request
			.getAttribute("outTime");
%>
<head>
<script src="http://code.jquery.com/jquery-1.11.1.min.js"></script>
<link rel="stylesheet" type="text/css"
	href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.22/themes/redmond/jquery-ui.css" />
<link rel="stylesheet" href="css/bootstrap-combined.min.css">
<link rel="stylesheet" type="text/css"
	href="css/jquery.ptTimeSelect.css" />
<script src="js/bootstrap.min.js"></script>
<script type="text/javascript" src="js/jquery.ptTimeSelect.js"></script>
<link rel='stylesheet' href='css/fullcalendar.css' />
<script src='js/fullcalendar.js'></script>
<script type="text/javascript" src="js/admindashboard.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Dashboard</title>
<script>
	var eventDate = new Date();
	$(document).ready(
					function() {

						$('table#inTimeTable td a.delete')
								.click(
										function() {
											if (confirm("Are you sure you want to delete this row?")) {
												updateRowId("inTimeTable");
												var id = $(this).parent().parent().attr('id');
												console.log("id="+id);
												//var data = 'id=' + id ;
												var parent = $(this).parent()
														.parent();
												var table = document
														.getElementById("inTimeTable");
												var data = table.rows[parseInt(id)].cells[0].outerText;
												//console.log(typeof(data));		
												//console.log($('inTimeTable').rows.length);
												//parent.fadeOut('slow', function() {$(this).remove();});
												$.ajax({
															type : "POST",
															dataType : "json",
															url : "/COS/admin",
															data : {
																action : "removeShiftTimings",
																type : "in",
																time : data

															},

															success : function() {
																console.log("int time delete successful");
																parent.fadeOut('slow',function() {
																					$(this).remove();
																				});
																
															}
														});
											}
										});

						$('table#outTimeTable td a.delete')
								.click(
										function() {
											if (confirm("Are you sure you want to delete this row?")) {
												updateRowId("outTimeTable");
												var id = $(this).parent()
														.parent().attr('id');	
												console.log("id="+id);											
												var parent = $(this).parent().parent();
												var table = document.getElementById("outTimeTable");
												var data = table.rows[parseInt(id)].cells[0].outerText;
												
												$
														.ajax({
															type : "POST",
															dataType : "json",
															url : "/COS/admin",
															data : {
																action : "removeShiftTimings",
																type : "out",
																time : data

															},

															success : function() {
																console.log("out time delete successful");
																parent.fadeOut('slow',function() {
																					$(this).remove();
																				});
															}
														});
											}
										});

						// style the table with alternate colors
						// sets specified color for every odd row
						$('table#delTable tr:odd')
								.css('background', ' #FFFFFF');

						$("#shiftDetails").hide();
						var calendar = $('#calendar')
								.fullCalendar(
										{
											header : {
												left : 'prev,next today',
												center : 'title',
											},
											selectable : true,
											aspectRatio : 2,
											selectHelper : true,
											select : function(start, end,
													allDay) {
												eventDate = start;
												var calendarEvents = $(
														'#calendar')
														.fullCalendar(
																'clientEvents');
												var flag = false;
												for ( var i = 0; i < calendarEvents.length; i++) {
													if (calendarEvents[i].start
															.toString() == eventDate
															.toString()) {
														document
																.getElementById('reason').value = calendarEvents[i].title;
														flag = true;
														break;
													}
												}
												document
														.getElementById('result').style.display = 'none';
												$("#markHolidayModal").modal(
														"show");
												if (flag)
													manageButtons("add",
															"remove");
												else
													manageButtons("remove",
															"add");
												calendar
														.fullCalendar('unselect');
											},
											editable : true,
											events :
<%=holidayList%>
	,
										});
					});
	function updateRowId(tableId)
	{
	         if (!document.getElementsByTagName) return;
	         table=document.getElementById(tableId);
	         var i;
	         for(i=0;i<table.rows.length;i++){
	        	 table.rows[i].setAttribute("id", i, 0);

		         }
	         console.log("row id updated!");
	}
	</script>
<style type="text/css">
a {
	color: #FF0000;
}

#calendar {
	width: 1200px;
	margin: 0 auto;
}

.fc-sat,.fc-sun {
	background: #F0F0F0;
}
</style>
</head>
<body>

	<div id="result"></div>
	<div id='shiftDetailsContainer'>
		<table>
			<tr>
				<td><button type="button" class="btn btn-primary"
						id="shiftManager" onclick="showShifts()">View Shift Details</button></td>
				<td><button type="button" class="btn btn-primary" id="addShift"
						onclick="addTiming()">Add new timings</button></td>
				<td>
					<div id="newShiftTimings" style="display: none">
						<b>In Time:</b> <input name="newInTime" id="newInTime" /> <b>Out
							Time:</b> <input name="newOutTime" id="newOutTime" />
						<button type="button" class="btn btn-success btn-xs" id="saveTime"
							onclick="save()">Save</button>
						<button type="button" class="btn btn-danger btn-xs"
							onclick="cancel()">Cancel</button>
					</div>
				</td>
			</tr>
		</table>
		<div id="shiftDetails">
			<table class="table table-striped table-bordered table-condensed"
				id="inTimeTable" style="float: left; width: 50%;">
				<tr>
					<th>In Time</th>
					<th></th>
				</tr>
				<%
							int i = 1;
						%>
				<c:forEach var="element" items="${inTime}" varStatus="status">
					<tr id=<%=i++%>>
						<td><c:out value="${element}" /></td>
						<td align="center" style="width: 4%"><a href="#"
							class="delete" style="color: #FF0000;"><img alt=""
								align="absmiddle" border="0" src="delete.png" /></a></td>
					</tr>
				</c:forEach>
			</table>

			<table class="table table-striped table-bordered table-condensed"
				id="outTimeTable" style="float: right; width: 50%;">
				<tr>
					<th>Out Time</th>
					<th></th>
				</tr>
				<%
							i=1;
						%>
				<c:forEach var="element" items="${outTime}" varStatus="status">
					<tr id=<%=i++%>>
						<td><c:out value="${element}" /></td>
						<td align="center" style="width: 4%"><a href="#"
							class="delete" style="color: #FF0000;"><img alt=""
								align="absmiddle" border="0" src="delete.png" /></a></td>
					</tr>
				</c:forEach>
			</table>

		</div>
	</div>
	<br>
	<div id='calendar'></div>
	<div id="markHolidayModal" class="modal fade">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header"></div>
				<div class="modal-body">
					<form id="holidayForm" method="post">
						<div class="form-group">
							<b>Reason: </b><input type="text" class="form-control"
								id="reason" name="reason">
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-primary" data-dismiss="modal"
						id="add" onclick="markHoliday()">Mark Holiday</button>
					<button type="button" class="btn btn-primary" data-dismiss="modal"
						id="remove" onclick="removeHoliday()">Remove Holiday</button>
					<button type="button" class="btn btn-danger" data-dismiss="modal">Cancel</button>
				</div>
			</div>
		</div>
	</div>
</body>
</html>