package learning;

import java.util.Arrays;
import java.util.List;

/** An example for a binary classification learning task. */
public class BinaryClassExample {
  String id;
  String[] values;
  boolean classification;
  List<Attribute> attribOrder;

  /** Creates a new binary classification example with a specified id, array of values, boolean classification
   * and list of Attributes. The list of Attributes is needed to be able to look up a value by its attribute
   * name. Since a list is an object and passed by reference, it only adds a small amount to the storage of
   * each example, and can prevent errors between learning and testing.
   * @param id A string id for the example. Generally used for debugging / verbose modes
   * @param values An array of String attribute values
   * @param classification The ground truth of the example (true/false)
   * @param attribOrder A list of Attributes that determines the order in which the values are presneted.
   */
  public BinaryClassExample(String id, String[] values, boolean classification, List<Attribute> attribOrder) {
    this.id = id;
    this.values = values;
    this.classification = classification;
    this.attribOrder = attribOrder;
  }

  /** Return the value of a specific attribute.  */
  public String getValue(Attribute attrib) {
    int aindex = attribOrder.indexOf(attrib);
    return values[aindex];   
  }

  /** Return the ground truth of the example. */
  public boolean getClassification() {
    return classification;
  }
  
  /** Pretty-print the example. */
  public String toString() {
    return id + " " + Arrays.toString(values) + " " + classification;
  }
}
