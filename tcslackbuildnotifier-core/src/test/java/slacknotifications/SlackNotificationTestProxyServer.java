package slacknotifications;

import org.mortbay.jetty.Request;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.security.B64Code;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.proxy.AsyncProxyServlet;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class SlackNotificationTestProxyServer {
	Server server;

	public SlackNotificationTestProxyServer(String host, Integer port) {
		server = new Server(port);
		Context root = new Context(server,"/",Context.SESSIONS);
		root.addServlet(new ServletHolder(new ProxyServerServlet()), "/*");
	}
	
	public SlackNotificationTestProxyServer(String host, Integer port, String proxyUsername, String proxyPassword) {
		server = new Server(port);
		Context root = new Context(server,"/",Context.SESSIONS);
		root.addServlet(new ServletHolder(new ProxyServerServlet(proxyUsername, proxyPassword)), "/*");	}

	public class ProxyServerServlet extends AsyncProxyServlet{
		
		private boolean authRequired;
		private String proxyUsername, proxyPassword;
		
		public ProxyServerServlet(){
			super();
		}

		public ProxyServerServlet(String proxyUsername, String proxyPassword){
			this();
			this.authRequired = true;
			this.proxyUsername = proxyUsername;
			this.proxyPassword = proxyPassword;
		}

		
		@Override
		public void service(ServletRequest req, ServletResponse res)
				throws ServletException, IOException {
			System.out.println("Handling Proxy request for " + ((Request) req).getUri().toString());
	        if ( authRequired )
	        {
	            final HttpServletRequest request = (HttpServletRequest) req;
	            final HttpServletResponse response = (HttpServletResponse) res;
	            String proxyAuthorization = request.getHeader( "Proxy-Authorization" );
	            if ( proxyAuthorization != null && proxyAuthorization.startsWith( "Basic " ) )
	            {
	                String proxyAuth = proxyAuthorization.substring( 6 );
	                String authorization = B64Code.decode( proxyAuth );
	                String[] authTokens = authorization.split( ":" );
	                String user = authTokens[0];
	                String password = authTokens[1];

	                if ( user.equals(this.proxyUsername) && password.equals( this.proxyPassword ) )
	                {
	                	super.service(req, res);
	                    return;
	                }
	            }

	            // Proxy-Authenticate Basic realm="CCProxy Authorization"
	            response.addHeader( "Proxy-Authenticate", "Basic realm=\"Jetty Proxy Authorization\"" );
	            response.setStatus( HttpServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED );
	            System.out.println("Proxy Auth Creds not supplied");
	            
	        } else {
				super.service(req, res);
	        }

		}
		
		
	}
}
