package testarea.util;

import java.lang.*;

import arc.*;
import arc.struct.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.world.meta.*;

import testarea.world.extensions.*;
import testarea.world.modules.*;

public class StatUtils {
	
	//temporary
	private static Seq<InblockTurret> tmpComp = new Seq(false, 4);
	private static int indent = 0;
	
	/** Displays turret module stats in a hacky way. Takes a turret module definition as input */
	public static void displayTurrets(Stats stats, Seq<InblockTurret> turrets) {
		tmpComp.clear();
		
		StringBuilder b = new StringBuilder("[stat]");
		b.append(turrets.size).append(" [lightgray]turrets in total. Turrets by types:");
		for (InblockTurret turret : turrets) {
			if (!tmpComp.contains(turret)) {
				tmpComp.add(turret); //do not display twice
				
				b.append("[white]\n|    \n|    [accent]").append(turret.localized()).append(":");
				for (InblockTurret.AmmoEntry ammo : turret.ammoList) {
					indent = 2;
					b.append("\n[white]|    \n|    |    [lightgray]");
					if (ammo.item != null) {
						b.append("[white]").append(ammo.item.emoji()).append(" [stat]")
										   .append(ammo.item.localizedName).append("[]\n|    |    []");
					}
					displayRecursive(b, ammo.bullet);
				}
			}
		}
		stats.add(Stat.ammo, b.toString());
	}
	
	// Displays bullet and its frag bullets
	private static void displayRecursive(StringBuilder b, BulletType bullet) {
		//ifififififififiififiifififififififififififif
		if (bullet.ammoMultiplier != 2) {
			indent(b, indent); b.append(Core.bundle.format("bullet.multiplier", bullet.ammoMultiplier));
		}
		if (bullet.damage > 0) {
			indent(b, indent); b.append(Core.bundle.format("bullet.damage", bullet.damage));
		}
		if (bullet.reloadMultiplier != 1) {
			indent(b, indent); b.append(Core.bundle.format("bullet.reload", bullet.reloadMultiplier));
		}
		if (bullet.splashDamage != 0) {
			indent(b, indent); b.append(Core.bundle.format("bullet.splashdamage", bullet.splashDamage, bullet.splashDamageRadius / 8));
		}
		if (bullet.lightningDamage > 0) {
			indent(b, indent); b.append(Core.bundle.format("bullet.lightning", bullet.lightning, bullet.lightningDamage));
		}
		if (bullet.homingPower > 0) {
			indent(b, indent); b.append(Core.bundle.get("bullet.homing")).append("[]"); //for some reason it doesn't have a []
		}
		if (bullet.fragBullet != null) {
			indent(b, indent); b.append(Core.bundle.format("bullet.frags", bullet.fragBullets));
			indent++; displayRecursive(b, bullet.fragBullet); indent--;
		}
	}
	
	/* Creates a visual indent */
	public static void indent(StringBuilder b, int amount) {
		b.append("\n[white]");
		for (int i = 0; i < amount; i++) b.append("|    ");
		b.append("[lightgray]");
	}
}