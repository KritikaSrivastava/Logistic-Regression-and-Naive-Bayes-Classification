

/**
 * model class presenting the data structure, in which the content of the instances will be stored. 
 * 
 * @author Kritika Srivastava
 *
 */

public class TextInstance {
	private TextName textName;
	private String content;
	private String documentPath;
	
	public TextInstance(TextName textName) {
		this.textName = textName;
	}

	public TextInstance(TextName textName, String content) {
		this(textName);
		this.content = content;
	}

	public TextInstance(TextName textName, String content, String documentPath) {
		this(textName, content);
		this.documentPath = documentPath;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public TextName getTextClass() {
		return textName;
	}
	
	public void setTextClass(TextName textClass) {
		this.textName = textClass;
	}
	
	public void setDocumentPath(String documentPath) {
		this.documentPath = documentPath;
	}
	
	public String getDocumentPath() {
		return documentPath;
	}
	
	@Override
	public String toString() {
		return textName.getValue().toString();
	}

}
