package emodroid.web;

import com.caucho.hessian.server.HessianServlet;

import emodroid.api.SMSAPI;
import emodroid.app.SMSApp;

@SuppressWarnings("serial")
public class SMSHessianServlet extends HessianServlet implements SMSAPI {

	private final SMSAPI api = new SMSApp();
	
	@Override
	public void push(String[] to, String text) {
		api.push(to, text);
	}
	
	@Override
	public void register(String userName) {
		api.register(userName);
	}

}
