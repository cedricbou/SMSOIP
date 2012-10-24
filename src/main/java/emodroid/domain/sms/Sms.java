package emodroid.domain.sms;

import java.util.LinkedList;
import java.util.List;

import emodroid.domain.recipient.RecipientName;

public class Sms {
	
	public final String text;
	
	// NOTE: pity there is no access only collection interface, here we'd have
	// to create a getter with unmodifiableCollections but it would trigger
	// many objects creation, although most will probably be young generation,
	// thus not GC intensive. Something to consider for latter.
	public final List<RecipientName> recipients = new LinkedList<RecipientName>();
	
	public Sms(final String text) {
		this.text = text;
	}
	
	public void addRecipient(final RecipientName name) {
		recipients.add(name);
	}
	
}
