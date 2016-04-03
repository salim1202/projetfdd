package models;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Outils {
	
	/**
	 * Permet de lire un fichier
	 * @param String pathname
	 * @return String
	 * @throws IOException
	 */
	public static String read(String pathname) throws IOException {
	    File file = new File(pathname);
	    StringBuilder fileContents = new StringBuilder((int)file.length());
	    Scanner scanner = new Scanner(file);
	    String lineSeparator = System.getProperty("line.separator");
	    try {
	        while(scanner.hasNextLine()) {        
	            fileContents.append(scanner.nextLine() + lineSeparator);
	        }
	        return fileContents.toString();
	    } finally {
	        scanner.close();
	    }
	}
	
	/**
	 * Permet de parser un string pour le mettre en forme de hashmap de 
	 * lignes
	 * @param String str
	 * @return HashMap<Integer,Ligne>
	 */
	public static HashMap<Integer,Ligne> extract(String str){
		//Enlever les espaces,tabulations et les sauts de lignes
		str = str.replaceAll("[\r]+", "");
		int i = 0 , j = 0;
		HashMap<Integer, Ligne> resultat = new HashMap<>();
		String column = "";
		Ligne ligne = new Ligne();
		int nbrePipe = 0;
		//Parcourir caractere par caractere...
		while(i<str.length()){
			//Get the current char
			char currentChar = str.charAt(i);
			if(currentChar == ';' || currentChar == '\n'){
				if(!column.equals("")) ligne.addItem(column);
				nbrePipe = 0;column = "";
				resultat.put(j, ligne);
				ligne = new Ligne();
				j++;i++;
				continue;
			}
			if(currentChar == '|'){
				nbrePipe++;
				if(nbrePipe == 1){
					ligne.setId(column);
				}
				else{
					ligne.addItem(column);
				}
				column = "";i++;
				continue;
			}
			column += Character.toString(currentChar);
			i++;
		}
		return resultat;
	}
	
	
	/**
	 * Ecirie sur le fichier
	 * 
	 * @param String content
	 */
	public static void writeFile(String content){
		BufferedWriter writer = null;
		try {
		    writer = new BufferedWriter( new FileWriter( "result.txt" ));
		    writer.write(content);
		}
		catch ( IOException e){}
		finally{
		    try{
		        if ( writer != null) writer.close( );
		    }
		    catch ( IOException e){}
		}
	}
}
