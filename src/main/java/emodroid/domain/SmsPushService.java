package emodroid.domain;

import emodroid.domain.recipient.RecipientName;
import emodroid.domain.recipient.RecipientRepository;
import emodroid.domain.sms.Sms;
import emodroid.domain.sms.SmsRepository;

public class SmsPushService {
	
	private final RecipientRepository recipients;
	
	private final SmsRepository smses;
	
	private final SmsPushServiceListener listener;
	
	public SmsPushService(final RecipientRepository recipients, final SmsRepository smses, SmsPushServiceListener listener) {
		this.recipients = recipients;
		this.smses = smses;
		this.listener = listener;
	}
	
	public void push(final Sms sms) {
		detectUnregisteredRecipient(sms);
		smses.push(sms);
	}
	
	private void detectUnregisteredRecipient(final Sms sms) {
		for(final RecipientName r : sms.recipients) {
			if(!recipients.contain(r)) {
				listener.onUnregisteredRecipient(sms, r);
			}
		}
	}
}
