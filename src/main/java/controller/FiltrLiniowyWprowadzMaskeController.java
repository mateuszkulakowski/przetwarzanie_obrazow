package controller;

import data.ItemKeeper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FiltrLiniowyWprowadzMaskeController {

	@FXML
	Button bAnuluj;
	
	@FXML
	TextField tf1;
	
	@FXML
	TextField tf2;
	
	@FXML
	TextField tf3;
	
	@FXML
	TextField tf4;
	@FXML
	
	TextField tf5;
	@FXML
	
	TextField tf6;
	@FXML
	
	TextField tf7;
	@FXML
	
	TextField tf8;
	@FXML
	
	TextField tf9;
	
	
	@FXML
	public void initialize(){}
	
	
	@FXML
	public void onActionOk()
	{
		// Tworzenie wpisanej tablicy przez u¿ytkownika
		int tab[][] = new int[3][3];
		tab[0][0] = Integer.parseInt(tf1.getText());
		tab[0][1] = Integer.parseInt(tf2.getText());
		tab[0][2] = Integer.parseInt(tf3.getText());
		tab[1][0] = Integer.parseInt(tf4.getText());
		tab[1][1] = Integer.parseInt(tf5.getText());
		tab[1][2] = Integer.parseInt(tf6.getText());
		tab[2][0] = Integer.parseInt(tf7.getText());
		tab[2][1] = Integer.parseInt(tf8.getText());
		tab[2][2] = Integer.parseInt(tf9.getText());
		
		
		//Zapisywanie w globalnej klasie
		ItemKeeper.FiltrLiniowyMaska = tab;
		
		//Zamykanie okna
		Stage stage = (Stage)bAnuluj.getScene().getWindow();
		stage.close();
	}
	
	@FXML
	public void onActionAnuluj()
	{
		ItemKeeper.FiltrLiniowyMaska = null;
		Stage stage = (Stage)bAnuluj.getScene().getWindow();
		stage.close();
	}
	
	
}
