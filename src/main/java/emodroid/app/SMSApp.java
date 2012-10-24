package emodroid.app;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lmax.disruptor.SingleThreadedClaimStrategy;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;

import emodroid.api.SMSAPI;
import emodroid.api.SMSConstants;
import emodroid.command.SMSCommand;
import emodroid.command.translate.PushSmsTranslator;
import emodroid.domain.SmsPushService;
import emodroid.domain.SmsPushServiceListener;
import emodroid.domain.recipient.RecipientName;
import emodroid.domain.recipient.RecipientRepository;
import emodroid.domain.sms.Sms;
import emodroid.domain.sms.SmsRepository;

@SuppressWarnings("unchecked")
public class SMSApp implements SMSAPI {

	private final static ExecutorService executor = Executors.newCachedThreadPool();
	
	private final static Disruptor<SMSCommand> disruptor;
	
	private final static int RING_BUFFER_CAPACITY = 1024;
	
	static {
		disruptor = new Disruptor<SMSCommand>(
			SMSCommand.FACTORY, executor,
				new SingleThreadedClaimStrategy(RING_BUFFER_CAPACITY),
				new SleepingWaitStrategy());
		
		disruptor.handleEventsWith(new SMSProcessor(new SmsPushService(new RecipientRepository(), new SmsRepository(), new SmsPushServiceListener() {
			
			@Override
			public void onUnregisteredRecipient(Sms sms, RecipientName recipientName) {
				// TODO Auto-generated method stub
			}
		})));
		disruptor.start();
	}
	
	private final PushSmsTranslator pushTranslator = new PushSmsTranslator();
	
	@Override
	public void push(String[] to, String text) {
		if(to.length > SMSConstants.MAX_TO_RECIPIENTS) {
			throw new IllegalArgumentException("max allowed recipients : " + SMSConstants.MAX_TO_RECIPIENTS);
		}
		
		if(null == text || text.length() == 0) {
			throw new IllegalArgumentException("undefined or empty text");
		}
		
		// TODO: check domains here or async ?
		
		pushTranslator.setData(to, text);
		disruptor.publishEvent(pushTranslator);
	}

	@Override
	public void register(String userName) {
		// TODO Auto-generated method stub
		
	}

}
