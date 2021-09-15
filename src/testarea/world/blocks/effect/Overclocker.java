package testarea.world.blocks.effect;

import arc.*;
import arc.util.*;
import arc.struct.*;
import arc.math.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import mindustry.*;
import mindustry.graphics.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.meta.*;

public class Overclocker extends Block {
	
	public float multiplier = 1.5f, baseDamage = 150f / 60f, relativeDamage = 0.1f / 60f;
	public float blobSpeed = 1f / 60f;
	
	public TextureRegion laser;
	public TextureRegion laserEnd;
	
	public Overclocker(String name) {
		super(name);
		update = true;
	}
	
	@Override
	public void load() {
		super.load();
		laser = Core.atlas.find("laser");
		laserEnd = Core.atlas.find("laser-end");
	}
	
	@Override
	public void setStats() {
		super.setStats();
		
		stats.add(Stat.speedIncrease, "@x", multiplier);
		stats.add(Stat.damage, "(@ + @%)@", baseDamage * 60, Math.round(relativeDamage * 6000), Core.bundle.get("unit.persecond"));
	}
	
	public class OverclockerBuild extends Building {
		
		public float heat = 0, blobPosition = 0, remainder = 0;
		
		@Override
		public void updateTile() {
			float realBoost = multiplier - 1 + remainder;
			boolean working = enabled && cons.valid();
			boolean othersWorking = false;
			
			if (working) {
				for (Building p : proximity) {
					if (p instanceof Overclocker.OverclockerBuild) continue; //Stackoverflow exception lol
					
					if (p.enabled && p.cons.valid()) {
						for (int i = 0; i < realBoost; i++) p.updateTile();
						othersWorking = true;
						
						p.damage((baseDamage + p.maxHealth * relativeDamage) * Time.delta);
					}
				};
				
				if (blobPosition >= 1) {
					blobPosition = 0;
				}
			}
			
			blobPosition = Mathf.clamp(blobPosition + blobSpeed * Time.delta * (working && othersWorking ? 1f : -0.5f), 0, 1);
			remainder = realBoost % 1;
			heat = Mathf.lerp(heat, working ? 1 : 0.1f, 0.04f);
		}
		
		@Override
		public void draw() {
			super.draw();
			Draw.z(Layer.power);
			
			for (Building p : proximity) {
				if (p instanceof Overclocker.OverclockerBuild) continue;
				
				Draw.color(Pal.redSpark, Pal.logicOperations, heat * (p.enabled && p.cons.valid() ? 1f : 0.1f));
				Draw.alpha(heat);
				
				float ang = angleTo(p), offA = block.size * 2, offB = p.block.size * 2;
				float x1 = x + Angles.trnsx(ang, offA), y1 = y + Angles.trnsy(ang, offA);
				float distance = Mathf.lerp(0, dst(p) - offA - offB, blobPosition);
				
				Drawf.laser(team, laser, laserEnd, x1, y1, p.x - Angles.trnsx(ang, offB), p.y - Angles.trnsy(ang, offB), 0.3f);
				Fill.circle(x1 + Angles.trnsx(ang, distance), y1 + Angles.trnsy(ang, distance), 1.3f);
			}
		}
		
	}
	
}