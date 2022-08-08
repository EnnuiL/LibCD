package io.github.cottonmc.libcd;

import java.util.List;

import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.loader.api.config.QuiltConfig;

@SuppressWarnings("unchecked")
public class CDConfigManager {
	public static final CDConfig CONFIG = QuiltConfig.create("libcd", "config", CDConfig.class);

	public static final TrackedValue<Boolean> DEV_MODE = (TrackedValue<Boolean>) CONFIG.getValue(List.of("dev_mode"));

	public CDConfigManager() {};
}
