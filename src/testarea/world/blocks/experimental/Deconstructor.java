package testarea.world.blocks.experimental;

import arc.*;
import arc.util.*;
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
import mindustry.world.meta.*;
import mindustry.ui.*;

import testarea.content.*;
import testarea.world.blocks.util.*;

public class Deconstructor extends Block {
	
	public float deconstructRate = 60f, deconstructSpeed = 0.1f;
	
	public Deconstructor(String name) {
		super(name);
		update = true;
		solid = true;
		rotate = true;
		hasItems = true;
	}
	
	@Override
	public void setStats() {
		super.setStats();
		stats.add(Stat.buildSpeed, "@@", deconstructSpeed * 100, "%");
		stats.add(Stat.cooldownTime, deconstructRate / 60, StatUnit.seconds);
	}
	
	@Override
	public void drawPlace(int x, int y, int rotation, boolean valid) {
		super.drawPlace(x, y, rotation, valid);
		
		Tile t = Vars.world.tile(x, y);
		if (t != null) t = t.nearby(rotation);
		if (t != null) {
			Lines.stroke(1);
			Draw.color(Pal.accent, Pal.remove, (1 + (float) Math.sin(Time.globalTime / 25)) / 2f);
			Lines.square(t.x * 8, t.y * 8, 4);
		}
	}
	
	@Override
    public boolean outputsItems (){
        return true;
    }
	
	@Override
	public void setBars() {
		super.setBars();
		
		bars.add("progress", (DeconstructorBuild b) -> new Bar("bar.progress", Pal.ammo, () -> {
			return b.isDeconstructing ? 
				((b.targetTile.build instanceof ConstructBlock.ConstructBuild cb) ? cb.progress : 0f) :
				Math.min(b.heat / deconstructRate, 1);
		}));
	}
	
	
	public class DeconstructorBuild extends Building {
		
		public float heat = 0;
		public boolean isDeconstructing = false;
		public Tile targetTile = null;
		public Block targetBlock = null;
		public BlockUnitc unit = null;
		public FloatSeq accumulator = new FloatSeq(4);
		
		@Override
		public void update() {
			super.update();
			
			dump(null);
			
			if (!isDeconstructing) { 
				if (enabled && (heat += Time.delta) > deconstructRate) {
					targetTile = tile.nearby(rotation);
					
					if (targetTile != null && Build.validBreak(team, targetTile.x, targetTile.y)) {
						isDeconstructing = true;
						targetBlock = targetTile.block();
						//no need to begin deconstruction if it's ongoing already
						if (!(targetBlock instanceof ConstructBlock)) {
							Build.beginBreak(null, team, targetTile.x, targetTile.y);
						} else {
							targetBlock = ((ConstructBlock.ConstructBuild) targetTile.build).current;
						};
						
						//clear le accumulator seq without actually clearing it
						for (int i = 0; i < targetBlock.requirements.length; i++) {
							if (i < accumulator.size) accumulator.set(i, 0); else accumulator.add(0);
						};
						heat = 0;
					};
				};
			} else {
				if (!(targetTile.build instanceof ConstructBlock.ConstructBuild cb)) {
					isDeconstructing = false; //The building either was deconstructed or destroyed. 
					return;
				};
				if (!canContinue() || !enabled) return;
				
				float p = Math.min(cb.progress, (deconstructSpeed * Time.delta) / targetBlock.buildCost);
				if ((cb.progress -= p) <= 0) {
					isDeconstructing = false;
					ConstructBlock.deconstructFinish(targetTile, targetBlock, unit());
				};
				
				//refund. may not be perfectly accurate.
				for (int i = 0; i < targetBlock.requirements.length; i++) {
					Item type = targetBlock.requirements[i].item;
					float amount = targetBlock.requirements[i].amount * Vars.state.rules.buildCostMultiplier * Vars.state.rules.deconstructRefundMultiplier * Time.delta;
					float realAmount = amount * p + accumulator.get(i);
					int free = block.itemCapacity - items.get(type);
					int transfer = (int) Math.round(Math.min(realAmount, free));
					
					for (int t = 0; t < transfer; t++) offload(type); 
					accumulator.set(i, (float) Math.min(realAmount - transfer, free));
				};
			};
		};
		
		public Unit unit() {
			if(unit == null){
				unit = (BlockUnitc) UnitTypes.block.create(team);
				unit.tile(this);
			};
			return (Unit) unit;
		};
		
		//Returns whether the block's inventory can accept refund of deconstructing the current target
		public boolean canContinue() {
			if (targetBlock == null) return true;
			for (int i = 0; i < targetBlock.requirements.length; i++) {
				Item type = targetBlock.requirements[i].item;
				if (items.get(type) >= block.itemCapacity) return false;
			};
			return true;
		};
		
		@Override
		public void draw() {
			super.draw();
			
			if (!(isDeconstructing && enabled)) return;
			if (!(targetTile.build instanceof ConstructBlock.ConstructBuild cb)) return;
			
			Draw.z(Layer.power);
			Draw.color(Pal.remove);
			Lines.stroke(1f);
			
			for (int i = 0; i < 4; i++) {
				float fin = Mathf.mod((cb.progress * 4) - i / 4f, 1);
				
				arcInterp(Tmp.v1.set(x, y), Tmp.v2.set(targetTile.x * 8, targetTile.y * 8), fin, i % 2 == 0 ? 50 : -50);
				float x = Tmp.v1.x, y = Tmp.v1.y;
				
				Lines.circle(x, y, 0.9f * (1 - Math.abs((fin - 0.5f) * 2)));
			};
		};
		
		//Arc vec2 interpolation. Offset is the difference between start and end angles (in degrees)
		private void arcInterp(Vec2 origin, Vec2 end, float distance, float offset) {
			float dst = origin.dst(end), slope = 1 - Math.abs((distance - 0.5f) * 2);
			float angle = origin.angleTo(end) + offset * slope; 
			origin.x += Angles.trnsx(angle, dst * distance);
			origin.y += Angles.trnsy(angle, dst * distance);
		}
		
	}
}