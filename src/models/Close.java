package models;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

public class Close implements IClose {

	/**
	 * Les lignes du fichier
	 */
	private HashMap<Integer, Ligne> lignes = new HashMap<>();
	/**
	 * les generateurs FF1,FF2,...,FFn
	 */
	private ArrayList<Generator> generators = new ArrayList<>();
	/**
	 * Le dernier generateur des regles approximatives
	 */
	private Generator generatorAppreoximative = new Generator();
	
	/**
	 *  Le support minimum donné par l'utilisateur 
	 */
	private Double supportMin;
	


	/**
	 * Le constructeur 
	 * @param lines
	 * @param supportMin
	 */
	public Close(HashMap<Integer, Ligne> lines,Double supportMin){
		this.supportMin = supportMin;
		this.lignes = lines;
	}

	/**
	 * Créer la liste de tous les candidats pour FF1
	 */
	private void setCandidiatFF1(){
		Integer index = 0;
		ArrayList<String> candidats = new ArrayList<>();
		this.generators.add(new Generator());
		//Pour chaque ligne
		for(int i=0;i<getLignes().size();i++){
			//Ensuite pour chaque items d'une ligne
			for(int j=0;j<getLignes().get(i).getItems().size();j++){
				String item = getLignes().get(i).getItems().get(j);
				//Si le candidat n'existe pas encore on le rajoute
				if( !candidats.contains(item) ){
					candidats.add(item);
				}
			}
		}
		//ordonner tous les candidats en ordre alphabétique
		Collections.sort(candidats);
		//Ajouter les valeurs des candidats obtenues dans une LinkedHashSet
		for(String c : candidats){
			TreeSet<String> v = new TreeSet<String>();
			v.add(c);
			Candidat can = new Candidat(v);
			getGenerators().get(0).getCandidats().put(can.getStringValue(),can );
			index++;
		}
	}
	
	
	/**
	 * Créer tous les candidats possibles de la table FF (n)
	 * @param Integer n : le niveau FF (n) exemple FF2,FF3...
	 */
	private void setCandidatFFn(Integer n){
		int i = 0;
		this.generators.add(new Generator());
		for (Map.Entry<String, Candidat> candidat11 : getGenerators().get(n-1).getCandidats().entrySet()) {
			TreeSet<String> candidat1 = (TreeSet<String>) candidat11.getValue().getValue();
			Iterator<Entry<String, Candidat>> entries = getGenerators().get(n-1).getCandidats().entrySet().iterator();
			int j=0;
			while (entries.hasNext()) {
				if(j<i+1){
					j++;
					continue;
				}
				TreeSet<String> candidat2 = (TreeSet<String>) (entries.next()).getValue().getValue();
				@SuppressWarnings("unchecked")
				TreeSet<String> newItem = (TreeSet<String>) candidat1.clone();
				newItem.addAll(candidat2);
				Candidat c = new Candidat(newItem);
				this.generators.get(n).getCandidats().put(c.getStringValue(),c);
				j++;
			}
			i++;
		}
		calculFermeAndSupport(this.generators.get(n),n);
	}


	/**
	 * Calculer les fermés et les supports de chaque candidat
	 */
	private void calculFermeAndSupport(Generator generator,Integer n ){
		//Pour chaque ligne
		for(int i=0;i<getLignes().size();i++){
			Ligne currentLigne = getLignes().get(i);	
			for (Map.Entry<String, Candidat> candidat : generator.getCandidats().entrySet()) {
				if(currentLigne.getItems().containsAll(candidat.getValue().getValue())){
					if(candidat.getValue().getFerme().isEmpty()){
						candidat.getValue().getFerme().addAll(currentLigne.getItems());
						candidat.getValue().setSupport(1.0);
					}
					else
					{
						candidat.getValue().getFerme().retainAll(currentLigne.getItems());
						candidat.getValue().setSupport(candidat.getValue().getSupport()+1);
					}
				}	
			}
		}
		//Supprimer les candidats qui ont un support inferieur au support minimum
		deleteCandidatWhereSupportinferior(generator,n);
	}

	
	/**
	 * Appliquer tous l'algorithme
	 */
	public void applyClose(){
		this.setCandidiatFF1();
		this.calculFermeAndSupport(this.getGenerators().get(0),0);
		int index = 1;
		while(! getGenerators().get(index-1).getCandidats().isEmpty()){
			this.setCandidatFFn(index);
			index++;
		}
		for(int i=0;i<getGenerators().size();i++){
			for (Map.Entry<String, Candidat> c : getGenerators().get(i).getCandidats().entrySet()) {
				setReglesApproximatives(c.getValue());	
			}
		}
	}
	

	/**
	 * Calcul d'une regle exacte d'un candidat
	 * @param Candidat candidat
	 */
	private void setReglesExactes(Candidat candidat){
		for(String item : candidat.getFerme()){
			if(!candidat.getValue().contains(item)){
				candidat.setReglesExactes(candidat.getReglesExactes() + item);
			}
		}
	}
	
	
	/**
	 * calcul d'une regle Approximative et les sur-Ensemble d'un candidat 
	 * calcul du support et Confiance aussi
	 * 
	 * @param candidat
	 */
	private void setReglesApproximatives(Candidat candidat){
		for(int i=0;i<getGenerators().size();i++){
			for(Map.Entry<String, Candidat> currentCandidat : getGenerators().get(i).getCandidats().entrySet()){
				if(currentCandidat.getValue().getFerme().containsAll(candidat.getFerme()) && !candidat.getFerme().containsAll(currentCandidat.getValue().getFerme()) ){
					Candidat c= new Candidat(candidat.getValue(), candidat.getFerme(), candidat.getSupport());
					//Calcul regle appro.
					String ra = "";
					for(String f : currentCandidat.getValue().getFerme()){
						if(!candidat.getValue().contains(f)) ra +=f;
					}
					c.setReglesApproximatives(ra);
					c.setSurEnsemblesFerme(currentCandidat.getValue().getFerme());
					if(!isExistRegleapproximative(c)){
						//supp AB
						ArrayList<String> surEnsemble = new ArrayList<>();
						for(int j=0;j<c.getStringSurEnsemble().length();j++){
							surEnsemble.add(String.valueOf(c.getStringSurEnsemble().charAt(j)));
						}
						c.setSupport(calculSupportOf(surEnsemble));
						
						//supp A
						ArrayList<String> value = new ArrayList<>();
						for(String item : c.getValue()){
							value.add(item);
						}
						double suppA = calculSupportOf(value);
						
						//Supp B
						ArrayList<String> regleAppr = new ArrayList<>();
						for(int j=0;j<c.getReglesApproximatives().length();j++){
							regleAppr.add(String.valueOf(c.getReglesApproximatives().charAt(j)));
						}
						double suppB = calculSupportOf(regleAppr);
						c.setConfiance(c.getSupport()/candidat.getSupport());
						c.setLift(c.getSupport()/(suppA*suppB));
						getGeneratorAppreoximative().addCandidat(c, false);
					}
				}
			}
		}
	}
	
	
	/**
	 * Calcul support d'un candidat sur la table des regles approximatives
	 * 
	 * @param String items
	 * @return double
	 */
	private double calculSupportOf(ArrayList<String> items){
		double supp = 0;
		for(int i=0;i<getGenerators().size();i++){
			for(Map.Entry<String, Candidat> currentCandidat : getGenerators().get(i).getCandidats().entrySet()){
				if(currentCandidat.getValue().getFerme().containsAll(items)){
					supp++;
				}
			}
		}
		
		return supp/getLignes().size();
	}
	
	
	
	/**
	 * Tester si la regle approximative existe déja
	 * 
	 * @param Candidat c
	 * @return boolean
	 */
	private boolean isExistRegleapproximative(Candidat c){
		for (Map.Entry<String, Candidat> candidat : getGeneratorAppreoximative().getCandidats().entrySet()) {
			//Afficher le candidat
			Candidat currentCandidat = candidat.getValue();
			if(currentCandidat.getStringValue().equals(c.getStringValue())){
				if(currentCandidat.getReglesApproximatives().equals(c.getReglesApproximatives()))
					return true;
			}
		}
		return false;
		
	}
	

	/**
	 * Supprimer les candidats qui ont un support inferieur au support minimum
	 * 
	 * @param Generator generator
	 * @param Integer n
	 */
	private void deleteCandidatWhereSupportinferior(Generator generator,Integer n){
		Integer index = 0;
		HashMap<String,Candidat> newCandidats = new HashMap<>();
		for (Map.Entry<String, Candidat> candidat : generator.getCandidats().entrySet()) {
			if(((candidat.getValue().getSupport())/(getLignes().size()))>= (this.supportMin)){
				if(!FermeExist(candidat.getValue(),n)){
					candidat.getValue().setSupport(candidat.getValue().getSupport()/getLignes().size());
					candidat.getValue().setConfiance(calculSupportOf(candidat.getValue().getFerme())/candidat.getValue().getSupport());
					//suppA
					double suppA= candidat.getValue().getSupport();
					//suppB
					ArrayList<String> regleExacte = new ArrayList<>();
					for(int j=0;j<candidat.getValue().getReglesExactes().length();j++){
						regleExacte.add(String.valueOf(candidat.getValue().getReglesExactes().charAt(j)));
					}
					double suppB = calculSupportOf(regleExacte);
					candidat.getValue().setLift(calculSupportOf(candidat.getValue().getFerme())/(suppA*suppB));
					//candidat.getValue().setLift(lift);
					newCandidats.put(candidat.getValue().getStringValue(),candidat.getValue());
					//Déduire Les règles exactes et approximatives 
					this.setReglesExactes(candidat.getValue());
					index++;
				}
			}
		}
		generator.setCandidats(newCandidats);
	}
	
	
	/**
	 * Tester le candidat s'il existe dans les fermés des generateurs precedants
	 * 
	 * @param Candidat newCandidat
	 * @param Integer niveau
	 * @return boolean
	 */
	private boolean FermeExist(Candidat newCandidat, Integer niveau){
		if(niveau==0) return false ;
		for(int i=0;i<niveau;i++){
			Generator generator = getGenerators().get(i);
			for (Map.Entry<String, Candidat> c : generator.getCandidats().entrySet()) {
				Candidat candidat = c.getValue();
				if(candidat.getFerme().containsAll(newCandidat.getValue()) || newCandidat.getFerme().isEmpty()){
					return true;
				}
			}
		}
		return false;
	}

	// AFFICHAGE
	
	/**
	 * Affichage des regles exactes
	 * 
	 * @return
	 */
	public String displayReglesExactes(){
		String result = "<body><table><caption>Les règles exactes<caption><tr><th>Candidat</th><th>Fermé</th><th>Regle exacte</th><th>Supp.</th><th>Conf.</th><th>Lift</th></tr><tr>";
		for(int i=0;i<getGenerators().size();i++){
			for (Map.Entry<String, Candidat> c : getGenerators().get(i).getCandidats().entrySet()) {
				if(c.getValue().getValue().containsAll(c.getValue().getFerme()) && c.getValue().getFerme().containsAll(c.getValue().getValue())) continue;
				//Afficher le candidat
				Candidat currentCandidat = c.getValue();
					result += "<td>" + (currentCandidat.getStringValue()) + "</td>";
					result += "<td>" + (currentCandidat.getStringFerme()) + "</td>";
					result += "<td>" + currentCandidat.getStringRegleExact() +"</td>";
					result += "<td>" + currentCandidat.getStringSupport() +"</td>";
					result += "<td>" + currentCandidat.getStringConfiance() +"</td>";
					result += "<td>" + currentCandidat.getStringLiftt() +"</td>";
					result += "</tr>";
			}
		}
		return result + "</table>";
	}
	
	
	/**
	 * Afficher les regles approximatives
	 * 
	 * @return String
	 */
	public String displayReglesApproximatives(){
		String result = "<br/><table><caption>Les règles approximatives<caption><tr><th>Candidat</th><th>Fermé</th><th title='Sur-ensemble fermé'>SA</th><th>Regle Appr.</th><th>Supp.</th><th>Conf.</th><th>Lift</th></tr><tr>";
		for (Map.Entry<String, Candidat> c : getGeneratorAppreoximative().getCandidats().entrySet()) {
		//Afficher le candidat
		Candidat currentCandidat = c.getValue();
			result += "<td>" + currentCandidat.getStringValue() + "</td>";
			//Afficher le suipport du candidat
			result += "<td>" + currentCandidat.getStringFerme()+ "</td>";
			result += "<td>" + currentCandidat.getStringSurEnsemble() + "</td>";
			//Afficahage Règle exacte
			if(!currentCandidat.getReglesApproximatives().isEmpty()){
				result +=  "<td>" + currentCandidat.getStringRegleApproximative() + "</td>";
			}
			else{
				result +=  "<td>/</td>" ;
			}
			//Support
			result +=  "<td>"+ currentCandidat.getStringSupport() +"</td>";
			//Confiance
			result +=  "<td>"+ currentCandidat.getStringConfiance() +"</td>" ;
			//Lift
			result +=  "<td>"+ currentCandidat.getStringLiftt() +"</td></tr>" ;
		}
		return result + "</table></body>";
	}
	
	/**
	 * Le contenu a ecrire sur un fichier
	 * @return String
	 */
	public String WriteRegles(){
		String result = "---Les regles exactes:\n";
		for(int i=0;i<getGenerators().size();i++){
			for (Map.Entry<String, Candidat> c : getGenerators().get(i).getCandidats().entrySet()) {
				if(c.getValue().getValue().containsAll(c.getValue().getFerme()) && c.getValue().getFerme().containsAll(c.getValue().getValue())) continue;
				//Afficher le candidat
				Candidat currentCandidat = c.getValue();
					result +=  (currentCandidat.getStringValue()) +", -fermé:" ;
					result +=  (currentCandidat.getStringFerme()) + ", -regleExacte:";
					result +=  currentCandidat.getStringValue()+ "->" +currentCandidat.getReglesExactes() +", -support:";
					result +=  currentCandidat.getStringSupport() +", -conf.:";
					result +=  currentCandidat.getStringConfiance() +", -lift:";
					result +=  currentCandidat.getStringLiftt();
					result += "\n";
			}
		}
		result += "\n\n---Les regles approximatives:\n";
		for (Map.Entry<String, Candidat> c : getGeneratorAppreoximative().getCandidats().entrySet()) {
			Candidat currentCandidat = c.getValue();
				result +=  currentCandidat.getStringValue() + ", -fermé:";
				result +=  currentCandidat.getStringFerme()+ ", -SA:";
				result +=  currentCandidat.getStringSurEnsemble() + ", -reglesAppr.:";
				result +=  currentCandidat.getStringValue()+ "->" +currentCandidat.getReglesApproximatives() + ", -support:";
				result +=  currentCandidat.getStringSupport() +", -conf.:";
				result +=  currentCandidat.getStringConfiance() +", -lift:" ;
				result +=  currentCandidat.getStringLiftt() +"\n" ;
			}
		return result ;
	}


	//GETTERS & SETTERS

	
	public ArrayList<Generator> getGenerators(){
		return this.generators;
	}

	
	public HashMap<Integer, Ligne> getLignes() {
		return lignes;
	}

	public void setLignes(HashMap<Integer, Ligne> lignes) {
		this.lignes = lignes;
	}

	public Double getSupportMin() {
		return supportMin;
	}

	public void setSupportMin(Double supportMin) {
		this.supportMin = supportMin;
	}

	public Generator getGeneratorAppreoximative() {
		return generatorAppreoximative;
	}

	public void setGeneratorAppreoximative(Generator generatorAppreoximative) {
		this.generatorAppreoximative = generatorAppreoximative;
	}

	
	public void setGenerators(ArrayList<Generator> generators) {
		this.generators = generators;
	}

	
	public String getHead(){
		return "<head><style>" +
				"body{font-family:arial;}" +
				".fleche{color:rgb(18,165,244)}" +
				"table{border-collapse:collapse;width:98%;}" +
				"th,td{border:1px solid black;width:20%;}" +
				"td{text-align:center;}caption{font-weight:bold}" +
				"</style></head>";
	}
	

}
