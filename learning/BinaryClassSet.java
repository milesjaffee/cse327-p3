package learning;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.io.StringReader;

/** A set of examples for binary classification. The set can be initialized by
 * specifying a List of attributes and a List of examples, or by providing
 * two files: 1) an attribute file with the names and values of each input feature and
 * 2) a training file with the ground truth class (true/false),
 * and values for each feature.
 */
public class BinaryClassSet {

	List<BinaryClassExample> examples;
	List<Attribute> attribs;
	
	/** Return the value of a specific attribute for a specific example. */
	public static String getValue(BinaryClassExample ex, Attribute attrib, List<Attribute> attribs) {
		int aindex = attribs.indexOf(attrib);
		return ex.values[aindex];		
	}

	/** Create a new set of examples using the attribute and example files named in the parameters. */
	public BinaryClassSet(String attFile, String exampleFile) {
		this.attribs = readAttributeFile(new File(attFile));
		this.examples = readTrainingDataFromFile(new File(exampleFile), attribs);
	}
	
	/** Create a new set of examples using the example file and an array of attributes. */
	public BinaryClassSet(String filename, Attribute[] attribs) {
		this.attribs = new ArrayList<Attribute>(attribs.length);
		for (int i=0; i < attribs.length; i++)
			this.attribs.add(i, attribs[i]);
		this.examples = readTrainingDataFromFile(new File(filename), this.attribs);
	}

	/** Create a new set of examples using the example file and a List of attributes. */
	public BinaryClassSet(String filename, List<Attribute> attribs) {
		this.attribs = attribs;
		this.examples = readTrainingDataFromFile(new File(filename), attribs);
	}
	
	/** Create a new set of examples using a List of examples and a List of attributes. */
	public BinaryClassSet(List<BinaryClassExample> examples, List<Attribute> attribs) {
		this.examples = examples;
		this.attribs = attribs;
	}
	
	/** Return the value of specific attribute for a specific example. */
	public String getValue(BinaryClassExample ex, Attribute attrib) {
		return getValue(ex, attrib, attribs);
	}
	
	/** Return all of the examples. */
	public List<BinaryClassExample> getExamples() {
		return examples;
	}
	
	/** Return all of the attributes. */
	public List<Attribute> getAttributes() {
		return attribs;
	}
	
	/** Read attribute names and values from a tab-separated file. */
	public static List<Attribute> readAttributeFile(File file) {

		Scanner in = null;
		String attName;
		Attribute attrib;
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();

		try {
			in = new Scanner(file);
		} catch(FileNotFoundException ex) {
			System.out.println(ex.getMessage());
			System.exit(1);
		}

		while (in.hasNext()) {
			String line = in.nextLine();
			Scanner lineScan = new Scanner(new StringReader(line));
			if (lineScan.hasNext()) {
				attName = lineScan.next();
				ArrayList<String> range = new ArrayList<String>();
				while (lineScan.hasNext()) {
					String value = lineScan.next();
					range.add(value);
				}
				attrib = new Attribute(attName, range.toArray(new String[0]));
				attributes.add(attrib);
			}
		}
		return attributes;
	}
	
	/** Read training data from a tab-separated file. Assumes the classification is the first feature. Assigns each
	 * example an id by concatenating "X" and its sequence number. */
   public static List<BinaryClassExample> readTrainingDataFromFile(File file, List<Attribute> attributes) {

      String id;
      boolean classification;
      String[] vals;
      
      ArrayList<BinaryClassExample> set = new ArrayList<BinaryClassExample>();

      try {
        BufferedReader br = new BufferedReader(new FileReader(file));
      
        int count = 0;
        String line;
        while ((line = br.readLine()) != null) {
          String[] entries = line.split("\t");

          classification = Boolean.parseBoolean(entries[0]);      // fist entry is the classification
          vals = new String[entries.length -1];
          System.arraycopy(entries,  1, vals, 0, vals.length);    // copy the rest of entries into a new array

          BinaryClassExample x = new BinaryClassExample("X"+(count+1), vals, classification, attributes);
          set.add(x);
          count++;
        }
      } catch (Exception ex) {
        System.out.println(ex.getMessage());
        System.exit(1);
      }
      return set;
    }
 }
