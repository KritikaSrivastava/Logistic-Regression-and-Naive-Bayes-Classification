import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * class implementing Naive Bayes classifier.
 * 
 * @author Kritika Srivastava
 * 
 */

public class NaiveBayes extends CommonClassifier {
	private Map<TextName, Double> classPriorMap;
	private Map<String, Map<TextName, Double>> classifiedProbabilityMap;

	@Override
	public void reset() {
		super.reset();
		getClassPriorMap().clear();
		getClassifiedProbabilityMap().clear();
	}
	
	public Map<TextName, Double> getClassPriorMap() {
		if (null == classPriorMap)
			classPriorMap = new LinkedHashMap<TextName, Double>();

		return classPriorMap;
	}

	public Map<String, Map<TextName, Double>> getClassifiedProbabilityMap() {
		if (classifiedProbabilityMap == null)
			classifiedProbabilityMap = new LinkedHashMap<String, Map<TextName, Double>>();

		return classifiedProbabilityMap;
	}

	@Override
	public void specificTraining() {

		for (TextName textName : TextName.values()) {
			
			// calculate class prior
			getClassPriorMap().put(textName, (double) getClassifiedTrainingInstances().get(textName).length / getTotalTrainingInstances());

			// creating denominator for conditional probability.
			long commonDenominator = aggregateTokenCount(getGlobalClassifiedDictionary().get(textName))
					+ getGlobalVocabulary().size();
			
			for (Map.Entry<String, Integer> localClassifiedDictionaryEntry : getGlobalClassifiedDictionary().get(textName).entrySet()) {
				String token = localClassifiedDictionaryEntry.getKey();
				
				// creating numerator for conditional probability.
				long numerator = localClassifiedDictionaryEntry.getValue() + 1;
				
				double fraction = (double) numerator / commonDenominator;

				if (!getClassifiedProbabilityMap().containsKey(token)) {
					Map<TextName, Double> classifiedProbability = new HashMap<TextName, Double>();
					classifiedProbability.put(textName, fraction);

					getClassifiedProbabilityMap().put(token, classifiedProbability);
				} else {
					Map<TextName, Double> classifiedProbability = getClassifiedProbabilityMap().get(token);
					classifiedProbability.put(textName, fraction);
				}
			}
		}
	}

	@Override
	protected double specificTesting() {
		int totalCorrectClassifications = 0;
		
		for (Map.Entry<TextName, TextInstance[]> getClassifiedTestInstancesEntry : getClassifiedTestInstances().entrySet()) {
			
			TextName originalTextClass = getClassifiedTestInstancesEntry.getKey();
			TextInstance[] classTextInstances = getClassifiedTestInstancesEntry.getValue();
			int correctClassifications = 0;
			
			for (TextInstance textInstance : classTextInstances) {
				Map<TextName, Double> classificationDataMap = new HashMap<TextName, Double>();

				// instance specific dictionary
				Map<String, Integer> localInstanceDictionary = populateDictionary(textInstance);
				
				for (TextName textClass : TextName.values()) {
					Double overallInstanceClassficationValue = (Math.log (getClassPriorMap().get(textClass)) / Math.log(2));

					for (Map.Entry<String, Integer> localInstanceDictionaryEntry : localInstanceDictionary.entrySet()) {
						String token = localInstanceDictionaryEntry.getKey();
						Double classifiedConditionalProbablity;

						if (getClassifiedProbabilityMap().containsKey(token)) {
							classifiedConditionalProbablity = getClassifiedProbabilityMap().get(token).get(textClass);
							overallInstanceClassficationValue += (double) localInstanceDictionaryEntry.getValue() * ((Math.log (classifiedConditionalProbablity) / Math.log(2)));
						}
					}

					classificationDataMap.put(textClass, overallInstanceClassficationValue);
				}
				
				// best class for the test instance
				Double maxCount = null;
				TextName bestClass = null;

				for (Map.Entry<TextName, Double> classificationDataEntry : classificationDataMap.entrySet()) {
					if (maxCount == null || (classificationDataEntry.getValue().compareTo(maxCount) > 0)) {
						maxCount = classificationDataEntry.getValue();
						bestClass = classificationDataEntry.getKey();
					}
				}

				if (originalTextClass == bestClass)
					correctClassifications ++;
			}
			totalCorrectClassifications += correctClassifications;
			System.out.println("\t" + originalTextClass.getValue() + " accuracy : " + (double) (correctClassifications * 100d / classTextInstances.length));
		}
		
		return (double) (totalCorrectClassifications * 100d / getTotalTestInstances());
	}

}
