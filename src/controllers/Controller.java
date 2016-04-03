package controllers;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import models.Close;
import models.Ligne;
import models.Outils;

public class Controller implements Initializable {
	//Fichier txt
	private File fichierChoisi = null;
	
	@FXML
    private Button getfile;
	
	@FXML
	private Label labelfile;
	
	@FXML
    private Slider supportSlider;
	
	@FXML
    private Label valueSupportLabel;
	
	@FXML
	private WebView webView;
	
	
	public File getFichierChoisi() {
	     return this.fichierChoisi;
	}
	
	/**
	 * OnClick Button Choisir Fichier
	 */
	@FXML
    protected void openFile() {
		
		//
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
		fileChooser.getExtensionFilters().add(extFilter);
		fileChooser.setTitle("Choisir le fichier :");
		
		//Le fichier txt choisi
		this.fichierChoisi = fileChooser.showOpenDialog(null);
		
		if(this.fichierChoisi != null){
			//Modifier le Label
			String filePath = this.fichierChoisi.getPath();
			filePath = (filePath.length()>40)?("..." + filePath.substring(filePath.length()-37)):filePath;
			this.labelfile.setText(filePath);
		}
		
    }
	
	@FXML
	public void applyClose() throws IOException, InterruptedException{
		if(this.fichierChoisi == null){
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Attention");
			alert.setHeaderText(null);
			alert.setContentText("Vous devez d'abord choisir un fichier texte");
			alert.showAndWait();
			return;
		}
		
		String StringFile = Outils.read(fichierChoisi.getAbsolutePath());
		HashMap<Integer, Ligne> lines = Outils.extract(StringFile);
		Close c = new Close(lines, supportSlider.getValue());
		c.applyClose();
		WebEngine webEngine = webView.getEngine();
		webEngine.loadContent(c.getHead() + c.displayReglesExactes() + c.displayReglesApproximatives());
		Outils.writeFile(c.WriteRegles());
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		//Listner de Slider maj le label
		valueSupportLabel.textProperty().bind(
	            javafx.beans.binding.Bindings.format(
	                "%.2f",
	                supportSlider.valueProperty()
	            )
	        );
	}
	

}
