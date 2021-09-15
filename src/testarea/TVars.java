package testarea;

import mindustry.*;

class TVars {
	
	//Whether to enable some cool stuff
	public static boolean owo = false;
	
	public static void init() {
		owo = Vars.mods.getMod("uwubundles") != null && Vars.mods.getMod("uwubundles").enabled();
	}
	
}