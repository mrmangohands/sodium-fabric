package me.jellysquid.mods.sodium.mixin.features.options;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jellysquid.mods.sodium.client.SodiumClientMod;
import net.minecraft.client.render.BackgroundRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BackgroundRenderer.class)
public abstract class MixinBackgroundRenderer {
    @Redirect(method = "applyFog", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setupNvFogDistance()V"))
    private static void redirectSetupNvFogDistance() {
        if (SodiumClientMod.options().unofficial.usePlanarFog) {
            return;
        } else {
            RenderSystem.setupNvFogDistance();
        }
    }
}
