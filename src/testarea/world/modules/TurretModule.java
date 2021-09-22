package testarea.world.modules;

import arc.struct.*;
import arc.util.io.*;
import arc.math.*;
import arc.math.geom.*;
import arc.func.*;
import mindustry.type.*;
import mindustry.gen.*;
import mindustry.world.meta.*;

import testarea.util.*;
import testarea.world.extensions.*;

/**
 * Handles on-block turrets.
 * update(), draw(), read() and write() must be called upon respective building's events
 * logic & player control can be implemented separately
 */
public class TurretModule {
	
	public Seq<InblockTurret.TurretEntity> turrets;
	public Building parent;
	
	/** Creates a turret module from an existing turret entity sequence */
	public TurretModule(Building parent, int offset, float rotationOffset, Seq<InblockTurret.TurretEntity> turrets) {
		this.turrets = turrets;
		this.parent = parent;
		
		rearrange(offset, rotationOffset);
	}
	
	/** Creates a turret module from a turret module "definition" */
	public TurretModule(Building parent, Seq<InblockTurret> turrets, int offset, float rotationOffset) {
		this(parent, offset, rotationOffset, fromDefinition(turrets));
	}
	
	/* Whoever reads this... I too hate myself for doing this shitcode. */
	private static Seq<InblockTurret.TurretEntity> fromDefinition(Seq<InblockTurret> definition) {
		Seq<InblockTurret.TurretEntity> t = new Seq(definition.size);
		for (var turret : definition) t.add(turret.create(null));
		return t;
	}
	
	/** Creates a turret module "definition" from pairs of "InblockTurret turret, int count" */
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
	
	/** Updates offset & parent of existing turrets */
	public void rearrange(int offset, float rotationOffset) {
		for (int i = 0; i < turrets.size; i++) {
			var t = turrets.get(i);
			float rotation = (360 / turrets.size) * i + rotationOffset;
			t.parent = parent;
			t.offX = Angles.trnsx(rotation, offset);
			t.offY = Angles.trnsy(rotation, offset);
			t.set(parent.x + t.offX, parent.y + t.offY);
		}
	}
	
	/** Returns whether this module's turrets need the provided item */
	public boolean acceptItem(Item item) {
		for (InblockTurret.TurretEntity t : turrets) {
			if (t.acceptAsAmmo(item)) return true;
		}
		return false;
	}
	
	/** Iterates through every turret. */
	public void each(Cons<InblockTurret.TurretEntity> c) {
		turrets.each(c);
	}
	
	/** Returns the amount of turrets this module handles */
	public int amount() {
		return turrets.size;
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