package com.teamwizardry.shotgunsandglitter.common.effects;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.shotgunsandglitter.api.Effect;
import com.teamwizardry.shotgunsandglitter.api.util.RandUtil;
import com.teamwizardry.shotgunsandglitter.client.ClientEventHandler;
import com.teamwizardry.shotgunsandglitter.common.entity.EntityBullet;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class EffectGraviton implements Effect {

	private final int direction;
	private final String idSuffix;

	public EffectGraviton(boolean outwards) {
		direction = outwards ? -1 : 1;
		idSuffix = outwards ? "out" : "in";
	}

	@Override
	public String getID() {
		return "gravity_" + idSuffix;
	}

	// The visual effect of the Graviton should be random particles rushing into the central point.

	@Override
	public void onImpact(@NotNull World world, @NotNull EntityBullet bullet, @NotNull RayTraceResult hit) {
		for (EntityLivingBase target : world.getEntitiesWithinAABB(EntityLivingBase.class,
				new AxisAlignedBB(bullet.posX - 10, bullet.posY - 10, bullet.posZ - 10,
						bullet.posX + 10, bullet.posY + 10, bullet.posZ + 10),
				(entity) -> {
					if (entity == null || !entity.isEntityAlive()) return false;
					Vec3d differenceVec = entity.getPositionVector().subtract(bullet.getPositionVector());
					return differenceVec.lengthSquared() < 7.5 * 7.5;
				})) {
			Vec3d differenceVec = bullet.getPositionVector().subtract(target.getPositionVector());
			differenceVec = differenceVec.scale(direction / differenceVec.lengthSquared());
			target.motionX = differenceVec.x * 2f;
			target.motionY = Math.max(differenceVec.y, 0) * 2f + 0.5f;
			target.motionZ = differenceVec.z * 2f;
			target.velocityChanged = true;
		}
	}

	@Override
	public void renderImpact(@NotNull World world, @NotNull EntityBullet bullet, @NotNull RayTraceResult hit) {
		Color color, color2;
		if (direction == 1) {
			color = Color.CYAN;
			color2 = Color.BLUE;
		} else {
			color = Color.RED;
			color2 = Color.ORANGE;
		}

		ParticleBuilder glitter = new ParticleBuilder(10);
		glitter.setRender(ClientEventHandler.SPARKLE);
		glitter.setCollision(true);
		glitter.setAcceleration(new Vec3d(0, RandUtil.nextDouble(-0.03, -0.04), 0));
		glitter.setCanBounce(true);
		glitter.enableMotionCalculation();
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(bullet.getPositionVector()), 100, 0, (i, build) -> {
			build.setMotion(Vec3d.ZERO);
			build.setLifetime(RandUtil.nextInt(50, 100));
			build.setScale(RandUtil.nextFloat(0.2f, 1));

			if (direction == 1) {
				build.setAlphaFunction(new InterpFadeInOut(RandUtil.nextFloat(0.7f, 1f), RandUtil.nextFloat(0.5f, 1f)));
				double radius = RandUtil.nextDouble(5, 10);
				double theta = 2.0f * (float) Math.PI * RandUtil.nextFloat();
				double r = radius * RandUtil.nextFloat();
				double x = r * MathHelper.cos((float) theta);
				double z = r * MathHelper.sin((float) theta);
				build.setPositionOffset(new Vec3d(x, RandUtil.nextDouble(-radius, radius), z));
				build.addMotion(build.getPositionOffset().scale(-1.0 / RandUtil.nextDouble(10, 30)));
			} else {
				build.setAlphaFunction(new InterpFadeInOut(0, RandUtil.nextFloat(0.5f, 1f)));
				double radius = RandUtil.nextDouble(0.2, 1);
				double theta = 2.0f * (float) Math.PI * RandUtil.nextFloat();
				double r = radius * RandUtil.nextFloat();
				double x = r * MathHelper.cos((float) theta);
				double z = r * MathHelper.sin((float) theta);
				build.addMotion(new Vec3d(x, RandUtil.nextDouble(-radius, radius), z));
			}
			if (RandUtil.nextBoolean()) build.setColor(color);
			else build.setColor(color2);
		});
	}

	@Override
	public void renderUpdate(@NotNull World world, @NotNull EntityBullet bullet) {
		Color color, color2;
		if (direction == 1) {
			color = Color.CYAN;
			color2 = Color.BLUE;
		} else {
			color = Color.RED;
			color2 = Color.ORANGE;
		}

		ParticleBuilder glitter = new ParticleBuilder(10);
		glitter.setRender(ClientEventHandler.SPARKLE);
		glitter.setCollision(true);
		glitter.setCanBounce(true);
		glitter.disableMotionCalculation();
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(bullet.getPositionVector()), 10, 0, (i, build) -> {
			build.setMotion(Vec3d.ZERO);
			build.setLifetime(RandUtil.nextInt(10, 20));
			build.setScale(RandUtil.nextFloat(0.2f, 1));

			if (direction == 1) {
				build.setAlphaFunction(new InterpFadeInOut(RandUtil.nextFloat(0.7f, 1f), RandUtil.nextFloat(0.5f, 1f)));
				double radius = RandUtil.nextDouble(0.3, 1);
				double theta = 2.0f * (float) Math.PI * RandUtil.nextFloat();
				double r = radius * RandUtil.nextFloat();
				double x = r * MathHelper.cos((float) theta);
				double z = r * MathHelper.sin((float) theta);
				build.setPositionOffset(new Vec3d(x, RandUtil.nextDouble(-radius, radius), z));
				build.addMotion(build.getPositionOffset().scale(-1.0 / RandUtil.nextDouble(5, 10)));
			} else {
				build.setAlphaFunction(new InterpFadeInOut(0, RandUtil.nextFloat(0.5f, 1f)));
				double radius = RandUtil.nextDouble(0.1, 0.2);
				double theta = 2.0f * (float) Math.PI * RandUtil.nextFloat();
				double r = radius * RandUtil.nextFloat();
				double x = r * MathHelper.cos((float) theta);
				double z = r * MathHelper.sin((float) theta);
				build.addMotion(new Vec3d(x, RandUtil.nextDouble(-radius, radius), z));
			}
			if (RandUtil.nextBoolean()) build.setColor(color);
			else build.setColor(color2);
		});
	}
}