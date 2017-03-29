package controller;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.idrsolutions.image.tiff.TiffDecoder;
import com.sun.javafx.image.impl.IntArgb;

import binaryzacjaObrazu.Binaryzacja;
import data.ClassKeeper;
import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.process.ImageConverter;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import rozciagniecieHistogramu.RozciagniecieHistogramu;
import sun.misc.IOUtils;
import wyrownanieHistogramu.WyrownanieHistogramu;

public class StackPaneWindowController {

	
	@FXML
	private MenuItem mWczytaj;
	
	@FXML
	private MenuItem mZapiszJako;
	
	@FXML
	private MenuItem mPokaz_histogram;
	
	@FXML
	private MenuItem mPrzyciemnianieRozjasnianie;
	
	@FXML
	private VBox kontenerVBOX;
	
	@FXML
	public ImageView iOryginalImage;
	
	@FXML
	public ImageView iWorkingImage; 
	
	@FXML
	protected ScrollPane sOryginalImage;
	
	@FXML
	private ScrollPane sWorkingImage;
	
	@FXML
	private Label lPozycjaKursor;
	
	private DoubleProperty zoomPropertyOryginalImage = new SimpleDoubleProperty();
	private DoubleProperty zoomPropertyWorkingImage = new SimpleDoubleProperty();
	private boolean czy_zaladowany_image = false;
	private boolean pressed=false, dragged = false;
	
	public StackPaneWindowController(){}
	
	@FXML
	void initialize()
	{
		//DODAJEMY CONTROLLERA DO WIDZIALNOSCI W CALYM PROGRAMIE
		ClassKeeper.setStackPaneWindowController(this);
		
		 zoomPropertyOryginalImage.addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				iOryginalImage.setFitWidth(zoomPropertyOryginalImage.get());
				iOryginalImage.setFitHeight(iOryginalImage.getFitWidth() * iOryginalImage.getImage().getHeight() / iOryginalImage.getImage().getWidth());
			}
		});
		 
		 zoomPropertyWorkingImage.addListener(new InvalidationListener() {
				@Override
				public void invalidated(Observable observable) {
					iWorkingImage.setFitWidth(zoomPropertyWorkingImage.get());
					iWorkingImage.setFitHeight(iWorkingImage.getFitWidth() * iWorkingImage.getImage().getHeight() / iWorkingImage.getImage().getWidth());
				}
			});

        sOryginalImage.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent event) {
				if (event.getDeltaY() > 0) {
                    zoomPropertyOryginalImage.set(zoomPropertyOryginalImage.get() * 1.1);
                } else if (event.getDeltaY() < 0) {
                    zoomPropertyOryginalImage.set(zoomPropertyOryginalImage.get() / 1.1);
                }
			}
		});
        
        sWorkingImage.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent event) {
				if (event.getDeltaY() > 0) {
                    zoomPropertyWorkingImage.set(zoomPropertyWorkingImage.get() * 1.1);
                } else if (event.getDeltaY() < 0) {
                    zoomPropertyWorkingImage.set(zoomPropertyWorkingImage.get() / 1.1);
                }
			}
		});
        
        
        iOryginalImage.preserveRatioProperty().set(true);
        iOryginalImage.setPickOnBounds(true);

	}
	

	@FXML
	public void onActionmWczytaj()
	{
		FileChooser chooser = new FileChooser();
		FileChooser.ExtensionFilter jpgFilter = new FileChooser.ExtensionFilter("JPG Picture (*.jpg)", "*.jpg");
		FileChooser.ExtensionFilter tiffFilter = new FileChooser.ExtensionFilter("TIFF Picture (*.tif)", "*.tif");
		FileChooser.ExtensionFilter bmpFilter = new FileChooser.ExtensionFilter("Bitmap Picture (*.bmp)", "*.bmp");
		FileChooser.ExtensionFilter pngFilter = new FileChooser.ExtensionFilter("PNG Picture (*.png)", "*.png");
		FileChooser.ExtensionFilter gifFilter = new FileChooser.ExtensionFilter("GIF Picture (*.gif)", "*.gif");
		
	    chooser.setTitle("Otwórz plik");
	    chooser.getExtensionFilters().add(jpgFilter);
	    chooser.getExtensionFilters().add(tiffFilter);
	    chooser.getExtensionFilters().add(bmpFilter);
	    chooser.getExtensionFilters().add(pngFilter);
	    chooser.getExtensionFilters().add(gifFilter);
	    
	    File file = chooser.showOpenDialog(kontenerVBOX.getScene().getWindow());
	    
	    if(chooser.getSelectedExtensionFilter().equals(tiffFilter)) // wczytujemy format .tiff
	    {
	    	try {
	    		TiffDecoder decoder = new TiffDecoder(new RandomAccessFile(file, "r"));
                Image wczytany_obrazek = SwingFXUtils.toFXImage(decoder.read(), null);
	    		
		    	iOryginalImage.setImage(wczytany_obrazek);
		    	iWorkingImage.setImage(wczytany_obrazek);

	    	} catch (Exception e) { System.out.println("Problem z wczytaniem pliku .tiff");}
	    }
	    else // formaty jpg, bmp, png
	    {
	    	Image image = new Image(file.toURI().toString());
		    iOryginalImage.setImage(image);
		    iWorkingImage.setImage(image);
	    }
	    
	    
	    zoomPropertyOryginalImage.set(iOryginalImage.getFitWidth());
	    zoomPropertyWorkingImage.set(iWorkingImage.getFitWidth());
	    
	    czy_zaladowany_image = true;
	}
	
	
	@FXML
	public void onActionmZapiszJako()
	{
		if(czy_zaladowany_image) // GDY OBRAZ JEST WCZYTANY MO¯EMY GO ZAPISAÆ
		{
			FileChooser chooser = new FileChooser();
			FileChooser.ExtensionFilter jpgFilter = new FileChooser.ExtensionFilter("JPG Picture (*.jpg)", "*.jpg");
			FileChooser.ExtensionFilter tiffFilter = new FileChooser.ExtensionFilter("TIFF Picture (*.tif)", "*.tif");
			FileChooser.ExtensionFilter bmpFilter = new FileChooser.ExtensionFilter("Bitmap Picture (*.bmp)", "*.bmp");
			FileChooser.ExtensionFilter pngFilter = new FileChooser.ExtensionFilter("PNG Picture (*.png)", "*.png");
			FileChooser.ExtensionFilter gifFilter = new FileChooser.ExtensionFilter("GIF Picture (*.gif)", "*.gif");
			
			chooser.setTitle("Wybierz miejsce zapisu pliku");
		    chooser.getExtensionFilters().add(jpgFilter);
		    chooser.getExtensionFilters().add(tiffFilter);
		    chooser.getExtensionFilters().add(bmpFilter);
		    chooser.getExtensionFilters().add(pngFilter);
		    chooser.getExtensionFilters().add(gifFilter);
		    
		    File file = chooser.showSaveDialog(kontenerVBOX.getScene().getWindow());
		    
		    BufferedImage bImage = SwingFXUtils.fromFXImage(iWorkingImage.getImage(), null);
		    
		    
		    if(file != null) //JE¯ELI KLIKNIÊTO PRZYCISK ZAPIS
		    {
			    Alert alert = new Alert(AlertType.CONFIRMATION); // ZAPYTANIE CZY ZMIANA ROZDZIELCZOŒCI
			    alert.setHeaderText("");
			    alert.setTitle("Zapytanie");
			    alert.setContentText("Czy chcesz zmieniæ rozdzielczoœæ?");
			    Optional<ButtonType> result = alert.showAndWait();
		    
		     
			    if ((result.isPresent()) && (result.get() == ButtonType.OK)) { // POTWIERDZENIE ZMIANY ROZDZIELCZOŒCI
			    	
			    	TextInputDialog dialog = new TextInputDialog(""); // ZMIANA ROZDZIELCZOŒCI
			    	dialog.setTitle("Wybór nowej rozdzielczoœci");
			    	dialog.setHeaderText("Przyk³ad u¿ycia: (1920x1080)");
			    	dialog.setContentText("Wartoœæ:");
			    	Optional<String> wpisana_wartosc = dialog.showAndWait();
			    	
			    	String wybrana_rozdzielczosc = wpisana_wartosc.toString().substring(9, wpisana_wartosc.toString().length()-1);
			    	
			    	int width = bImage.getWidth();
			    	int height = bImage.getHeight();
			    	
			    	Pattern pattern = Pattern.compile("[0-9]+x[0-9]+");
			    	Matcher matcher = pattern.matcher(wybrana_rozdzielczosc.toString());
			    	
			    	if(matcher.matches()) // CZY PASUJE DO WYRA¯ENIA np 1920x1080
			    	{
			    		String[] width_height = wybrana_rozdzielczosc.split("x");
			    		
			    		if(width >= Integer.parseInt(width_height[0]) && height >= Integer.parseInt(width_height[1])) //je¿eli rozdzielczosc jest mniejsza od akt to git
			    		{
			    		    java.awt.Image tmp = bImage.getScaledInstance(Integer.parseInt(width_height[0]), Integer.parseInt(width_height[1]), java.awt.Image.SCALE_SMOOTH); //zmiana rozdzielczosci
			    		    bImage = new BufferedImage(Integer.parseInt(width_height[0]), Integer.parseInt(width_height[1]), BufferedImage.TYPE_INT_ARGB);
			    		    Graphics2D g2d = bImage.createGraphics();
			    		    g2d.drawImage(tmp, 0, 0, null);
			    		    g2d.dispose();
			    		}
			    	}
			    }
		    }
		    
		    try {
		    	//ZAPIS OBRAZU JAKO WSKAZANY FORMAT
		    	ImagePlus imagePlus = new ImagePlus("Title", bImage);
		    
		    	FileSaver fileSaver = new FileSaver(imagePlus);
		    
		    	
		    	if(chooser.getSelectedExtensionFilter().equals(jpgFilter))
		    	{
		    		fileSaver.saveAsJpeg(file.getAbsolutePath());
		    	}
		    	else if(chooser.getSelectedExtensionFilter().equals(pngFilter))
		    	{
		    		fileSaver.saveAsPng(file.getAbsolutePath());
		    	}
		    	else if(chooser.getSelectedExtensionFilter().equals(bmpFilter))
		    	{
		    		fileSaver.saveAsBmp(file.getAbsolutePath());
		    	}
		    	else if(chooser.getSelectedExtensionFilter().equals(tiffFilter))
		    	{
		    		fileSaver.saveAsTiff(file.getAbsolutePath());
		    	}
		    	else if(chooser.getSelectedExtensionFilter().equals(gifFilter))
		    	{
		    		// PLIK ZAPISYWANY DO FORMATU GIF MUSI BYÆ PRZEKONWERTOWANY DO INDEXED COLOR ABY ZAPISAÆ
		    		ImageConverter imageConverter = new ImageConverter(imagePlus);
		    		imageConverter.convertRGBtoIndexedColor(256);
		    		imagePlus.updateAndDraw();
		    		fileSaver.saveAsGif(file.getAbsolutePath());
		    	}
		    	
		   
		    } catch (Exception e) {}
		}
	}
	
	@FXML
	public void onActionPokazHistogram() throws IOException
	{
		//OKIENKO HISTOGRAMU
		FXMLLoader fxlm = new FXMLLoader();
		fxlm.setLocation(this.getClass().getResource("/fxml/okienkoHistogramu.fxml"));
		
		VBox stack = fxlm.load();
		Scene scene = new Scene(stack);
		
		Stage stage = new Stage();
		stage.setTitle("Histogram obrazu zmodyfikowanego");
		stage.setScene(scene);
		stage.show();
		
	}
	
	
	@FXML
	public void onActionPrzyciemnianieRozjasnianie() throws IOException
	{
		//OKIENKO ZMIANY JASNOŒCI OBRAZU
		FXMLLoader fxlm = new FXMLLoader();
		fxlm.setLocation(this.getClass().getResource("/fxml/okienkoZmianaJasnosci.fxml"));
		
		VBox stack = fxlm.load();
		Scene scene = new Scene(stack);
		
		Stage stage = new Stage();
		stage.setTitle("Zmiana jasnoœci obrazu modyfikowanego");
		stage.setScene(scene);
		stage.show();
	}
	
	@FXML
	public void onActionRozciagniecieHistogramu()
	{
		RozciagniecieHistogramu.rozciagnijHistogram();
	}
	
	@FXML
	public void onActionWyrownanieHistogramu()
	{
		WyrownanieHistogramu.wyrownajHistogram();
	}
	
	@FXML
	public void onActionBinaryzacjaReczna()
	{
		Binaryzacja.reczna();
	}
	
	@FXML
	public void onActionBinaryzacjaOtsu()
	{
		Binaryzacja.otsu();
	}
	
	@FXML
	public void onActionBinaryzacjaNiblack()
	{
		Binaryzacja.niblack();
	}
	
	
	
	
	@FXML
	public void onActionResetObrazka()
	{
		iWorkingImage.setImage(iOryginalImage.getImage());
	}
	
	
	@FXML
	public void onMousePressedOryginalImage()
	{
		pressed = true;
	}
	
	@FXML
	public void onMouseDraggedOryginalImage()
	{
		dragged = true;
	}
	
	
	@FXML
	public void onMouseMovedOryginalImage(MouseEvent e)
	{
		if(czy_zaladowany_image)
		{
			Image oryginalImage = iOryginalImage.getImage();
        	
        	int akt_pozycja_X = (int) Math.floor(e.getX()*oryginalImage.getWidth()/iOryginalImage.getFitWidth());
        	int akt_pozycja_Y = (int) Math.floor(e.getY()*oryginalImage.getHeight()/iOryginalImage.getFitHeight());
        	
        	Color color = oryginalImage.getPixelReader().getColor(akt_pozycja_X,akt_pozycja_Y);
	    	
        	int red =(int) Math.round(color.getRed()*255);
        	int green =(int) Math.round(color.getGreen()*255);
        	int blue =(int) Math.round(color.getBlue()*255);
        	
	    	lPozycjaKursor.setText("Pozycja kursor: ("+akt_pozycja_X+"px x "+akt_pozycja_Y+"px) RED: "+red+", GREEN:"+green+" BLUE:"+blue);
		}
    	
	}
	
	@FXML
	public void onMouseMovedWorkingImage(MouseEvent e)
	{
		if(czy_zaladowany_image)
		{
			Image WorkingImage = iWorkingImage.getImage();
        	
        	int akt_pozycja_X = (int) Math.floor(e.getX()*WorkingImage.getWidth()/iWorkingImage.getFitWidth());
        	int akt_pozycja_Y = (int) Math.floor(e.getY()*WorkingImage.getHeight()/iWorkingImage.getFitHeight());
        	
        	Color color = WorkingImage.getPixelReader().getColor(akt_pozycja_X,akt_pozycja_Y);
	    	
        	int red =(int) Math.round(color.getRed()*255);
        	int green =(int) Math.round(color.getGreen()*255);
        	int blue =(int) Math.round(color.getBlue()*255);
        	
	    	lPozycjaKursor.setText("Pozycja kursor: ("+akt_pozycja_X+"px x "+akt_pozycja_Y+"px) RED: "+red+", GREEN:"+green+" BLUE:"+blue);
		}
    	
	}
	
	
	
	@FXML
	public void onMouseClickedOryginalImage(MouseEvent e)
	{
		if(pressed && !dragged && czy_zaladowany_image)
		{
			Image oryginalImage = iOryginalImage.getImage();
	    	
	    	int akt_pozycja_X = (int) Math.floor(e.getX()*oryginalImage.getWidth()/iOryginalImage.getFitWidth());
	    	int akt_pozycja_Y = (int) Math.floor(e.getY()*oryginalImage.getHeight()/iOryginalImage.getFitHeight());
	    	
	    	TextInputDialog dialog = new TextInputDialog("przyk³ad: 255;66;123");
	    	dialog.setTitle("Wybór RGB pixela");
	    	dialog.setHeaderText("Wybierz RGB pixela (0-255);(0-255);(0-255)");
	    	dialog.setContentText("Wartoœæ:");
	    	Optional<String> wpisana_wartosc = dialog.showAndWait();
	    	
	    	Pattern pattern = Pattern.compile("Optional\\[(([1][0-9][0-9]|[2][0-5][0-5]|[1-9][0-9]|[0-9]);){2}([1][0-9][0-9]|[2][0-5][0-5]|[1-9][0-9]|[0-9])\\]");
	    	Matcher matcher = pattern.matcher(wpisana_wartosc.toString());
	    	
	    	if(matcher.matches() && wpisana_wartosc.isPresent()) // wpisano poprawnie kolor w postaci RGB (0-255);(0-255);(0-255)
	    	{
	    		Image image = iWorkingImage.getImage();
	    		
	    		WritableImage writableImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
	    	    PixelReader pixelReader = image.getPixelReader();
	    	    PixelWriter pixelWriter = writableImage.getPixelWriter();
	    	    
	    	    String rgb = wpisana_wartosc.toString().substring(9, wpisana_wartosc.toString().length()-1); // wyrzucenie Optional[rgb]
	    	    String[] kolory = rgb.split(";");
	    	    
	    	    double red = Double.parseDouble(kolory[0])/255;
	    	    double green = Double.parseDouble(kolory[1])/255;
	    	    double blue = Double.parseDouble(kolory[2])/255;
	    	    
	    	    for (int i = 0; i < (int) image.getWidth(); i++) {
	    	        for (int j = 0; j < (int) image.getHeight(); j++) { 
	    	        	if(akt_pozycja_X == i && akt_pozycja_Y == j)pixelWriter.setColor(i, j, new Color(red, green, blue, 1));
	    	        	else pixelWriter.setColor(i, j, pixelReader.getColor(i, j)); 
	    	        }
	    	  }
	    	    
	    	    
	    	    iWorkingImage.setImage(writableImage);
	    	}
	    	else if(wpisana_wartosc.isPresent())
	    	{
	    		Alert alert = new Alert(AlertType.ERROR);
	    		alert.setTitle("Ostrze¿enie");
	    		alert.setHeaderText("Niepoprawny format koloru!");
	    		alert.setContentText("Przyk³ad: (0-255);(0-255);(0-255)");
	
	    		alert.showAndWait();
	    	}
		}
		
		pressed = false;
		dragged = false;
	}
	
	@FXML
	public void onMouseExitedOryginalImage()
	{
		if(czy_zaladowany_image)
			lPozycjaKursor.setText("");
	}
	

	
	
	
}
