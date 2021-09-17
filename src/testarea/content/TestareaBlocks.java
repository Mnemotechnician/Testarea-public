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

import testarea.content.*;
import testarea.world.blocks.effect.*;
import testarea.world.blocks.experimental.*;
import testarea.world.blocks.util.*;
import testarea.world.blocks.storage.*;
import testarea.world.extensions.*;
import testarea.world.modules.*;

import static testarea.content.TestareaExtensions.*;

public class TestareaBlocks implements ContentList {
	
	public static Block 
	
	weaponizedCore,
		
	accelerator, overclocker,
		
	selfreplicator, deconstructor,
	
	placeholder;
	
	@Override
	public void load() {
		weaponizedCore = new WeaponizedCore("core-uranium") {{
			size = 6;
			
			unitType = UnitTypes.gamma;
			health = 9000;
			itemCapacity = 17500;
			thrusterLength = 40 / 4f;
			
			unitCapModifier = 30;
			researchCostMultiplier = 0.15f;
			requirements(Category.effect, ItemStack.with(Items.copper, 13000, Items.lead, 11000, Items.silicon, 9000, Items.thorium, 6000, Items.plastanium, 4000));
			
			turretMounts = TurretModule.define(vortex, 2, maelstrom, 2, vortex, 2, maelstrom, 2);
			turretOffset = 16;
		}};
		
		selfreplicator = new Selfreplicator("selfreplicator") {{
			size = 1;
			fillsTile = false;
			replicateRate = 210f;
			health = 80;
			requirements(Category.effect, ItemStack.with(Items.silicon, 20, Items.plastanium, 10, Items.phaseFabric, 8));
		}};
		
		deconstructor = new Deconstructor("deconstructor") {{
			size = 1;
			deconstructRate = 200f;
			deconstructSpeed = 0.1f;
			health = 120;
			requirements(Category.effect, ItemStack.with(Items.silicon, 150, Items.plastanium, 90, Items.metaglass, 50));
		}};
		
		accelerator = new Overclocker("accelerator") {{
			size = 2;
			fillsTile = false;
			multiplier = 1.8f;
			baseDamage = 60f / 60f;
			relativeDamage = 0.03f / 60f;
			blobSpeed = 1f / 190f;
			consumes.power(420f / 60f);
			requirements(Category.effect, ItemStack.with(Items.lead, 250, Items.silicon, 170, Items.plastanium, 40));
		}};
		
		overclocker = new Overclocker("overclocker") {{
			size = 3;
			fillsTile = false;
			multiplier = 2.6f;
			baseDamage = 150f / 60f;
			relativeDamage = 0.1f / 60f;
			blobSpeed = 1f / 120f;
			consumes.power(1040f / 60f);
			requirements(Category.effect, ItemStack.with(Items.titanium, 400, Items.silicon, 750, Items.surgeAlloy, 400));
		}};
		
		
		placeholder = new Placeholder("placeholder") {{
			size = 1;
			requirements(Category.effect, BuildVisibility.hidden, ItemStack.with(Items.thorium, 1));
		}};
	}
	
}