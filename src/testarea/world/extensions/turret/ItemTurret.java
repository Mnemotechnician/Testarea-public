package testarea.world.extensions.turret;

import arc.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.entities.*;
import mindustry.entities.Units.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.ctype.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;

import testarea.world.extensions.*;

//Todo: same name as mindustry.world.blocks.defense.ItemTurret?
/** Inblock turret that consumes parent's items in order to shoot. */
public class ItemTurret extends InblockTurret {
	
	public ObjectMap<Item, BulletType> ammoMap = new ObjectMap();
	
	public ObjectMap<UnlockableContent, BulletType> tmp = new ObjectMap();
	
	public ItemTurret(String name) {
		super(name);
	}
	
	/** Defines which ammo the turret can use. Input: item, bullet, item, bullet ... */
	public void ammo(Object... ammo) {
		ammoMap.clear();
		for (int i = 0; i < ammo.length; i += 2) {
			ammoMap.put((Item) ammo[i], (BulletType) ammo[i + 1]);
		}
	}
	
	@Override
	public TurretEntity create(Building parent) {
		init(); //h
		TurretEntity turret = new TurretEntity(this, parent);
		return turret;
	}
	
	@Override
	public float maxRange() {
		float maxRange = 0;
		for (BulletType b : ammoMap.values()) maxRange = Math.max(b.range(), maxRange); //can't use a lambda here
		return maxRange;
	}
	
	@Override
	public float minRange() {
		float minRange = 1000000;
		for (BulletType b : ammoMap.values()) minRange = Math.min(b.range(), minRange);
		return minRange;
	}
	
	@Override
	public ObjectMap<UnlockableContent, BulletType> allAmmo() {
		tmp.clear();
		tmp.putAll(ammoMap);
		return tmp;
	}
	
	//Big brain 100500 iq move
	public class TurretEntity extends InblockTurret.TurretEntity {
		
		protected BulletType currentBullet = null;
		protected Item currentItem = null; 
		public int ammoLeft = 0;
		
		public TurretEntity(ItemTurret type, Building parent) {
			super(type, parent);
		}
		
		@Override
		public void update() {
			findAmmo();
			super.update();
		}
		
		@Override
		public boolean hasAmmo() {
			return currentItem == null ? false : ammoLeft > ammoPerShot || parent.items.has(currentItem, 1 + (int) Math.floor(ammoPerShot / currentBullet.ammoMultiplier));
		}
		
		@Override
		public BulletType peekAmmo() {
			return currentBullet;
		}
		
		@Override
		protected void useAmmo() {
			if ((ammoLeft -= ammoPerShot) < ammoPerShot) {
				//in case if ammoPerShot is higher than ammo multi of the current ammo, multiple items will be consumed
				int times = 1 + (int) Math.floor(ammoPerShot / currentBullet.ammoMultiplier);
				if (parent.items.has(currentItem, times)) {
					ammoLeft = (int) Math.round(currentBullet.ammoMultiplier * times) + ammoLeft;
					parent.items.remove(currentItem, times);
				}
			}
		}
		
		public void findAmmo() {
			//this is sick but it works
			if (interval.get(1, 15) || currentBullet == null) {
				var iterator = ammoMap.entries();
				while (iterator.hasNext()) {
					var entry = iterator.next();
					if (parent.items.has(entry.key)) {
						currentBullet = entry.value;
						currentItem = entry.key;
						return;
					}
				}
				currentBullet = null;
				currentItem = null; //no possible ammo
			}
			//use the old ammo
		}
		
		@Override
		public boolean acceptAsAmmo(Object ammo) {
			if (!(ammo instanceof Item i)) return false;
			for (Item ammoItem : ammoMap.keys()) {
				if (ammoItem == i) return true;
			}
			return false;
		}
		
	}
	
}