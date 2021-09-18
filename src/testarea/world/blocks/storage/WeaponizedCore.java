package testarea.world.blocks.storage;

import arc.*;
import arc.util.*;
import arc.util.io.*;
import arc.struct.*;
import arc.math.*;
import arc.math.geom.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import mindustry.*;
import mindustry.graphics.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.type.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.meta.*;
import mindustry.ui.*;

import testarea.content.*;
import testarea.world.extensions.*;
import testarea.world.modules.*;

public class WeaponizedCore extends CoreBlock {
	
	public Seq<InblockTurret> turretMounts;
	public int turretOffset = 8;
	
	public WeaponizedCore(String name) {
		super(name);
	}
	
	@Override
	public void setStats() {
		super.setStats();
		TurretModule.display(stats, turretMounts);
	}
	
	@Override
	protected TextureRegion[] icons(){
		TextureRegion r = variants > 0 ? Core.atlas.find(name + "1") : region;
		return new TextureRegion[]{r, teamRegions[Team.sharded.id]};
	}
	
	public class WeaponizedCoreBuild extends CoreBlock.CoreBuild {
		
		public TurretModule turrets = new TurretModule(this, turretMounts, turretOffset, 0);
		
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
		
	}
	
}