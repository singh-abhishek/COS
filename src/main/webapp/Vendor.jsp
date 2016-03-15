<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <link rel="stylesheet" href="css/bootstrap.min.css">
    <link rel="stylesheet" href="css/bootstrap-theme.min.css">
    <link rel="stylesheet" href="css/SendReportAnimation.css">
    <script src="js/jquery-1.11.1.min.js"></script>
    <script src="js/bootstrap.min.js"></script>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Resend Cab Schedule Reports</title>
    <script>
        function resendReport() {
            $("#sendAnimation").show()
            $.ajax({
                type : "POST",                
                url : "/COS/generateReport",
                success : function(msg) {
                    if (msg === true) {
                        $("#sendAnimation").hide()
                        $("#resultContainer").html("<p style='color:green' class='alert alert-success'>Report Sent</p>");
                        $("#resultContainer").delay(100).fadeIn(800);
                        $("#resultContainer").delay(5000).fadeOut(800);
                    } else {
                        $("#resultContainer").html("<div class='alert alert-danger'>Sending Report Failed</div>");
                        $("#resultContainer").delay(5000).fadeOut(800);
                    }
                },
                error : function() {
                    $("#resultContainer").html("<div class='alert alert-danger'>Server Error. Request could not be placed, please try again later</div>");
                }
            });
        }
    </script>
</head>
<body>
<div style="width:100%;height:100%;position:absolute;vertical-align:middle;align:center;">
    <button type="button" class="btn btn-primary btn-lg" onclick="resendReport()"
            style="margin-left:auto;margin-right:auto;display:block;margin-top:30%;margin-bottom:0%">
        Resend Report</button>
</div>
<p id="resultContainer"></p>
<div id="sendAnimation" class="container" style="width:5%;height:0%;position:absolute;vertical-align:middle;align:center;display: none">
    <div class="box" style="margin-left:50%;margin-right:auto;display:block;margin-top:50%;margin-bottom:0%">
        <div class="border one" ></div>
        <div class="border two"></div>
        <div class="border three"></div>
        <div class="border four"></div>

        <div class="line one"></div>
        <div class="line two"></div>
        <div class="line three"></div>
    </div>
</div>
</body>
</html>