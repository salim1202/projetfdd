package models;

import java.util.HashMap;

public class Generator {
	
	private HashMap<String,Candidat> candidats =  new HashMap<>();
	
	
	public Generator(){
		
	}
	
	public Generator(HashMap<String,Candidat> candidats) {
		this.candidats = candidats;
		
	}
	
	

	public HashMap<String, Candidat> getCandidats() {
		return candidats;
	}

	public void setCandidats(HashMap<String, Candidat> candidats) {
		this.candidats = candidats;
	}
	
	public void addCandidat(Candidat c,boolean unique){
		String key = "";
		if(unique){
			key = c.getStringValue();
		}
		else{
			key = (this.getCandidats().size() + 1) + "";
		}
		this.candidats.put(key, c);
	}
	
	
	
	

}
