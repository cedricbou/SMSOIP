package emodroid.domain.recipient;

public class RecipientName {
	private final String name;
	
	public RecipientName(final String name) {
		// TODO: validate name pattern ?
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
