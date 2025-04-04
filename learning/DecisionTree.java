package learning;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.io.*;

/** A decision tree for supervised learning. Can perform binary classification. */
public class DecisionTree {

  DecisionTreeNode root = null;
  boolean verbose = false;

  // Using inner classes to define nodes in the tree
  
  /* A node in a decision tree. Can either be a test node or an output node. */
  abstract class DecisionTreeNode {
    // TODO: consider keeping track of each nodes level, to support indenting and max depth
    
    /** Return a binary classification prediction for an example. */
    abstract boolean predict(BinaryClassExample ex);

    /** Prints the current node, with an indentation specified by level. */
    abstract void print(int level);

  }

  /* A leaf node of decision tree. Outputs a prediction. */
  class DecisionTreeLeaf extends DecisionTreeNode {
    boolean value;

    DecisionTreeLeaf(boolean value) {
      this.value = value;
    }

    /** Return a binary classification prediction for an example. */
    boolean predict(BinaryClassExample ex) {
      return value;
    }

    /** Prints the current node, with an indentation specified by level. */
    void print(int level) {

      if (value)
        indent("Yes",level);
      else
        indent("No",level);
    }

  }

  /* An internal node for a decision tree. Tests on one attribute. */
  class DecisionTreeTest extends DecisionTreeNode {
    Attribute attrib;
    int attribIndex;
    ValueNodePair[] branches;
    private int index = 0;

    DecisionTreeTest(Attribute attrib) {
      this.attrib = attrib;
      this.branches = new ValueNodePair[attrib.range.length];
    }

    void addBranch(String value, DecisionTreeNode branch) {
      branches[index] = new ValueNodePair(value,branch);
      index++;
    }

    /** Return a binary classification prediction for an example. */
    boolean predict(BinaryClassExample ex) {
      String exVal = ex.getValue(attrib);
      for (ValueNodePair branch:branches) {
        if (exVal.equals(branch.value))
          return branch.branch.predict(ex);
      }
      System.out.println("Making a prediction with an unknown value " + exVal);
      return false;
    }

    /** Prints the current node, with an indentation specified by level. */
    void print(int level) {

      indent(attrib.toString(),level);
      for (int i=0; i < branches.length; i++) {
        indent("=" + branches[i].value, level + 1);
        branches[i].branch.print(level + 2);
      }
    }
  }

  /** A pair of an attribute value and a DecisionTreeNode. Used to specify the child of a
   *  DecisionTreeTest.
   */
  class ValueNodePair {
    String value;
    DecisionTreeNode branch;

    ValueNodePair(String value, DecisionTreeNode branch) {
      this.value = value;
      this.branch = branch;
    }
  }

  /** Used to indent branches when printing out the tree */
  private static void indent(String s, int level) {
    for (int i=0; i < level; i++)
      System.out.print("   ");
    System.out.println(s);
  }


  /** Create a new decision tree object rooted at the node given as a parameter. Currently,
   * this code only supports decision trees for binary classification problems. */
  public DecisionTree(DecisionTreeNode root) {
    this.root = root;
  }

  /** Creates a new decision tree by learning from a data set. Currently,
   * this code only supports decision trees for binary classification problems.*/
  public DecisionTree(BinaryClassSet trainSet) {
    // TODO: generalize to multiclass classification problems 
    this.root = learnDecisionTreeHelper(trainSet, trainSet.getAttributes(), null);
  }

  /** Given an example, predicts a boolean value for it. */
  public boolean predict(BinaryClassExample ex) {
    return root.predict(ex);
  }


  /** A recursive helper to learn a decision tree. Returns a subtree that classifies 
   * the input examples using the remaining attribute.  
   */
  private DecisionTreeNode learnDecisionTreeHelper(BinaryClassSet trainSet, 
      List<Attribute> attribs, List<BinaryClassExample> parentExs) {

    List<BinaryClassExample> examples = trainSet.examples;
    if (examples.size() == 0)
      return pluralityValue(parentExs);
    else {
      int numPos = countPositive(examples);
      if (numPos == examples.size())
        return new DecisionTreeLeaf(true);
      else if (numPos == 0)
        return new DecisionTreeLeaf(false);
      else if (attribs.size() == 0) {
        if (numPos > examples.size()/2)
          return new DecisionTreeLeaf(true);
        else
          return new DecisionTreeLeaf(false);
      }
      else {
        Attribute bestTest = chooseBestAttribute(trainSet, attribs);
        if (verbose) {
          System.out.println("Chose to split on " + bestTest);
          System.out.println();
        }
        DecisionTreeTest tree = new DecisionTreeTest(bestTest);
        // List<Attribute> newAttribs = removeAttribute(attribs, bestTest);
        ArrayList<Attribute> newAttribs = new ArrayList<Attribute>(attribs);
        newAttribs.remove(bestTest); 
        List<BinaryClassExample>[] partition = partitionOnAttribute(trainSet, bestTest);
        for (int i=0; i < bestTest.range.length; i++) {
          DecisionTreeNode subtree = learnDecisionTreeHelper(new BinaryClassSet(partition[i],trainSet.getAttributes()), newAttribs, examples);
          tree.addBranch(bestTest.range[i], subtree);
        }
        return tree;
      }
    }
  }

  /** Returns the decision value held by the greatest number of examples. */
  private DecisionTreeNode pluralityValue(List<BinaryClassExample> examples) {
    int numYes = 0;
    int numNo = 0;
    for (BinaryClassExample ex : examples){
      if (ex.classification)
        numYes++;
      else
        numNo++;
    }
    if (numYes >= numNo) {
      return new DecisionTreeLeaf(true);
    }
    else
      return new DecisionTreeLeaf(false);
  }

  /** Chose the most important attribute to split on. */
  private Attribute chooseBestAttribute(BinaryClassSet trainSet, List<Attribute> attribs) {
    double bestGain = 0;
    double gain;
    Attribute bestAtt = null;
    List<BinaryClassExample> examples = trainSet.getExamples();
    double infoTrain = computeEntropy(examples);

    // calculate gain one attribute at a time
    for (int i=0; i < attribs.size(); i++) {
      double rem = 0;
      if (verbose)
        System.out.println("Attribute: " + attribs.get(i));
      ArrayList<BinaryClassExample>[] partition = partitionOnAttribute(trainSet, attribs.get(i));

      // for each partition
      for (int j=0; j < partition.length; j++) {
        if (verbose)
          System.out.println(partition[j]);
        double subi = computeEntropy(partition[j]);
        rem = rem + ((double)partition[j].size() / examples.size()) * subi;
      }
      gain = infoTrain - rem;
      if (verbose) {
        System.out.println("rem["+i+"]=" + rem);
        System.out.println("gain["+i+"]=" + gain);
        System.out.println();
      }
      if (gain > bestGain || 
          (gain == bestGain && attribs.get(i).range.length < bestAtt.range.length)) {
        // if two attributes have the same gain, choose the one with fewest values
        bestGain = gain;
        bestAtt = attribs.get(i);
      }
    }
    return bestAtt;
  }

  /** Compute the entropy in a set of Boolean examples. */
  public double computeEntropy(List<BinaryClassExample> examples) {
    int pos = countPositive(examples);
    int neg = examples.size() - pos;
    double temp = computeEntropy(pos,neg);
    if (verbose)
      System.out.println("B(" + pos + "/" + examples.size() + ")=" + temp);
    return temp;
  }

  /** Outputs the decision tree. */
  public void printTree() {
    root.print(0);
  }

  public static double log2(double a) {
    return Math.log(a) / Math.log(2);
  }

  /** Computes the entropy in a set of Boolean examples with numpos
   * positive examples and numneg negative examples.
   * @param numpos
   * @param numneg
   * @return
   */
  public static double computeEntropy(int numpos, int numneg) {
    int total = numpos + numneg;
    if (total == 0)                   // no examples --> no entropy
      return 0;
    double pratio = (double)numpos / total;
    double nratio = (double)numneg / total;
    // note, we do the following because log2(0) = infinity, which causes trouble for Java
    // but, since we then multiply by 0, we can just leave the term out in that case
    if (pratio == 0)
      return -1 * nratio * log2(nratio);
    if (nratio == 0)
      return -1 * pratio * log2(pratio);
    return -1 * pratio * log2(pratio) - nratio * log2(nratio); 
  }


  /** Splits examples according to their values for a chosen attribute. */
  private static ArrayList<BinaryClassExample>[] partitionOnAttribute(BinaryClassSet trainSet, Attribute a) {

    @SuppressWarnings("unchecked")
    ArrayList<BinaryClassExample>[] partition = (ArrayList<BinaryClassExample>[]) new ArrayList[a.range.length];
    for (int i=0; i < partition.length; i++)
      partition[i] = new ArrayList<BinaryClassExample>();

    // put each example into a partition
    List<BinaryClassExample> examples = trainSet.getExamples();
    for (int i=0; i < examples.size(); i++) {
      String exValue = trainSet.getValue(examples.get(i), a);
      for (int j=0; j < a.range.length; j++) {
        if (exValue.equals(a.range[j])) {
          partition[j].add(examples.get(i));
          break;
        }
      }
    }
    return partition;
  }

//  private static List<Attribute> removeAttribute(List<Attribute> attribs, Attribute selection) {
//    // ArrayList<Attribute> newList = (ArrayList<Attribute>)attribs.clone();
//    ArrayList<Attribute> newList = new ArrayList<Attribute>(attribs);
//    newList.remove(selection);
//    return newList;
//  }

  /** Returns the number of positive examples in a set. */
  public static int countPositive(List<BinaryClassExample> examples) {
    int pos = 0;
    for (int i=0; i < examples.size(); i++) {
      if (examples.get(i).classification == true)
        pos++;
    }
    return pos;
  }

  public static void main(String args[]) {

    // these arguments are used by default
    String attFile = "hwp3data/red-circle-attrib.txt";
    String exampleFile = "hwp3data/red-circle-train.txt";
    
    if (args.length == 2) {      // user can specify the attribute and training file
      attFile = args[0];
      exampleFile = args[1];
    }
    else if (args.length != 0) {
      System.out.println("Usage: java DecisionTree [attFile] [trainFile]");
      System.exit(1);
    }

    BinaryClassSet set = new BinaryClassSet(attFile, exampleFile);
    List<Attribute> attributes = set.attribs;

    // inspect attributes
    System.out.println("Attributes used in the training set:");
    for (int i=0; i < attributes.size(); i++) {
      System.out.println("attributes["+i+"]: " + attributes.get(i).toString());
    }
    System.out.println();

    System.out.println("Read " + set.examples.size() + " examples");
    System.out.println();
    
    // build the tree and print it
    DecisionTree tree = new DecisionTree(set);
    System.out.println("Resulting Tree:");
    tree.printTree();
    System.out.println();
    
    // test the tree by making a few predictions
    BinaryClassExample testPos = new BinaryClassExample("tp", new String[] {"red", "small", "circle"}, true, attributes);
    System.out.println("Considering: " + testPos);
    System.out.println("Prediction: " + tree.predict(testPos));
    System.out.println();
    
    BinaryClassExample testNeg = new BinaryClassExample("tn", new String[] {"red", "large", "triangle"}, false, attributes);
    System.out.println("Considering: " + testNeg);
    System.out.println("Prediction: " + tree.predict(testNeg));

  }

}  

