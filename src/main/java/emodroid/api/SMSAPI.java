package emodroid.api;

public interface SMSAPI {
	
	public void push(final String[] to, final String text);
	
	public void register(final String userName);
}
