/**
 * Machine learning exception.
 * 
 * @author Kritika Srivastava
 */

public class ClassifierException extends Exception {
private static final long serialVersionUID = 1L;
	
	private static String ClassifierException = "[Classifier Exception] ";
	
	public ClassifierException() {
		super();
	}

	public ClassifierException(String message) {
		super(ClassifierException + message);
	}

	public ClassifierException(Throwable cause) {
		super(cause);
	}

	public ClassifierException(String message, Throwable cause) {
		super(ClassifierException + message, cause);
		// TODO Auto-generated constructor stub
	}

	public ClassifierException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(ClassifierException + message, cause, enableSuppression, writableStackTrace);
	}

}
