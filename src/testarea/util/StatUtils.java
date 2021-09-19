package testarea.util;

import java.lang.*;

import arc.*;
import arc.util.*;
import arc.struct.*;
import mindustry.ctype.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.world.meta.*;

import testarea.*;
import testarea.world.extensions.*;
import testarea.world.modules.*;

public class StatUtils {
	
	//temporary
	private static Seq<InblockTurret> tmpTurrets = new Seq(false, 8);
	private static int indent = 0;
	
	/** Displays turret module stats in a hacky way. Takes a turret module definition as input */
	public static void displayTurrets(Stats stats, Seq<InblockTurret> turrets) {
		tmpTurrets.clear();
		StringBuilder b = new StringBuilder("[stat]");
		b.append(Core.bundle.format("stat.multiturret-header", turrets.size));
		
		for (InblockTurret turret : turrets) {
			if (tmpTurrets.contains(turret)) continue;
			tmpTurrets.add(turret); //do not display twice
			
			indent(b, 1);
			indent(b, 2); b.append("[accent]").append(turret.localized()).append(":[]");
			indent(b, 2); b.append(Core.bundle.get("stat.shootrange")).append(": ")
						   .append(Math.round(turret.minRange() / 8)).append("~").append(Math.round(turret.maxRange() / 8))
						   .append(" ").append(Core.bundle.get("unit.blocks"));
			indent(b, 2); b.append(Core.bundle.get("stat.inaccuracy")).append(": ").append(Math.round(turret.inaccuracy)).append("°");
			indent(b, 2); b.append(Core.bundle.get("stat.reload")).append(": ").append(Strings.fixed(60f / turret.reloadTime, 1)).append(Core.bundle.get("unit.persecond"));
			
			for (ObjectMap.Entry<UnlockableContent, BulletType> ammo : turret.allAmmo()) {
				indent(b, 2); indent(b, 3);
				indent = 3;
				if (ammo.key != null) {
					b.append("[white]").append(ammo.key.emoji()).append(" [stat]")
									   .append(ammo.key.localizedName);
					indent(b, 3);
				}
				if (ammo.value != null) displayRecursive(b, ammo.value);
			}
			
		}
		stats.add(Stat.ammo, b.toString());
	}
	
	// Displays bullet and its frag bullets
	private static void displayRecursive(StringBuilder b, BulletType bullet) {
		//ifififififififiififiifififififififififififif
		if (bullet.ammoMultiplier != 1)   stat(b, Core.bundle.format("bullet.multiplier", bullet.ammoMultiplier));
		if (bullet.damage > 0)            stat(b, Core.bundle.format("bullet.damage", bullet.damage));
		if (bullet.reloadMultiplier != 1) stat(b, Core.bundle.format("bullet.reload", bullet.reloadMultiplier));
		if (bullet.splashDamage != 0)     stat(b, Core.bundle.format("bullet.splashdamage", bullet.splashDamage, bullet.splashDamageRadius / 8));
		if (bullet.lightningDamage > 0)   stat(b, Core.bundle.format("bullet.lightning", bullet.lightning, bullet.lightningDamage));
		if (bullet.homingPower > 0)       stat(b, Core.bundle.get("bullet.homing")); b.append("[]"); //for some reason it doesn't have a []
		
		if (bullet.fragBullet != null) {
			stat(b, Core.bundle.format("bullet.frags", bullet.fragBullets));
			indent++; displayRecursive(b, bullet.fragBullet); indent--;
		}
	}
	
	//utility cus i hate this repeating code
	private static void stat(StringBuilder b, String stat) {
		indent(b, indent); b.append(stat);
	}
	
	/** Creates a visual indent */
	public static void indent(StringBuilder b, int amount) {
		b.append("\n").append(TVars.indentColor);
		for (int i = 0; i < amount; i++) b.append(TVars.indentStyle);
		b.append("[]");
	}
}