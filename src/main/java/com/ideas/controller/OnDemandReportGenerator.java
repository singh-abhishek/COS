package com.ideas.controller;

import com.ideas.domain.Repository;
import vendorreport.EmailDispatcher;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/generateReport")
public class OnDemandReportGenerator extends HttpServlet{

    private ControllerHelper helper;

    @Override
    public void init(ServletConfig config) throws ServletException {
        helper = (ControllerHelper) config.getServletContext().getAttribute("helper");
    }


    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws IOException {
        EmailDispatcher.generateReportAndSend();
        Boolean isDone = true;
        helper.sendServerResponse(response, isDone.toString());
    }
}
