package io.github.cottonmc.libcd.mixin;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import io.github.cottonmc.libcd.api.tag.TagHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

import org.quiltmc.qsl.tag.api.TagRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShapedRecipe.class)
public class MixinShapedRecipe {
	//TODO: nbt crafting support?
	@Inject(method = "getItemStack", at = @At("HEAD"), cancellable = true)
	private static void loadResource(JsonObject json, CallbackInfoReturnable<ItemStack> info) {
		if (json.has("tag")) {
			String tagName = JsonHelper.getString(json, "tag");
			Identifier id = new Identifier(tagName);
			TagKey<Item> itemTag = TagKey.of(Registry.ITEM_KEY, id);
			if (!Registry.ITEM.isKnownTag(itemTag)) {
				throw new JsonSyntaxException("Unknown tag " + tagName);
			}
			Item item = TagHelper.ITEM.getDefaultEntry(TagRegistry.getTag(itemTag));
			if (item == Items.AIR) {
				throw new JsonSyntaxException("No items in tag " + tagName);
			}
			int count = JsonHelper.getInt(json, "count", 1);
			ItemStack stack = new ItemStack(item, count);
			if (json.has("data")) {
				JsonObject data = JsonHelper.getObject(json, "data");
				NbtElement tag = Dynamic.convert(JsonOps.INSTANCE, NbtOps.INSTANCE, data);
				if (tag instanceof NbtCompound compound) {
					stack.setNbt(compound);
				}
			}
			info.setReturnValue(stack);
		}
	}
}
