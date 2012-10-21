package emodroid.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@SuppressWarnings("serial")
@WebServlet(urlPatterns={"/smoke"}, asyncSupported=true)
public class SmokeTestServlet extends HttpServlet {

	public SmokeTestServlet() {
		super();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		
		res.setContentType("text/html;charset=UTF-8");
		
        final PrintWriter out = res.getWriter();
        
        try {
            out.println("<html><head><title>Smoke Test Page</title></head><body><pre>");
            out.println("smoke test for SMS over IP");
            out.println("remote addr: " + req.getRemoteAddr());
            out.println("requested uri: " + req.getRequestURI());

        } 
        finally {
            out.println("</pre></body></html>");
        	out.close();
        }
	}
}
