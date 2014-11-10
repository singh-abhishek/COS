package com.ideas.controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ControllerHelper {
	public void sendRequest(HttpServletRequest request, HttpServletResponse response, String path) throws ServletException, IOException {
		RequestDispatcher dispatcher = request.getRequestDispatcher(path);
		dispatcher.forward(request, response);
	}

	public void sendServerResponse(HttpServletResponse response, String content) throws IOException {
		response.setContentType("application/json");
		response.getWriter().append(content);
		response.flushBuffer();
	}
}
