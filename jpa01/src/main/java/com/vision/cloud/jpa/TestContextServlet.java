package com.vision.cloud.jpa;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

public class TestContextServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// Obtain our environment naming context
		Context initCtx;
		try {
			initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");

			// Look up our data source
			DataSource ds = (DataSource) envCtx.lookup("jdbc/kthx");

			// Allocate and use a connection from the pool
			Connection conn = ds.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from msgconfig limit 5");
			StringBuilder sb = new StringBuilder();
			sb.append("<table>");
			while (rs.next()) {
				sb.append("<tr>");
				sb.append("<td>instance:" + rs.getString("instance") + "</td>");
				sb.append("<td>assetName:" + rs.getString("assetName")
						+ "</td>");
				sb.append("</tr>");
			}
			sb.append("</table>");
			System.out.println(sb.toString());

			PrintWriter pw = resp.getWriter();
			pw.write(sb.toString());
			pw.close();
			rs.close();
			stmt.close();
			conn.close();

		} catch (NamingException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		super.doGet(req, resp);
	}

}
