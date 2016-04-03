package models;

import java.util.ArrayList;
import java.util.TreeSet;

public class Candidat{
	
	private TreeSet<String> value = new TreeSet<String>();
	private ArrayList<String> ferme = new ArrayList<>();
	private String reglesExactes = new String();
	private String reglesApproximatives = new String();
	private ArrayList<String> surEnsemblesFerme = new ArrayList<>();
	private double support;
	private double confiance;
	private double lift ;
	
	
	public TreeSet<String> getValue() {
		return value;
	}
	
	public Candidat(TreeSet<String> value){
		this.value = value;
	}
	public Candidat(TreeSet<String> value, ArrayList<String> ferme, double support) {
		super();
		this.value = value;
		this.ferme = ferme;
		this.support = support;
	}


	public void setValue(TreeSet<String> value) {
		this.value = value;
	}
	public ArrayList<String> getFerme() {
		return ferme;
	}
	public void setFerme(ArrayList<String> ferme) {
		this.ferme = ferme;
	}
	public double getSupport() {
		return support;
	}
	public void setSupport(double support) {
		this.support = support;
	}

	public String getReglesExactes() {
		return reglesExactes;
	}

	public void setReglesExactes(String reglesExactes) {
		this.reglesExactes = reglesExactes;
	}

	public ArrayList<String> getSurEnsemblesFerme() {
		return surEnsemblesFerme;
	}

	public void setSurEnsemblesFerme(ArrayList<String> reglesapproximatives) {
		this.surEnsemblesFerme = reglesapproximatives;
	}

	public String getReglesApproximatives() {
		return reglesApproximatives;
	}

	public void setReglesApproximatives(String reglesApproximatives) {
		this.reglesApproximatives = reglesApproximatives;
	}
	
	public double getConfiance() {
		return confiance;
	}

	public void setConfiance(double confiance) {
		this.confiance = confiance;
	}
	
	
	public double getLift() {
		return lift;
	}

	public void setLift(double lift) {
		this.lift = lift;
	}

	public String getStringValue(){
		String value = "";
		for(String i : this.getValue()){
			value += i;
		}
		return value;
	}
	
	
	public String getStringSurEnsemble(){
		String value = "";
		for(String i : this.getSurEnsemblesFerme()){
			value += i;
		}
		return value;
	}
	
	public String getStringFerme(){
		String value = "";
		for(String i : this.getFerme()){
			value += i;
		}
		return value;
	}
	
	public String getStringRegleExact(){
		return this.getStringValue()+ " <span class='fleche'>-></span> " + this.getReglesExactes();
	}
	
	
	public String getStringRegleApproximative(){
		return this.getStringValue() + " <span class='fleche'>-></span> " + this.getReglesApproximatives();
	}
	
	public double getStringSupport(){
		return Math.floor(this.support * 10000) / 10000;
	}
	
	public double getStringLiftt(){
		return Math.floor(this.lift * 10000) / 10000;
	}
	
	public double getStringConfiance(){
		return Math.floor(this.confiance * 10000) / 10000;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ferme == null) ? 0 : ferme.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Candidat other = (Candidat) obj;
		if (ferme == null) {
			if (other.ferme != null)
				return false;
		} else if (!ferme.equals(other.ferme))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}		

}
