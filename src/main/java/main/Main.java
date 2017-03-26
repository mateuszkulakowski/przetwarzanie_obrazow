package main;



import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

	
	public static void main(String[] args) {
		launch(args);

	}
	

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		FXMLLoader fxlm = new FXMLLoader();
		fxlm.setLocation(this.getClass().getResource("/fxml/StackPaneWindow.fxml"));
		
		VBox stack = fxlm.load();
		Scene scene = new Scene(stack);
		
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
		
	}

}
