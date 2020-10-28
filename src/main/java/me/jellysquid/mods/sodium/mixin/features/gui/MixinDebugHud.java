package me.jellysquid.mods.sodium.mixin.features.gui;

import com.google.common.base.Strings;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.render.*;
import org.apache.commons.lang3.Validate;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DebugHud.class)
public abstract class MixinDebugHud {
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    @Final
    private TextRenderer fontRenderer;

    private List<String> capturedList = null;

    @Redirect(method = { "renderLeftText", "renderRightText" }, at = @At(value = "INVOKE", target = "Ljava/util/List;size()I"))
    private int preRenderText(List<String> list) {
        // Capture the list to be rendered later
        this.capturedList = list;

        return 0; // Prevent the rendering of any text
    }

    @Inject(method = "renderLeftText", at = @At("RETURN"))
    public void renderLeftText(CallbackInfo ci) {
        this.renderCapturedText(false);
    }

    @Inject(method = "renderRightText", at = @At("RETURN"))
    public void renderRightText(CallbackInfo ci) {
        this.renderCapturedText(true);
    }

    private void renderCapturedText(boolean right) {
        Validate.notNull(this.capturedList, "Failed to capture string list");

        this.renderBackdrop(this.capturedList, right);
        this.renderStrings(this.capturedList, right);

        this.capturedList = null;
    }

    private void renderStrings(List<String> list, boolean right) {
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());

        for (int i = 0; i < list.size(); ++i) {
            String string = list.get(i);

            if (!Strings.isNullOrEmpty(string)) {
                int height = 9;
                int width = this.fontRenderer.getStringWidth(string);

                float x1 = right ? this.client.getWindow().getScaledWidth() - 2 - width : 2;
                float y1 = 2 + (height * i);

                this.fontRenderer.draw(string, x1, y1, 0xe0e0e0);
            }
        }

        immediate.draw();
    }

    private void renderBackdrop(List<String> list, boolean right) {
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();

        int color = 0x90505050;

        float f = (float) (color >> 24 & 255) / 255.0F;
        float g = (float) (color >> 16 & 255) / 255.0F;
        float h = (float) (color >> 8 & 255) / 255.0F;
        float k = (float) (color & 255) / 255.0F;

        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);

        for (int i = 0; i < list.size(); ++i) {
            String string = list.get(i);

            if (Strings.isNullOrEmpty(string)) {
                continue;
            }

            int height = 9;
            int width = this.fontRenderer.getStringWidth(string);

            int x = right ? this.client.getWindow().getScaledWidth() - 2 - width : 2;
            int y = 2 + height * i;

            float x1 = x - 1;
            float y1 = y - 1;
            float x2 = x + width + 1;
            float y2 = y + height - 1;

            bufferBuilder.vertex(x1, y2, 0.0F).color(g, h, k, f).next();
            bufferBuilder.vertex(x2, y2, 0.0F).color(g, h, k, f).next();
            bufferBuilder.vertex(x2, y1, 0.0F).color(g, h, k, f).next();
            bufferBuilder.vertex(x1, y1, 0.0F).color(g, h, k, f).next();
        }

        bufferBuilder.end();

        BufferRenderer.draw(bufferBuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
}
