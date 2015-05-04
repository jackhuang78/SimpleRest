package idv.jhuang78.simplerest.exception;

public class ItemNotFoundException extends RuntimeException {
	
	public ItemNotFoundException(String format, Object... args) {
		super(String.format(format, args));
	}
}
