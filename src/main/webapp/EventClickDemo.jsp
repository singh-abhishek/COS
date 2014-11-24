<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>

<script src="http://code.jquery.com/jquery-1.11.1.min.js"></script>
<script src='js/fullcalendar.js'></script>
<link rel='stylesheet' href='css/fullcalendar.css' />
<script type="text/javascript">

//FullCalendar v1.5
//Script modified from the "theme.html" demo file
$(document).ready(function() {

 var date = new Date();
 var d = date.getDate();
 var m = date.getMonth();
 var y = date.getFullYear();

 $('#calendar').fullCalendar({
     theme: true,
     header: {
         left: 'prev,next today',
         center: 'title',
         right: 'month,agendaWeek,agendaDay'
     },
     editable: true,

     // add event name to title attribute on mouseover
     eventMouseover: function(event, jsEvent, view) {
         if (view.name !== 'agendaDay') {
             $(jsEvent.target).attr('title', event.title);
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

     // For DEMO only
     // *************
     events: [
         {
         title: 'All Day Event',
         start: new Date(y, m, 1)},
     {
         title: 'Long Event',
         start: new Date(y, m, d - 5),
         end: new Date(y, m, d - 2)},
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


 // Visual Event
 // http://www.sprymedia.co.uk/article/Visual+Event
 $('.vebookmarklet').click(function() {
     if (typeof VisualEvent != 'undefined') {
         if (document.getElementById('Event_display')) {
             VisualEvent.fnClose();
         } else {
             VisualEvent.fnInit();
         }
     } else {
         var n = document.createElement('script');
         n.setAttribute('language', 'JavaScript');
         n.setAttribute('src', 'http://www.sprymedia.co.uk/design/event/media/js/event-loader.js');
         document.body.appendChild(n);
     }
 });

 // See https://gist.github.com/938670
 $('.layers').click(function() {
     $('.vebookmarklet').trigger('click');
     var veColors = ['black', 'orange', 'purple', 'green', 'blue', 'yellow', 'red'],
         veColorLength = veColors.length - 1,
         veLayerIndex = 0;

     function showVeLayer(nxt) {
         veLayerIndex += (nxt) ? 1 : -1;
         if (veLayerIndex > veColorLength) {
             veLayerIndex = 0;
         }
         if (veLayerIndex < 0) {
             veLayerIndex = veColorLength;
         }

         var veLayer = $('.Event_bg_' + veColors[veLayerIndex]);
         if (veLayer.length === 0) {
             showVeLayer(nxt);
             return;
         }

         $('.Event_bg_' + veColors.join(', .Event_bg_')).hide();
         veLayer.show();
     }

     $(document).keyup(function(e) {
         switch (e.which) {
         case 39:
         case 40:
             // right/down
             showVeLayer(true);
             break;
         case 37:
         case 38:
             // left/up
             showVeLayer();
             break;
         }
     });

 });

});

</script>
</head>
<body>
<div id='calendar'></div>
</body>
</html>