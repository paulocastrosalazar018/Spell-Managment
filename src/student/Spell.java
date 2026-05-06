package student;


public class Spell {
	String m_name;
	SpellList m_adjs;
	SpellList m_prereq;
	Boolean m_marked;
	
	public Spell(String _name) {
		m_name = _name;
		m_marked = false;
		m_adjs = new SpellList();
		m_prereq = new SpellList();
	}
	// we have visit the node
	public void setMarked() {
		m_marked = true;
	}
	// unMarking the node
	public void setUnmarked() {
		m_marked = false;
	}
	// check if it is marked
	public boolean isMarked() {
		return m_marked;
	}
	// add a neighbour to the adjs list
	public void addAdj (String s) {
		// Adds a neighbour n to the current vertex 
		m_adjs.push(s);
	}
	// add a neighbour to the adjs list
	public void addPrereq (String s) {
		// Adds a neighbour n to the current vertex 
		m_prereq.push(s);
	}
	public void removeAdj (String s) {
		// we go trough the adjList
		SpellList temp = new SpellList();
		while(!m_adjs.isEmpty()) {
			String top = m_adjs.top();
			if(s != top) {
				temp.push(top);
			}
			m_adjs.pop();
		}
		m_adjs = temp;
	}
	
	//give me all the list connected to this Spell
	public SpellList getAdjs() {
		return m_adjs;
	}

	public SpellList getPrereq(){
		return m_prereq;
	}
	// get me the name of the Spell
	public String getSpellName() {
		return m_name;
	}
	
	// show me the node and its neighbours
	public void print() {
		System.out.print("Node " + m_name + " (" + m_marked + ","  + ") :");
		m_adjs.print();
	}
	
}
