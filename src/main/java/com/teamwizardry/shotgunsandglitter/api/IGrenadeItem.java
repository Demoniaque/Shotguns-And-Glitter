package com.teamwizardry.shotgunsandglitter.api;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface IGrenadeItem {

	default GrenadeEffect getEffectFromStack(@NotNull ItemStack stack) {
		String effectID = ItemNBTHelper.getString(stack, "effect", "basic");
		return EffectRegistry.getGrenadeEffectByID(effectID);
	}
}
