package emodroid.command;

import javolution.io.Struct;

public enum SMSCommandType {

	PushSms(new PushSmsCommand()), Register(new RegisterCommand());

	private final Struct struct;

	private SMSCommandType(Struct struct) {
		this.struct = struct;
	}

	public Struct getStruct() {
		return struct;
	}

}
