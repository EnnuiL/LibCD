package io.github.cottonmc.libcd.mixin;

import com.google.gson.JsonObject;
import io.github.cottonmc.libcd.api.CDCommons;
import io.github.cottonmc.libcd.api.condition.ConditionalData;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.pack.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

@Mixin(NamespaceResourceManager.class)
public abstract class NamespaceResourceManagerMixin implements ResourceManager {
	@Inject(method = "findResources", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void checkConditionalRecipes(String startingPath, Predicate<Identifier> pathFilter, CallbackInfoReturnable<Map<Identifier, Resource>> cir,
										 Map<Identifier, ?> map, Map<Identifier, ?> map2, int i,
										 Map<Identifier, Resource> map3) {
		Set<Identifier> clonedMapKeys = Set.copyOf(map3.keySet());
		for (var id : clonedMapKeys) {
			//don't try to load for things that use mcmetas already!
			if (id.getPath().contains(".mcmeta") || id.getPath().contains(".png")) continue;
			try {
				Optional<Boolean> shouldLoad = map3.get(id).getMetadata().readMetadata(new ResourceMetadataReader<>() {
					@Override
					public String getKey() {
						return "libcd";
					}

					@Override
					public Boolean fromJson(JsonObject json) {
						return ConditionalData.shouldLoad(id, json);
					}
				});

				if (shouldLoad.isPresent() && !shouldLoad.get()) {
					map3.remove(id);
				}
			} catch (IOException e) {
				CDCommons.logger.error("Error when accessing resource metadata for {}: {}", id.toString(), e.getMessage());
			}
		}
	}
}
