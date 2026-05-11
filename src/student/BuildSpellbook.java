package student;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class BuildSpellbook extends Graph{
	
	public final Integer MAXCOMS = 1000;  // maximum number of specs
	// TODO: add appropriate attributes / variables
	// List all the commands in a list
	static String m_commandName[] = {"PREREQ", "LEARN", "FORGET", "ENUM", "END"};
	
	
		static String cutComd(String _s, int _commandNameLength) {
		return _s.substring(_commandNameLength + 1);
	}
	
	public Vector<String> execNSpecs (Vector<String> specs, Integer N) {
		// PRE: specs contains set of specifications readed in by readSpecsFromFile()
		// POST: executed min(N, all) specifications,
        //       returning required output, one line per string in vector
		
		// TODO
		// if N is bigger than the speccs size then we are out of bounds
		//N is the value where we start the to read the command (0 - specs.size())
		Vector<String> outSpecs = new Vector<String>();
		//and it will go all the way through the specs size
		int c = 0;
		Iterator<String> sIt = specs.iterator();
		while(sIt.hasNext() && c != N) {
			String s = sIt.next();
			outSpecs.add(s);	
			
			if(s.contains(m_commandName[4])|| terminate == true ) {
				terminate = true;
				break;
			}
			if(terminate == false){
				// we need to recognise the command
				if(s.contains(m_commandName[0])) {
					// WE ARE PREREQ
					String line = cutComd(s,m_commandName[0].length());
					addFromPrereq(line,outSpecs);	
				}
				if(s.contains(m_commandName[1])) {
					// WE ARE LEARNING
					String line = cutComd(s,m_commandName[1].length());
					Learn(line,outSpecs);
				}
				if(s.contains(m_commandName[2])) {
					// WE ARE FORGETTING
					String line = cutComd(s,m_commandName[2].length());
					Forget(line,outSpecs);
				}
				if(s.contains(m_commandName[3])) {
					// we are printing learned skills
					Iterator<String> lIt = m_learnedSkills.iterator();
					while(lIt.hasNext()) {
						outSpecs.add("   "+ lIt.next());
					}
				}
				
			}
			c++;
		}
		System.out.println("------out specs ---------");
		Iterator<String> oIt = outSpecs.iterator();
		while(oIt.hasNext()) {
			System.out.println(oIt.next());
		}
		
		return outSpecs;
	}
	
	public static void Forget(String _s , Vector<String> outSpecs) {
		// we need to go trough the spells that we know
		// if find s in one of our require then we stop forgetting 
		if(!m_learnedSkills.contains(_s)) {
			outSpecs.add("   "+_s + " is not learned");
		}else {
			//lets check if the spell is needed by others
			if(!isNeeded(_s)) {
				// the spell is save to forget
				removeFromLearned(_s);
				outSpecs.add("   Forgetting "+ _s);
				
				
				// we check if their prereq are deletable;
				SpellList prereq = getSpell(_s).getPrereq();
				List<String> prereqCopy = new ArrayList<String>();
				Iterator<String> pIt = prereq.iterator();
				while(pIt.hasNext()) {
					prereqCopy.add(pIt.next());
				}
				for (String p: prereqCopy){
					if(m_learnedSkills.contains(p)) {
						Forget(p,outSpecs);
					}
				}
			}else {
				outSpecs.add("   "+_s + " is still needed");
			}
		}
	}
	public static void removeFromLearned(String _s) {
		for(int i = 0; i < m_learnedSkills.size(); i++){
			if(m_learnedSkills.get(i).equals(_s)) {
				m_learnedSkills.remove(i);
			}
		}
	}
	
	
	
	public static Boolean isNeeded(String _s) {
		//_s is the spell that we are checking to delete
		// post condition: returns false 
		//    iff a spell in the learnedskill list needs this spell
		
		// for each learned skills
		for(String s: m_learnedSkills) {
			//if it is equal skip 
			if(s.equals(_s))
				continue;
			// if it is not then we check its prereqs
			SpellList prereq = getSpell(s).getPrereq();
			//if it does contain the spell that we need to remove
			if(prereq.contains(_s))
				return false;
		}
		
		return true;
		
	}
	// go through the learnedskills 
	// check 
	
	public void learnSpell(String _s, Vector<String> outSpecs) {
		if(!m_skillTree.containsKey(_s)) {
			// skill not in the tree therefore we learn it
			Spell spell = new Spell(_s);
			m_skillTree.put(_s,spell);
			m_learnedSkills.add(_s);
			outSpecs.add(" learning " +_s);
			return;
		}
		learnRecur(_s,outSpecs);
		setUnmarked();
	}
	public void learnRecur(String _s, Vector<String> outSpecs) {
		Spell spell = getSpell(_s);
		
		if(spell.isMarked()) return; // we  have checked it there for no need to check that path
		spell.setMarked(); // if not mark it
		
		// lets learn all the prereqs before learning the spell
		SpellList prereqs = spell.getPrereq();
		Iterator<String> pIt = prereqs.iterator();
		while(pIt.hasNext()) {
			String prereq = pIt.next();
			if(!m_learnedSkills.contains(prereq)) {
				learnRecur(prereq,outSpecs);
			}
		}
		// start from last
		if(m_learnedSkills.contains(_s)) {
			outSpecs.add( "  "+_s +" is already leanred");
		}else {
			m_learnedSkills.add(_s);
			outSpecs.add(" Learning " + _s);
		}
	}
		
	
	
	
	// learning function
	public void Learn(String _s, Vector<String> outSpecs ) {
		// 1 - if their adj list are empty then we learn
		// 2 - if their adj list is not empty and not learn we learn it
		if(m_skillTree.containsKey(_s)) {
			
			
			
			List<String> path = DFS(_s);
			Stack<String> lStack = new Stack<String>();
			SpellList prereq = getSpell(_s).getPrereq();
			
			
			lStack.push(path.get(0));
			
			Iterator<String> pIt = path.iterator();
			while(pIt.hasNext()) {					
				String sName = pIt.next();
				if(prereq.contains(sName) && !m_learnedSkills.contains(sName)) {						
					lStack.push(sName);					
				}
			}
			while(!lStack.isEmpty()) {
				String stackTop = lStack.pop();
				if(m_learnedSkills.contains(stackTop)) {
					outSpecs.add("   "+stackTop + " is already learned");
				}else {
					m_learnedSkills.add(stackTop);
					outSpecs.add("   Learning " + stackTop);
				}
			}
			setUnmarked();
		}else {
			Spell spell = new Spell(_s);
			m_skillTree.put(_s, spell);
			m_learnedSkills.add(_s);
			outSpecs.add("   Learning " + _s);
			
		}
		
	}

	
	
	public Vector<String> execNSpecswCheck (Vector<String> specs, Integer N) {
		// PRE: specs contains set of specifications read in by readSpecsFromFile()
		// POST: executed min(N, all) specifications, checking for cycles,
        //       returning required output, one line per string in vector

		// TODO
		// we need to iterate through the specs
		//
		Vector<String> outSpecs = new Vector<String>();
		//and it will go all the way through the specs size
		int c = 1;
		Iterator<String> sIt = specs.iterator();
		while(sIt.hasNext() && c <= N ) {
			String s = sIt.next();
				outSpecs.add(s);
			if(s.contains(m_commandName[4])) {
				terminate = true;	
				break;
			}else {
				// we need to recognise the command
				if(s.contains(m_commandName[0])) {
					// WE ARE PREREQ
					String line = cutComd(s,m_commandName[0].length());
					addFromPrereq(line,outSpecs);	
				}
				if(s.contains(m_commandName[1])) {
					// WE ARE LEARNING
					String line = cutComd(s,m_commandName[1].length());
					learnSpell(line,outSpecs);
				}
				if(s.contains(m_commandName[2])) {
					// WE ARE FORGETTING
					String line = cutComd(s,m_commandName[2].length());
					Forget(line,outSpecs);
				}
				if(s.contains(m_commandName[3])) {
					// we are printing learned skills
					Iterator<String> lIt = m_learnedSkills.iterator();
					while(lIt.hasNext()) {
						outSpecs.add("   "+ lIt.next());
					}
				}
				
			}
			c++;
		}
		return outSpecs;
	}
	
	public Vector<String> execNSpecswCheckRecLarge (Vector<String> specs, Integer N) {
		// PRE: specs contains set of specifications read in by readSpecsFromFile()
		// POST: executed min(N, all) specifications, checking for cycles and 
		//       recommending fix by removing largest cycle,
		//       returning required output, one line per string in vector

		
		// TODO

		return new Vector<String>();
	}

	public Vector<String> execNSpecswCheckRecSmall (Vector<String> specs, Integer N) {
		// PRE: specs contains set of specifications read in by readSpecsFromFile()
		// POST: executed min(N, all) specifications, checking for cycles and 
		//       recommending fix by removing smallest cycle,
        //       returning required output, one line per string in vector

		
		// TODO

		return new Vector<String>();
	}

	
	
	
	// Provided files below
	public Vector<String> readSpecsFromFile(String fInName) throws IOException {
		// PRE: -
		// POST: returns lines from input file as vector of string

		BufferedReader fIn = new BufferedReader(
							 new FileReader(fInName));
		String s;
		Vector<String> comList = new Vector<String>();
		
		while ((s = fIn.readLine()) != null) {
			comList.add(s);
			
		}
		fIn.close();
		
		return comList;
	}

	public Vector<String> readSolnFromFile(String fInName, Integer N) throws IOException {
		// PRE: -
		// POST: returns (up to) N lines from input file as a vector of N strings;
		//       only the specification lines are counted in this N, not responses

		BufferedReader fIn = new BufferedReader(
							 new FileReader(fInName));
		String s;
		Vector<String> out = new Vector<String>();
		Integer i = 0;
		System.out.println("--------sol ---------");
		while (((s = fIn.readLine()) != null) && (i <= N)) {
			System.out.println(s);
			if ((i != N) || s.startsWith("   ")) // responses to commands start with three spaces
				
				
				out.add(s);
			if (!s.startsWith("   "))  
				i += 1;
		}
		fIn.close();
		
		return out;
	}
	
	public Boolean compareExecWSoln (Vector<String> execd, Vector<String> soln) {
		// PRE: -
		// POST: Returns True if execd and soln string-match exactly, False otherwise

		if (execd.size() != soln.size()) {
			return Boolean.FALSE;
		}
		for (int i = 0; i < execd.size(); i++) {
			if (!execd.get(i).equals(soln.get(i))) {
				System.out.println("----- errror ----");
				System.out.println(execd.get(i));
				System.out.println(soln.get(i));
				return Boolean.FALSE;
			}
		}

		return Boolean.TRUE;

	}
	
	public static void main(String[] args) {
		
		// Build the SMS system
		BuildSpellbook m_SMS = new BuildSpellbook();
		// Read the inputs 
		String PATH = "C:\\Users\\travi\\OneDrive\\Desktop\\Workshop\\SampleData\\";
		String datafile = "sample_P1.in";
		// The number of commands we can run
		//Integer N = m_SMS.MAXCOMS;
		// holder of inputs line by line
		Vector<String> m_inputs = null;
		
		// try reading the commands
		try {
			m_inputs = m_SMS.readSpecsFromFile(PATH+datafile);
		}catch(IOException e){
			System.out.println("in exception: " + e);

		}
		
		
		
	}
	
	
	
}
