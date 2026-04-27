package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.nbt.TagParser;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[16, 26)")
@Extension
public class ABI {

    public static CompoundTag parseCompoundFully(@ThisClass Class<?> clazz, String contents) throws CommandSyntaxException {
        return TagParser.parseTag(contents);
    }
}
