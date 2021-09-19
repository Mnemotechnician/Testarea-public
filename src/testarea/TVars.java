package testarea;

import mindustry.*;

public class TVars {
	
	/** Whether to enable some cool stuff */
	public static boolean owo = false;
	
	/** Indent style used in custom stat display */
	public static String indentStyle = "|   ";
	/** Indentation lines color used in custom stat display */
	public static String indentColor = "[gray]";
	
	public static void init() {
		owo = Vars.mods.getMod("uwubundles") != null && Vars.mods.getMod("uwubundles").enabled();
	}
	
}