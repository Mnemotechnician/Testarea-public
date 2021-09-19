package testarea.world.blocks.distribution;

import arc.struct.*;
import arc.math.*;
import arc.util.*;
import arc.util.io.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.world.*;
import mindustry.graphics.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.logic.*;
import mindustry.world.blocks.distribution.*;

import testarea.content.*;
import testarea.world.extensions.*;
import testarea.world.modules.*;

/** Router-duo chimera. Take that, Anuke! */
public class Durouter extends Router {
	
	public Seq<InblockTurret> turretMounts;
	public int turretOffset = 0;
	
	public Durouter(String name) {
		super(name);
		turretMounts = TurretModule.define(TestareaExtensions.officialDuo, 1);
	}
	
	@Override
	public void setStats() {
		super.setStats();
		TurretModule.display(stats, turretMounts);
	}
	
	@Override
	protected TextureRegion[] icons() {
		//todo: why is adding Blocks.duo.region here throws a NPE at startup?
		return new TextureRegion[] {region/*, Blocks.duo.region*/};
	}
	
	public class DurouterBuild extends Router.RouterBuild {
		
		public TurretModule turrets = new TurretModule(this, turretMounts, turretOffset, 0);
		public float heat = 0, rotation = 0, logicControlTime = 0;
		boolean logicShooting = false;
		
		@Override	
		public void control(LAccess type, double p1, double p2, double p3, double p4) {
			if (type == LAccess.shoot && (unit == null || !unit.isPlayer())) {
				turrets.each(t -> t.targetPos.set(World.unconv((float) p1), World.unconv((float) p2)));
				logicControlTime = 30;
				logicShooting = !Mathf.zero(p3);
			}
			super.control(type, p1, p2, p3, p4);
		}
		
		@Override
		public void control(LAccess type, Object p1, double p2, double p3, double p4) {
			if (type == LAccess.shootp && (unit == null || !unit.isPlayer())) {
				logicControlTime = 30;
				logicShooting = !Mathf.zero(p2);
				
				if (p1 instanceof Posc) {
					turrets.each(t -> t.targetPosition((Posc) p1));
				}
			}
			super.control(type, p1, p2, p3, p4);
		}
		
		@Override
		public void updateTile() {
			super.updateTile();
			turrets.update();
			
			if(logicControlTime > 0) {
				logicControlTime -= Time.delta;
			}
			
			//logic shoot position is set by control() methods
			boolean c = isControlled(), l = logicControlled();
			turrets.each(t -> {
				t.isControlled = c || l;
				t.controlShooting = (c && unit.isShooting()) || (l && !c && logicShooting);
				if (c) t.targetPos.set(unit.aimX(), unit.aimY());
			});
		}
		
		@Override
		public boolean acceptItem(Building source, Item item){
			return team == source.team && items.total() < itemCapacity;
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
		
		private boolean logicControlled(){
			return logicControlTime > 0;
		}
		
		@Override
		public void drawSelect() {
			super.drawSelect();
			turrets.each(t -> Drawf.dashCircle(t.x, t.y, t.range, Pal.accent));
		}
		
	}
	
}