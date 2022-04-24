package io.github.cottonmc.libcd;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.quiltmc.loader.api.QuiltLoader;

import io.github.cottonmc.libcd.api.CDCommons;
import io.github.cottonmc.libcd.api.CDSyntaxError;
import io.github.cottonmc.libcd.api.LibCDInitializer;
import io.github.cottonmc.libcd.api.advancement.AdvancementRewardsManager;
import io.github.cottonmc.libcd.api.condition.ConditionManager;
import io.github.cottonmc.libcd.api.condition.ConditionalData;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.Map;

public class CDContent implements LibCDInitializer {

	@Override
	public void initConditions(ConditionManager manager) {
		manager.registerCondition(new Identifier(CDCommons.MODID, "mod_loaded"), value -> {
			if (value instanceof String stringValue) return QuiltLoader.isModLoaded(stringValue);
			if (value instanceof List) {
				//noinspection unchecked
				for (JsonElement el : (List<JsonElement>)value) {
					if (!(el instanceof JsonPrimitive)) return false;
					String name = el.getAsString();
					if (!QuiltLoader.isModLoaded(name)) return false;
				}
				return true;
			}
			throw new CDSyntaxError("mod_loaded must accept either a String or an Array!");
		});
		manager.registerCondition(new Identifier(CDCommons.MODID, "item_exists"), value -> {
			if (value instanceof String stringValue) return Registry.ITEM.get(new Identifier(stringValue)) != Items.AIR;
			if (value instanceof List) {
				//noinspection unchecked
				for (JsonElement el : (List<JsonElement>)value) {
					if (!(el instanceof JsonPrimitive)) throw new CDSyntaxError("item_exists array must only contain Strings!");
					String name = el.getAsString();
					if (Registry.ITEM.get(new Identifier(name)) == Items.AIR) return false;
				}
				return true;
			}
			throw new CDSyntaxError("item_exists must accept either a String or an Array!");
		});
		manager.registerCondition(new Identifier(CDCommons.MODID, "item_tag_exists"), value -> {
			if (value instanceof String stringValue) return Registry.ITEM.isKnownTag(TagKey.of(Registry.ITEM_KEY, new Identifier(stringValue)));
			if (value instanceof List) {
				//noinspection unchecked
				for (JsonElement el : (List<JsonElement>)value) {
					if (!(el instanceof JsonPrimitive)) throw new CDSyntaxError("item_tag_exists array must only contain Strings!");
					String name = el.getAsString();
					Identifier id = new Identifier(name);
					if (!Registry.ITEM.isKnownTag(TagKey.of(Registry.ITEM_KEY, id))) return false;
				}
				return true;
			}
			throw new CDSyntaxError("item_tag_exists must accept either a String or an Array!");
		});
		manager.registerCondition(new Identifier(CDCommons.MODID, "block_exists"), value -> {
			if (value instanceof String stringValue) return Registry.BLOCK.get(new Identifier(stringValue)) != Blocks.AIR;
			if (value instanceof List) {
				//noinspection unchecked
				for (JsonElement el : (List<JsonElement>)value) {
					if (!(el instanceof JsonPrimitive)) throw new CDSyntaxError("block_exists array must only contain Strings!");
					String name = el.getAsString();
					if (Registry.BLOCK.get(new Identifier(name)) == Blocks.AIR) return false;
				}
				return true;
			}
			throw new CDSyntaxError("block_exists must accept either a String or an Array!");
		});
		manager.registerCondition(new Identifier(CDCommons.MODID, "block_tag_exists"), value -> {
			if (value instanceof String stringValue) return Registry.BLOCK.isKnownTag(TagKey.of(Registry.BLOCK_KEY, new Identifier(stringValue)));
			if (value instanceof List) {
				//noinspection unchecked
				for (JsonElement el : (List<JsonElement>)value) {
					if (!(el instanceof JsonPrimitive)) throw new CDSyntaxError("block_tag_exists array must only contain Strings!");
					String name = (el).getAsString();
					Identifier id = new Identifier(name);
					if (!Registry.BLOCK.isKnownTag(TagKey.of(Registry.BLOCK_KEY, id))) return false;
					Registry.BLOCK.getTag(TagKey.of(Registry.BLOCK_KEY, id)).isEmpty();
				}
				return true;
			}
			throw new CDSyntaxError("block_tag_exists must accept either a String or an Array!");
		});
		manager.registerCondition(new Identifier(CDCommons.MODID, "not"), value -> {
			if (value instanceof JsonObject json) {
				for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
					String key = entry.getKey();
					Identifier id = new Identifier(key);
					Object result = ConditionalData.parseElement(json.get(key));
					if (ConditionalData.hasCondition(id)) {
						return !ConditionalData.testCondition(id, result);
					} else return false;
				}
			}
			throw new CDSyntaxError("not must accept an Object!");
		});
		manager.registerCondition(new Identifier(CDCommons.MODID, "none"), value -> {
			if (value instanceof JsonArray json) {
				for (JsonElement elem : json) {
					if (elem instanceof JsonObject) {
						JsonObject obj = (JsonObject) elem;
						for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
							String key = entry.getKey();
							if (ConditionalData.testCondition(new Identifier(key), ConditionalData.parseElement(obj.get(key)))) return false;
						}
					}
				}
				return true;
			}
			throw new CDSyntaxError("none must accept an Array!");
		});
		manager.registerCondition(new Identifier(CDCommons.MODID, "or"), value -> {
			if (value instanceof JsonArray json) {
				for (JsonElement elem : json) {
					if (elem instanceof JsonObject) {
						JsonObject obj = (JsonObject) elem;
						for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
							String key = entry.getKey();
							if (ConditionalData.testCondition(new Identifier(key), ConditionalData.parseElement(obj.get(key)))) return true;
						}
					}
				}
			}
			throw new CDSyntaxError("or must accept an Array!");
		});
		manager.registerCondition(new Identifier(CDCommons.MODID, "xor"), value -> {
			if (value instanceof JsonArray json) {
				boolean ret = false;
				for (JsonElement elem : json) {
					if (elem instanceof JsonObject obj) {
						for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
							String key = entry.getKey();
							if (ConditionalData.testCondition(new Identifier(key), ConditionalData.parseElement(obj.get(key)))) {
								if(ret) return false;
								else ret = true;
							}
						}
					}
				}
				return ret;
			}
			throw new CDSyntaxError("xor must accept an Array!");
		});
		manager.registerCondition(new Identifier(CDCommons.MODID, "dev_mode"), value -> {
			if (value instanceof Boolean booleanValue) return booleanValue == LibCD.isDevMode();
			throw new CDSyntaxError("dev_mode must accept a Boolean!");
		});
	}

	@Override
	public void initAdvancementRewards(AdvancementRewardsManager manager) {
		//no-op
	}
}
