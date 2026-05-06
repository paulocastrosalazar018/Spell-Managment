package student;

public class Edge {
	// record the spells
	private String first;
	private String second;
	
	public Edge (String f, String s) {
		first  = f; // set the first spell 
		second = s;// set the second spell
	}
	public String getFirst() { // get the first spell
		return first;
	}

	public String getSecond() { // get the second spell
		return second;
	}

	public void setFirst(String f) { // set the first spell
		first = f;
	}
	
	public void setSecond(String s) { // set the second spell
		second = s;
	}
	
	public void PrintEdges() {
		System.out.println("first: "+first+" Second: " + second);
	}
}
