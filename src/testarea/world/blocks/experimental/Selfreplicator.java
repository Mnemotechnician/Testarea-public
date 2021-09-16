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
import mindustry.type.*;
import mindustry.ctype.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.meta.*;
import mindustry.world.modules.*;

import testarea.content.*;
import testarea.world.blocks.util.*;

public class Selfreplicator extends Block {
	
	public float replicateRate = 60f, replicateTime = 170f;
	
	private static Seq<Tile> tmpTilesAdj = new Seq<Tile>(4);
	
	public Selfreplicator(String name) {
		super(name);
		update = true;
		solid = true;
	}
	
	@Override
	public void setStats() {
		super.setStats();
		stats.add(Stat.buildTime, replicateTime / 60, StatUnit.seconds);
		stats.add(Stat.cooldownTime, replicateRate / 60, StatUnit.seconds);
	}
	
	public class SelfreplicatorBuild extends Building {
		
		float heat = 0, nextReplicate = replicateRate * Mathf.random(0.75f, 1f), replicateTimer = 0;
		boolean isReplicating = false;
		Tile targetTile = null;
		
		@Override
		public void update() {
			super.update();
			
			if (!isReplicating) { 
				if ((heat += Time.delta) > nextReplicate) {
					targetTile = randomAdjacent();
					
					if (targetTile != null && (Vars.state.rules.infiniteResources || tryConsume(team.items(), block.requirements, Vars.state.rules.buildCostMultiplier))) {
						team.items().remove(block.requirements);
						isReplicating = true;
						targetTile.setBlock(TestareaBlocks.placeholder, team, 0);
					}
				}
			} else {
				if ((replicateTimer += Time.delta) > replicateTime) {
					isReplicating = false;
					heat = 0;
					replicateTimer = 0;
					
					if (tile.build instanceof Selfreplicator.SelfreplicatorBuild && (canReplicate(targetTile) || targetTile.block() instanceof Placeholder)) {
						targetTile.setBlock(TestareaBlocks.selfreplicator, team, 0);
						nextReplicate = replicateRate * Mathf.random(0.75f, 1f);
					}
				}
			}
		}
		
		private Tile randomAdjacent() {
			tmpTilesAdj.clear();
			for (Point2 p : Geometry.d4) {
				Tile t = Vars.world.tile(p.x + tile.x, p.y + tile.y);
				if (t != null && canReplicate(t)) {
					tmpTilesAdj.add(t);
				}
			}
			
			if (tmpTilesAdj.size < 1) return null;
			return tmpTilesAdj.get(Mathf.random(tmpTilesAdj.size - 1));
		}
		
		//Tries to consume items from the providen item module. returns true on success.
		private boolean tryConsume(ItemModule inventory, ItemStack[] items, float multiplier) {
			for (ItemStack stack : items) {
				if (!inventory.has(stack.item, Math.round(stack.amount * multiplier))) return false;
			}
			
			for (ItemStack stack : items) inventory.remove(stack.item, Math.round(stack.amount * multiplier));
			return true;
		}
		
		//100500 checks cus idk which are really needed
		private boolean canReplicate(Tile t) {
			return t.block() == Blocks.air && !t.solid() && t.passable() && !t.floor().isDeep();
		}
		
		@Override
		public void draw() {
			super.draw();
			
			if (!isReplicating) return;
			
			float fin = replicateTimer / replicateTime;
			Tmp.v1.set(x, y).interpolate(Tmp.v2.set(targetTile.x * 8, targetTile.y * 8), Math.min(fin * 2, 1), Interp.sineOut);
			float x = Tmp.v1.x, y = Tmp.v1.y;
			
			Draw.draw(Draw.z(), () -> {
				Drawf.construct(targetTile.x * 8, targetTile.y * 8, TestareaBlocks.selfreplicator.region, 0, fin, 1, fin * 180f);
			});
			Draw.color(Pal.accent);
			Lines.stroke(Math.min((1 - fin) * 3, 1.5f));
			Lines.square(x, y, 4);
		}
		
	}
	
}