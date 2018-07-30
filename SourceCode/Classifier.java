

import java.io.File;
import java.util.Map;


/**
 * enumeration class having the valid classifier enums.
 * 
 * @author Kritika Srivastava
 *
 */
public enum Classifier {
	
	NB ("\'Naive Bayes\'", new NaiveBayes()),LR ("\'Logistic Regression\'", new LogisticRegression());

	private String classifierName;
	private ClassifiableInterface classifiable;
	private File baseDirectory;
	private Map<String, Double> learningParameters;
	
	private Classifier(String classifierName, ClassifiableInterface classifiable) {
		this.classifierName = classifierName;
		this.classifiable = classifiable;
	}
	
	public Map<String, Double> getLearningParameters() {
		return learningParameters;
	}
	
	public void setLearningParameters(Map<String, Double> learningParameters) throws ClassifierException {
		this.learningParameters = learningParameters;
		getClassifiable().setLearningParameters(learningParameters);
	}
	
	public String getClassifierName() {
		return classifierName;
	}
	
	public ClassifiableInterface getClassifiable() throws ClassifierException {
		classifiable.setBaseDirectory(baseDirectory);
		return classifiable;
	}
	
	public File getBaseDirectory() {
		return baseDirectory;
	}
	
	public void setBaseDirectory(File baseDirectory) {
		this.baseDirectory = baseDirectory;
	}
	
}