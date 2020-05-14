import java.nio.file.*;
import java.util.*;
import java.io.File;

public class TextSummarization {

    //method to read whole file as a string
	public static String readFileAsString(String fileName)throws Exception { 
		String data = ""; 
		data = new String(Files.readAllBytes(Paths.get(fileName))); 
		return data; 
    }
    
    //method to get each paragraph
	public static List<String> getParagraph(String text) { 
        final String PARAGRAPH_SPLIT_REGEX = "(?m)\\n^\\s*";  //  (?m)^$\\r?
		List <String>paragraphs = new ArrayList<>();
		for(String paragraph: text.split(PARAGRAPH_SPLIT_REGEX)) {
            if(paragraph.charAt(0)!='\r')
                paragraphs.add(paragraph.substring(0,paragraph.length()));   //bug check paragraph.length()-1
        }
        //System.out.println(paragraphs);
		return paragraphs;
    }
    
    //method to get each sentences or sentence tokenization
    public static List<String> sentenceTokenization(List<String> paragraphs) {
        // final String SENTENCE_SPLIT_REGEX = "[.!?;] +";
        List <String>sentences = new ArrayList<>(); 
        String sentence ="";
        for(int i = 0; i<paragraphs.size(); i++) {
            String paragraph = paragraphs.get(i);
            for(int j = 0; j<paragraph.length(); j++) {
                if(j<paragraph.length()-1 && (paragraph.charAt(j)=='.' || paragraph.charAt(j)=='!' || paragraph.charAt(j)=='?' || paragraph.charAt(j)==';') && paragraph.charAt(j+1)==' ') {
                    sentence = sentence + paragraph.charAt(j);
                    sentences.add(sentence.trim());
                    sentence = "";
                }
                else
                    sentence = sentence + paragraph.charAt(j);
            }
            //sentence = sentence + paragraph.charAt(0);
            sentences.add(sentence.trim());
            sentence="";
        }
        return sentences;
    }

    //method to get distinct words throughout the program
	public static HashSet<String> wordTokenization(List<String> sentence) { 
		HashSet<String> unique = new HashSet<>();
		for(int i = 0; i<sentence.size(); i++) {
			for(String word : sentence.get(i).split("[.,?!;:\\s]+"))  // \\s+ (for white spaces), [\\p{Punct}\\s]+ (for any punctuation)  //\\s+|[\\.,?!;:]\\s+|[.]$
				unique.add(word.toLowerCase());
			//System.out.println(sentence.get(i)); 
		}
		return unique; 
    }

    public static HashMap<String, Integer> getFrequency(String sentence, HashSet<String> unique) {
        HashMap<String, Integer> frequency = new HashMap<>();
        Iterator<String> itr = unique.iterator();
		while(itr.hasNext()) {
			int count=0;
            String s = (String)itr.next(); 
            for(String word : sentence.split("[.,?!;:\\s]+")) {
                if(word.equalsIgnoreCase(s))
                    count++;
            }
            frequency.put(s, count);
        }
        return frequency;
    }
    
    public static Map<String, Map<String, Integer>> getFrequencyMatrix(List<String> sentences) {
        Map<String, Map<String, Integer>> freq_mat = new HashMap<>();
        for(int i = 0; i<sentences.size(); i++) {
            HashMap<String, Integer> frequency = new HashMap<>();
            String sentence = sentences.get(i);
            HashSet<String> words = new HashSet<>();
			for(String word : sentence.split("[.,?!;:\\s]+"))  // \\s+ (for white spaces), [\\p{Punct}\\s]+ (for any punctuation)
                words.add(word.toLowerCase());
            HashMap<String, Integer> count = getFrequency(sentence, words);
            Iterator<String> itr = words.iterator();
            while(itr.hasNext()) {
                String addword = (String)itr.next();
                frequency.put(addword, count.get(addword));
            }
            
            freq_mat.put(sentence, frequency);
        }
        return freq_mat;
    }

    //method to evaluate the term frequency
	public static HashMap<String, Double> calculateTF(HashMap<String, Integer> frequency, int totalWords) { 
		Iterator itr = frequency.entrySet().iterator(); 
		HashMap <String, Double> tf_calc = new HashMap<>();
		while(itr.hasNext()) {
			Map.Entry mapElement = (Map.Entry)itr.next(); 
			double tf_value = (double)((int)mapElement.getValue()/(double)totalWords);
			tf_calc.put(mapElement.getKey().toString(), tf_value);//storing the word along with its termfrequency in the hashmap tf
		}
		return tf_calc;
    }
    
    public static HashMap<String, Double> mapTF(HashSet<String> words, HashMap<String, Double> tf_calc) { 
		Iterator<String> itr = words.iterator();
		HashMap <String, Double> mapTF = new HashMap<>();
		while(itr.hasNext()) {
            String word = (String)itr.next();
            mapTF.put(word, tf_calc.get(word));
        }
       // System.out.println("map: " + mapTF);
		return mapTF;
	}

    public static Map<String, Map<String, Double>> getTermFrequency(List<String> sentences, List<String> paragraphs) {
        Map<String, Map<String, Double>> tf = new HashMap<>();
        for(int i = 0; i<paragraphs.size(); i++) {
            String paragraph = paragraphs.get(i);
            HashSet <String> unique = new HashSet<>();
            String line = "";
            int total_paragraph_words = 0;
            for(String word : paragraph.split("[.,?!;:\\s]+")) { 
                line = line + " " + word.toLowerCase();
                unique.add(word.toLowerCase());
                total_paragraph_words++;
            }
            //System.out.println("para: " + line);
            HashMap <String, Integer> count = getFrequency(line, unique);
            HashMap <String, Double> tf_calc = calculateTF(count, total_paragraph_words);
            //System.out.println("Frequency of paragraph words" + count + "\n" + total_paragraph_words);
            //System.out.println("TF calc" + tf_calc);
            unique.clear(); 
            for(int j=0; j<sentences.size(); j++){
                String sentence = sentences.get(j);
               if(paragraph.contains(sentence)) {
                    for(String word : sentence.split("[.,?!;:\\s]+")) { 
                        unique.add(word.toLowerCase());
                    }
              //      System.out.println("Unique line words" + unique);
                    HashMap <String, Double> mapTF = mapTF(unique, tf_calc);
                    //System.out.println("Sentence"+ sentence +"TF" + mapTF);
                    tf.put(sentence, mapTF);
                    unique.clear();
                }
            }
        }
        //System.out.println(tf);
        return tf;
    }

    public static HashMap<String, Integer> getDocPerWords(List<String> sentences, HashSet<String> words) { 
        HashMap <String, Integer> dpw_calc = new HashMap<>();
        Iterator<String> itr = words.iterator();
        while(itr.hasNext()) {
            int count = 0;
            String word = (String)itr.next();
            for(int i = 0; i<sentences.size(); i++) {
                String sentence = sentences.get(i).toLowerCase();
                for(String splitwords : sentence.split("[.,?!;:\\s]+")) { 
                    if(splitwords.equalsIgnoreCase(word)){
                        count++;
                        break;
                    }
                }
                
            }
            dpw_calc.put(word, count);
        }
        
		return dpw_calc;
    }
    
    public static Map<String, Map<String, Double>> getInverseDocumentFrequency(List<String> paragraphs, HashSet<String> words, List<String> sentences) {
        Map<String, Map<String, Double>> idf = new HashMap<>();
        HashMap<String, Double> occurence = new HashMap<>();
        int documents = paragraphs.size();
        Iterator<String> itr = words.iterator();
        while(itr.hasNext()) {
            int count = 0;
            String word = (String)itr.next();
            for(int i = 0; i<documents; i++) {
                String paragraph = paragraphs.get(i);
                for(String splitwords : paragraph.split("[.,?!;:\\s]+")) {
                    if(splitwords.equalsIgnoreCase(word)){
                        count++;
                        break;
                    }
                }
            }
            double idf_val = Math.log((double)documents/(double)count);
            occurence.put(word, idf_val);
        }
        for(int i = 0 ;i<sentences.size(); i++){
            HashMap<String, Double> idf_calc = new HashMap<>();
            String sentence = sentences.get(i);
            for(String word : sentence.split("[.,?!;:\\s]+")) {
                idf_calc.put(word.toLowerCase(), occurence.get(word.toLowerCase()));
            }
            idf.put(sentence, idf_calc);
        }
        // System.out.println(occurence+"\n");
        // System.out.print(idf);
        return idf;
    }

    public static Map<String, Map<String, Double>> getTF_IDF(Map<String, Map<String, Double>> tf, Map<String, Map<String, Double>> idf, List<String> sentences) {
        Map<String, Map<String, Double>> tf_idf = new HashMap<>();
        for(int i = 0; i<sentences.size(); i++) {
            String sentence = sentences.get(i);
            Map <String, Double> tf_val = tf.get(sentence);
            Map <String, Double> idf_val = idf.get(sentence);
            HashMap <String, Double> tf_idf_value = new HashMap<>();
            //System.out.println("TF: "+ tf_val+"\nIDF: "+idf_val+"\n\n");
            Iterator<Map.Entry<String, Double>> itr = tf_val.entrySet().iterator(); 
            while(itr.hasNext()) { 
                Map.Entry<String, Double> entry = itr.next(); 
                String key = entry.getKey();
                double tf_idf_calc = entry.getValue() * idf_val.get(key);
                tf_idf_value.put(key, tf_idf_calc);
                //System.out.println("Key: "+key+"\t\tTF = " + entry.getValue() +  ", IDF = " + idf_val.get(key)); 
            } 
            tf_idf.put(sentence, tf_idf_value);     
        }
        return tf_idf;
    }

    public static HashMap<String, Double> getScore(List<String> sentences, Map<String, Map<String, Double>> tf_idf) { 
        HashMap <String, Double> scoreSet = new HashMap<>();
        for (String key : tf_idf.keySet()) { 
            double total_tf_idf_value = 0;
            Map <String, Double> tf_idf_value = tf_idf.get(key);
            for (double value : tf_idf_value.values())  
                total_tf_idf_value += value;
            int wordcount = 0;
            for(String word: key.split("[.,?!;:\\s]+"))
                wordcount++;
            double score = total_tf_idf_value/wordcount;
            scoreSet.put(key, score);
        } 
		return scoreSet;
    }

    public static Double getThreshold(HashMap<String, Double> scoreSet) {
        int sentenceCount = scoreSet.size(); 
        double totalScore = 0;
        Iterator itr = scoreSet.entrySet().iterator();
        while (itr.hasNext()) { 
            Map.Entry sentenceScore = (Map.Entry)itr.next(); 
            totalScore += (double)sentenceScore.getValue(); 
            //System.out.println((double)sentenceScore.getValue()); 
        } 
        double threshold =  totalScore/(double)sentenceCount;
        //System.out.println(sentenceCount +", "+ totalScore); 
        return threshold;
    }

    public static List<String> summarizeText(HashMap<String, Double> scoreSet, double threshold) {
        List <String> summary = new ArrayList<>();
        Iterator itr = scoreSet.entrySet().iterator();
        while (itr.hasNext()) { 
            Map.Entry element = (Map.Entry)itr.next(); 
             Double sentenceScore = (double)element.getValue(); 
             if(sentenceScore>= threshold)
                summary.add((String)element.getKey());
        } 
        return summary;
    }
    
    public static void main(String args[]) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter file location: "); 
        String filePath = sc.next(); 
		String fileName = filePath.substring(filePath.lastIndexOf('/')+1);
        String text = readFileAsString(filePath);  //reading the file
        sc.close();
        List <String> paragraph = getParagraph(text);  //paragraph added in the list 
        List <String> sentences = sentenceTokenization(paragraph);  //sentences added in the list
        HashSet <String> words = wordTokenization(sentences);  //storing all the words
        Map<String, Map<String, Integer>> freq_Mat = getFrequencyMatrix(sentences); //finding frequency of each word ina paragraph
        Map<String, Map<String, Double>> tfMatrix = getTermFrequency(sentences, paragraph); //finding term frequency of each word in a paragraph
        HashMap <String, Integer> docPerWords = getDocPerWords(sentences, words);
        Map<String, Map<String, Double>> idfMatrix = getInverseDocumentFrequency(paragraph, words, sentences);
        Map<String, Map<String, Double>> tf_idfMatrix = getTF_IDF(tfMatrix, idfMatrix, sentences);
        HashMap <String, Double> scoreSet = getScore(sentences, tf_idfMatrix);
        Double threshold = getThreshold(scoreSet);
        List <String> summary = summarizeText(scoreSet, threshold);
        System.out.println("File: " + fileName); 
        // System.out.println("PARAGRAPHS:\n" + paragraph);
        // System.out.println("SENTENCES:\n" + sentences);
        // System.out.println("UNIQUE WORDS:\n" + words);
        // System.out.println("FREQUENCY MATRIX:\n" + freq_Mat);
        // System.out.println("TERM FREQUENCY MATRIX:\n" + tfMatrix);
        // System.out.println("DOCUMENT PER WORDS:\n" + docPerWords);
        // System.out.println("INVERSE DOCUMENT MATRIX:\n" + idfMatrix);
        // System.out.println("TF-IDF MATRIX:\n" + tf_idfMatrix);
        // System.out.println("SCORE:\n" + scoreSet);
        // System.out.println("THRESHOLD:\n" + threshold);
        // System.out.println("SUMMARY:\n" + summary);
        String summarizedText = "";
        for(int i = 0; i<summary.size(); i++)
            summarizedText = summarizedText + " " + summary.get(i);
        summarizedText = summarizedText.trim();
        System.out.println("THE ORIGINAL TEXT:\n" + text);
        System.out.println("THE SUMMARY:\n"+ summarizedText);
    }
}