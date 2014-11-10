package com.ideas.routeOptimization;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Time;

import org.junit.BeforeClass;
import org.junit.Test;

public class RouteOptimizerHandlerTest {
	private static Connection conn;
	private static RouteOptimizerHandler routeOptimizer;

	@BeforeClass
	public static void before() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection("jdbc:mysql://didnsaeina6/cabservice", "admin", "admin");
		routeOptimizer = new RouteOptimizerHandler(conn);
	}

	@Test
	public void testDummy() {
		int a = 1;
		a = a + 1;
		routeOptimizer.getData(new Time(17, 00, 00));
		assertTrue(true);
	}

}
