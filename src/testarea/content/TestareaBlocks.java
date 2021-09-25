package testarea.content;

import arc.util.*;
import arc.struct.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.meta.*;
import static mindustry.type.ItemStack.*;

import testarea.content.*;
import testarea.world.blocks.effect.*;
import testarea.world.blocks.distribution.*;
import testarea.world.blocks.experimental.*;
import testarea.world.blocks.util.*;
import testarea.world.blocks.storage.*;
import testarea.world.blocks.defense.*;
import testarea.world.extensions.*;
import testarea.world.modules.*;
import static testarea.content.TestareaExtensions.*;

public class TestareaBlocks implements ContentList {
	
	public static Block 
	
	duet,
	
	durouter, provider,
	
	weaponizedCore,
		
	accelerator, overclocker,
		
	selfreplicator, deconstructor,
	
	placeholder;
	
	@Override
	public void load() {
		duet = new Multiturret("duet") {{
			size = 3;
			
			turretMounts = TurretModule.define(vortex, 1, maelstrom, 1);
			turretOffset = 6;
			
			consumes.power(140f / 60f);
			requirements(Category.turret, with(Items.copper, 300, Items.titanium, 150, Items.silicon, 250, Items.plastanium, 50));
		}};
		
		durouter = new Durouter("durouter") {{
			itemCapacity = 4;
			requirements(Category.distribution, with(Items.copper, 50));
		}};
		
		provider = new Provider("provider") {{
			size = 3;
			range = 8 * 8;
			speed = 0.4f;
			
			consumes.power(230 / 60f);
			requirements(Category.distribution, with(Items.silicon, 120, Items.phaseFabric, 70));
		}};
		
		weaponizedCore = new WeaponizedCore("core-uranium") {{
			size = 6;
			
			unitType = UnitTypes.gamma;
			health = 9000;
			itemCapacity = 17500;
			thrusterLength = 40 / 4f;
			
			unitCapModifier = 30;
			researchCostMultiplier = 0.15f;
			requirements(Category.effect, with(Items.copper, 13000, Items.lead, 11000, Items.silicon, 9000, Items.thorium, 6000, Items.plastanium, 4000));
			
			turretMounts = TurretModule.define(vortex, 2, maelstrom, 2, vortex, 2, maelstrom, 2);
			turretOffset = 16;
		}};
		
		selfreplicator = new Selfreplicator("selfreplicator") {{
			size = 1;
			fillsTile = false;
			replicateRate = 210f;
			health = 80;
			requirements(Category.effect, with(Items.silicon, 20, Items.plastanium, 10, Items.phaseFabric, 8));
		}};
		
		deconstructor = new Deconstructor("deconstructor") {{
			size = 1;
			deconstructRate = 200f;
			deconstructSpeed = 0.1f;
			health = 120;
			requirements(Category.effect, with(Items.silicon, 40, Items.plastanium, 16, Items.metaglass, 8));
		}};
		
		accelerator = new Overclocker("accelerator") {{
			size = 2;
			fillsTile = false;
			multiplier = 1.4f;
			baseDamage = 60f / 60f;
			relativeDamage = 0.08f / 60f;
			blobSpeed = 1f / 190f;
			
			consumes.power(420f / 60f);
			requirements(Category.effect, with(Items.lead, 120, Items.silicon, 80, Items.plastanium, 90));
		}};
		
		overclocker = new Overclocker("overclocker") {{
			size = 3;
			fillsTile = false;
			multiplier = 1.95f;
			baseDamage = 150f / 60f;
			relativeDamage = 0.15f / 60f;
			blobSpeed = 1f / 120f;
			
			consumes.power(1040f / 60f);
			requirements(Category.effect, with(Items.titanium, 250, Items.silicon, 300, Items.surgeAlloy, 100));
		}};
		
		placeholder = new Placeholder("placeholder") {{
			size = 1;
			itemCapacity = 10;
			requirements(Category.effect, BuildVisibility.hidden, with(Items.thorium, 1));
		}};
	}
	
}