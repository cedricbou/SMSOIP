package emodroid.domain.event;

import javolution.io.Struct;

public class Event extends Struct {
	public Enum32<EventType> type = new Enum32<EventType>(EventType.values());
}
