package emodroid.command;

import javolution.io.Union;

public class SMSCommandEvent extends Union {
	public final PushSmsCommand asPushSms = inner(new PushSmsCommand());
	public final RegisterCommand asRegister = inner(new RegisterCommand());
}
