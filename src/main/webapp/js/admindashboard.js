		function manageButtons(hide_id, show_id){
 			document.getElementById(hide_id).disabled = true;
 			document.getElementById(show_id).disabled = false;
 		}
		
		function markHoliday(){
			var holidayReason = document.getElementById('reason').value;
			$.ajax({
				type : "POST",
				dataType : "json",
				url : "/COS/admin",
				data : {
					action: "add",
					title: holidayReason,
					start: eventDate.getTime()
				},
				success : function(msg) {
					if (msg === true) {
						$('#calendar').fullCalendar('renderEvent',
								{
									title : holidayReason,
									start : eventDate,
									allDay : true
								}, true 
						);
						window.location.reload();
					} else {
						$("#result").html("<div class='alert alert-danger' align='center'>Holidays for only current year can be added</div>");
						document.getElementById('result').style.display = 'block';
					}
				}
			});
		}
		
		function removeHoliday(){
			jQuery.post("/COS/admin",
					{
						action: "remove",
						start: eventDate.getTime()
					}
			);
			window.location.reload();
		}
		
		function showShifts(){
			var caption = document.getElementById("shiftManager");
			var div = document.getElementById("shiftDetails");
			if(caption.firstChild.data == "View Shift Details"){
				caption.firstChild.data = "Hide Shift Details";
				div.style.display = "block";
			}
			else{
				caption.firstChild.data = "View Shift Details";
				div.style.display = "none";
			}
		}
		
		function addTiming(){
 			document.getElementById('addShift').disabled = true;
 			document.getElementById('newShiftTimings').style.display = 'block';
 			$('input[name="newInTime"]').ptTimeSelect();
 			$('input[name="newOutTime"]').ptTimeSelect();
		}
		
		function cancel(){
			document.getElementById('addShift').disabled = false;
			document.getElementById('newShiftTimings').style.display = 'none';
		}
		
		function save(){
			var inTime = document.getElementById('newInTime').value;
			var outTime = document.getElementById('newOutTime').value;
			$.ajax({
				type : "POST",
				dataType : "json",
				url : "/COS/admin",
				data : {
					action: "addShift",
					start: inTime,
					end: outTime
				},
				success : function(msg) {
					if (msg === true) {
						window.location.reload(true);
					} else {
						$("#result").html("<div class='alert alert-danger' align='center'>Timings already are existing</div>");
						document.getElementById('result').style.display = 'block';
					}
				}
			});
		}