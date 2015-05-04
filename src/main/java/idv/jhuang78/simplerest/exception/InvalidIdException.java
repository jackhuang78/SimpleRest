package idv.jhuang78.simplerest.exception;

public class InvalidIdException extends RuntimeException {
	
	public InvalidIdException(String format, Object... args) {
		super(String.format(format, args));
	}
}
