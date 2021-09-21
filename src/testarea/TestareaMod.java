package testarea;

import arc.*;
import arc.util.*;
import arc.struct.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.mod.*;
import mindustry.ui.dialogs.*;
import mindustry.ctype.*;
import mindustry.type.*;

import testarea.*;
import testarea.features.*;
import testarea.content.*;

public class TestareaMod extends Mod {
	
	public static final ContentList[] contents = {
		new TestareaExtensions(),
		new TestareaBlocks() //must be init later
	};
	
	public AntiRouterchain antiRouterchain = new AntiRouterchain();

	public TestareaMod() {
		Events.on(EventType.ClientLoadEvent.class, ignored -> {
			TVars.init();
			antiRouterchain.init();
		});
	}

	@Override
	public void loadContent(){
		for (ContentList list : contents) {
			list.load();
		}
	}
}
