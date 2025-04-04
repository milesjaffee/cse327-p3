package learning;

import java.util.Random;
import java.util.Arrays;

/** This class does a simplistic nearest neighbor calculation using the Manhattan distance
  * on numeric features. You will not use this class in HW P3. */
public class KNearestNeighbor {
  
  KnnExample[] trainingSet = null;
  int k;                                 // number of neighbors to select
  
  public static String[] classes = {"POSITIVE", "NEGATIVE"};
  public static final int DEFAULT_K = 3;
  
  public KNearestNeighbor(KnnExample[] trainingSet, int k) {
    this.trainingSet = trainingSet;
    this.k = k;
  }

  public String predict(double[] features) {
    DistanceResult[] distances = new DistanceResult[trainingSet.length];
    for (int i=0; i < trainingSet.length; i++) {
      double distance = manhattanDistance(trainingSet[i].features, features);
      distances[i] = new DistanceResult(trainingSet[i], distance);
      System.out.println(distances[i]);
    }
    Arrays.sort(distances);
    int posCount = 0;
    
    System.out.println("Neighbors:");
    for (int i=0; i < k; i++) {
      System.out.println(distances[i]);
      if (distances[i].example.classification.equals("POSITIVE"))
        posCount++;
    }
    if (posCount > k/2)
      return "POSITIVE";
    else 
      return "NEGATIVE";
  }
  
  public static double manhattanDistance(double[] x, double[] y) {
    double distance = 0;
    for (int i=0; i < x.length; i++)
      distance += Math.abs(x[i] - y[i]);
    return distance;
  }
  
  public static void main(String[] args) {
    Random rand = new Random();
    int trainSize = 8;
    int numFeat = 2;
    KnnExample[] trainingSet = new KnnExample[trainSize];
    for (int i=0; i < trainSize; i++) {
      double[] feat = new double[numFeat];
      for (int j=0; j < numFeat; j++)
        feat[j] = rand.nextInt(10)+1;
      trainingSet[i] = new KnnExample(feat, classes[rand.nextInt(2)]);
    }
    
    KNearestNeighbor knn = new KNearestNeighbor(trainingSet, DEFAULT_K);
    
    double[] test = {2,4};
    String predict = knn.predict(test);
    System.out.println(Arrays.toString(test) + ": " + predict);
  }
}

class KnnExample {
  
  double features[];
  String classification;
  
  public KnnExample(double[] features, String classification) {
    this.features = features;
    this.classification = classification;
  }
}

class DistanceResult implements Comparable<DistanceResult>{
  KnnExample example;
  double distance;
  
  public DistanceResult(KnnExample example, double distance) {
    this.example = example;
    this.distance = distance;
  }
  
  public int compareTo(DistanceResult other) {
    if (distance > other.distance)
      return 1;
    else if (distance <  other.distance)
      return -1;
    else 
      return 0;
  }
  
  public String toString() {
    String s = "[";
    for (int i=0; i < example.features.length; i++) {
      s = s + example.features[i];
      if (i < example.features.length-1)
        s = s + ",";
    }
    s = s + "]:" + example.classification + ", Dist: " + distance;
    return s;
  }
}