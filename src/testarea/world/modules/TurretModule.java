package testarea.world.modules;

import arc.struct.*;
import arc.util.io.*;
import arc.math.*;
import arc.math.geom.*;
import mindustry.type.*;
import mindustry.gen.*;
import mindustry.world.meta.*;

import testarea.util.*;
import testarea.world.extensions.*;

/**
 * Handles on-block turrets.
 * update(), draw(), read() and write() must be called upon respective building's events
 */
public class TurretModule {
	
	public Seq<InblockTurret.TurretEntity> turrets;
	public Building parent;
	
	public Seq<Item> acceptedItems = new Seq(false, 5);
	
	/** Creates a turret module from an existing turret entity sequence */
	public TurretModule(Building parent, int offset, Seq<InblockTurret.TurretEntity> turrets) {
		this.turrets = turrets;
		this.parent = parent;
		
		for (int i = 0; i < turrets.size; i++) {
			var t = turrets.get(i);
			float rotation = (360 / turrets.size) * i;
			t.parent = parent;
			t.offX = Angles.trnsx(rotation, offset);
			t.offY = Angles.trnsy(rotation, offset);
			t.set(parent.x + t.offX, parent.y + t.offY);
			
			updateAccepted();
		}
	}
	
	/** Creates a turret module from a turret module "definition" */
	public TurretModule(Building parent, Seq<InblockTurret> turrets, int offset) {
		this(parent, offset, fromDefinition(turrets));
	}
	
	/* Whoever reads this... I too hate myself for doing this shitcode. */
	private static Seq<InblockTurret.TurretEntity> fromDefinition(Seq<InblockTurret> definition) {
		Seq<InblockTurret.TurretEntity> t = new Seq(definition.size);
		for (var turret : definition) t.add(turret.create(null));
		return t;
	}
	
	/** Creates a turret module "definition" from pairs of "TurretEntity turret, int count" */
	public static Seq<InblockTurret> define(Object... pairs) {
		var t = new Seq<InblockTurret>(pairs.length / 2);
		for (int i = 0; i < pairs.length; i += 2) {
			for (int j = 0; j < (int) pairs[i + 1]; j++) {
				t.add((InblockTurret) pairs[i] );
			}
		}
		return t;
	}
	
	/** Displays module stats in a hacky way. Takes a turret module definition as an input */
	public static void display(Stats stats, Seq<InblockTurret> turrets) {
		StatUtils.displayTurrets(stats, turrets);
	}
	
	/** Updates accepted ammo list */
	public void updateAccepted() {
		acceptsItems.clear();
		for (InblockTurret.TurretEntity t : turrets) {
			for (InblockTurret.AmmoEntry ammo : t.type.ammoList) {
				if (!acceptedItems.contains(ammo.item)) acceptedItems.add(ammo.item);
			}
		}
	}
	
	/** Returns whether this module's turrets need the provided item */
	public boolean acceptItem(Item item) {
		return acceptedItems.contains(item);
	}
	
	/*Building methods*/
	public void update() {
		turrets.each(InblockTurret.TurretEntity::update);
	}
	
	public void draw() {
		turrets.each(InblockTurret.TurretEntity::draw);
	}
	
	public void write(Writes write) {
		write.i(turrets.size); //in case if amount of turrets changes
		for (InblockTurret.TurretEntity t : turrets) t.write(write);
	}
	
	public void read(Reads read, byte revision) {
		int size = read.i(); 
		for (int i = 0; i < size; i++) {
			//if there are exceed bytes, they'll be readen by the last turret
			turrets.get(Math.min(i, turrets.size)).read(read, revision);
		}
	}
	/*Building methods end*/
	
}