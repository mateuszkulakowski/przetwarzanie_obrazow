package rozciagniecieHistogramu;

import java.util.Optional;

import data.ItemKeeper;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class RozciagniecieHistogramu {

	
	public static void rozciagnijHistogram()
	{
		Dialog<Pair<String,String>> dialog = new Dialog<>();
		dialog.setTitle("Wybór wartoœci min/max");
		dialog.setHeaderText("Dla podanych wartoœci jasnoœci a i b, ucinane s¹ wartoœci poni¿ej a i powy¿ej b,\n a pozosta³a czêœæ rozci¹gana do pe³nego zakresu jasnoœci");
		
		ButtonType ButtonOK = new ButtonType("Ok", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonOK, ButtonType.CANCEL);
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField wspolczynnik_a = new TextField();
		wspolczynnik_a.setPromptText("Wartoœæ a:");
		TextField wspoczynnik_b = new TextField();
		wspoczynnik_b.setPromptText("Wartoœæ b:");

		grid.add(new Label("A:"), 0, 0);
		grid.add(wspolczynnik_a, 1, 0);
		grid.add(new Label("B:"), 0, 1);
		grid.add(wspoczynnik_b, 1, 1);
		
		dialog.getDialogPane().setContent(grid);
		
		dialog.setResultConverter(dialogButton -> {
		    if (dialogButton == ButtonOK) {
		        return new Pair<>(wspolczynnik_a.getText(), wspoczynnik_b.getText());
		    }
		    return null;
		});
		
		Optional<Pair<String, String>> result = dialog.showAndWait();

		//KLIKNIÊTO OK
		result.ifPresent(wpisane_wartosci -> {
			
			try
			{
				double a = Integer.parseInt(wpisane_wartosci.getKey());
				double b = Integer.parseInt(wpisane_wartosci.getValue());
				
				if(a>b) // LICZBA A JEST ZAWSZE LICZB¹ MNIEJSZ¹
				{
					double help = a;
					a = b;
					b = help;
				}
				
				double[] tablica_lut = new double[256];

		        for (int i = 0; i < 256; i++) {
		        	// wzór http://www.algorytm.org/przetwarzanie-obrazow/histogram-rozciaganie.html
		        	tablica_lut[i] = (255/(b-a))*(i-a);
		            if (tablica_lut[i] < 0) {
		            	tablica_lut[i] = 0;
		            }
		            if (tablica_lut[i] > 255) {
		            	tablica_lut[i] = 255;
		            }

		        }
		        
		        //for(int i=0;i<256;i++)System.out.println(i+ ":" +(int)tablica_lut[i]);
		        
				ImageView iWorkingImage = ItemKeeper.getStackPaneWindowController().iWorkingImage;
		        WritableImage wimage = new WritableImage((int) iWorkingImage.getImage().getWidth(), (int) iWorkingImage.getImage().getHeight());
		        PixelWriter pw = wimage.getPixelWriter();
		        PixelReader pixelReader = iWorkingImage.getImage().getPixelReader();
		        
		        for (int i = 0; i < iWorkingImage.getImage().getWidth(); i++) {
		            for (int j = 0; j < iWorkingImage.getImage().getHeight(); j++) {
		                int argb = pixelReader.getArgb(i, j);
		                int x = (argb >> 24) & 0xFF;
		                int red = (argb >> 16) & 0xFF;
		                int green = (argb >> 8) & 0xFF;
		                int blue = argb & 0xFF;
		                
		                argb = (x << 24) + ((int) tablica_lut[red] << 16) + ((int) tablica_lut[green] << 8) + (int) tablica_lut[blue];
		                pw.setArgb(i, j, argb);

		            }
		        }
		        
		        iWorkingImage.setImage(wimage);
		        
				
			
			}catch(Exception e){};
		});
	}
	
}
