import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;

import learning.*;

class MushroomLearning {
    public static void main(String[] args) {

        List<Attribute> attributesAL = new ArrayList<>();
        attributesAL = BinaryClassSet.readAttributeFile(new File("hwp3data/agaricus-lepiota-attrib.txt"));

        /*I made three new files for the data - agaricus-lepiota-attrib.txt (attribute file),
        agaricus-lepiota-training.data (first 80 percent of the data, 6499 entries), and agaricus-lepiota-testing.data (last 20%, 1625 entries)
        */
        List<BinaryClassExample> trainingAL = new ArrayList<>();
        trainingAL = readTrainingDataFromCSV(new File("hwp3data/agaricus-lepiota-training.data"), attributesAL);
        BinaryClassSet trainingSetAL = new BinaryClassSet(trainingAL, attributesAL);

        List<BinaryClassExample> testingAL = new ArrayList<>();
        testingAL = readTrainingDataFromCSV(new File("hwp3data/agaricus-lepiota-testing.data"), attributesAL);
        BinaryClassSet testingSetAL = new BinaryClassSet(testingAL, attributesAL);

        DecisionTree treeAL = new DecisionTree(trainingSetAL);
        treeAL.printTree();

        printAccuracy(treeAL, trainingAL, "A.L. Training");
        printAccuracy(treeAL, testingAL, "A.L. Testing");



    }

        /** Read training data from a tab-separated file. Assumes the classification is the first feature. Assigns each
         * example an id by concatenating "X" and its sequence number. */
        public static List<BinaryClassExample> readTrainingDataFromCSV(File file, List<Attribute> attributes) {

            String id;
            boolean classification;
            String[] vals;
            
            ArrayList<BinaryClassExample> set = new ArrayList<BinaryClassExample>();
    
            try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            
            int count = 0;
            String line;
            while ((line = br.readLine()) != null) {
                String[] entries = line.split(",");
    
                classification = false;
                if (entries[0].equals("e")) {
                    classification = true;
                }
                vals = new String[entries.length -1];
                System.arraycopy(entries,  1, vals, 0, vals.length);    // copy the rest of entries into a new array
    
                BinaryClassExample x = new BinaryClassExample("M"+(count+1), vals, classification, attributes);
                set.add(x);
                count++;
            }
            } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
            }
            return set;
        }

        public static void printAccuracy(DecisionTree tree, List<BinaryClassExample> testSet, String setName) {
            System.out.println("Testing on " + setName + " set:");
            int correct = 0;
            for (BinaryClassExample example : testSet) {
                boolean predicted = tree.predict(example);
                if (predicted == example.getClassification()) {
                    correct++;
                } else {
                    System.out.println("Wrong prediction on example " + example + ": incorrectly predicted " + predicted);
                }
            }
            double accuracy = (double) correct / testSet.size() * 100;
            System.out.printf("Accuracy: %f percent (%d/%d)", accuracy, correct, testSet.size());
            System.out.println();
        }
    }