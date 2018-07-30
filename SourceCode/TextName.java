
/**
 * enumeration class having the valid text class names. 
 * 
 * @author Kritika Srivastava
 *
 */
public enum TextName {
	
	POSITIVE_CLASS ("ham"),
	NEGATIVE_CLASS ("spam");
	
	private String value;
	
	private TextName (String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
