package testarea;

import arc.*;
import arc.util.*;
import arc.struct.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.mod.*;
import mindustry.ui.dialogs.*;
import mindustry.ctype.*;
import mindustry.type.*;

import testarea.*;
import testarea.content.*;

public class TestareaMod extends Mod {
	
	public static ContentList[] contents = {
		new TestareaExtensions(),
		new TestareaBlocks() //must be init later
	};

	public TestareaMod() {
		TVars.init();
		
		if (TVars.owo && false) { //disabled until i find a way to check whether a specific mod is REALLY enabled
			//listen for game load event
			Events.on(ClientLoadEvent.class, e -> {
				//show dialog upon startup
				Time.runTask(10f, () -> {
					BaseDialog dialog = new BaseDialog("dialogOne");
					dialog.cont.add("HEWWO ;w;").pad(20).row();
					dialog.cont.add("OwO").pad(20).row();
					dialog.cont.button("hi", () -> {
					dialog.hide();
						
						BaseDialog dialogBye = new BaseDialog("dialogTwo");
						dialogBye.cont.add("hail anuke").pad(20).row();
						dialogBye.cont.button("yes.", dialogBye::hide).size(200f, 50f);
						dialogBye.show();
					}).size(100f, 50f);
					dialog.show();
				});
			});
		};
	}

	@Override
	public void loadContent(){
		for (ContentList list : contents) {
			list.load();
		}
	}
}
