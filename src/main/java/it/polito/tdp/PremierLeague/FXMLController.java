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
    	/* Se il grafo per quella coppia di parametri scelti dall'utente non e' ancora stato creato,
    	 	catturo l'eccezione e avviso l'utente. Cattura eccezione RuntimeException */
    	try {
	    	List<CoppiaMatch> coppie = model.getConnessioneMax();
	    	txtResult.appendText("COPPIA/E DI MATCH in cui hanno giocato per almeno MIN minuti\n"
	    			+ " in entrambe le partite il maggior numero di giocatori: ");
	    	txtResult.appendText("" + coppie.size() + "\n\n");
	 
	    	for(CoppiaMatch coppia: coppie)
	    		txtResult.appendText(coppia.toString() + "\n");
    	} catch(RuntimeException rte) {	// grafo non creato
    		txtResult.setText("Grafo inesistente per la combinazione di parametri 'MIN' e 'MESE'! "
    				+ "\nCliccare prima su 'Crea grafo'");
    	}
    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	txtResult.clear();
    	this.cmbM1.getItems().clear();
    	this.cmbM2.getItems().clear();
    	
    	// 1) Controllo che il campo MIN sia stato compilato
		if(this.txtMinuti.getText().equals("")) {
			txtResult.setText("Devi inserire un valore nel campo 'MIN'\nNon lasciare vuoto!");
			return;
		}
		
		// 2) Controllo che il campo MIN sia stato correttamente compilato
    	int minutiMinimi;
    	try {
    		minutiMinimi = Integer.parseInt(this.txtMinuti.getText());
    		if(minutiMinimi > 90)
    			txtResult.setText("Il numero di minuti 'MIN' deve essere minore o uguale di 90");
    		else {
    			// 3) Controllo che sia stato selezionato un MESE dal menu' a tendina
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
    	// 1) Controllo se le scelte dei match m1 e m2 fatte dall'utente sono corrette
    	if(this.cmbM1.getValue() != null && this.cmbM2.getValue() != null) {
    		if(!this.cmbM1.getValue().equals(this.cmbM2.getValue())) {
    			// 2) Controllo che il grafo con quei parametri sia stato creato
    			try {
    				List<Match> percorso = model.calcolaPercorso(cmbM1.getValue(), cmbM2.getValue());
    				if(percorso != null) {	// la destinazione e' raggiungibile
    					txtResult.appendText("Percorso di peso massimo: \n");
    					for(Match match: percorso)
    						txtResult.appendText(match.toString() + "\n");
    				} else
    					txtResult.setText(this.cmbM2.getValue().toString() + " non e' raggiungibile da " + 
    							this.cmbM1.getValue().toString());
    			} catch(RuntimeException rte) {	// grafo non creato
    	    		txtResult.setText("Grafo inesistente per la combinazione di parametri 'MIN' e 'MESE'! "
    	    				+ "\nCliccare prima su 'Crea grafo'");
    			}
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
