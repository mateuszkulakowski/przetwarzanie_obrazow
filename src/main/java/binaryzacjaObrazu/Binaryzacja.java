package binaryzacjaObrazu;

import java.util.Optional;

import data.ClassKeeper;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

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
                int red = (argb >> 16) & 0xFF;
                hist[red]++;
            }
        }
        float sum = 0;
        for (int t = 0; t < 256; t++) {
            sum += t * hist[t];
        }
        float sumB = 0;
        int wB = 0;
        int wF = 0;
        float varMax = 0;
        threshold = 0;
        for (int t = 0; t < 256; t++) {
            wB += hist[t];               // Weight Background
            if (wB == 0) {
                continue;
            }

            wF = total - wB;                 // Weight Foreground
            if (wF == 0) {
                break;
            }

            sumB += (float) (t * hist[t]);

            float mB = sumB / wB;            // Mean Background
            float mF = (sum - sumB) / wF;    // Mean Foreground

            // Calculate Between Class Variance
            float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

            // nowe maximum znalezione
            if (varBetween > varMax) {
                varMax = varBetween;
                threshold = t;
            }
        }
        WritableImage wimage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        PixelWriter pw = wimage.getPixelWriter();
        int newPixelR;
        int newPixelG;
        int newPixelB;
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                int argb = pr.getArgb(i, j);
                int a = (argb >> 24) & 0xFF;
                int red = (argb >> 16) & 0xFF;
                int green = (argb >> 8) & 0xFF;
                int blue = argb & 0xFF;
                if (red > threshold) {
                    newPixelR = 255;
                } else {
                    newPixelR = 0;
                }
                if (green > threshold) {
                    newPixelG = 255;
                } else {
                    newPixelG = 0;
                }
                if (blue > threshold) {
                    newPixelB = 255;
                } else {
                    newPixelB = 0;
                }
                argb = (a << 24) + ((int) newPixelR << 16) + ((int) newPixelG << 8) + (int) newPixelB;
                pw.setArgb(i, j, argb);
            }

        }
        
        ClassKeeper.spwk.iWorkingImage.setImage(wimage);
	}
}
