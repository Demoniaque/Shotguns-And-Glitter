package com.teamwizardry.shotgunsandglitter.common.recipes;

import com.teamwizardry.shotgunsandglitter.ShotgunsAndGlitter;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber
public class ModRecipes {

	@SubscribeEvent
	public static void register(RegistryEvent.Register<IRecipe> evt) {
		IForgeRegistry<IRecipe> r = evt.getRegistry();

		r.register(new RecipeMagazine().setRegistryName(path("magazine")));
	}

	private static ResourceLocation path(String name) {
		return new ResourceLocation(ShotgunsAndGlitter.MODID, "recipe_" + name);
	}
}
