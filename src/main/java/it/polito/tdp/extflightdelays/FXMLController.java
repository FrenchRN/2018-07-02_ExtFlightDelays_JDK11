package it.polito.tdp.extflightdelays;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.extflightdelays.model.Airport;
import it.polito.tdp.extflightdelays.model.Model;
import it.polito.tdp.extflightdelays.model.Vicino;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

//controller turno A --> switchare ai branch master_turnoB o master_turnoC per turno B o C

public class FXMLController {
	
	private Model model;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextArea txtResult;

    @FXML
    private TextField distanzaMinima;

    @FXML
    private Button btnAnalizza;

    @FXML
    private ComboBox<Airport> cmbBoxAeroportoPartenza;

    @FXML
    private Button btnAeroportiConnessi;

    @FXML
    private TextField numeroVoliTxtInput;

    @FXML
    private Button btnCercaItinerario;

    @FXML
    void doAnalizzaAeroporti(ActionEvent event) {
    	txtResult.clear();
    	String d = distanzaMinima.getText();
    	if (d == null) {
    		txtResult.appendText("Inserire una distanza minima \n");
    		return ;
    	}
    	Double dist = null;
    	try {
    		dist = Double.parseDouble(d);
    	}catch(NumberFormatException n) {
    		txtResult.appendText("Inserire un numero di tipo double");
    		return ;
    	}
    	this.model.creaGrafo(dist);
    	txtResult.appendText("Grafo creato, n. vertici: "+this.model.getNVertex()+" e n. archi: "+this.model.getNArchi()+"\n");
    	this.cmbBoxAeroportoPartenza.getItems().setAll(this.model.getAirports());
    }

    @FXML
    void doCalcolaAeroportiConnessi(ActionEvent event) {
    	txtResult.clear();
    	Airport air = this.cmbBoxAeroportoPartenza.getValue();
    	if (air == null) {
    		txtResult.appendText("Scegliere un aereoporto di partenza, verificando di aver creato il grafo \n");
    		return ;
    	}
    	List<Vicino> list = new ArrayList<>(this.model.getVicini(air));
    	if(list == null) {
    		txtResult.appendText("Prima creare il grafo, poi chiedere i vicini");
    		return ;
    	}
    	for(Vicino v : list) {
    		txtResult.appendText(air + " --> " + v + " distanza: " + v.getDistanza() +"\n");
    	}
    }

    @FXML
    void doCercaItinerario(ActionEvent event) {
    	txtResult.clear();
    	String d = distanzaMinima.getText();
    	if(d == null) {
    		txtResult.appendText("Inserire la distanza massima percorribile");
    		return ;
    	}
    	Double dist = null;
    	try {
    		dist = Double.parseDouble(d);
    	}catch(NumberFormatException n) {
    		txtResult.appendText("Formato non corretto");
    		return;
    	}
    	String sol = this.model.creaPercorso(dist, this.cmbBoxAeroportoPartenza.getValue());
    	if (sol == null) {
    		txtResult.appendText("Costruire prima il grafo \n");
    		return ;
    	}
    	txtResult.appendText(sol);
    }

    @FXML
    void initialize() {
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'ExtFlightDelays.fxml'.";
        assert distanzaMinima != null : "fx:id=\"distanzaMinima\" was not injected: check your FXML file 'ExtFlightDelays.fxml'.";
        assert btnAnalizza != null : "fx:id=\"btnAnalizza\" was not injected: check your FXML file 'ExtFlightDelays.fxml'.";
        assert cmbBoxAeroportoPartenza != null : "fx:id=\"cmbBoxAeroportoPartenza\" was not injected: check your FXML file 'ExtFlightDelays.fxml'.";
        assert btnAeroportiConnessi != null : "fx:id=\"btnAeroportiConnessi\" was not injected: check your FXML file 'ExtFlightDelays.fxml'.";
        assert numeroVoliTxtInput != null : "fx:id=\"numeroVoliTxtInput\" was not injected: check your FXML file 'ExtFlightDelays.fxml'.";
        assert btnCercaItinerario != null : "fx:id=\"btnCercaItinerario\" was not injected: check your FXML file 'ExtFlightDelays.fxml'.";

    }

	public void setModel(Model model) {
		this.model = model;
	}
}
