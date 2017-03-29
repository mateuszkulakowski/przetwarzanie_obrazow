package binaryzacjaObrazu;

import java.util.Optional;

import data.ClassKeeper;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class Binaryzacja {

	
	public static Image czarnobialy(Image image)
	{
        WritableImage wimage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        PixelWriter pw = wimage.getPixelWriter();
        PixelReader pixelReader = image.getPixelReader();
        
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                int argb = pixelReader.getArgb(i, j);
                int x = (argb >> 24) & 0xFF;
                int red = (argb >> 16) & 0xFF;
                int green = (argb >> 8) & 0xFF;
                int blue = argb & 0xFF;
                
                int srednia = (red+green+blue)/3;
                
                argb = (x << 24) + ((int) srednia << 16) + ((int) srednia << 8) + (int) srednia;
                pw.setArgb(i, j, argb);

            }
        }
        
        return wimage;
	}
	
	
	
	public static void reczna()
	{
		TextInputDialog dialog = new TextInputDialog();
    	dialog.setTitle("Wybór progu binaryzacji");
    	dialog.setHeaderText("Wybierz wartoœæ progu");
    	dialog.setContentText("Wartoœæ:");
    	Optional<String> wpisana_wartosc = dialog.showAndWait();
    	
    	if(wpisana_wartosc.isPresent())
    	{
    		String wartosc_progu = wpisana_wartosc.toString().substring(9, wpisana_wartosc.toString().length()-1);
    		
    		
    		//Zamiana na czarno bia³y Ÿród³o: http://www.algorytm.org/przetwarzanie-obrazow/skala-szarosci.html
			ImageView iWorkingImage = ClassKeeper.getStackPaneWindowController().iWorkingImage;
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
	                
	                int srednia = (red+green+blue)/3; // DANE PIXEL I JEGO WARTOŒÆ R G B musi siê równaæ œredniej ¿eby by³ czarno-bia³y
	                
	                // Ÿród³o wzór na binaryzacjê rêczn¹: http://www.mif.pg.gda.pl/homepages/marcin/Wyklad3.pdf
	                int wartosc_binarna;
	                if(srednia < Integer.parseInt(wartosc_progu))wartosc_binarna = 0; // PIXELE OBIEKTU - CZARNE
	                else wartosc_binarna = 255;// PIXELE OBIEKTU - BIA£E
	                
	                argb = (x << 24) + ((int) wartosc_binarna << 16) + ((int) wartosc_binarna << 8) + (int) wartosc_binarna;
	                pw.setArgb(i, j, argb);

	            }
	        }
	        
	        iWorkingImage.setImage(wimage);
    	}
	}
	
	
	
	
	public static void otsu()
	{
		Image image = czarnobialy(ClassKeeper.spwk.iWorkingImage.getImage());
		
		
		int threshold;
        int hist[] = new int[256];
        int total = (int) image.getWidth() * (int) image.getHeight();
        PixelReader pr = image.getPixelReader();
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                int argb = pr.getArgb(i, j);
                int grey = (argb >> 16) & 0xFF;
                hist[grey]++;
            }
        }
        float UT = 0;
        for (int t = 0; t < 256; t++) {
            UT += t * hist[t];
        }
        float sumB = 0;
        int P1 = 0;
        int P0 = 0;
        float varMax = 0;
        threshold = 0;
        for (int t = 0; t < 256; t++) {
            P1 += hist[t];               // prawdopodobieñstwo t³a
            if (P1 == 0) {
                continue;
            }

            P0 = total - P1;                 // prawdopodobieñstwo obiektu
            if (P0 == 0) {
                break;
            }

            sumB += (float) (t * hist[t]);

            float U1 = sumB / P1;            // œrednia jasnoœæ klasy t³a
            float U0 = (UT - sumB) / P0;    // œrednia jasnoœæ klasy obiektu

            // wariancja miêdzyklasowa
            float varBetween = (float) P1 * (float) P0 * (U0 - U1) * (U0 - U1);

            // nowe maximum znalezione
            if (varBetween > varMax) {
                varMax = varBetween;
                threshold = t;
            }
        }
        
        WritableImage wimage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        PixelWriter pw = wimage.getPixelWriter();
        int newPixel;
        
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                int argb = pr.getArgb(i, j);
                int a = (argb >> 24) & 0xFF;
                int red = (argb >> 16) & 0xFF;
                
                //Wszystkie kolory tak¹ sam¹ wartoœæ maj¹ bo obraz jest szary
                if (red > threshold) {
                    newPixel = 255;
                } else {
                    newPixel = 0;
                }
                
                argb = (a << 24) + ((int) newPixel << 16) + ((int) newPixel << 8) + (int) newPixel;
                pw.setArgb(i, j, argb);
            }

        }
        
        ClassKeeper.spwk.iWorkingImage.setImage(wimage);
	}
	
	private static int oblicz_srednia_pixeli_okna(Image image, int start_x, int koniec_x, int start_y, int koniec_y)
	{
		int suma = 0;
		int liczba_pixeli = 0;
		
        WritableImage wimage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        PixelReader pr = image.getPixelReader();
		
		for(int i=start_x; i<=koniec_x; i++)
		{
			for(int j=start_y; j<=koniec_y; j++)
			{
				int argb = pr.getArgb(i, j);
                int a = (argb >> 24) & 0xFF;
                int grey = (argb >> 16) & 0xFF;
                
                suma+= grey;
                liczba_pixeli++;
			}
		}

		suma = suma/liczba_pixeli;
		
		return suma;
	}
	
	private static double oblicz_odchylenie_pixeli_okna(Image image,int srednia, int start_x, int koniec_x, int start_y, int koniec_y)
	{
		double suma = 0;
		double odchylenie = 0;
		double liczba_pixeli = 0;
		
        WritableImage wimage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        PixelReader pr = image.getPixelReader();
		
		for(int i=start_x; i<=koniec_x; i++)
		{
			for(int j=start_y; j<=koniec_y; j++)
			{
				int argb = pr.getArgb(i, j);
                int grey = (argb >> 16) & 0xFF;
                
                suma+= Math.pow((grey-srednia),2);
                liczba_pixeli++;
			}
		}

		odchylenie = Math.sqrt(suma/(liczba_pixeli));
		
		return odchylenie;
	}
	
	
	
	public static void niblack()
	{
		Dialog<Pair<String,String>> dialog = new Dialog<>();
		dialog.setTitle("Wybór wartoœci rozmiar okna i wartoœci k");
		dialog.setHeaderText("Rozmiar okna otoczanego pixela i wartoœæ progowania k (wartoœæ z przedzia³u 0-1)");
		
		ButtonType ButtonOK = new ButtonType("Ok", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonOK, ButtonType.CANCEL);
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField wspolczynnik_a = new TextField();
		wspolczynnik_a.setPromptText("wartoœæ int");
		TextField wspoczynnik_b = new TextField();
		wspoczynnik_b.setPromptText("wartoœæ int");

		grid.add(new Label("Rozmiar okna:"), 0, 0);
		grid.add(wspolczynnik_a, 1, 0);
		grid.add(new Label("Wartoœæ k:"), 0, 1);
		grid.add(wspoczynnik_b, 1, 1);
		
		dialog.getDialogPane().setContent(grid);
		
		dialog.setResultConverter(dialogButton -> {
		    if (dialogButton == ButtonOK) {
		        return new Pair<>(wspolczynnik_a.getText(), wspoczynnik_b.getText());
		    }
		    return null;
		});
		
		Optional<Pair<String, String>> result = dialog.showAndWait();
		
		
		result.ifPresent(wpisane_wartosci -> {
			
			int rozmiar_okna = Integer.parseInt(wpisane_wartosci.getKey());
			double k = Double.parseDouble(wpisane_wartosci.getValue());
			int promien = rozmiar_okna/2;
			
			Image image = czarnobialy(ClassKeeper.getStackPaneWindowController().iWorkingImage.getImage());
			
			WritableImage wimage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
	        PixelWriter pw = wimage.getPixelWriter();
	        PixelReader pr = image.getPixelReader();
	        
	        for (int i = 0; i < image.getHeight(); i++) {
	            for (int j = 0; j < image.getWidth(); j++) {
	            	
	            	int okno_start_x = 0;
	            	int okno_koniec_x = 0;
	            	int okno_start_y = 0;
	            	int okno_koniec_y = 0;
	            	
	            	if(i-promien<0)okno_start_x=0;
	            	else okno_start_x=i-promien;
	            	
	            	if(i+promien>=image.getHeight())okno_koniec_x = (int)image.getHeight()-1;
	            	else okno_koniec_x=i+promien;
	            	
	            	if(j-promien<0)okno_start_y=0;
	            	else okno_start_y=j-promien;
	            	
	            	if(j+promien>=image.getWidth())okno_koniec_y=(int)image.getWidth()-1;
	            	else okno_koniec_y=j+promien;

	            	int srednia_pixeli_okna = oblicz_srednia_pixeli_okna(image,okno_start_y,okno_koniec_y,okno_start_x,okno_koniec_x);
	            	double odchylenie_pixeli_okna = oblicz_odchylenie_pixeli_okna(image,srednia_pixeli_okna,okno_start_y,okno_koniec_y,okno_start_x,okno_koniec_x);
	            	//System.out.println(srednia_pixeli_okna+" "+odchylenie_pixeli_okna+" "+okno_start_x+" "+okno_koniec_x+" "+okno_start_y+" "+okno_koniec_y);
	            
	            	double prog = (srednia_pixeli_okna+k*odchylenie_pixeli_okna);
	            	
	            	int argb = pr.getArgb(j, i);
	                int a = (argb >> 24) & 0xFF;
	                int grey = (argb >> 16) & 0xFF;
	            	int newPixel;
	            	
	            	if(grey < prog)newPixel=0;
	            	else newPixel=255;
	                
	                argb = (a << 24) + ((int) newPixel << 16) + ((int) newPixel << 8) + (int) newPixel;
	                pw.setArgb(j, i, argb);
	            }
	        }
	        
	        ClassKeeper.spwk.iWorkingImage.setImage(wimage);
		});
			
        
		
	}
	
}
