package com.ideas.controller;

import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import vendorreport.CreateExcelFile;

import com.ideas.domain.Repository;
import com.ideas.routeOptimization.RouteOptimizer;

@WebListener
public class COSServletContextListener implements ServletContextListener {
	private Repository repository;
	private DataSource dataSource;
	private ControllerHelper helper;
	private RouteOptimizer routeOptimizer;
	private CreateExcelFile excelFile;
	
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();
		String username = servletContext.getInitParameter("username");
		String password = servletContext.getInitParameter("password");
		String driverClassName = servletContext.getInitParameter("driver");
		String url = servletContext.getInitParameter("url");
		dataSource = setupDataSource(username, password, driverClassName, url);
		try {
			repository = new Repository(dataSource.getConnection());
			routeOptimizer = new RouteOptimizer(dataSource.getConnection());
			excelFile = new CreateExcelFile(dataSource.getConnection());
			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		servletContext.setAttribute("repository", repository);
		servletContext.setAttribute("routeOptimizer", routeOptimizer);
		servletContext.setAttribute("excelFile", excelFile);
		helper = new ControllerHelper();
		servletContext.setAttribute("helper", helper);
	}

	private static DataSource setupDataSource(String username, String password, String driverClassName, String url) {
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(driverClassName);
		ds.setUsername(username);
		ds.setPassword(password);
		ds.setUrl(url);
		return ds;
	}

	public void contextDestroyed(ServletContextEvent sce) {
		BasicDataSource bds = (BasicDataSource) dataSource;
		try {
			bds.close();
		} catch (SQLException e) {}
	}

}
