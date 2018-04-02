package com.teamwizardry.shotgunsandglitter.common.entity;

import com.teamwizardry.shotgunsandglitter.ShotgunsAndGlitter;
import com.teamwizardry.shotgunsandglitter.api.EffectRegistry;
import com.teamwizardry.shotgunsandglitter.api.GrenadeEffect;
import com.teamwizardry.shotgunsandglitter.api.IGrenadeEntity;
import com.teamwizardry.shotgunsandglitter.api.util.RandUtil;
import com.teamwizardry.shotgunsandglitter.common.core.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class EntityGrenade extends EntityThrowable implements IGrenadeEntity {

	private static final DataParameter<String> GRENADE_EFFECT = EntityDataManager.createKey(EntityGrenade.class, DataSerializers.STRING);
	private static final DataParameter<Integer> CASTER_ID = EntityDataManager.createKey(EntityGrenade.class, DataSerializers.VARINT);

	public EntityGrenade(@NotNull World world) {
		super(world);
		setSize(0.1F, 0.1F);
	}

	public EntityGrenade(@NotNull World world, Vec3d normal, GrenadeEffect effect) {
		super(world);
		setSize(0.1F, 0.1F);

		setEffect(effect);
		setCasterId(-1);

		shoot(normal.x, normal.y, normal.z, 1f, 1f);
	}

	public EntityGrenade(@NotNull World world, @NotNull EntityLivingBase caster, GrenadeEffect effect) {
		super(world, caster);
		setSize(0.1F, 0.1F);

		setEffect(effect);
		setCasterId(caster.getEntityId());

		shoot(caster, caster.rotationPitch, caster.rotationYaw, 0f, 1f, 1f);
	}

	@Override
	protected void entityInit() {
		dataManager.register(GRENADE_EFFECT, "");
		dataManager.register(CASTER_ID, -1);
	}

	@Override
	protected float getGravityVelocity() {
		return 0.03f;
	}

	@Override
	protected void onImpact(@NotNull RayTraceResult result) {
		if (world.isRemote) return;

		if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
			IBlockState state = world.getBlockState(result.getBlockPos());
			if (state.getCollisionBoundingBox(world, result.getBlockPos()) != Block.NULL_AABB &&
					state.getBlock().canCollideCheck(state, false))
				die();
		} else if (result.typeOfHit == RayTraceResult.Type.ENTITY && result.entityHit != null)
			die();
	}

	public void die() {
		ShotgunsAndGlitter.PROXY.grenadeImpact(world, this, getEffect(), getPositionVector());
		setDead();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (isDead) return;

		if (ticksExisted >= 50) {
			if (!world.isRemote)
				die();
		} else {
			if (world.isRemote)
				world.playSound(posX, posY, posZ, ModSounds.DUST_SPARKLE, SoundCategory.PLAYERS, RandUtil.nextFloat(0.1f, 0.3f), RandUtil.nextFloat(0.95f, 1.1f), false);

			 ShotgunsAndGlitter.PROXY.grenadeUpdate(world, this, getEffect());
		}
	}

	@Override
	public boolean isPushedByWater() {
		return false;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setString("grenadeEffect", dataManager.get(GRENADE_EFFECT));
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		dataManager.set(GRENADE_EFFECT, compound.getString("grenadeEffect"));
	}

	@NotNull
	public GrenadeEffect getEffect() {
		return EffectRegistry.getGrenadeEffectByID(dataManager.get(GRENADE_EFFECT));
	}

	@Override
	public void setEffect(@NotNull GrenadeEffect effect) {
		dataManager.set(GRENADE_EFFECT, effect.getID());
	}

	@Override
	public int getCasterId() {
		return dataManager.get(CASTER_ID);
	}

	@Override
	public void setCasterId(int casterId) {
		dataManager.set(CASTER_ID, casterId);
	}

	@Override
	public @NotNull Entity getAsEntity() {
		return this;
	}

	@Override
	public double posX() {
		return posX;
	}

	@Override
	public double posY() {
		return posY;
	}

	@Override
	public double posZ() {
		return posZ;
	}

	@Override
	public double motionX() {
		return motionX;
	}

	@Override
	public double motionY() {
		return motionY;
	}

	@Override
	public double motionZ() {
		return motionZ;
	}
}
