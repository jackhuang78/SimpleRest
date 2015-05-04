package idv.jhuang78.simplerest.exception;

public class ItemAlreadyExistException extends RuntimeException {
	
	public ItemAlreadyExistException(String format, Object... args) {
		super(String.format(format, args));
	}
}
