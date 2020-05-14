//Java program to read File
import java.nio.file.*;
import java.util.*;
import java.io.File;
public class ReadFile 
{ 
	//method to read whole file as a string
	public static String readFileAsString(String fileName)throws Exception { 
		String data = ""; 
		data = new String(Files.readAllBytes(Paths.get(fileName))); 
		return data; 
	}

	//method to get each sentences or sentence tokenization
	public static List<String> sentenceTokenization(List<String> paragraph) { 
		String word = "";
		List <String>sentence = new ArrayList<>();
		for(int i = 0 ; i<data.length(); i++) {

			//condition to check sentence endings with ?, !, .
			if(data.charAt(i)=='?' || data.charAt(i)=='!') {
				word = word + data.charAt(i);
				sentence.add(word);
				word = "";
			}
			else if(i < data.length()-1 && data.charAt(i)=='.' && (data.charAt(i+1)==' ' || data.charAt(i+1) == '\n' || data.charAt(i+1) == '\r')) {
				word = word + data.charAt(i++);
				sentence.add(word);
				word = "";
			}
			//new Line
			else if(word.length()!=0 && data.charAt(i) == '\r' && data.charAt(i+1) == '\n') {
				sentence.add(word);
				word = "";
			}
			//new para
			else if(word.length()==0 && data.charAt(i) == '\r' && data.charAt(i+1) == '\n' ) 
				continue;
			else if(data.charAt(i) == '\n')
				continue;
			else
				word = word + data.charAt(i);
		}
		sentence.add(word); 
		return sentence;
	} 

	//method to get distinct words throughout the program
	public static HashSet<String> getUniqueWords(List<String> sentence) { 
		HashSet<String> unique = new HashSet<>();
		for(int i = 0; i<sentence.size(); i++) {
			for(String word : sentence.get(i).split("[.,?!;:\\s]+"))  // \\s+ (for white spaces), [\\p{Punct}\\s]+ (for any punctuation)
				unique.add(word.toLowerCase());
			System.out.println(sentence.get(i)); 
		}
		return unique; 
	}

	//method to get the count of each word
	public static HashMap<String, Integer> getFrequency(HashSet<String> unique, List<String> sentence) { 
		HashMap <String, Integer> frequency = new HashMap<>();
		Iterator<String> word = unique.iterator();
		while(word.hasNext()) {
			int count=0;
			String s = (String)word.next();  //picking one word from the hashset
			for(int i=0; i<sentence.size(); i++) {
				for(String words : sentence.get(i).split("[.,?!;:\\s]+")) {  // \\s+ (for white spaces), [\\p{Punct}\\s]+ (for any punctuation)
					if(s.equalsIgnoreCase(words))  //.equals()
						count++;  //counting the frequency of that word
				}
			}
			frequency.put(s,count);//storing the word along with its frequency in the hashmap frequency
		}
		return frequency;
	}

	//method to find total number of words in the document
	public static int getCount(HashMap<String, Integer> frequency) {
		Iterator frequencyIterator = frequency.entrySet().iterator(); 
		int count = 0;
		while(frequencyIterator.hasNext()){
			Map.Entry mapElement = (Map.Entry)frequencyIterator.next(); 
            count = count + (int)mapElement.getValue(); 
		}
		return count;
	}

	//method to evaluate the term frequency
	public static HashMap<String, Double> getTermFrequency(HashMap<String, Integer> frequency, int totalWords) { 
		Iterator frequencyIterator = frequency.entrySet().iterator(); 
		HashMap <String, Double> tf = new HashMap<>();
		while(frequencyIterator.hasNext()) {
			Map.Entry mapElement = (Map.Entry)frequencyIterator.next(); 
			double tf_value = (double)((int)mapElement.getValue()/(double)totalWords);
			tf.put(mapElement.getKey().toString(), tf_value);//storing the word along with its termfrequency in the hashmap tf
		}
		return tf;
	}

	//method to evaluate the inverse document frequency
	public static HashMap<String, Double> getInverseDocumentFrequency(int corpus, HashMap<String, Integer> frequency) { 
		Iterator frequencyIterator = frequency.entrySet().iterator(); 
		HashMap <String, Double> idf = new HashMap<>();
		while(frequencyIterator.hasNext()) {
			Map.Entry mapElement = (Map.Entry)frequencyIterator.next(); 
			double idf_value = Math.log((double)((double)corpus/(int)mapElement.getValue()));
			idf.put(mapElement.getKey().toString(), idf_value);//storing the word along with its inversedocumentfrequency in the hashmap idf
		}
		return idf;
	}

	//method to evaluate the tf-idf weight (product of tf and idf)
	public static HashMap<String, Double> getTF_IDF(HashMap<String, Double> tf, HashMap<String, Double> idf) { 
		Iterator itr = tf.entrySet().iterator(); 
		HashMap <String, Double> tf_idf = new HashMap<>();
		while(itr.hasNext()) {
			Map.Entry mapElement = (Map.Entry)itr.next(); 
			String key = mapElement.getKey().toString();
			double idf_value = idf.get(key);
			double tf_idfWeight = (double)mapElement.getValue() * idf_value;
			tf_idf.put(mapElement.getKey().toString(), tf_idfWeight);//storing the word along with its tf-idf weight in the hashmap tf-idf
		}
		return tf_idf;
	}

	//main method
	public static void main(String args[]) throws Exception { 
		Scanner sc = new Scanner(System.in);
		int corpus = 1;
		
		/* First find out every details: 
		*  sentence tokenization
		*  word tokenization
		*  frequency of each word
		*  term frequency
		*  inverse document frequency
		*  tf-idf weight 
		*/
		
		String filePath = sc.next(); 
		String fileName = filePath.substring(filePath.lastIndexOf('/')+1);
		String data = readFileAsString(filePath);  //reading the file
		List <String>sentences = sentenceTokenization(data);  //sentences added in the list 
		System.out.println("File: " + fileName); 
		HashSet <String> uniqueWords = getUniqueWords(sentences);  //declaring hashset named uniqueWords
		HashMap <String, Integer> frequency = getFrequency(uniqueWords, sentences);
		int totalWordCount = getCount(frequency);
		HashMap <String, Double> tf = getTermFrequency(frequency, totalWordCount);
		System.out.println(sentences.size() + " lines, "+ totalWordCount + " words."); 
		HashMap <String, Double> idf = getInverseDocumentFrequency(corpus, frequency);
		System.out.println(uniqueWords + "\n" + frequency + "\n" + tf + "\n" + idf);
		HashMap <String, Double> tf_idf = getTF_IDF(tf, idf);
		System.out.print(tf_idf);
		sc.close();
		
		
	} 
} 
