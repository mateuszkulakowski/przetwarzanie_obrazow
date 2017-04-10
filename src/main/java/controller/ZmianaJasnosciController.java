package controller;

import data.ItemKeeper;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class ZmianaJasnosciController {
	
	private ImageView iWorkingImage;
	private ImageView iOryginalImage;
	
	int[] tablica_lut_jasniej = new int[256];
	int[] tablica_lut_ciemniej = new int[256];
	
	
	double szybkosc = 1.0001;
	
	@FXML
	void initialize()
	{
		iWorkingImage = ItemKeeper.getStackPaneWindowController().iWorkingImage;
		iOryginalImage = ItemKeeper.getStackPaneWindowController().iOryginalImage;
		
	}
	
	double pierwiastek(double a, double n)
	{
	double result = a;
	double tmp = Math.pow(result,(n-1));
	double e = 0.00000001;
	 
	while (Math.abs(a - tmp * result)>= e)
	{
		result = 1/n*((n-1)*result + (a/tmp));
		//x^n-1
		tmp = Math.pow(result, n-1);
	}
	return result;
	}
	
	
	
	@FXML
	public void onActionJasniej()
	{
		//TABLICA JAŒNIEJ
		for (int i = 0; i < 256; i++) {
			tablica_lut_jasniej[i] = (int) (szybkosc*pierwiastek(256*i, 2));
			
			if(tablica_lut_jasniej[i] > 255)tablica_lut_jasniej[i] = 255;
			if(tablica_lut_jasniej[i] < 0)tablica_lut_jasniej[i] = 0;
        }
		
		
		tablica_lut_jasniej[0] = 1;
        PixelReader pixelReader = iWorkingImage.getImage().getPixelReader();
        WritableImage wimage = new WritableImage((int) iWorkingImage.getImage().getWidth(), (int) iWorkingImage.getImage().getHeight());
        PixelWriter pw = wimage.getPixelWriter();
        for (int i = 0; i < iWorkingImage.getImage().getWidth(); i++) {
            for (int j = 0; j < iWorkingImage.getImage().getHeight(); j++) {
                int argb = pixelReader.getArgb(i, j);
                int a = (argb >> 24) & 0xFF;
                int red = (argb >> 16) & 0xFF;
                int green = (argb >> 8) & 0xFF;
                int blue = argb & 0xFF;
                argb = (a << 24) + ((int) tablica_lut_jasniej[red] << 16) + ((int) tablica_lut_jasniej[green] << 8) + (int) tablica_lut_jasniej[blue];
                pw.setArgb(i, j, argb);

            }
        }
        iWorkingImage.setImage(wimage);
	}
	
	
	
	@FXML
	public void onActionCiemniej()
	{
		//TABLICA CIEMNIEJ
		for (int i = 0; i < 256; i++) {
			tablica_lut_ciemniej[i] = (int) (szybkosc*(Math.pow(i, 2)/256));
			
			if(tablica_lut_ciemniej[i] > 255)tablica_lut_ciemniej[i] = 255;
			if(tablica_lut_ciemniej[i] < 0)tablica_lut_ciemniej[i] = 0;
        }
		
        PixelReader pixelReader = iWorkingImage.getImage().getPixelReader();
        WritableImage wimage = new WritableImage((int) iWorkingImage.getImage().getWidth(), (int) iWorkingImage.getImage().getHeight());
        PixelWriter pw = wimage.getPixelWriter();
        for (int i = 0; i < iWorkingImage.getImage().getWidth(); i++) {
            for (int j = 0; j < iWorkingImage.getImage().getHeight(); j++) {
                int argb = pixelReader.getArgb(i, j);
                int a = (argb >> 24) & 0xFF;
                int red = (argb >> 16) & 0xFF;
                int green = (argb >> 8) & 0xFF;
                int blue = argb & 0xFF;
                argb = (a << 24) + ((int) tablica_lut_ciemniej[red] << 16) + ((int) tablica_lut_ciemniej[green] << 8) + (int) tablica_lut_ciemniej[blue];
                pw.setArgb(i, j, argb);
            }
        }
        iWorkingImage.setImage(wimage);
	}
	
	
	@FXML
	public void onActionOdnowa()
	{
		iWorkingImage.setImage(iOryginalImage.getImage());
	}
	
	
	
	

}
