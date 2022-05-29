/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.PremierLeague;

import java.net.URL;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.PremierLeague.model.CoppiaMatch;
import it.polito.tdp.PremierLeague.model.Match;
import it.polito.tdp.PremierLeague.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {

	Model model;
	
	boolean grafoCreato = false;
	
    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnConnessioneMassima"
    private Button btnConnessioneMassima; // Value injected by FXMLLoader

    @FXML // fx:id="btnCollegamento"
    private Button btnCollegamento; // Value injected by FXMLLoader

    @FXML // fx:id="txtMinuti"
    private TextField txtMinuti; // Value injected by FXMLLoader

    @FXML // fx:id="cmbMese"
    private ComboBox<Month> cmbMese; // Value injected by FXMLLoader

    @FXML // fx:id="cmbM1"
    private ComboBox<Match> cmbM1; // Value injected by FXMLLoader

    @FXML // fx:id="cmbM2"
    private ComboBox<Match> cmbM2; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doConnessioneMassima(ActionEvent event) {
    	txtResult.clear();
    	if(grafoCreato == true) {
	    	List<CoppiaMatch> coppie = model.getConnessioneMax();
	    	for(CoppiaMatch coppia: coppie)
	    		txtResult.appendText(coppia.toString() + "\n");
    	} else	// grafo non creato
    		txtResult.setText("Grafo inesistente! Cliccare prima su 'Crea grafo'");
    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	txtResult.clear();
    	this.cmbM1.getItems().clear();
    	this.cmbM2.getItems().clear();
    	
    	int minutiMinimi;
    	try {
    		minutiMinimi = Integer.parseInt(this.txtMinuti.getText());
    		if(minutiMinimi > 90)
    			txtResult.setText("Il numero di minuti 'MIN' deve essere minore o uguale di 90");
    		else {
    			if(this.cmbMese.getValue() != null) {
    				model.creaGrafo(this.cmbMese.getValue(),minutiMinimi);
    				
    				int numVertici = model.numVertici();
    				
    				if(numVertici == 0)
    					txtResult.setText("Non e' stato giocato alcun match nel mese " + this.cmbMese.getValue());
    				else {
    					txtResult.appendText("Creato grafo!\n");
	    				txtResult.appendText("# VERTICI: " + numVertici + "\n");
	    				txtResult.appendText("# ARCHI: " + model.numArchi() + "\n");
	    				this.cmbM1.getItems().addAll(model.getMatchCombo());
	    				this.cmbM2.getItems().addAll(model.getMatchCombo());
	    				this.grafoCreato = true;
    				}
    			}
    			else
    				txtResult.setText("E' necessario scegliere un mese dal menu' a tendina 'MESE'");
    		}
    	} catch(NumberFormatException nfe) {
    		txtResult.setText("Devi inserire un valore numerico, non caratteri o stringhe");
    	}
    }

    @FXML
    void doCollegamento(ActionEvent event) {
    	txtResult.clear();
    	if(this.cmbM1.getValue() != null && this.cmbM2.getValue() != null) {
    		if(!this.cmbM1.getValue().equals(this.cmbM2.getValue())) {
    		List<Match> percorso = model.calcolaPercorso(cmbM1.getValue(), cmbM2.getValue());
    		for(Match match: percorso)
    			txtResult.appendText(match.toString() + "\n");
    		} else
    			txtResult.setText("m1 e m2 devono essere diversi!");
    	} else
    		txtResult.appendText("Devi selezionare un match di inizio (m1) e uno di fine (m2)");
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnConnessioneMassima != null : "fx:id=\"btnConnessioneMassima\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnCollegamento != null : "fx:id=\"btnCollegamento\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtMinuti != null : "fx:id=\"txtMinuti\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbMese != null : "fx:id=\"cmbMese\" was not injected: check your FXML file 'Scene.fxml'.";        assert cmbM1 != null : "fx:id=\"cmbM1\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbM2 != null : "fx:id=\"cmbM2\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    	
    	for(int indice = 1; indice <= 12; indice++)
    		this.cmbMese.getItems().add(Month.of(indice));
    }
    
    
}
