package wyrownanieHistogramu;

import data.ItemKeeper;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import rozciagniecieHistogramu.RozciagniecieHistogramu;

public class WyrownanieHistogramu {

	
	
	public static void wyrownajHistogram()
	{
		int tablica_dystrybuanty_red[] = new int[256];
		int tablica_dystrybuanty_green[] = new int[256];
		int tablica_dystrybuanty_blue[] = new int[256];
		
		long tablica_lut_red[] = new long[256];
		long tablica_lut_green[] = new long[256];
		long tablica_lut_blue[] = new long[256];
		
		//HISTOGRAM
		int ilosc_pixeli_red[] = new int[256];
		int ilosc_pixeli_green[] = new int[256];
		int ilosc_pixeli_blue[] = new int[256];
		
		ImageView iWorkingImage = ItemKeeper.getStackPaneWindowController().iWorkingImage;
		PixelReader pixelReader = iWorkingImage.getImage().getPixelReader();
		
		int rozmiar_obrazu = (int) (iWorkingImage.getImage().getWidth() * iWorkingImage.getImage().getHeight());
		
		//INICJALIZACJA NA 0
		for (int i = 0; i < 256; i++) {
			ilosc_pixeli_red[i] = 0;
			ilosc_pixeli_green[i] = 0;
			ilosc_pixeli_blue[i] = 0;
			
			tablica_dystrybuanty_red[i] = 0;
			tablica_dystrybuanty_green[i] = 0;
			tablica_dystrybuanty_blue[i] = 0;
        }
		
		int value_red = 0;
		int value_green = 0;
		int value_blue = 0;
		
		for(int i=0; i < iWorkingImage.getImage().getWidth(); i++)
		{
			for(int j=0; j < iWorkingImage.getImage().getHeight(); j++)
			{
				//POBIERANIE KOLORU I PRZEKONWERTOWANIE NA WARTOŒÆ 0-255
				Color kolor = pixelReader.getColor(i, j);
				value_red = (int) (kolor.getRed() * 255);
				value_green = (int) (kolor.getGreen() * 255);
				value_blue = (int) (kolor.getBlue() * 255);
				
				//UZUPE£NIANIE ILOSCI DANEGO PIXELA
				ilosc_pixeli_red[value_red]++;
				ilosc_pixeli_green[value_green]++;
				ilosc_pixeli_blue[value_blue]++;
			}
		}
		
		tablica_dystrybuanty_red[0] = ilosc_pixeli_red[0];
		tablica_dystrybuanty_green[0] = ilosc_pixeli_green[0];
		tablica_dystrybuanty_blue[0] = ilosc_pixeli_blue[0];
		
		
		//UZUPE£NIENIE DYSTRYBUANTY
		for(int i=1; i < 256; i++)
		{
			tablica_dystrybuanty_red[i] = ilosc_pixeli_red[i] + tablica_dystrybuanty_red[i-1];
			tablica_dystrybuanty_green[i] = ilosc_pixeli_green[i] + tablica_dystrybuanty_green[i-1];
			tablica_dystrybuanty_blue[i] = ilosc_pixeli_blue[i] + tablica_dystrybuanty_blue[i-1];
		}
		
		//WYKONANIE TABLICY LUT
		for(int i=0; i<256; i++)
		{
			// wykorzystane wzory http://www.algorytm.org/przetwarzanie-obrazow/histogram-wyrownywanie.html
			tablica_lut_red[i] = (int) (255*(tablica_dystrybuanty_red[i] - tablica_dystrybuanty_red[0]))/rozmiar_obrazu;
			tablica_lut_green[i] = (int) (255*(tablica_dystrybuanty_green[i] - tablica_dystrybuanty_green[0]))/rozmiar_obrazu;
			tablica_lut_blue[i] = (int) (255*(tablica_dystrybuanty_blue[i] - tablica_dystrybuanty_blue[0]))/rozmiar_obrazu;

		}
		
		WritableImage wimage = new WritableImage((int) iWorkingImage.getImage().getWidth(), (int) iWorkingImage.getImage().getHeight());
        PixelWriter pw = wimage.getPixelWriter();

        for (int i = 0; i < iWorkingImage.getImage().getWidth(); i++) {
            for (int j = 0; j < iWorkingImage.getImage().getHeight(); j++) {
                int argb = pixelReader.getArgb(i, j);
                int a = (argb >> 24) & 0xFF;
                value_red = (argb >> 16) & 0xFF;
                value_green = (argb >> 8) & 0xFF;
                value_blue = argb & 0xFF;
                argb = (a << 24) + ((int) tablica_lut_red[value_red] << 16) + ((int) tablica_lut_green[value_green] << 8) + (int) tablica_lut_blue[value_blue];
                pw.setArgb(i, j, argb);

            }
        }
		
        iWorkingImage.setImage(wimage);
		
		
		
	}
	
	
}
