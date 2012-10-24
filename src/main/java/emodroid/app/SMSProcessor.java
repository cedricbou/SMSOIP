package emodroid.app;

import com.lmax.disruptor.EventHandler;

import emodroid.command.PushSmsCommand;
import emodroid.command.SMSCommand;
import emodroid.command.SMSCommandType;
import emodroid.domain.SmsPushService;
import emodroid.domain.recipient.RecipientName;
import emodroid.domain.sms.Sms;

public class SMSProcessor implements EventHandler<SMSCommand> {

	private final SmsPushService smsService;
	
	public SMSProcessor(final SmsPushService smsService) {
		this.smsService = smsService;
	}
	
	public void onEvent(SMSCommand event, long sequence, boolean endOfBatch)
			throws Exception {

		System.out.println("process: " + event);
		
		switch ((SMSCommandType) event.type.get()) {
		case PushSms:
			final PushSmsCommand cmd = event.cmd.asPushSms;
			final Sms sms = new Sms(cmd.text.toString());
			for(int i = 0; i < cmd.to.length; ++i) {
				sms.addRecipient(new RecipientName(cmd.to[i].toString()));
			}
			smsService.push(sms);
			break;
		default:
		}
	};
}
