package emodroid.command;

import emodroid.api.SMSConstants;
import javolution.io.Struct;

public class PushSmsCommand extends Struct {
	public final Signed16 numRecipient = new Signed16();
	public final Recipient[] to = array(new Recipient[SMSConstants.MAX_TO_RECIPIENTS]);
	public final UTF8String text = new UTF8String(SMSConstants.TEXT_MAX_LENGTH);
}
