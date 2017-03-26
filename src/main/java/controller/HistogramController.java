package controller;

import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

import data.ClassKeeper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class HistogramController {
	
	@FXML
	private LineChart<Number,Number> redChart;
	
	@FXML
	private LineChart<Number,Number> greenChart;
	
	@FXML
	private LineChart<Number,Number> blueChart;
	
	@FXML
	private LineChart<Number,Number> mixChart;
	
	@FXML
	private Label lHistogramMouse;
	
	
	XYChart.Series<Number, Number> seriaRed = new XYChart.Series<Number, Number>();
	XYChart.Series<Number, Number> seriaGreen = new XYChart.Series<Number, Number>();
	XYChart.Series<Number, Number> seriaBlue = new XYChart.Series<Number, Number>();
	XYChart.Series<Number, Number> seriaMixed = new XYChart.Series<Number, Number>();

	
	
    @FXML
    void initialize() {
		StackPaneWindowController controller = ClassKeeper.getStackPaneWindowController();
    	
		try
		{
			Image iWorkingImage = controller.iWorkingImage.getImage();
			PixelReader pixelReader = iWorkingImage.getPixelReader();
	    	
	    	int red_colors[] = new int[256];
	    	int green_colors[] = new int[256];
	    	int blue_colors[] = new int[256];
	    
	    	for (int i = 0; i < iWorkingImage.getWidth(); i++) {
	            for (int j = 0; j < iWorkingImage.getHeight(); j++) {
	                Color kolo = pixelReader.getColor(i, j);
	                int argb = pixelReader.getArgb(i, j);
	                
	                int r = (argb >> 16) & 0xFF;
	                int g = (argb >> 8) & 0xFF;
	                int b = argb  & 0xFF;
	                red_colors[r]++;
	                green_colors[g]++;
	                blue_colors[b]++;
	
	            }
	        }
	    	
	    	
	    	for(int i=0; i<256; i++)
	    	{
	    		seriaRed.getData().add(new XYChart.Data<Number, Number>(i, red_colors[i]));
	    		seriaGreen.getData().add(new XYChart.Data<Number, Number>(i, green_colors[i]));
	    		seriaBlue.getData().add(new XYChart.Data<Number, Number>(i, blue_colors[i]));
	    		seriaMixed.getData().add(new XYChart.Data<Number, Number>(i, (blue_colors[i]+green_colors[i]+red_colors[i])/3));
	    		
	    	}
	    	
	    	int minr = 0;
	    	int ming = 0;
	    	int minb = 0;
	    	
	    	int maxr = 0;
	    	int maxg = 0;
	    	int maxb = 0;
	    	
	    	for(int i=0; i<256; i++)
	    	{
	    		if(red_colors[i] >0)minr = i;
	    		if(green_colors[i] >0)ming = i;
	    		if(blue_colors[i] >0)minb = i;
	    	}
	    	
	    	for(int i=255; i>=0; i--)
	    	{
	    		if(red_colors[i] >0)maxr = i;
	    		if(green_colors[i] >0)maxg = i;
	    		if(blue_colors[i] >0)maxb = i;
	    	}
	    	
	    	//System.out.println("minr:"+minr+" maxr:"+maxr+" ming:"+ming+" maxg:"+maxg+" minb:"+minb+" maxb:"+maxb);
	    	
	    	
	    	redChart.setTitle("RED");
	        redChart.setLegendVisible(false);
	        redChart.setStyle("CHART_COLOR_1:#ff0000;");
	        redChart.getData().add(seriaRed);
	        
	        greenChart.setTitle("GREEN");
	        greenChart.setLegendVisible(false);
	        greenChart.setStyle("CHART_COLOR_1:#00ff00;");
	        greenChart.getData().add(seriaGreen);
	        
	        blueChart.setTitle("BLUE");
	        blueChart.setLegendVisible(false);
	        blueChart.setStyle("CHART_COLOR_1:#0000ff;");
	        blueChart.getData().add(seriaBlue);
	        
	        mixChart.setTitle("AVERAGE");
	        mixChart.setLegendVisible(false);
	        mixChart.setStyle("CHART_COLOR_1:#000000;");
	        mixChart.getData().add(seriaMixed);
		}
		catch(NullPointerException e){System.out.println("Nie ma histogramu obraz nie za³adowany!");}
    }
    

}
