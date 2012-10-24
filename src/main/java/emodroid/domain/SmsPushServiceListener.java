package emodroid.domain;

import emodroid.domain.recipient.RecipientName;
import emodroid.domain.sms.Sms;

public interface SmsPushServiceListener {

	public void onUnregisteredRecipient(final Sms sms, final RecipientName recipientName);
}
