package emodroid.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet(urlPatterns={"/sms"}, asyncSupported=true)
public class SMSServlet extends HttpServlet {

	public SMSServlet() {
		super();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		
		res.setContentType("text/plain;charset=UTF-8");
		
        final PrintWriter out = res.getWriter();
        
        try {
            out.print("sent");
        } 
        finally {
        	out.close();
        }
	}
}
