<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page isELIgnored="false"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<link rel="stylesheet" href="css/bootstrap.min.css">
	<link rel="stylesheet" href="css/bootstrap-theme.min.css">
	<script src="js/jquery-1.11.1.min.js"></script>
	<script src="js/bootstrap.min.js"></script>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Enroll For Cab Service</title>
	<script type="text/javascript">
		function validate(){
			var mobile = document.getElementById('mobile').value;
			if(mobile.length != 10 || isNaN(mobile) == true){
				document.getElementById("resultContainer").innerHTML = "Mobile number should be a 10 digit number";
				document.getElementById("resultContainer").style.display = 'block';
				return false;
			}
				return true;
		}
		function clearMessage(){
			document.getElementById("resultContainer").style.display = 'none';
		}
	</script>
</head>
<body>
 	<h3 class="text-center">Employee Details For Cab Service <span class="label label-default"></span>
	</h3>
	<form class="form-horizontal" role="form" method="post" action="employee">
		<div class="form-group container-fluid">
			<label for="EmployeeId" class="col-sm-2 control-label">Employee Id</label>
			<div class="col-sm-10">
				<input type="text" class="form-control container-fluid" id="EmployeeId" name="EmployeeId" value="${employeeDetails.employeeID}" readonly>
			</div>
		</div>
		<div class="form-group container-fluid">
			<label for="name" class="col-sm-2 control-label">Name</label>
			<div class="col-sm-10">
				<input type="text" class="form-control container-fluid" id="name" name="name" value="${employeeDetails.name}" readonly>
			</div>
		</div>
		<div class="form-group container-fluid">
			<label for="inputEmail3" class="col-sm-2 control-label">Email</label>
			<div class="input-group container-fluid">
				<input class="form-control " type="text" value="${employeeDetails.getEmailId()}" readonly>
				<div class="input-group-addon container-fluid">@IDeaS.com</div>
			</div>
		</div>
		<div class="form-group container-fluid">
			<label for="mobile" class="col-sm-2 control-label">Mobile</label>
			<div class="col-sm-10">
				<input type="text" class="form-control container-fluid" id="mobile" name="mobile" value="${employeeDetails.getMobile()}" onclick="clearMessage()">
			</div>
		</div>
		<p id="resultContainer" align="center" style="color: red"></p>
		<div class="form-group container-fluid">
			<label for="address" class="col-sm-2 control-label">Address</label>
			<div class="col-sm-10">
				<input type="text" class="form-control container-fluid" id="address" name="userAddress" value="${locationDetails.getAddress().getPickUpLocation()}" required>
			</div>
		</div>
		<div class="form-group container-fluid">
			<div class="col-sm-10">
				<input type="hidden" class="form-control container-fluid" id="latitude" name="latitude" value="${locationDetails.getAddress().getLatitude()}">
				<input type="hidden" class="form-control container-fluid" id="longitude" name="longitude" value="${locationDetails.getAddress().getLongitude()}">
			</div>
		</div>
		<div class="form-group" style="text-align: center">
			<div class="col-sm-offset-1 col-sm-10">
				<button type="submit" class="btn btn-success btn-large" onclick="return validate()">Submit</button>
			</div>
		</div>
	</form>
 </body>
</html>