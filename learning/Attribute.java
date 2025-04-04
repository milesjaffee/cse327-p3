package learning;

import java.util.Arrays;

/** A feature to be used in machine learning. Has a name and a 
 * set of valid values.
 */
public class Attribute {
	String name;
	String[] range = null;
		  	
	public Attribute(String name, String[] range) {
		this.name = name;
		this.range= range;
	}
	
	public String toString() {
		return name + "=" + Arrays.toString(range);
	}
	
	public boolean equals(Object o) {
		if (o instanceof Attribute) {
			Attribute a = (Attribute)o;
			return name.equals(a.name);
		}
		return false;
	}
	
	public int hashCode() {
		return name.hashCode();
	}
	
}

