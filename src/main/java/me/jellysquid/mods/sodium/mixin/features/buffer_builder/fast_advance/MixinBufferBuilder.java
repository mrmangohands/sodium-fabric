package me.jellysquid.mods.sodium.mixin.features.buffer_builder.fast_advance;

import net.minecraft.class_4584;
import net.minecraft.class_4585;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BufferBuilder.class)
public abstract class MixinBufferBuilder extends class_4585 implements class_4584 {
    @Shadow
    private VertexFormat format;

    @Shadow
    private VertexFormatElement currentElement;

    @Shadow
    private int field_20884;

    @Shadow
    private int currentElementId;

    /**
     * @author JellySquid
     * @reason Remove modulo operations and recursion
     */
    @Overwrite
    public void nextElement() {
        do {
            ++this.currentElementId;
            this.field_20884 += this.currentElement.getSize();

            // Wrap around the element pointer without using modulo
            if (this.currentElementId >= this.format.getElementCount()) {
                this.currentElementId -= this.format.getElementCount();
            }

            this.currentElement = this.format.getElement(this.currentElementId);
        } while (this.currentElement.getType() == VertexFormatElement.Type.PADDING);

        if (this.field_20889 && this.currentElement.getType() == VertexFormatElement.Type.COLOR) {
            class_4584.super.color(this.field_20890, this.field_20891, this.field_20892, this.field_20893);
        }

       if (this.field_20894 && this.currentElement.getType() == VertexFormatElement.Type.UV && this.currentElement.getIndex() == 1) {
          class_4584.super.method_22917(this.field_20895, this.field_20896);
       }
    }
}
