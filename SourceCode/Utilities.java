

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.Set;

/**
 * class handling all the file system specific tasks.
 * 
 * @author Kritika Srivastava
 *
 */

public class Utilities {
	public static File[] locateClassData(File baseDirectory, String type, TextName textName) {
		File[] textClassFiles = null;
		File childClassDirectory = new File(baseDirectory, type + File.separator + textName.getValue());
		
		if (childClassDirectory.exists() && childClassDirectory.isDirectory()) {
			textClassFiles = childClassDirectory.listFiles(new FilenameFilter() {

				public boolean accept(File dir, String name) {
					File file = new File(dir, name);
					if (file.isFile() && name.endsWith(Constants.EXTENSION))
						return true;
					return false;
				}

			});
		}

		return textClassFiles;
	}

	@SuppressWarnings("finally")
	public static Set<String> loadFile(File baseDirectory, String filename){
		Set<String> lines = new HashSet<String>();
		try {
			BufferedReader bufferedReader = null;
			File textClassFile = new File(baseDirectory, filename);

			bufferedReader = new BufferedReader(new FileReader(textClassFile));
			String line = null;

			while ((line = bufferedReader.readLine()) != null)
				lines.add(line);

			bufferedReader.close();
		}
		finally{
			return lines;

		}
	}

	public static TextInstance[] prepareTextDocuments(File[] textClassFiles, TextName textName) throws ClassifierException{
		TextInstance[] textInstances = new TextInstance[textClassFiles.length];

        try {
        	
        	BufferedReader bufferedReader = null;
			for (int index = 0; index < textClassFiles.length; index ++) {
				File textClassFile = textClassFiles[index];

				bufferedReader = new BufferedReader(new FileReader(textClassFile));
				StringBuffer sb = new StringBuffer();
				String line = null;

				while ((line = bufferedReader.readLine()) != null) {
					sb.append(line).append(" ");
				}

				bufferedReader.close();

				textInstances[index] = new TextInstance(textName, sb.toString(), textClassFile.getAbsolutePath());
			}
		}
        catch (Exception exception) {
			throw new ClassifierException(" caught in exception while loading text documents !");
		}

		return textInstances;
	}
}
