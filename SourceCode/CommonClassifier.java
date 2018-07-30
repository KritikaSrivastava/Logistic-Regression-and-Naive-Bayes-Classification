import java.io.File;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * abstract class for handling the background shared code
 * @author Kritika Srivastava
 *
 */

public abstract class CommonClassifier implements ClassifiableInterface {

	private Set<String> stopwords;
	private Map<TextName, Map<String, Integer>> globalClassifiedDictionary;
	private Map<TextName, TextInstance[]> classifiedTrainingInstances;
	private Map<TextName, TextInstance[]> classifiedTestInstances;
	private Set<String> globalVocabulary;
	private int totalTrainingInstances;
	private int totalTestInstances;
	private File baseDirectory;
	private Map<String, Double> learningParameters;
	protected MathContext divisionMathContext = new MathContext(5, RoundingMode.HALF_EVEN);
	
	protected Set<String> getStopwords() {
		if (this.stopwords == null)
			this.stopwords = new LinkedHashSet<String>();
		return stopwords;
	}
	
	public void setLearningParameters(Map<String, Double> learningParameters) {
		this.learningParameters = learningParameters;
	}
	
	public Map<String, Double> getLearningParameters() {
		if (null == learningParameters)
			learningParameters = new LinkedHashMap<String, Double>();

		return learningParameters;
	}
	
	protected void addStopwords(Set<String> stopwords) {
		this.stopwords.addAll(stopwords);
	}
	
	protected int getTotalTrainingInstances() {
		return totalTrainingInstances;
	}
	
	protected void setTotalTrainingInstances(int totalInstances) {
		this.totalTrainingInstances = totalInstances;
	}
	
	public int getTotalTestInstances() {
		return totalTestInstances;
	}
	
	public void setTotalTestInstances(int totalTestInstances) {
		this.totalTestInstances = totalTestInstances;
	}
	
	protected Map<TextName, Map<String, Integer>> getGlobalClassifiedDictionary() {
		if (null == globalClassifiedDictionary)
			globalClassifiedDictionary = new LinkedHashMap<TextName, Map<String, Integer>>();
		
		return globalClassifiedDictionary;
	}

	protected Map<TextName, TextInstance[]> getClassifiedTrainingInstances() {
		if (null == classifiedTrainingInstances) 
			classifiedTrainingInstances = new LinkedHashMap<TextName, TextInstance[]>();
			
		return classifiedTrainingInstances;
	}
	
	public File getBaseDirectory() {
		return baseDirectory;
	}
	
	public void setBaseDirectory(File baseDirectory) {
		this.baseDirectory = baseDirectory;
	}
	
	public Map<TextName, TextInstance[]> getClassifiedTestInstances() {
		if (null == classifiedTestInstances) 
			classifiedTestInstances = new LinkedHashMap<TextName, TextInstance[]>();
			
		return classifiedTestInstances;
	}
	
	public Set<String> getGlobalVocabulary() {
		if (globalVocabulary == null)
			globalVocabulary = new LinkedHashSet<String>();
		
		return globalVocabulary;
	}
	
	public void reset() {
		getClassifiedTestInstances().clear();
		getClassifiedTrainingInstances().clear();
		getGlobalClassifiedDictionary().clear();
		getGlobalVocabulary().clear();
		getLearningParameters().clear();
		getStopwords().clear();
		setTotalTestInstances(0);
		setTotalTrainingInstances(0);
	}
	
	public void train(boolean stopWordsUsed) throws ClassifierException {
	
		if (stopWordsUsed) {
			addStopwords(Utilities.loadFile(getBaseDirectory(), "stopwords.txt"));
		}
		
		/* load training data */
		for (TextName textName : TextName.values()) {
			TextInstance[] documents = Utilities.prepareTextDocuments(Utilities.locateClassData (getBaseDirectory(), Constants.TRAIN, textName), textName);
			getClassifiedTrainingInstances().put(textName, documents);
			
			populateDictionary(textName, documents);
			getGlobalVocabulary().addAll(getGlobalClassifiedDictionary().get(textName).keySet());
			
			totalTrainingInstances += documents.length;
		}
		
		manageClassDictionaries();
		
		/* train it! */
		specificTraining();	
	}

	protected abstract void specificTraining();
	
	protected Map<String, Integer> populateDictionary(TextName textName, TextInstance[] instances) {
		return getGlobalClassifiedDictionary().put(textName, populateDictionary(instances));
	}
	
	private void manageClassDictionaries() {
		for (TextName textName : TextName.values()) {
			Map<String, Integer> sourceClassDictionaryMap = getGlobalClassifiedDictionary().get(textName);
			
			for (TextName innerTextName : TextName.values()) {
				if (innerTextName == textName)
					continue;
				else {
					Map<String, Integer> targetClassDictionaryMap = getGlobalClassifiedDictionary().get(innerTextName);
		
					for (Map.Entry<String, Integer> sourceClassDictionaryMapEntry : sourceClassDictionaryMap.entrySet()) {
						if (!targetClassDictionaryMap.containsKey(sourceClassDictionaryMapEntry.getKey()))
							targetClassDictionaryMap.put(sourceClassDictionaryMapEntry.getKey(), 0);
					}
				}
			}
		}
	}

	protected Map<String, Integer> populateDictionary(TextInstance[] instances) {
		return populateDictionary(instances, null);
	}

	protected Map<String, Integer> populateDictionary(TextInstance[] instances, Map<String, Integer> existingDictionary) {
		if (existingDictionary == null)
			existingDictionary = new LinkedHashMap<String, Integer>();
		
		StringTokenizer stringTokenizer;
		for (TextInstance textDocument : instances) {
			stringTokenizer = new StringTokenizer(textDocument.getContent(), Constants.DELIMITERS);
			
			while (stringTokenizer.hasMoreTokens()) {
				String token = stringTokenizer.nextToken().trim();
				
				if (isTokenValid(token)) {
					if (existingDictionary.containsKey(token))
						existingDictionary.put(token, existingDictionary.get(token) + 1);
					else
						existingDictionary.put(token, 1);
				}
			}
		}		
		
		return existingDictionary;
	}
	
	protected Map<String, Integer> populateDictionary(TextInstance instance) {
		Map<String, Integer> dictionary = new LinkedHashMap<String, Integer>();

		StringTokenizer stringTokenizer;
		stringTokenizer = new StringTokenizer(instance.getContent(), Constants.DELIMITERS);

		while (stringTokenizer.hasMoreTokens()) {
			String token = stringTokenizer.nextToken().trim();
			
			if (isTokenValid(token)) {
				if (dictionary.containsKey(token))
					dictionary.put(token, dictionary.get(token) + 1);
				else
					dictionary.put(token, 1);
			}
		}
		
		return dictionary;
	}
	
	public boolean isTokenValid(String token) {
		return ((!token.matches(Constants.NUMERICS)) && (!getStopwords().contains(token)) && (token.length() >= 2));
	}
	
	public double test() throws ClassifierException {
		/* load testing data */
		for (TextName textName : TextName.values()) {
			TextInstance[] documents = Utilities.prepareTextDocuments(Utilities.locateClassData (getBaseDirectory(), Constants.TEST, textName), textName);
			getClassifiedTestInstances().put(textName, documents);
			totalTestInstances += documents.length;
		}
		
		/* test it! */
		return specificTesting();	
	}
	
	protected abstract double specificTesting();
	
	protected int aggregateTokenCount(Map<String, Integer> dictionary) {
		int totalcount = 0;
		for (Map.Entry<String, Integer> dictionaryEntry : dictionary.entrySet())
			totalcount += dictionaryEntry.getValue();
		
		return totalcount;
	}
		
}
