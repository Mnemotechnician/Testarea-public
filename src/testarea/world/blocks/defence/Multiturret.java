package testarea.world.blocks.defense;

import arc.*;
import arc.util.*;
import arc.util.io.*;
import arc.struct.*;
import arc.math.*;
import arc.math.geom.*;
import mindustry.*;
import mindustry.ctype.*;
import mindustry.type.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.meta.*;
import mindustry.ui.*;

import testarea.world.extensions.*;
import testarea.world.modules.*;

public class Multiturret extends Block {
	
	public Seq<InblockTurret> turretMounts;
	public int turretOffset = 8;
	
	public Multiturret(String name) {
		super(name);
		hasItems = true;
		solid = true;
		update = true;
	}
	
	@Override
	public void setStats() {
		super.setStats();
		TurretModule.display(stats, turretMounts);
	}
	
	public class MultiturretBuild extends Building {
		
		public TurretModule turrets = new TurretModule(this, turretMounts, turretOffset);
		
		@Override
		public void updateTile() {
			super.updateTile();
			turrets.update();
		}
		
		@Override
		public void draw() {
			super.draw();
			turrets.draw();
		}
		
		@Override
		public void write(Writes write) {
			super.write(write);
			turrets.write(write);
		}
		
		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			turrets.read(read, revision);
		}
		
		@Override
		public boolean acceptItem(Building source, Item item){
			return turrets.acceptItem(item) && super.acceptItem(source, item);
		}
		
	}
	
}