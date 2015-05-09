package idv.jhuang78.simplerest;

import javax.servlet.http.HttpServletRequest;

public class RestException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public final HttpServletRequest req;
	
	
	public RestException(Exception cause, HttpServletRequest req, String format, Object... args) {
		super(String.format(format, args), cause);
		this.req = req;
	}
	
	
	public static class InvalidIdException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		public InvalidIdException(String format, Object... args) {
			super(String.format(format, args));
		}
	}
	public static class ItemAlreadyExistException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		public ItemAlreadyExistException(String format, Object... args) {
			super(String.format(format, args));
		}
	}
	public static class ItemNotFoundException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		public ItemNotFoundException(String format, Object... args) {
			super(String.format(format, args));
		}
	}
	
}
