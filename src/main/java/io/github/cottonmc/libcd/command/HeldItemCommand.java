package io.github.cottonmc.libcd.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.MessageType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class HeldItemCommand implements Command<ServerCommandSource> {

	@Override
	public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayer();
		ItemStack toDescribe = player.getMainHandStack();
		StringBuilder description = new StringBuilder();
		Identifier id = Registry.ITEM.getId(toDescribe.getItem());
		String idString = (id==null) ? "unknown" : id.toString();
		description.append(idString);
		
		MutableText feedback = new LiteralText(idString);
		
		CompoundTag tag = toDescribe.getTag();
		if (tag!=null) {
			description.append(tag.asString());
			feedback.append(tag.toText());
		}
		
		Style clickableStyle = Style.EMPTY
			.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Click to copy to clipboard")))
			.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, description.toString()));
		
		feedback.setStyle(clickableStyle);
		player.sendMessage(feedback, false);
		
		return 1;
	}

}
