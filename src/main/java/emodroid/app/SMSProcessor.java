package emodroid.app;

import com.lmax.disruptor.EventHandler;

import emodroid.command.SMSCommand;
import emodroid.command.SMSCommandType;

public class SMSProcessor implements EventHandler<SMSCommand> {

	public void onEvent(SMSCommand event, long sequence, boolean endOfBatch)
			throws Exception {

		System.out.println("process: " + event);
		
		switch ((SMSCommandType) event.type.get()) {
		case PushSms:
			// TODO : call domain
			break;
		default:
		}
	};
}
