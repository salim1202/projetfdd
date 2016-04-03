package models;


public interface IClose {
	
	
	/**
	 * Appliquer l'algorithme close
	 */
	public void applyClose();
	
	/**
	 * Affichage des regles exactes
	 * 
	 * @return
	 */
	public String displayReglesExactes();
	
	/**
	 * Afficher les regles approximatives
	 * 
	 * @return String
	 */
	public String displayReglesApproximatives();
	
	/**
	 * Le contenu a ecrire sur un fichier
	 * @return String
	 */
	public String WriteRegles();
	

}
