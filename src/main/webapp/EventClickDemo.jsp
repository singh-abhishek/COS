<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script src="http://code.jquery.com/jquery-1.11.1.min.js"></script>
<link rel='stylesheet' href='css/fullcalendar.css' />
<script src='js/fullcalendar.min.js'></script>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>


<script type="text/javascript">

$(document).ready(function() {

 var date = new Date();
 var d = date.getDate();
 var m = date.getMonth();
 var y = date.getFullYear();

 $('#calendar').fullCalendar({     
     header: {
         left: 'prev,next today',
         center: 'title',
         right: 'month,agendaWeek,agendaDay'
     },
     editable: false,

     // add event name to title attribute on mouseover
     eventMouseover: function(event, jsEvent, view) {
         if (view.name !== 'agendaDay') {
             $(jsEvent.target).attr('title', event.title);
         }
     },
     dayRender: function (date, cell) {
	        debugger;
	        var today = new Date();
	       
	       /* for(var i=0;i < receivedEvents.length; i++){
					if(receivedEvents[i].start == date){
							
						cell.css("background-color", "red");
						}
					
		        } */
		        if (date.getDate() === today.getDate()) {
		            cell.css("background-color", "red");
		        }
	      
	    },
     eventDestroy: function(event, element, view)
     {
         alert("removing stuff");
     },
     eventClick: function(calEvent, jsEvent, view)
     {
         var r=confirm("Delete " + calEvent.title);
         if (r===true)
           {
               $('#calendar').fullCalendar('removeEvents', calEvent._id);
           }
     },

     
     events: [
         {
         title: 'All Day Event',
         start: new Date(y, m, 1)},
     {
         title: 'Long Event',
         start: new Date(y, m, d - 5),
         allDay: true
         },
     {
         id: 999,
         title: 'Repeating Event',
         start: new Date(y, m, d - 3, 16, 0),
         allDay: false},
     {
         id: 999,
         title: 'Repeating Event',
         start: new Date(y, m, d + 4, 16, 0),
         allDay: false},
     {
         title: 'Meeting',
         start: new Date(y, m, d, 10, 30),
         allDay: false},
     {
         title: 'Lunch',
         start: new Date(y, m, d, 12, 0),
         end: new Date(y, m, d, 14, 0),
         allDay: false},
     {
         title: 'Birthday Party',
         start: new Date(y, m, d + 1, 19, 0),
         end: new Date(y, m, d + 1, 22, 30),
         allDay: false},
     {
         title: 'Click for Google',
         start: new Date(y, m, 28),
         end: new Date(y, m, 29),
         url: 'http://google.com/'}
     ]
 }); 

 
});

</script>
<style type="text/css">
		body
		{
			margin-top: 40px;
			text-align: center;
			font-size: 14px;
			font-family: "Lucida Grande",Helvetica,Arial,Verdana,sans-serif;
		}
		#calendar
		{
			width: 900px;
			margin: 0 auto;
		}
	</style>
</head>
<body>
<div id='calendar'></div>
</body>
</html>