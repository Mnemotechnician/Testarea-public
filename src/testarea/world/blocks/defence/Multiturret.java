package testarea.world.blocks.defense;

import arc.*;
import arc.util.*;
import arc.util.io.*;
import arc.struct.*;
import arc.math.*;
import arc.math.geom.*;
import mindustry.*;
import mindustry.core.*;
import mindustry.graphics.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.type.*;
import mindustry.logic.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.meta.*;
import mindustry.ui.*;

import testarea.world.extensions.*;
import testarea.world.modules.*;

/**
 * A block with multiple independent turrets.
 */
public class Multiturret extends Block {
	
	public final static float logicControlCooldown = 30;
	
	public Seq<InblockTurret> turretMounts;
	public int turretOffset = 8;
	public float maxRange = -1; //negative = auto
	
	public boolean heatSpin = true;
	public float rotationSpeed = 1f;
	
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
	
	@Override
	public void drawPlace(int x, int y, int rotation, boolean valid) {
		super.drawPlace(x, y, rotation, valid);
		Drawf.dashCircle(x * 8, y * 8, maxRange, Pal.accent);
	}
	
	@Override
	public void init() {
		super.init();
		//find max range, if not pre-set
		if (maxRange < 0) turretMounts.each(t -> t.ammoList.each(a -> {
			maxRange = Math.max(a.bullet.range() + turretOffset, maxRange);
		}));
	}
	
	public class MultiturretBuild extends Building implements ControlBlock {
		
		public TurretModule turrets = new TurretModule(this, turretMounts, turretOffset, 0);
		public float heat = 0, rotation = 0, logicControlTime = 0;
		
		public BlockUnitc unit;
		boolean logicShooting = false;
		
		@Override	
		public void control(LAccess type, double p1, double p2, double p3, double p4) {
			if (type == LAccess.shoot && (unit == null || !unit.isPlayer())) {
				turrets.each(t -> t.targetPos.set(World.unconv((float) p1), World.unconv((float) p2)));
				logicControlTime = logicControlCooldown;
				logicShooting = !Mathf.zero(p3);
			}
			super.control(type, p1, p2, p3, p4);
		}
		
		@Override
		public void control(LAccess type, Object p1, double p2, double p3, double p4) {
			if (type == LAccess.shootp && (unit == null || !unit.isPlayer())) {
				logicControlTime = logicControlCooldown;
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
			
			turrets.each(t -> heat += t.heat);
			heat /= turrets.amount(); //heat = average heat of all turrets
			if (heatSpin) {
				rotation += delta() * heat * rotationSpeed;
				turrets.rearrange(turretOffset, rotation);
			}
			
			if(unit != null){
				unit.health(health);
				unit.team(team);
				unit.set(x, y);
			}
			
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
			return turrets.acceptItem(item) && items.get(item) < block.itemCapacity;
		}
		
		@Override
		public Unit unit(){
			if(unit == null){
				unit = (BlockUnitc) UnitTypes.block.create(team);
				unit.tile(this);
			}
			return (Unit) unit;
		}
		
		private boolean logicControlled(){
			return logicControlTime > 0;
		}
		
		@Override
		public void drawSelect() {
			super.drawSelect();
			Drawf.dashCircle(x, y, maxRange, Pal.accent);
		}
		
	}
	
}