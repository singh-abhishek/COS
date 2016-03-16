package com.ideas.controller;

import java.sql.SQLException;
import java.util.Timer;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vendorreport.CreateExcelFile;
import vendorreport.EmailScheduler;

import com.ideas.domain.Repository;
import com.ideas.routeOptimization.RouteOptimizer;

@WebListener
public class COSServletContextListener implements ServletContextListener {
	
	private static final Logger LOGGER = LogManager.getLogger(COSServletContextListener.class);
	private Repository repository;
	private DataSource dataSource;
	private ControllerHelper helper;
	private RouteOptimizer routeOptimizer;
	private CreateExcelFile excelFile;
	private Timer timer = new Timer();
	
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
			new EmailScheduler().callScheduler(timer);

		} catch (SQLException e1) {
			LOGGER.error("Error initializing repository", e1);
		} catch (Exception e) {
			LOGGER.error("Error in context initialization", e);
		}

		servletContext.setAttribute("repository", repository);
		servletContext.setAttribute("routeOptimizer", routeOptimizer);
		servletContext.setAttribute("excelFile", excelFile);
		helper = new ControllerHelper();
		servletContext.setAttribute("helper", helper);
	}

	private static DataSource setupDataSource(String username, String password,
			String driverClassName, String url) {
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
			timer.cancel();
		} catch (SQLException e) {
			LOGGER.error("Error destroying data source.", e);
		}
	}

}
