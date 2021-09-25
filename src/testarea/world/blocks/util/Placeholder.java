package testarea.world.blocks.util;

import arc.*;
import arc.util.*;
import arc.struct.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;

/**
 * This block is exactly what it's name says
 * does absolutely nothing, disappears after 5 seconds
 */
public class Placeholder extends Block {
	
	float maxLife = 5 * 60f;
	
	public Placeholder(String name) {
		super(name);
		solid = false;
		update = true;
		destructible = false;
		rebuildable = false;
		targetable = false;
		
		hasShadow = false;
		hasItems = false;
		hasPower = false;
	}
	
	@Override
	public boolean canBreak(Tile tile) {
		return false;
	}
	
	@Override
	public boolean isHidden(){
		return true;
	}
	
	public class PlaceholderBuild extends Building {
		
		float life = 0f;
		
		@Override
		public boolean interactable(Team ignored) {
			return false;
		}
		
		@Override
		public void drawStatus() {
			
		}
		
		@Override
		public void drawCracks() {
			
		}
		
		@Override
		public void drawTeam() {
			
		}
		
		@Override
		public void update() {
			super.update();
			if ((life += Time.delta) > maxLife) tile.setBlock(Blocks.air, Team.sharded);
		}
		
		@Override
		public void draw() {
			
		}
		
		@Override
		public boolean canPickup() {
			return false;
		}
		
	}
	
}