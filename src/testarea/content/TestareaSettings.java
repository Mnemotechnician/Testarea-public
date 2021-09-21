package testarea.content;

import arc.*;
import arc.graphics.*;
import arc.scene.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import static arc.Core.*;

import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;
import mindustry.ui.dialogs.SettingsMenuDialog.*;
import static mindustry.Vars.*;

import testarea.*;

public class TestareaSettings {
	public SettingsTable table;
	
	public void init() {
		BaseDialog dialog = new BaseDialog("Testarea by Mnemotechnican");
		dialog.addCloseButton();
		
		table = new SettingsTable();
		table.checkPref("testarea-anti-router-chain", true, enabled -> {
			TVars.antiChain = enabled;
		});
		
		table.sliderPref("testarea-max-routers", 4, 1, 10, value -> {
			TVars.maxRouters = value;
			return value + " " + Blocks.router.emoji();
		});
		
		dialog.cont.center().add(table);
		
		//Stole some code from esoterium-solutions, lol. Thanks, meep.
		ui.settings.shown(() -> {
			Table settingUi = (Table)((Group)((Group)(ui.settings.getChildren().get(1))).getChildren().get(0)).getChildren().get(0); //This looks so stupid lol
			settingUi.row();
			settingUi.button("Testarea", Styles.cleart, dialog::show);
		});
	}
}