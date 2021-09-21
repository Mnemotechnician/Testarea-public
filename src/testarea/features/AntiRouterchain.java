package testarea.features;

import arc.*;
import arc.util.*;
import arc.func.*;
import arc.struct.*;
import arc.math.*;
import arc.math.geom.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.game.*;
import mindustry.entities.*;
import mindustry.world.*;
import mindustry.world.blocks.distribution.*;

import testarea.*;
import testarea.content.*;

/** Destroys router chains */
public class AntiRouterchain implements Cons<EventType.BlockBuildEndEvent> {
	
	protected static Seq<Building> tmpBuildings = new Seq(false, 16);
	
	public synchronized void get(EventType.BlockBuildEndEvent event) {
		if (TVars.antiChain && !event.breaking && shouldDestroy(event.tile.block())) {
			tmpBuildings.clear();
			tmpBuildings.add(event.tile.build);
			countRecursive(event.tile.build);
			
			if (tmpBuildings.size > TVars.maxRouters) {
				float power = (float) Math.min(Math.pow(tmpBuildings.size / 2, 1.5), 250 / tmpBuildings.size); //no more op explosions that lay the system down
				tmpBuildings.each(b -> {
					Damage.dynamicExplosion(b.x, b.y, power, power, 0, power * 3, true);
					b.damage(b.maxHealth); //to make sure the router itself would get destroyed. No routers on my land!
				});
			}
		}
	}
	
	protected void countRecursive(Building b) {
		for (Building p : b.proximity) {
			if (shouldDestroy(p.block) && !tmpBuildings.contains(p)) {
				tmpBuildings.add(p);
				countRecursive(p);
			}
		}
	}
	
	//todo: exceptions
	public boolean shouldDestroy(Block block) {
		return block instanceof Router;
	}
	
	public void init() {
		Events.on(EventType.BlockBuildEndEvent.class, this);
	}
}