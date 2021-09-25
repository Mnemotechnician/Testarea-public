package testarea.world.blocks.distribution;

import arc.util.*;
import arc.struct.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.type.*;
import mindustry.graphics.*;
import mindustry.entities.*;
import mindustry.world.*;
import mindustry.world.meta.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.blocks.distribution.*;
import mindustry.gen.*;

public class Provider extends Block {
	
	public float range = 8 * 5f, speed = 1f, sendDelay = 10f;
	public int maxPending = 5;
	public int stackSize = 4;
	
	public Provider(String name) {
		super(name);
		solid = true;
		update = true;
		hasItems = true;
		itemCapacity = 10;
		instantTransfer = true; //this prevents endless recursion when combined with overflow gate or smth similar
	}
	
	@Override
	public void drawPlace(int x, int y, int rotation, boolean valid) {
		super.drawPlace(x, y, rotation, valid);
		Drawf.dashCircle(x * 8, y * 8, range, Pal.accent);
	}
	
	public class ProviderBuild extends Building {
		
		public int clientRotation = 0;
		public float sendTimer = 0f;
		
		//these sequences are linked. i couldn't find a better way of doing that.
		public Seq<Item> pendingItems = new Seq(true, maxPending);
		public Seq<Building> clients = new Seq(true, maxPending);
		public FloatSeq itemPositions = new FloatSeq(true, maxPending);
		public IntSeq itemAmounts = new IntSeq(true, maxPending);
		
		//temporary
		public Seq<Building> tmpClients = new Seq();
		
		@Override
		public void updateTile() {
			super.updateTile();
			
			//finding clients
			if ((sendTimer += Time.delta * efficiency()) > sendDelay && pendingItems.size < maxPending) {
				items.each((item, num) -> {
					//additional check cus the amount of pending items can change during this process
					if (pendingItems.size >= maxPending || num < stackSize) return;
					
					Building client = findClient(item);
					if (client != null) {
						int amount = client.acceptStack(item, Math.max(stackSize, num), this);
						
						pendingItems.add(item);
						clients.add(client);
						itemPositions.add(0f);
						itemAmounts.add(amount);
					}
				});
			}
			
			//updating pending items
			for (int i = 0; i < pendingItems.size; i++) {
				Building client = clients.get(i);
				if (!client.isValid()) {
					removePending(i);
					continue;
				}
				float dst = dst(client);
				
				float pos = itemPositions.get(i) + Time.delta * speed * efficiency();
				if (pos >= dst) {
					Item item = pendingItems.get(i);
					int realAmount = Math.min(client.acceptStack(item, itemAmounts.get(i), this), items.get(item));
					
					//handleStack doesn't seem to work with some buildings. 
					for (int j = 0; j < realAmount; j++) client.handleItem(this, item);
					items.remove(item, realAmount);
					removePending(i);
				} else {
					itemPositions.set(i, pos); //update position
				}
			}
		}
		
		/** Removes a pending item from all 4 sequences */
		public void removePending(int index) {
			pendingItems.remove(index);
			clients.remove(index);
			itemPositions.removeIndex(index);
			itemAmounts.removeIndex(index);
		}
		
		/** Finds a client in block's range that needs the provided item */
		public Building findClient(Item provision) {
			tmpClients.clear();
			Units.nearbyBuildings(x, y, range, b -> {
				if (b.team == team && b.acceptItem(this, provision) && !(b.block instanceof CoreBlock || b.block.category == Category.distribution)) {
					tmpClients.add(b);
				}
			});
			if (tmpClients.size <= 0) return null;
			return tmpClients.get(clientRotation++ % tmpClients.size);
		}
		
		@Override
		public void draw() {
			super.draw();
			
			for (int i = 0; i < pendingItems.size; i++) {
				Item item = pendingItems.get(i);
				Building client = clients.get(i);
				float pos = itemPositions.get(i);
				int amount = itemAmounts.get(i);
				
				//haha variable spam go brrrr
				float angle = angleTo(client);
				float dst = dst(client);
				float realPos = Mathf.clamp(pos * 1.2f - dst * 0.1f, 0, dst);
				float slope = 1f - Math.abs(realPos / dst - 0.5f) * 2f;
				float sine = Mathf.sin(realPos / 5) * slope;
				float itemX = Angles.trnsx(angle, realPos) + Angles.trnsx(angle + 90, sine * 4);
				float itemY = Angles.trnsy(angle, realPos) + Angles.trnsy(angle + 90, sine * 4);
				
				Draw.color();
				Draw.z(Layer.flyingUnitLow);
				if (item.fullIcon != null) {
					Draw.rect(item.fullIcon, x + itemX, y + itemY, slope * amount, slope * amount, realPos * 10);
				}
				Draw.color(team.color);
				Lines.stroke(0.6f);
				Lines.circle(x + itemX, y + itemY, slope * amount + 1);
			}
		}
		
		@Override
		public boolean acceptItem(Building from, Item item) {
			return from.team == team && items.get(item) < getMaximumAccepted(item) 
				   && !(from.block instanceof Provider) && findClient(item) != null;
		}
		
		@Override
		public void drawSelect() {
			super.drawSelect();
			Drawf.dashCircle(x, y, range, team.color);
		}
		
	}
	
}