package me.jellysquid.mods.sodium.mixin.core.vector;

import me.jellysquid.mods.sodium.client.util.math.Vector4fExtended;
import net.minecraft.client.util.math.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Vector4f.class)
public class MixinVector4f implements Vector4fExtended {

    @Shadow
    @Final
    private float[] components;

    @Override
    public float getW() {
        return this.components[3];
    }
}
