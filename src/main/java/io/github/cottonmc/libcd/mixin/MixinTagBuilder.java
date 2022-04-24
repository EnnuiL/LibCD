package io.github.cottonmc.libcd.mixin;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;

import io.github.cottonmc.libcd.impl.TagBuilderWarningAccessor;
import io.github.cottonmc.libcd.loader.TagExtensions;
import io.github.cottonmc.libcd.tag.ItemTagHelper;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Holder;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

@Mixin(Tag.Builder.class)
public class MixinTagBuilder implements TagBuilderWarningAccessor {

    @Shadow @Final private List<Tag.TrackedEntry> entries;
    @Unique
    private final List<Object> libcdWarnings = new ArrayList<>();

    private Identifier defaultEntry;

    @Inject(method = "read", at = @At(value = "RETURN", remap = false))
    private void onFromJson(JsonObject json, String string, CallbackInfoReturnable<Tag.Builder> info) {
        try {
            if (json.has("libcd")) {
                TagExtensions.ExtensionResult result = TagExtensions.load(JsonHelper.getObject(json, "libcd"));

                if (result.shouldReplace()) {
                    entries.clear();
                }

                result.getEntries().forEach((entry) -> this.entries.add(new Tag.TrackedEntry(entry, string)));

                libcdWarnings.addAll(result.getWarnings());
                defaultEntry = result.getDefaultEntry();
            }
        } catch (Exception e) {
            libcdWarnings.add(e);
        }
    }

    @Override
    public List<Object> libcd$getWarnings() {
        return libcdWarnings;
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "build", at = @At("RETURN"))
    private <T> void injectDefaultEntry(Function<Identifier, Tag<T>> tagGetter, Function<Identifier, T> objectGetter, CallbackInfoReturnable<Either<Collection<Tag.TrackedEntry>, Tag<T>>> info) {
        Either<Collection<Tag.TrackedEntry>, Tag<T>> opt = info.getReturnValue();
        if (opt.right().isPresent()) {
            Tag<T> tag = opt.right().get();
            T t = objectGetter.apply(defaultEntry);
            if (t instanceof Holder<?> itemHolder) {
                ItemTagHelper.INSTANCE.add((Tag<Holder<Item>>)tag, (Item)itemHolder.value());
            }
        }
    }
}
