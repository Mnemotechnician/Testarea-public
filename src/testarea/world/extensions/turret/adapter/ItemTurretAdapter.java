package testarea.world.extensions.turret.adapter;

import arc.*;
import mindustry.world.*;

import testarea.world.extensions.turret.*;

/** Copies values from a turret block */
public class ItemTurretAdapter extends ItemTurret {
	
	mindustry.world.blocks.defense.turrets.ItemTurret origin;
	
	public ItemTurretAdapter(String name, mindustry.world.blocks.defense.turrets.ItemTurret copy) {
		super(name);
		
		origin = copy;
		
		shootEffect = copy.shootEffect;
		smokeEffect = copy.smokeEffect;
		ammoUseEffect = copy.ammoUseEffect;
		
		ammoPerShot = copy.ammoPerShot;
		ammoEjectBack = copy.ammoEjectBack;
		inaccuracy = copy.inaccuracy;
		velocityInaccuracy = copy.velocityInaccuracy;
		shots = copy.shots;
		reloadTime = copy.reloadTime;
		spread = copy.spread;
		
		recoilAmount = copy.recoilAmount;
		restitution = copy.restitution;
		cooldown = copy.cooldown;
		shootCone = copy.shootCone;
		shootShake = copy.shootShake;
		shootLength = copy.shootLength;
		xRand = copy.xRand;
		
		minRange = copy.minRange;
		burstSpacing = copy.burstSpacing;
		alternate = copy.alternate;
		targetAir = copy.targetAir;
		targetGround = copy.targetGround;
		
		chargeTime = copy.chargeTime;
		chargeEffects = copy.chargeEffects;
		chargeMaxDelay = copy.chargeMaxDelay;
		chargeEffect = copy.chargeEffect;
		chargeBeginEffect = copy.chargeBeginEffect;
		chargeSound = copy.chargeSound;
		
		unitSort = copy.unitSort;
		
		ammoMap = copy.ammoTypes;
	}
	
	@Override
	public String localized() {
		return Core.bundle.get("block." + origin.name + ".name");
	}
	
	@Override
	protected void init() {
		region = origin.region;
		initialized = true;
	}
	
}