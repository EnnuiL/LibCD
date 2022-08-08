package io.github.cottonmc.libcd.mixin;

import io.github.cottonmc.libcd.api.CDCommons;
import io.github.cottonmc.libcd.api.condition.ConditionalData;
import io.github.cottonmc.libcd.impl.ResourceSearcher;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.pack.ResourcePack;
import net.minecraft.util.Identifier;

import org.apache.commons.io.IOUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.google.common.base.Charsets;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

@Mixin(NamespaceResourceManager.class)
public abstract class NamespaceResourceManagerMixin implements ResourceManager, ResourceSearcher {
	@Shadow @Final protected List<NamespaceResourceManager.PackEntry> packs;

	@Shadow @Final private ResourceType type;

	@Shadow protected abstract boolean isPathAbsolute(Identifier id);

	public boolean libcd$contains(Identifier id) {
		if (!this.isPathAbsolute(id)) {
			return false;
		} else {
			for(int i = this.packs.size() - 1; i >= 0; --i) {
				ResourcePack pack = this.packs.get(i).pack();
				if (pack.contains(this.type, id)) {
					return true;
				}
			}

			return false;
		}
	}

	@Inject(method = "findResources", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void checkConditionalRecipes(String startingPath, Predicate<Identifier> pathFilter, CallbackInfoReturnable<Map<Identifier, Resource>> cir,
										Object2IntMap<Identifier> object2IntMap, int i, Map<Identifier, Resource> map) {
		Set<Identifier> clonedMapKeys = Set.copyOf(map.keySet());
		for (Identifier id : clonedMapKeys) {
			//don't try to load for things that use mcmetas already!
			if (id.getPath().contains(".mcmeta") || id.getPath().contains(".png")) continue;
			Identifier metaId = new Identifier(id.getNamespace(), id.getPath() + ".mcmeta");
			if (libcd$contains(metaId)) {
				try {
					Resource meta = getResourceOrThrow(metaId);
					String metaText = IOUtils.toString(meta.open(), Charsets.UTF_8);
					if (!ConditionalData.shouldLoad(id, metaText)) {
						map.remove(id);
					}
				} catch (IOException e) {
					CDCommons.logger.error("Error when accessing resource metadata for %s: %s", id.toString(), e.getMessage());
				}
			}
		}
	}
}
