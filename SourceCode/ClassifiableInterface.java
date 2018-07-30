

import java.io.File;
import java.util.Map;

/**
 * generic interface presenting a simple classifiable interface. 
 * 
 * @author Kritika Srivastava
 *
 */
public interface ClassifiableInterface {
	public void reset();
	
	public void setLearningParameters(Map<String, Double> learningParameters);
	
	public void setBaseDirectory(File baseDirectory);
	
	public void train(boolean stopWordsUsed) throws ClassifierException;
	
	public double test() throws ClassifierException;
}
