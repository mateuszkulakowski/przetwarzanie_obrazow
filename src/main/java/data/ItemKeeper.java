package data;

import controller.StackPaneWindowController;

public class ItemKeeper {
	
	public static StackPaneWindowController spwk;
	public static int FiltrLiniowyMaska[][]; // tablica wprowadzana przez klasê FiltrLiniowyWprowadzMaske - okienko
	
	public static int[][] getFiltrLiniowyMaska() {
		return FiltrLiniowyMaska;
	}

	public static void setFiltrLiniowyMaska(int[][] filtrLiniowyMaska) {
		FiltrLiniowyMaska = filtrLiniowyMaska;
	}

	public static void setStackPaneWindowController(StackPaneWindowController st)
	{
		spwk = st;
	}
	
	public static StackPaneWindowController getStackPaneWindowController()
	{
		return spwk;
	}
	
	

}
