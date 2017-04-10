package filtry;

import java.util.Iterator;

import data.ItemKeeper;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class Filtry {
	
	
	public static void FiltrLiniowy()
	{
		Image image = ItemKeeper.spwk.iWorkingImage.getImage();
		WritableImage wimage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        PixelWriter pw = wimage.getPixelWriter();
        PixelReader pixelReader = image.getPixelReader();
        
        for (int i = 1; i < image.getWidth()-1; i++) {
            for (int j = 1; j < image.getHeight()-1; j++) {
            	
            	int pozycjaxMaska = 0;
            	int pozycjayMaska = 0;
            	
            	int sumaWartosciMaska = 0;
            	
            	int wynikR = 0;
            	int wynikG = 0;
            	int wynikB = 0;
            	
            	int argb_poczatkowego = pixelReader.getArgb(i,j);
            	int x_poczatkowego = (argb_poczatkowego >> 24) & 0xFF;
            	
            	//Przeszukujemy s¹siadów aktualnego pixela maska 3x3
                for(int k=j-1;k<=j+1;k++)
                {
                	for(int l=i-1;l<=i+1;l++)
                	{
                		int argb = pixelReader.getArgb(l,k);
                		
                        int red = (argb >> 16) & 0xFF;
                        int green = (argb >> 8) & 0xFF;
                        int blue = argb & 0xFF;
                        
                        //sumujemy wyniki mno¿enia s¹siad pixela razy odpowiednia wartoœæ z maski
                        wynikR += red*ItemKeeper.FiltrLiniowyMaska[pozycjaxMaska][pozycjayMaska];
                        wynikG += green*ItemKeeper.FiltrLiniowyMaska[pozycjaxMaska][pozycjayMaska];
                        wynikB += blue*ItemKeeper.FiltrLiniowyMaska[pozycjaxMaska][pozycjayMaska];
                        
                        sumaWartosciMaska += ItemKeeper.FiltrLiniowyMaska[pozycjaxMaska][pozycjayMaska];
                        
                        pozycjayMaska++;
                	}
                	pozycjayMaska = 0;
                	pozycjaxMaska++;
                }
                
                if(sumaWartosciMaska != 0)
                {
                	wynikR /= sumaWartosciMaska;
	                wynikG /= sumaWartosciMaska;
	                wynikB /= sumaWartosciMaska;
                }
                
                if(wynikR < 0)wynikR = 0;
                if(wynikG < 0)wynikG = 0;
                if(wynikB < 0)wynikB = 0;
                if(wynikR > 255)wynikR = 255;
                if(wynikG > 255)wynikG = 255;
                if(wynikB > 255)wynikB = 255;
                
                
                int argb = (x_poczatkowego << 24) + ((int) wynikR << 16) + ((int) wynikG << 8) + (int) wynikB;
                pw.setArgb(i, j, argb);

            }
        }
		
        ItemKeeper.spwk.iWorkingImage.setImage(wimage);
	}
	
	public static void FiltrKuwahara()
	{
		
		Image image = ItemKeeper.spwk.iWorkingImage.getImage();
		WritableImage wimage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        PixelWriter pw = wimage.getPixelWriter();
        PixelReader pixelReader = image.getPixelReader();
        
        for (int i = 2; i < image.getWidth()-2; i++) {
            for (int j = 2; j < image.getHeight()-2; j++) {
            	
            	//ij wspó³rzedne œrodka akt. kwadratu 5x5
            	int maxsredniaR = 0;
            	int maxsredniaG = 0;
            	int maxsredniaB = 0;
            	
            	int minwariancjaR = 999999;
            	int minwariancjaG = 999999;
            	int minwariancjaB = 999999;
            	
            	//wyznaczanie pkt startowych podkwadratów 3x3
            	int pkt_startowy_1_podkwadrat_x = i-2;
            	int pkt_startowy_1_podkwadrat_y = j-2;
            	
            	int pkt_startowy_2_podkwadrat_x = i;
            	int pkt_startowy_2_podkwadrat_y = j-2;
            	
            	int pkt_startowy_3_podkwadrat_x = i-2;
            	int pkt_startowy_3_podkwadrat_y = j;
            	
            	int pkt_startowy_4_podkwadrat_x = i;
            	int pkt_startowy_4_podkwadrat_y = j;
            	
            	for(int k=0; k<4; k++)
            	{
            		int x,y;
            		
            		int sredniaR = 0;
            		int sredniaG = 0;
            		int sredniaB = 0;
            		
            		int wariancjaR = 0;
            		int wariancjaG = 0;
            		int wariancjaB = 0;
            		
            		if(k==0)
            		{
            			x=pkt_startowy_1_podkwadrat_x;
            			y=pkt_startowy_1_podkwadrat_y;
            		}
            		else if(k==1)
            		{
            			x=pkt_startowy_2_podkwadrat_x;
            			y=pkt_startowy_2_podkwadrat_y;
            		}
            		else if(k==2)
            		{
            			x=pkt_startowy_3_podkwadrat_x;
            			y=pkt_startowy_3_podkwadrat_y;
            		}else
            		{
            			x=pkt_startowy_4_podkwadrat_x;
            			y=pkt_startowy_4_podkwadrat_y;
            		}
            		
            		//liczenie œredniej w podkwadracie
            		for(int l=x; l<x+3; l++)
            		{
            			for(int m=y; m<y+3; m++)
            			{
            				int argb = pixelReader.getArgb(l, m);
            				
                            int red = (argb >> 16) & 0xFF;
                            int green = (argb >> 8) & 0xFF;
                            int blue = argb & 0xFF;
                            
                            sredniaR += red;
                            sredniaG += green;
                            sredniaB += blue;
            			}
            		}
            		
            		sredniaR /= 9;
            		sredniaG /= 9;
            		sredniaB /= 9;
            		
            		//liczenie wariancji w podkwadracie
            		for(int l=x; l<x+3; l++)
            		{
            			for(int m=y; m<y+3; m++)
            			{
            				int argb = pixelReader.getArgb(l, m);
            				
                            int red = (argb >> 16) & 0xFF;
                            int green = (argb >> 8) & 0xFF;
                            int blue = argb & 0xFF;
                            
                            wariancjaR += Math.pow((sredniaR - red), 2);
                            wariancjaG += Math.pow((sredniaG - green), 2);
                            wariancjaB += Math.pow((sredniaB - blue), 2);
            			}
            		}
            		
            		wariancjaR /= 9;
            		wariancjaG /= 9;
            		wariancjaB /= 9;
            		
            		
            		//sprawdzanie czy wartoœæ aktualna wariancji nie jest wiêksza ni¿ znaleziona
            		if(wariancjaR < minwariancjaR)
            		{
            			minwariancjaR = wariancjaR;
            			maxsredniaR = sredniaR;
            		}
            		if(wariancjaG < minwariancjaG)
            		{
            			minwariancjaG = wariancjaG;
            			maxsredniaG = sredniaG;
            		}
            		if(wariancjaB < minwariancjaB)
            		{
            			minwariancjaB = wariancjaB;
            			maxsredniaB = sredniaB;
            		}
            		
            	}
            	
            	
            	// ustawianie pixela na wartosc sredniej
            	int argb = pixelReader.getArgb(i, j);
            	int x = (argb >> 24)& 0xFF;
            	
                int argbb = (x << 24) + ((int) maxsredniaR << 16) + ((int) maxsredniaG << 8) + (int) maxsredniaB;
                pw.setArgb(i, j, argbb);
            	
            }
        }
        
        ItemKeeper.spwk.iWorkingImage.setImage(wimage);
		
	}
	
	public static void FiltrMedianowy(int maska)
	{
		Image image = ItemKeeper.spwk.iWorkingImage.getImage();
		WritableImage wimage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        PixelWriter pw = wimage.getPixelWriter();
        PixelReader pixelReader = image.getPixelReader();
        
        if(maska<=1)return;
        if(maska%2==0)maska++;
        
        // Maska 3x3 omijamy ramke o gruboœci 1 (maska/2) 5 -> 2 , 7 -> 3 itd.
        int wartosc_poczatkowa = maska/2;
        
        int tablica_medianaR[] = new int[(int)Math.pow(maska, 2)];
        int tablica_medianaG[] = new int[(int)Math.pow(maska, 2)];
        int tablica_medianaB[] = new int[(int)Math.pow(maska, 2)];
        

        for (int i = wartosc_poczatkowa; i < image.getWidth()-wartosc_poczatkowa; i++) {
            for (int j = wartosc_poczatkowa; j < image.getHeight()-wartosc_poczatkowa; j++) {
            	
            	int argbb = pixelReader.getArgb(i, j);
            	int a = (argbb >> 24) & 0xFF;
            	
            	int pozycja_tablica_mediana = 0;
            	
            	// okreœlenie wspó³rzêdnych kwadratu z którego wybieramy medianê
            	for(int k=i-wartosc_poczatkowa; k<=i+wartosc_poczatkowa; k++)
            	{
            		for(int l=j-wartosc_poczatkowa; l<=j+wartosc_poczatkowa; l++)
            		{
            			int argb = pixelReader.getArgb(k,l);
            			
                        int red = (argb >> 16) & 0xFF;
                        int green = (argb >> 8) & 0xFF;
                        int blue = argb & 0xFF;
                        
                        tablica_medianaR[pozycja_tablica_mediana] = red;
                        tablica_medianaG[pozycja_tablica_mediana] = green;
                        tablica_medianaB[pozycja_tablica_mediana] = blue;
                        
                        pozycja_tablica_mediana++;
            		}
            	}
            	
            	
            	//sortowanie b¹belkowe tablic
            	for(int k=0; k<((int)Math.pow(maska,2)); k++)
            	{
            		for(int l=0; l<((int)Math.pow(maska,2))-k-1; l++)
            		{
            			int help;
            			
            			if(tablica_medianaR[l] > tablica_medianaR[l+1])
            			{
            				help = tablica_medianaR[l];
            				tablica_medianaR[l] = tablica_medianaR[l+1];
            				tablica_medianaR[l+1] = help;
            			}
            			
            			if(tablica_medianaG[l] > tablica_medianaG[l+1])
            			{
            				help = tablica_medianaG[l];
            				tablica_medianaG[l] = tablica_medianaG[l+1];
            				tablica_medianaG[l+1] = help;
            			}
            			
            			if(tablica_medianaB[l] > tablica_medianaB[l+1])
            			{
            				help = tablica_medianaB[l];
            				tablica_medianaB[l] = tablica_medianaB[l+1];
            				tablica_medianaB[l+1] = help;
            			}
            		}
            	}
            	
            	int medianaR = tablica_medianaR[(int)Math.pow(maska,2)/2];
            	int medianaG = tablica_medianaG[(int)Math.pow(maska,2)/2];
            	int medianaB = tablica_medianaB[(int)Math.pow(maska,2)/2];
            	
            	
            	int argb = (a << 24) + ((int) medianaR << 16) + ((int) medianaG << 8) + (int) medianaB;
                pw.setArgb(i, j, argb);
            	
            }
        }
        
        ItemKeeper.spwk.iWorkingImage.setImage(wimage);
            
	}

}
