package io.github.cottonmc.libcd;

import org.quiltmc.config.api.WrappedConfig;
import org.quiltmc.config.api.annotations.Comment;

public class CDConfig extends WrappedConfig {
	@Comment("""
		Whether dev-env files, like the test tweaker, should be loaded.
		This will affect the loaded data for your game.
		""")
	public final boolean dev_mode = false;
}
