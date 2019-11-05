package io.github.cottonmc.libcd.mixin;

import io.github.cottonmc.libcd.impl.CookingRecipeFactoryInvoker;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.minecraft.recipe.CookingRecipeSerializer$RecipeFactory")
public abstract class MixinCookingRecipeFactory<T extends AbstractCookingRecipe> implements CookingRecipeFactoryInvoker {
	@Shadow public abstract T create(Identifier identifier, String s, Ingredient ingredient, ItemStack itemStack, float v, int i);

	public T libcd_create(Identifier id, String group, Ingredient input, ItemStack output, float experience, int cookingTime) {
		return create(id, group, input, output, experience, cookingTime);
	}
}
