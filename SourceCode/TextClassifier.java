

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * class handling the core work-flow for text classification.
 * 
 * @author Kritika Srivastava
 *
 */
public class TextClassifier {

	private File baseDirectory;
	private Classifier classifier;
	private boolean	stopWords;
	
	public TextClassifier(File baseLocation, Classifier classifier) {
		this.baseDirectory = baseLocation;
		this.classifier = classifier;
	}

	public File getDirectory() {
		return baseDirectory;
	}
	
	public void setDirectory(File baseDirectory) {
		this.baseDirectory = baseDirectory;
	}
	
	public Classifier getClassifier() {
		return classifier;
	}
	
	public void setClassifier(Classifier classifier) {
		this.classifier = classifier;
	}
	
	public boolean isStopWordsUsed() {
		return stopWords;
	}
	
	public void setStopWordsUsed(boolean stopWords) {
		this.stopWords = stopWords;
	}
	
	public void train() throws ClassifierException {
		try {
			/** perform training **/
			getClassifier().getClassifiable().train(isStopWordsUsed());
		} catch (ClassifierException e) {
			throw new ClassifierException(" exception during training !", e);
		}
	}
	
	public double test() throws ClassifierException {
		try {
			/** perform testing **/
			return getClassifier().getClassifiable().test();
		} catch (ClassifierException e) {
			throw new ClassifierException(" exception during training !", e);
		}
	}
	
	public void reset() throws ClassifierException {
		getClassifier().getClassifiable().reset();
	}
	
	private static Map<String, Double> parseArguments(Classifier classifier, String[] args) {
		Map<String, Double> parametermap = new HashMap<String, Double>();
		
		for (int index = 0; index < args.length; index ++) {
			if (index == 0)
				parametermap.put("learningRate", Double.valueOf(args[index]));
			else if(index == 1)
				parametermap.put("lamda", Double.valueOf(args[index]));
				else if (index == 2)
				parametermap.put("repetitions", Double.valueOf(args[index]));
		}
		
		return parametermap;
	}
	
	public static void main(String[] args) throws ClassifierException {

		if (args.length < 2) {
			System.out.println(" please provide valid inputs. kindly provide input in the following format :");
			System.out.println(" TextClassifier <directory-location-containing-test-and-training-dirs> <classifier-name> <learning-rate> <lambda-value> <repetitions> ");
			System.exit(1);
		} 
		
		String dataDirectoryLocation = args[0];
		File baseDirectory = new File (dataDirectoryLocation);
		
		if (!(baseDirectory.exists() && baseDirectory.isDirectory())) {
			System.out.println(" please check the data directory location : " + dataDirectoryLocation);
			System.exit(1);
		}
		
		Classifier classifier = Classifier.valueOf(args[1]);
		if (classifier == null) {
			System.out.println(" invalid value for classifier specified. Please pass values only :");
			for (Classifier classifierItem : Classifier.values())
				System.out.println("  -  " + classifierItem.name());
			
			System.exit(1);
		}
		classifier.setBaseDirectory(baseDirectory);
		
		Map<String, Double> parameterMap = null;
		
		if (args.length > 2)
			parameterMap = parseArguments(classifier, Arrays.copyOfRange(args, 2, args.length));
		
		TextClassifier textClassifier;
		try {
			int loopIndex = 0;
			
			while (loopIndex ++ < 2) {
				textClassifier = new TextClassifier(baseDirectory, classifier);
				
				// because of singleton nature of enums.
				textClassifier.reset();
				
				if (parameterMap != null)
					classifier.setLearningParameters(parameterMap);
				
				boolean stopwordsUsed = (loopIndex % 2 == 0);
			
				textClassifier.setStopWordsUsed (stopwordsUsed);
				System.out.println(classifier.getClassifierName() + (stopwordsUsed ? " with " : " without ") + "stopwords ");

				textClassifier.train();
				double accuracy = textClassifier.test();
				
				System.out.println("\ttotal accuracy : " + accuracy);
				
				System.out.println();
			}
		} catch (ClassifierException e) {
			throw new ClassifierException(" exception while performing text classification. ", e);
		}
	}
	
}
