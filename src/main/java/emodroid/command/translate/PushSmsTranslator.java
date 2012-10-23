package emodroid.command.translate;

import com.lmax.disruptor.EventTranslator;

import emodroid.command.PushSmsCommand;
import emodroid.command.SMSCommand;
import emodroid.command.SMSCommandType;

public class PushSmsTranslator implements EventTranslator<SMSCommand> {
	
	private String[] to;
	private String text;
	
	@Override
	public void translateTo(SMSCommand cmd, long sequence) {
		cmd.type.set(SMSCommandType.PushSms);
		
		final PushSmsCommand pushSms = cmd.cmd.asPushSms;
		
		final String[] to = this.to;
		final String text = this.text;
		
		for(int i = 0; i < to.length; ++i) {
			pushSms.to[i].name.set(to[i]);
		}
		
		pushSms.numRecipient.set((short)to.length);
		pushSms.text.set(text);
	}

	public void setData(final String[] to, final String text) {
		this.to = to;
		this.text = text;
	}
	
}
