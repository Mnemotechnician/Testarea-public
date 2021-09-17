package testarea.content;

import mindustry.gen.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.entities.bullet.*;

import testarea.world.extensions.*;

public class TestareaExtensions implements ContentList {
	
	public static InblockTurret
	
	vortex, maelstrom;
	
	@Override
	public void load() {
		vortex = new InblockTurret("vortex") {{
			reloadTime = 60f / 8f;
			inaccuracy = 20f;
			shootCone = 40f;
			shootShake = 0.4f;
			
			ammo(
				Items.surgeAlloy, Bullets.missileSurge,
				Items.blastCompound, Bullets.missileExplosive,
				Items.pyratite, Bullets.missileIncendiary
			);
			shootSound = Sounds.missile;
		}};
		
		maelstrom = new InblockTurret("maelstrom") {{
			reloadTime = 60f / 2f;
			shootShake = 2f;
			
			ammo(
				Items.surgeAlloy, Bullets.fragSurge,
				Items.blastCompound, Bullets.fragExplosive,
				Items.plastanium, Bullets.fragPlastic,
				Items.thorium, Bullets.standardThorium
			);
			shots = 3;
			burstSpacing = 3f;
			xRand = 2f;
			recoilAmount = 3f;
			rotateSpeed = 5f;
			inaccuracy = 10f;
			shootCone = 30f;
		}};
	}
}