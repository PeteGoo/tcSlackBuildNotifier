package slacknotifications;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.Request;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

public class SlackNotificationTestServer {
	Server server;

	public SlackNotificationTestServer(String host, Integer port) {
		server = new Server(port);
		Context root = new Context(server,"/",Context.SESSIONS);
		root.addServlet(new ServletHolder(new MyHttpServlet(HttpServletResponse.SC_OK)), "/200");
		root.addServlet(new ServletHolder(new MyHttpServlet(HttpServletResponse.SC_MOVED_TEMPORARILY)), "/302");
		root.addServlet(new ServletHolder(new MyHttpServlet(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)), "/500");
	}
	
	public static class MyHttpServlet extends HttpServlet
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		Integer response; 
		public MyHttpServlet(Integer response) {
			this.response = response;
		}
		protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
		{
			response.setContentType("text/plain");
			response.setStatus(this.response);
			switch (this.response) {
				case  HttpServletResponse.SC_OK:
					//response.getWriter().println("<h1>Hello SimpleServlet</h1>");
					this.printParams(request, response);
					break;
				case HttpServletResponse.SC_MOVED_TEMPORARILY:
					response.sendRedirect("/200");
					break;
				default:
					response.getWriter().println("<h1>Hello from defaultt</h1>");
					break;
			}
			System.out.println("Handling Web request for " + ((Request) request).getUri().toString());
		}
		@SuppressWarnings("unchecked")
		private void printParams(HttpServletRequest request, HttpServletResponse response){
			PrintWriter out = null;
			try {
				out = response.getWriter();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Enumeration<String> paramNames = request.getParameterNames();

		    while(paramNames.hasMoreElements()) {
		      String paramName = paramNames.nextElement();
		      out.print(paramName + " :: ");
		      String[] paramValues = request.getParameterValues(paramName);
		      if (paramValues.length == 1) {
		        String paramValue = paramValues[0];
		        if (paramValue.length() == 0)
		          out.println("No Value");
		        else
		          out.println(paramValue);
		      } else {
		    	  out.println();
		        for(int i=0; i<paramValues.length; i++) {
		          out.println(" ->  " + paramValues[i]);
		        }
		      }
		    }

		}
	}

}
