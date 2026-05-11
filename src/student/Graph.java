package student;

import java.util.*;
public class Graph {
	
	static TreeMap<String,Spell> m_skillTree; // String: name of Spell, Spell: adjs Spells to this one
	static List<String> m_learnedSkills;
	static Iterator<Map.Entry<String, Spell>> gIt;
	static Integer m_numEdges;
	static Boolean terminate;
	
	public Graph() {
		
		m_skillTree = new TreeMap<String,Spell>();
		m_learnedSkills = new LinkedList<String>();
		m_numEdges = 0;
		terminate = false;
	}
	
	
	// number of spells in the skilltree 
	public Integer numSpells() {
		return m_skillTree.size();
	}
	// number of spells learned
	public Integer numSpellLearned() {
		return m_learnedSkills.size();
	}
	public Integer numEdges() {
		return m_numEdges;
	}
	
	
	public static Spell getSpell(String _s) {
		return m_skillTree.get(_s);
	}
	public NavigableSet<String> getVertexSet() {
		// Returns set of all vertices in the graph
		return m_skillTree.navigableKeySet();
	}
	public void addSpell(String n) {
		// Adds a new vertex n to the graph
		Spell v = new Spell(n);
		m_skillTree.put(n, v);
	}
	public void deleteVertex(String n) {
		// Deletes a vertex n from the graph
		m_skillTree.remove(n);  // Deletes vertex key from TreeMap
		gIt = m_skillTree.entrySet().iterator(); // must come after remove
		// Also remove vertex n from list of neighbours
		while (gIt.hasNext()) {
			Map.Entry<String, Spell> pairs = gIt.next();
			pairs.getValue().getAdjs().remove(n);
		}		
	}
	
	public static void print() {
		gIt = m_skillTree.entrySet().iterator();
		
		System.out.println("Number of nodes is " + m_skillTree.size());
		System.out.println("Number of edges is " + m_numEdges);

		while (gIt.hasNext()) {
			Map.Entry<String, Spell> pairs = gIt.next();
			pairs.getValue().print();
		}		
		System.out.println();
	}
	public static void setUnmarked() {
		// Sets all vertices to be unmarked e.g. after traversal
		gIt = m_skillTree.entrySet().iterator();
		while (gIt.hasNext()) {
			Map.Entry<String, Spell> pairs = gIt.next();
			pairs.getValue().setUnmarked();
			//pairs.getValue().setNum(0);
		}				
	}
	public String getFirstSpellName() {
		// Returns first vertex ID in TreeMap ordering
		// e.g. for starting a traversal
		return m_skillTree.firstKey();
	}
	public static boolean containsSpell(String n) {
		// Checks if n is a vertex in the graph
		return m_skillTree.containsKey(n);
	}
	
	public static void addFromPrereq(String s, Vector<String> outSpecs) {
		Queue<String> q = new LinkedList<String>();
		StringTokenizer l = new StringTokenizer(s);
		while(l.hasMoreTokens()) {
			String spellName = l.nextToken();
			if(!m_skillTree.containsKey(spellName)) {
				Spell spell = new Spell(spellName);
				m_skillTree.put(spellName, spell);
			}
			q.add(spellName);
		}	
		setDirected(q, outSpecs);
		if(terminate == false)
			setPrereq(q);
		
	}
	public static void setPrereq(Queue<String> q) {
		
		Queue<String> temp = new LinkedList<String>(q);

		// get the first spell in the queue
		Spell spell = m_skillTree.get(temp.poll());
		while(!temp.isEmpty()) {
			String prereq = temp.poll();
			// add prereq to the spell
			if(!spell.m_prereq.contains(prereq))
				spell.addPrereq(prereq);
		}
	
	}
	
	public static void setDirected(Queue<String> q,  Vector<String> outSpecs) {
		
		Queue<String> temp = new LinkedList<String>(q);
		// get the fist spell
		Spell spell = m_skillTree.get(temp.poll());
		//setting directed in the graph
		while(!temp.isEmpty()) {
			//add neighbour by looking at the top of the queue
			//check if we can add
			
			if(!hasCycle(spell.m_name , temp.peek())) {
				spell.addAdj(temp.peek());
			}else {
				outSpecs.add("   Found cycle in prereqs");
				terminate = true;
				
			}
			//change the spell to the top of the queue
			spell = getSpell(temp.poll());
			setUnmarked();
		}
		
	}
	
	public static Boolean hasCycle(String spellName,String neighbourName) {
		System.out.println();
		DFS(neighbourName);
		if(getSpell(spellName).m_marked) {
			return true;
		}
		
		return false;
	}
	// return a list of strings containing a depth first traversal from s
	public static List<String> DFS(String s){
		
		List<String> visitedList = new Vector<String>();
		getSpell(s).setMarked();
		SpellList adjList = getSpell(s).getAdjs();
		Iterator<String> sIt = adjList.iterator();
		while(sIt.hasNext()) {
			String nextSpell = sIt.next();
			if (!getSpell(nextSpell).isMarked()) {
				List<String> temList = DFS(nextSpell);
				visitedList.addAll(temList);
			}
		}
		visitedList.add(0,s);
		return visitedList;
	}
	/**
	 * Find a cycle reachable from StartNode using DFS + Recursion
	 * Returns the cycle as an ordered list of Spell Name
	 * or an empty list with no cycle exist from this node
	 */
	public static List<String> findCycle(String startNode){
		Map<String,Boolean> recStack = new HashMap<>(); // tracks nodes
		Map<String,String> parent = new HashMap<>(); // tracks edges
		
		for( String key : m_skillTree.keySet()) {
			recStack.put(key, false);
		}
		
		List<String> cycle = new ArrayLsit<>();
		findCycleHelper(startNode, recStack, parent,cycle);
		return cycle;
	}
	
	private static boolean findCycleHelper(
			String node, Map<String,Boolean> recStack,
			Map<String, String> parent, List<String> cycle) {
		
		getSpell(node).setMarked();
		recStack.put(node, true);
		
		SpellList adjs = getSpell(node).getAdjs();
		Iterator<String> it = adjs.iterator();
		while(it.hasNext()) {
			String neighbour = it.next();
			
			// the neighbour hasnt been recursively visisted
			if(!getSpell(neighbour).isMarked()) {
				parent.put(neighbour, node);
				if(findCycleHelper(neighbour,recStack,parent,cycle))
					return true;
			}else if( recStack.getOrDefault(neighbour, false)) {
			//neighbour is on path therefore there is a cycle
			// reconstruct cycle by walkinf back through parent map
				cycle.add(neighbour);//cycle closes back to this node
				String cur = node;
				while(!cur.equals(neighbour)) {
					cycle.add(cur);
					cur = parent.get(cur);
				}
				cycle.add(neighbour); // add start again so it apears when printing
				Collections.reverse(cycle);
				return true;
			}
		}
		recStack.put(node, false);
		return false;
	}
	
	public static List<List<String>> findAllCycles(){
		List<List<String>> allCycles = new ArrayList<>();
		for(String key : m_skillTree.keySet()) {
			setUnmarked();
			List<String> cycle = findCycle(key);
			if(!cycle.isEmpty()) {
				//avoid Duplocates by checking if we have seen the set
				boolean duplicate = false;
				Set<String> cycleSet = new HashSet<>(cycle);
				for(List<String> existing : allCycles) {
					if(new HashSet<>(existing).equals(cycleSet)) {
						duplicate = true;
						break;
					}
				}
				if(!duplicate) {
					allCycles.add(cycle);
				}
			}
		}
		setUnmarked();
		return allCycles;
	}
	public static void removeEdge(String from, String to) {
	    Spell spell = getSpell(from);
	    if (spell != null) {
	        spell.removeAdj(to);
	        // Also clean up prereq list so Learn/Forget stay consistent
	        SpellList prereqs = spell.getPrereq();
	        if (prereqs.contains(to)) {
	            prereqs.remove(to);
	        }
	    }
	}
	
}
