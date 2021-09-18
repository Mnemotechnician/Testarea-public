package testarea.world.extensions;

import arc.*;
import arc.audio.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.g2d.TextureAtlas.*;
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
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.graphics.MultiPacker.*;
import mindustry.logic.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;

/**
 * Turret that can be used on non-turret blocks.
 * This class is just a definition, entitles are created using the create() function
 */
public class InblockTurret {
	
	public String name;
	private boolean initialized = false;
	
	public Effect shootEffect = Fx.none;
    public Effect smokeEffect = Fx.none;
    public Effect ammoUseEffect = Fx.none;
	public Sound shootSound = Sounds.shoot;
	
	//region
	public TextureRegion region;
	//expensive way to draw outline. looks really ugly. i can't just generate an outline region cus thanks anuke for not supporting irregular content types
	public boolean drawOtline = false;
    public Color outlineColor = Color.valueOf("404049");;
    public int outlineRadius = 8;
	
	//general info
	public float rotateSpeed = 5f;
	public float reloadTime = 20f;
	public int ammoPerShot = 1;
	public float ammoEjectBack = 1f;
	public float inaccuracy = 0f;
	public float velocityInaccuracy = 0f;
	public int shots = 1;
	public float spread = 4f;
	public float recoilAmount = 1f;
	public float restitution = 0.02f;
	public float cooldown = 0.02f;
	public float shootCone = 8f;
	public float shootShake = 0f;
	public float shootLength = -1;
	public float xRand = 0f;
	public float minRange = 0f;
	public float burstSpacing = 0;
	public float elevation = 1f;
	public boolean alternate = false;
    //charging
    public float chargeTime = -1f;
    public int chargeEffects = 5;
    public float chargeMaxDelay = 10f;
    public boolean accurateDelay = false;
    public Effect chargeEffect = Fx.none;
    public Effect chargeBeginEffect = Fx.none;
    public Sound chargeSound = Sounds.none;
    //h
    public float defaultRange = -1;
	
	public boolean targetAir = true, targetGround = true;
	public Sortf unitSort = Unit::dst2;
	public Seq<AmmoEntry> ammoList = new Seq(8);
	
	protected Vec2 tr = new Vec2();
	protected Vec2 tr2 = new Vec2();
	
	public InblockTurret(String name) {
		this.name = Vars.content.transformName(name);
	}
	
	//Creates a new instance of the turret entity. Initializes the type if it isn't initialized yet.
	public TurretEntity create(Building parent) {
		if (!initialized) init(); //h
		TurretEntity turret = new TurretEntity(this, parent);
		return turret;
	}
	
	private void init() {
		//h x2. if anyone know how to get a load()-like method in non-content classes, tell me pls. i really need that.
		region = Core.atlas.find(name);
		initialized = true;
	}
	
	/** First entries are checked first. Input: item, bullet, item, bullet, ... */
	public void ammo(Object... ammo) {
		ammoList.clear();
		for (int i = 0; i < ammo.length; i += 2) {
			ammoList.add(new AmmoEntry((Item) ammo[i], (BulletType) ammo[i + 1]));
		}
	}
	
	/** returns localized name of the turret defined in a bundle */
	public String localized() {
		return Core.bundle.get("extension." + name + ".name");
	}
	
	/**
	 * Turret entity itself.
	 * kill me pwease
	 */
	public class TurretEntity implements Position {
		
		public InblockTurret type;
		
		public float x = 0, y = 0;
		public float rotation = 90f;
		public float offX = 0, offY = 0;
		public Building parent;
		
		public float reload = 0, recoil = 0, heat = 0;
		public int shotCounter = 0;
		public Posc target = null;
		public Vec2 targetPos = new Vec2();
		public boolean wasShooting = false, charging = false;
		
		private Interval interval = new Interval(2);
		public AmmoEntry currentAmmo = null;
		public float range = 0;
		public int ammoLeft = 0;
		
		protected TurretEntity(InblockTurret type, Building parent) {
			this.type = type;
			this.parent = parent;
		}
		
		public void update() {
			if (parent == null || !parent.isValid()) return;
			set(parent.x + offX, parent.y + offY);
			
			currentAmmo = possibleAmmo();
			if (currentAmmo != null) range = (defaultRange > 0 ? defaultRange : currentAmmo.bullet.speed * currentAmmo.bullet.lifetime * 1.1);
			
			if(!validateTarget()) target = null;
			wasShooting = false;
			
			recoil = Mathf.lerpDelta(recoil, 0f, restitution);
			heat = Mathf.lerpDelta(heat, 0f, cooldown);
			
			if(hasAmmo()) {
				if(interval.get(0, 20)) {
					findTarget();
				}
				
				if(validateTarget()) {
					targetPosition(target);
					if(Float.isNaN(rotation)){
						rotation = 0;
					}
					
					float targetRot = angleTo(targetPos);
					if (shouldTurn()) {
						turnToTarget(targetRot);
					}
					
					if (currentAmmo != null && Angles.angleDist(rotation, targetRot) < shootCone) {
						wasShooting = true;
						updateShooting();
					}
				}
			}
		}
		
		protected void shoot(BulletType type) {
			//Charging pattern
			if (chargeTime > 0) {
				useAmmo();
				
				tr.trns(rotation, shootLength);
				chargeBeginEffect.at(x + tr.x, y + tr.y, rotation);
				chargeSound.at(x + tr.x, y + tr.y, 1);
				
				for (int i = 0; i < chargeEffects; i++) {
					Time.run(Mathf.random(chargeMaxDelay), () -> {
						if (!parent.isValid()) return;
						tr.trns(rotation, shootLength);
						chargeEffect.at(x + tr.x, y + tr.y, rotation);
					});
				}
				charging = true;
				
				Time.run(chargeTime, () -> {
					if (!parent.isValid()) return;
					tr.trns(rotation, shootLength);
					recoil = recoilAmount;
					heat = 1f;
					bullet(type, rotation + Mathf.range(inaccuracy));
					effects();
					charging = false;
				});
			//Bursting pattern
			} else if (burstSpacing > 0.0001f) {
				for (int i = 0; i < shots; i++) {
					Time.run(burstSpacing * i, () -> {
						if (!hasAmmo()) return;
						recoil = recoilAmount;
						
						tr.trns(rotation, shootLength, Mathf.range(xRand));
						bullet(type, rotation + Mathf.range(inaccuracy));
						effects();
						useAmmo();
						recoil = recoilAmount;
						heat = 1f;
					});
				}
			//Normal pattern
			} else {
				if (alternate) {
					float i = (shotCounter % shots) - (shots - 1) / 2f;
					tr.trns(rotation - 90, spread * i + Mathf.range(xRand), shootLength);
					bullet(type, rotation + Mathf.range(inaccuracy));
				} else {
					tr.trns(rotation, shootLength, Mathf.range(xRand));
					for (int i = 0; i < shots; i++) {
						bullet(type, rotation + Mathf.range(inaccuracy + type.inaccuracy) + (i - (int)(shots / 2f)) * spread);
					}
				}
				shotCounter++;
				recoil = recoilAmount;
				heat = 1f;
				effects();
				useAmmo();
			}
		}
		
		public void targetPosition(Posc pos){
			if(!hasAmmo() || pos == null) return;
			BulletType bullet = peekAmmo();
			
			var offset = Tmp.v1.setZero();
			//when delay is accurate, assume unit has moved by chargeTime already
			if(accurateDelay && pos instanceof Hitboxc h){
				offset.set(h.deltaX(), h.deltaY()).scl(chargeTime / Time.delta);
			}
			targetPos.set(Predict.intercept(this, pos, offset.x, offset.y, bullet.speed <= 0.01f ? 99999999f : bullet.speed));
			
			if(targetPos.isZero()){
				targetPos.set(pos);
			}
		}
		
		protected void updateShooting(){
			reload += parent.delta() * peekAmmo().reloadMultiplier;
			
			if (reload >= reloadTime && !charging) {
				BulletType type = peekAmmo();
				shoot(type);
				reload %= reloadTime;
			}
		}
		
		protected void bullet(BulletType type, float angle){
			float lifeScl = type.scaleVelocity ? Mathf.clamp(Mathf.dst(x + tr.x, y + tr.y, targetPos.x, targetPos.y) / type.range(), minRange / type.range(), range / type.range()) : 1f;

			type.create(parent, parent.team, x + tr.x, y + tr.y, angle, 1f + Mathf.range(velocityInaccuracy), lifeScl);
		}
	
		protected void effects(){
			Effect fshootEffect = shootEffect == Fx.none ? peekAmmo().shootEffect : shootEffect;
			Effect fsmokeEffect = smokeEffect == Fx.none ? peekAmmo().smokeEffect : smokeEffect;

			fshootEffect.at(x + tr.x, y + tr.y, rotation);
			fsmokeEffect.at(x + tr.x, y + tr.y, rotation);
			shootSound.at(x + tr.x, y + tr.y, Mathf.random(0.9f, 1.1f));

			if(shootShake > 0){
				Effect.shake(shootShake, shootShake, this);
			}

			recoil = recoilAmount;
		}
		
		public void draw() {
			Draw.z(Layer.turret);
			
			tr2.trns(rotation, -recoil);
			
			Drawf.shadow(region, x + tr2.x - elevation, y + tr2.y - elevation, rotation - 90);
			
			if (drawOtline) {
				Draw.color(outlineColor);
				Draw.rect(region, x + tr2.x, y + tr2.y, (region.width + outlineRadius) * Draw.scl * Draw.xscl, (region.height + outlineRadius) * Draw.scl * Draw.yscl, rotation - 90);
			}
			Draw.color(Color.white);
			Draw.rect(region, x + tr2.x, y + tr2.y, rotation - 90);
		}
		
		//Utility methods region
		public boolean hasAmmo() {
			return currentAmmo == null ? false : ammoLeft > ammoPerShot || parent.items.has(currentAmmo.item, 1 + (int) Math.floor(ammoPerShot / currentAmmo.bullet.ammoMultiplier));
		}
		
		protected void useAmmo() {
			if (ammoLeft >= ammoPerShot) {
				ammoLeft -= ammoPerShot;
			} else {
				//in case if ammoPerShot is higher than ammo multi of the current ammo, multiple items will be consumed
				int times = 1 + (int) Math.floor(ammoPerShot / currentAmmo.bullet.ammoMultiplier);
				if (parent.items.has(currentAmmo.item, times)) {
					ammoLeft = (int) Math.round(currentAmmo.bullet.ammoMultiplier * times) + ammoLeft;
					parent.items.remove(currentAmmo.item, times);
				}
			}
		}
		
		public BulletType peekAmmo(){
			return currentAmmo.bullet;
		}
		
		public AmmoEntry possibleAmmo() {
			//this is sick but it should work
			if (interval.get(1, 15) || currentAmmo == null) {
				for (AmmoEntry entry : ammoList) {
					if (parent.items.has(entry.item)) return (currentAmmo = entry);
				}
				return (currentAmmo = null); //no possible ammo
			}
			return currentAmmo;
		}
		
		protected boolean validateTarget(){
			return !Units.invalidateTarget(target, parent.team, x, y);
		}
		
		protected void findTarget(){
			if(targetAir && !targetGround){
				target = Units.bestEnemy(parent.team, x, y, range, e -> !e.dead() && !e.isGrounded(), unitSort);
			}else{
				target = Units.bestTarget(parent.team, x, y, range, e -> !e.dead() && (e.isGrounded() || targetAir) && (!e.isGrounded() || targetGround), b -> true, unitSort);
			}
		}

		protected void turnToTarget(float targetRot){
			rotation = Angles.moveToward(rotation, targetRot, rotateSpeed * parent.delta());
		}

		public boolean shouldTurn(){
			return !charging;
		}
		
		public void set(float x, float y) {
			this.x = x; this.y = y;
		}
		
		//Utility methods region end
		
		public void write(Writes write) {
			write.f(rotation);
			write.f(reload);
		}
		
		public void read(Reads read, byte revision) {
			rotation = read.f();
			reload = read.f();
		}
		
		//I'm disgusted.
		@Override public float getX() { return x; }
		
		@Override public float getY() { return y; }
		
	}
	
	public static class AmmoEntry {
		public Item item;
		public BulletType bullet;
		
		public AmmoEntry(Item i, BulletType b) {
			item = i;
			bullet = b;
		}
	}
	
}