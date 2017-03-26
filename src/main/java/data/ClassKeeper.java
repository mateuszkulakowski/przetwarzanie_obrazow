package data;

import controller.StackPaneWindowController;

public class ClassKeeper {
	
	public static StackPaneWindowController spwk;
	
	public static void setStackPaneWindowController(StackPaneWindowController st)
	{
		spwk = st;
	}
	
	public static StackPaneWindowController getStackPaneWindowController()
	{
		return spwk;
	}

}
