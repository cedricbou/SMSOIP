package emodroid.command;

import emodroid.api.SMSConstants;
import javolution.io.Struct;

public class RegisterCommand extends Struct {
	public final UTF8String name = new UTF8String(SMSConstants.RECIPIENT_MAX_LENGTH);
}
