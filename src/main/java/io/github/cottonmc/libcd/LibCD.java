package io.github.cottonmc.libcd;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

import io.github.cottonmc.libcd.api.LibCDInitializer;
import io.github.cottonmc.libcd.api.advancement.AdvancementRewardsManager;
import io.github.cottonmc.libcd.api.condition.ConditionManager;
import io.github.cottonmc.libcd.api.init.AdvancementInitializer;
import io.github.cottonmc.libcd.api.init.ConditionInitializer;

public class LibCD implements ModInitializer {
	public static final String MODID = "libcd";

	public static boolean isDevMode() {
		return CDConfigManager.DEV_MODE.value();
	}

	@Override
	public void onInitialize(ModContainer mod) {
		QuiltLoader.getEntrypoints(MODID + ":conditions", ConditionInitializer.class).forEach(init -> init.initConditions(ConditionManager.INSTANCE));
		QuiltLoader.getEntrypoints(MODID + ":advancement_rewards", AdvancementInitializer.class).forEach(init -> init.initAdvancementRewards(AdvancementRewardsManager.INSTANCE));
		QuiltLoader.getEntrypoints(MODID, LibCDInitializer.class).forEach(init -> {
			init.initConditions(ConditionManager.INSTANCE);
			init.initAdvancementRewards(AdvancementRewardsManager.INSTANCE);
		});
	}
}
