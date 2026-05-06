package student;
import java.util.*;
public class SpellList extends TreeMap<String,Boolean> {
	
	// set iterator
	Iterator<Map.Entry<String, Boolean>> it;
	
	// construct the spell list
	public SpellList() {
		// CONSTRUCT THE LSIT
		super();
	}
	
	public String top() {
		// return the first Spell of the list, as a queue
		return this.firstKey();
	}
	public void pop() {
		// deletes first Spell of list, treating as a queue
		this.remove(this.firstKey());
	}
	public void push(String n) {
		// inserts first Spell of list, treating as a queue
		this.put(n, true);
	}
	
	
	public Iterator<String> iterator() {
		// returns an iterator to use to iterate over the list
		return this.keySet().iterator();
	}
	
	public Boolean contains (String s) {
		it = this.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Boolean> pairs = it.next();
			//System.out.println(" " + pairs.getKey());
			if(pairs.getKey().equals(s)) {
				return true;
			}
			
		}
		return false;
	}
	
	public void print() {
		it = this.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Boolean> pairs = it.next();
			System.out.print(" " + pairs.getKey());
		}
		
		System.out.println();
	}
}
