package testarea;

import arc.*;
import mindustry.*;

import testarea.content.*;

public class TVars {
	
	public static TestareaSettings settings = new TestareaSettings();
	
	/** Whether to enable some cool stuff */
	public static boolean owo = false;
	
	/** Indent style used in custom stat display */
	public static String indentStyle = "|   ";
	/** Indentation lines color used in custom stat display */
	public static String indentColor = "[gray]";
	
	/** Whether to enable anti-router-chain mode */
	public static boolean antiChain = true;
	/** If antiChain is enabled, this sets the maximum allowed amount of routers in a chain */
	public static int maxRouters = 4;
	
	public static void init() {
		settings.init();
		
		antiChain = Core.settings.getBool("testarea-anti-router-chain", true);
		maxRouters = Core.settings.getInt("testarea-max-routers", 4);
		
		owo = Vars.mods.getMod("uwubundles") != null && Vars.mods.getMod("uwubundles").enabled();
	}
	
}