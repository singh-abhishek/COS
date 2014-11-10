<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page isELIgnored="false"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.sql.Time"%>
<%@ page import="org.json.JSONObject"%>
<%@page import="java.security.Principal"%>
<%@page import="waffle.windows.auth.WindowsAccount"%>
<%@page import="waffle.servlet.WindowsPrincipal"%>
<%@page import="com.sun.jna.platform.win32.Secur32"%>
<%@page import="com.sun.jna.platform.win32.Secur32Util"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%	String employeeUsername = request.getRemoteUser();
	ArrayList<?> eventScheduleArray = (ArrayList<?>) request.getAttribute("eventScheduleArray");
	ArrayList<String> inTime = (ArrayList<String>) request.getAttribute("inTime");
	ArrayList<String> outTime = (ArrayList<String>) request.getAttribute("outTime");%>
<html>
<head>
<script src="http://code.jquery.com/jquery-1.11.1.min.js"></script>
<link rel="stylesheet" href="css/bootstrap-combined.min.css">
<script src="js/bootstrap.min.js"></script>
<link rel='stylesheet' href='css/fullcalendar.css' />
<script src='js/fullcalendar.js'></script>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Dashboard</title>
<script>
	var startSelect = new Date();
	var endSelect = new Date();
	var eventsFromCalendar;
	var events = [];
	var inTimesString = "<%=inTime%>";
	var addSkip = ["SKIP"];
	var inTimes = addSkip.concat(inTimesString.substring(1,inTimesString.length-1).split(","));
	var outTimesString = "<%=outTime%>";
	var outTimes = addSkip.concat(outTimesString.substring(1,outTimesString.length-1).split(","));
	var check;
	var today;
	var tomorrow;
	var selectedMonth;
	function updateChanges() {
		eventsFromCalendar = $('#calendar').fullCalendar('clientEvents');
		for (var i = 0; i < eventsFromCalendar.length; i++) {
			var JSONObj = {
				"title" : eventsFromCalendar[i].title,
				"start" : new Date(
						eventsFromCalendar[i].start.getTime() + 330 * 60000)
			};
			events.push(JSON.stringify(JSONObj));
		}
		document.getElementById('eventInput').value = events;
		$.ajax({
			type : "POST",
			dataType : "json",
			url : "/COS/dashboard",
			data : $('#eventsFromCalendarForm').serialize(),
			success : function(msg) {
				if (msg === true) {
					$("#resultContainer").html("<p style='color:green' class='alert alert-success'>Schedule Updated</p>");
				} else {
					$("#resultContainer").html("<div class='alert alert-danger'>Schedule Update Failed</div>");
				}
			},
			error : function() {
				$("#resultContainer").html("<div class='alert alert-danger'>Server Error. Request could not be placed, please try again later</div>");
			}
		});
	}

	function selectOne(timeType, timeArray) {
		  var select = document.getElementById(timeType);
		  for(var i=select.options.length-1;i>=0;i--)
		    {
			  select.remove(i);
		    }
		  for (var i =0;i<timeArray.length;i++) {
		     select.options[select.options.length] = new Option(timeArray[i], i);
		  } 
	}
	
	function saveChange() {
		$('#calendar').fullCalendar('removeEvents', function(event) {
			endSelect.setHours(23, 59, 59, 999);
			if ((event.start >= startSelect) && (event.start <= endSelect))
				return true;
			return false;
		});
		var date = startSelect;

		while (date <= endSelect) {
			var e = document.getElementById("inTime");
			var inTime = e.options[e.selectedIndex].text;
			if (inTime != 'SKIP') {
				var inTimeArray = inTime.split(":");
				$('#calendar').fullCalendar(
						'renderEvent',
						{
							title : 'In-Time',
							start : new Date(date.getFullYear(), date
									.getMonth(), date.getDate(),
									inTimeArray[0], inTimeArray[1]),
							allDay : false
						}, true // make the event "stick"
				);
			
				}
				e = document.getElementById("outTime");
				var outTime = e.options[e.selectedIndex].text;
				if (outTime != 'SKIP') {
					var outTimeArray = outTime.split(":");
					$('#calendar').fullCalendar(
							'renderEvent',
							{
								title : 'Out-Time',
								start : new Date(date.getFullYear(), date.getMonth(), date.getDate(), outTimeArray[0], outTimeArray[1]),
								allDay : false
							}, true // make the event "stick"
					);
				}
				date.setDate(date.getDate() + 1);
			}
			date.setDate(date.getDate() + 1);
		}

	$(document).ready(function() {
		var date = new Date();
		var d = date.getDate();
		var m = date.getMonth();
		var y = date.getFullYear();

		var calendar = $('#calendar').fullCalendar({
			header : {
				left : 'prev,next today',
				center : 'title',
			},
			selectable : true,
			aspectRatio : 1.7,
			selectHelper : true,
			eventClick : function(event, element) {
			},
			select : function(start, end, allDay) {
				var validInTimes=[];
				var validOutTimes=[];
				startSelect = start;
				endSelect.setTime(end.getTime());
				check = $.fullCalendar.formatDate(start,'yyyy-MM-dd');
				today = $.fullCalendar.formatDate(new Date(),'yyyy-MM-dd');
				tomorrow = $.fullCalendar.formatDate(new Date(new Date().getTime() + (1000 * 60 * 60 * 24)),'yyyy-MM-dd');
				    if((check < today)||(check == today && new Date().getHours()>=11))
				    {
				    	$("#wrongDateSelectionModal").modal("show");
				    }
				    else if((check == today && new Date().getHours()<11)||(check == tomorrow && new Date().getHours()>16))
				    {	
				    eventTime = $('#calendar').fullCalendar('clientEvents', function(event) {
		                		if($.fullCalendar.formatDate(event.start,'yyyy-MM-dd') == check) {
		                    		return true;
		                		}
		                			return false;  });
        			for(var i=0;i<eventTime.length;i++){
							if( eventTime[i].start.getHours()<16){
									if(eventTime[i].title=='In-Time')	{
												validInTimes.push(eventTime[i].start.getHours()+":"+eventTime[i].start.getMinutes());													
								}
									if(eventTime[i].title=='Out-Time')	{
										validOutTimes.push(eventTime[i].start.getHours()+":"+eventTime[i].start.getMinutes());													
								}
            			}
        			}
        			if(validInTimes.length==0){
        				validInTimes.push(inTimes[0]);					
							for(var i=1;i<inTimes.length;i++){
									var tokens=inTimes[i].split(":");
									if(parseInt(tokens[0])>=16){
										validInTimes.push(inTimes[i]);	
										}
								}
            			}
        			if(validOutTimes.length==0){
        				validOutTimes.push(outTimes[0]);					
							for(var i=1;i<outTimes.length;i++){
									var tokens=outTimes[i].split(":");
									if(parseInt(tokens[0])>=16){
										validOutTimes.push(outTimes[i]);	
										}
								}
            			}
					    selectOne('inTime',validInTimes);
					    selectOne('outTime',validOutTimes);
				    	$("#createEventModal").modal("show");
				    }
				    else{
				    	 selectOne('inTime',inTimes);
						 selectOne('outTime',outTimes);
					     $("#createEventModal").modal("show");
					}
				calendar.fullCalendar('unselect');
			},
			viewDisplay: function(view){
				selectedMonth = $("#calendar").fullCalendar('getDate').getMonth();
				showButton(selectedMonth);
			},
			editable : true,
			events: <%=eventScheduleArray%>
		});
	});
	
	function showButton(){
		var currentMonth = new Date().getMonth();
		if(selectedMonth == (currentMonth + 1))
			document.getElementById('import').disabled = false;
		else
			document.getElementById('import').disabled = true;
	}
	
</script>
<style type="text/css">
	body {
		margin-top: 30px;
		text-align: center;
		font-size: 14px;
		font-family: "Lucida Grande", Helvetica, Arial, Verdana, sans-serif;
	}
	#calendar {
		width: 1200px;
		margin: 0 auto;
	}
	.fc-sat, .fc-sun {
		background: #F0F0F0;
	}
	.disabled .fc-day-content {
		background-color: #123959;
		color: #FFFFFF;
		cursor: default;
	}
</style>
</head>
<body>
<div class="btn-group" style="position:relative;right:-440px;top:-10px;
            border-color:blue;">
  <button type="button" class="btn btn-link dropdown-toggle"  data-toggle="dropdown">
    ${employeeDetails.name} <span class="caret"></span>
  </button>
  <ul class="dropdown-menu" role="menu"  >
    <li><a class="text-left" href="Maps.jsp">Edit Pick-up Location</a></li>
    <li><a class="align-left" data-toggle="modal" data-backdrop="static" href="#updateMobile" style="
    text-align: left;
" >Edit Contact</a></li>
  </ul>
</div>

<div id="updateMobile" class="modal fade">
					<form  class="form-horizontal" role="form"  action="employee" >
									
					<div class="form-group container-fluid">
					<h1> </h1>
					<h3>Edit Contact Details</h3>
			<label for="mobile" class="col-sm-2 control-label">Mobile No.</label>
			<div class="col-sm-10">
				<input type="text" class="form-control container-fluid" id="mobile" name="mobile" value="${employeeDetails.getMobile()}" placeholder="Enter mobile number" onclick="clearMessage()">
			</div>
		</div>
		<p id="resultContainer1" align="center" style="color: red"></p>
					<div class="modal-footer">
					<button id="updateNumber" type="submit" class="btn btn-primary" onclick="return validate()"
						>Update</button>
					<button type="button" class="btn btn-default" data-dismiss="modal"
						onclick="getLatestItems()">Close</button>
				</div>
				</form>
				</div>
				
	<div id='calendar'></div><br>
	<button type="button" class="btn btn-success" onclick="updateChanges()" style="font-size: large">Save My Schedule</button>
	<br><br>
	<p id="resultContainer"></p>
	<div id="wrongDateSelectionModal" class="modal fade">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-body">
					<h5>Sorry, but you cannot select past dates</h5>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-danger" data-dismiss="modal">Close</button>
				</div>
			</div>
		</div>
	</div>
	<div id="createEventModal" class="modal fade">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header"></div>
				<div class="modal-body">
					<form class="form-horizontal" role="form">
						<div class="form-group">
							<label for="inTimeLabel" class="col-sm-2 control-label">In-Time:</label>
							<div class="col-sm-10">
								<select class="form-control" id="inTime">
									<option>SKIP</option>
									<% for(int i = 0; i < inTime.size(); i++) {%>
									<option><%=inTime.get(i) %></option>
									<% } %>
								</select>
							</div>
						</div>
						<div class="form-group">
							<label for="outTimeLabel" class="col-sm-2 control-label">Out-Time:</label>
							<div class="col-sm-10">
								<select class="form-control" id="outTime">
									<option>SKIP</option>
									<% for(int i = 0; i < outTime.size(); i++) {%>
									<option><%=outTime.get(i) %></option>
									<% } %>
								</select>
							</div>
						</div>

					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-primary" data-dismiss="modal" onclick="saveChange()">Done</button>
					<button type="button" class="btn btn-danger" data-dismiss="modal">Cancel</button>
				</div>
			</div>
		</div>
	</div>
	<form method="post" id="eventsFromCalendarForm">
		<input type="hidden" name="events" id="eventInput">
		<input type="hidden" name="username" value="<%=employeeUsername%>">
	</form>
</body>
</html>