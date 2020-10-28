package me.jellysquid.mods.sodium.mixin.core.matrix;

import me.jellysquid.mods.sodium.client.util.UnsafeUtil;
import me.jellysquid.mods.sodium.client.util.math.Matrix4fExtended;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import sun.misc.Unsafe;

import java.nio.BufferUnderflowException;
import java.nio.FloatBuffer;

@Mixin(Matrix4f.class)
public abstract class MixinMatrix4f implements Matrix4fExtended {
    @Shadow
    public abstract void set(int row, int column, float value);

    @Shadow
    public abstract float get(int row, int column);

    @Override
    public void translate(float x, float y, float z) {
        this.set(0, 3, this.get(0, 0) * x + this.get(0, 1) * y + this.get(0, 2) * z + this.get(0, 3));
        this.set(1, 3, this.get(1, 0) * x + this.get(1, 1) * y + this.get(1, 2) * z + this.get(1, 3));
        this.set(2, 3, this.get(2, 0) * x + this.get(2, 1) * y + this.get(2, 2) * z + this.get(2, 3));
        this.set(3, 3, this.get(3, 0) * x + this.get(3, 1) * y + this.get(3, 2) * z + this.get(3, 3));
    }

    @Override
    public float transformVecX(float x, float y, float z) {
        return (this.get(0, 0) * x) + (this.get(0, 1) * y) + (this.get(0, 2) * z) + (this.get(0, 3) * 1.0f);
    }

    @Override
    public float transformVecY(float x, float y, float z) {
        return (this.get(1, 0) * x) + (this.get(1, 1) * y) + (this.get(1, 2) * z) + (this.get(1, 3) * 1.0f);
    }

    @Override
    public float transformVecZ(float x, float y, float z) {
        return (this.get(2, 0) * x) + (this.get(2, 1) * y) + (this.get(2, 2) * z) + (this.get(2, 3) * 1.0f);
    }

    @Override
    public void rotate(Quaternion quaternion) {
        boolean x = quaternion.getB() != 0.0F;
        boolean y = quaternion.getC() != 0.0F;
        boolean z = quaternion.getD() != 0.0F;

        // Try to determine if this is a simple rotation on one axis component only
        if (x) {
            if (!y && !z) {
                this.rotateX(quaternion);
            } else {
                this.rotateXYZ(quaternion);
            }
        } else if (y) {
            if (!z) {
                this.rotateY(quaternion);
            } else {
                this.rotateXYZ(quaternion);
            }
        } else if (z) {
            this.rotateZ(quaternion);
        }
    }

    private void rotateX(Quaternion quaternion) {
        float x = quaternion.getB();
        float w = quaternion.getA();

        float xx = 2.0F * x * x;
        float ta11 = 1.0F - xx;
        float ta22 = 1.0F - xx;

        float xw = x * w;

        float ta21 = 2.0F * xw;
        float ta12 = 2.0F * -xw;

        float a01 = this.get(0, 1) * ta11 + this.get(0, 2) * ta21;
        float a02 = this.get(0, 1) * ta12 + this.get(0, 2) * ta22;
        float a11 = this.get(1, 1) * ta11 + this.get(1, 2) * ta21;
        float a12 = this.get(1, 1) * ta12 + this.get(1, 2) * ta22;
        float a21 = this.get(2, 1) * ta11 + this.get(2, 2) * ta21;
        float a22 = this.get(2, 1) * ta12 + this.get(2, 2) * ta22;
        float a31 = this.get(3, 1) * ta11 + this.get(3, 2) * ta21;
        float a32 = this.get(3, 1) * ta12 + this.get(3, 2) * ta22;

        this.set(0, 1, a01);
        this.set(0, 2, a02);
        this.set(1, 1, a11);
        this.set(1, 2, a12);
        this.set(2, 1, a21);
        this.set(2, 2, a22);
        this.set(3, 1, a31);
        this.set(3, 2, a32);
    }

    private void rotateY(Quaternion quaternion) {
        float y = quaternion.getC();
        float w = quaternion.getA();

        float yy = 2.0F * y * y;
        float ta00 = 1.0F - yy;
        float ta22 = 1.0F - yy;
        float yw = y * w;
        float ta20 = 2.0F * -yw;
        float ta02 = 2.0F * yw;

        float a00 = this.get(0, 0) * ta00 + this.get(0, 2) * ta20;
        float a02 = this.get(0, 0) * ta02 + this.get(0, 2) * ta22;
        float a10 = this.get(1, 0) * ta00 + this.get(1, 2) * ta20;
        float a12 = this.get(1, 0) * ta02 + this.get(1, 2) * ta22;
        float a20 = this.get(2, 0) * ta00 + this.get(2, 2) * ta20;
        float a22 = this.get(2, 0) * ta02 + this.get(2, 2) * ta22;
        float a30 = this.get(3, 0) * ta00 + this.get(3, 2) * ta20;
        float a32 = this.get(3, 0) * ta02 + this.get(3, 2) * ta22;

        this.set(0, 0, a00);
        this.set(0, 2, a02);
        this.set(1, 0, a10);
        this.set(1, 2, a12);
        this.set(2, 0, a20);
        this.set(2, 2, a22);
        this.set(3, 0, a30);
        this.set(3, 2, a32);
    }

    private void rotateZ(Quaternion quaternion) {
        float z = quaternion.getD();
        float w = quaternion.getA();

        float zz = 2.0F * z * z;
        float ta00 = 1.0F - zz;
        float ta11 = 1.0F - zz;
        float zw = z * w;
        float ta10 = 2.0F * zw;
        float ta01 = 2.0F * -zw;

        float a00 = this.get(0, 0) * ta00 + this.get(0, 1) * ta10;
        float a01 = this.get(0, 0) * ta01 + this.get(0, 1) * ta11;
        float a10 = this.get(1, 0) * ta00 + this.get(1, 1) * ta10;
        float a11 = this.get(1, 0) * ta01 + this.get(1, 1) * ta11;
        float a20 = this.get(2, 0) * ta00 + this.get(2, 1) * ta10;
        float a21 = this.get(2, 0) * ta01 + this.get(2, 1) * ta11;
        float a30 = this.get(3, 0) * ta00 + this.get(3, 1) * ta10;
        float a31 = this.get(3, 0) * ta01 + this.get(3, 1) * ta11;

        this.set(0, 0, a00);
        this.set(0, 1, a01);
        this.set(1, 0, a10);
        this.set(1, 1, a11);
        this.set(2, 0, a20);
        this.set(2, 1, a21);
        this.set(3, 0, a30);
        this.set(3, 1, a31);
    }

    private void rotateXYZ(Quaternion quaternion) {
        float x = quaternion.getB();
        float y = quaternion.getC();
        float z = quaternion.getD();
        float w = quaternion.getA();

        float xx = 2.0F * x * x;
        float yy = 2.0F * y * y;
        float zz = 2.0F * z * z;
        float ta00 = 1.0F - yy - zz;
        float ta11 = 1.0F - zz - xx;
        float ta22 = 1.0F - xx - yy;
        float xy = x * y;
        float yz = y * z;
        float zx = z * x;
        float xw = x * w;
        float yw = y * w;
        float zw = z * w;
        float ta10 = 2.0F * (xy + zw);
        float ta01 = 2.0F * (xy - zw);
        float ta20 = 2.0F * (zx - yw);
        float ta02 = 2.0F * (zx + yw);
        float ta21 = 2.0F * (yz + xw);
        float ta12 = 2.0F * (yz - xw);

        float a00 = this.get(0, 0) * ta00 + this.get(0, 1) * ta10 + this.get(0, 2) * ta20;
        float a01 = this.get(0, 0) * ta01 + this.get(0, 1) * ta11 + this.get(0, 2) * ta21;
        float a02 = this.get(0, 0) * ta02 + this.get(0, 1) * ta12 + this.get(0, 2) * ta22;
        float a10 = this.get(1, 0) * ta00 + this.get(1, 1) * ta10 + this.get(1, 2) * ta20;
        float a11 = this.get(1, 0) * ta01 + this.get(1, 1) * ta11 + this.get(1, 2) * ta21;
        float a12 = this.get(1, 0) * ta02 + this.get(1, 1) * ta12 + this.get(1, 2) * ta22;
        float a20 = this.get(2, 0) * ta00 + this.get(2, 1) * ta10 + this.get(2, 2) * ta20;
        float a21 = this.get(2, 0) * ta01 + this.get(2, 1) * ta11 + this.get(2, 2) * ta21;
        float a22 = this.get(2, 0) * ta02 + this.get(2, 1) * ta12 + this.get(2, 2) * ta22;
        float a30 = this.get(3, 0) * ta00 + this.get(3, 1) * ta10 + this.get(3, 2) * ta20;
        float a31 = this.get(3, 0) * ta01 + this.get(3, 1) * ta11 + this.get(3, 2) * ta21;
        float a32 = this.get(3, 0) * ta02 + this.get(3, 1) * ta12 + this.get(3, 2) * ta22;

        this.set(0, 0, a00);
        this.set(0, 1, a01);
        this.set(0, 2, a02);
        this.set(1, 0, a10);
        this.set(1, 1, a11);
        this.set(1, 2, a12);
        this.set(2, 0, a20);
        this.set(2, 1, a21);
        this.set(2, 2, a22);
        this.set(3, 0, a30);
        this.set(3, 1, a31);
        this.set(3, 2, a32);
    }

    /**
     * @reason Optimize
     * @author JellySquid
     */
    @Environment(EnvType.CLIENT)
    @Overwrite
    public void writeToBuffer(FloatBuffer buf) {
        if (buf.remaining() < 16) {
            throw new BufferUnderflowException();
        }

        if (UnsafeUtil.isAvailable()) {
            this.writeToBufferUnsafe(buf);
        } else {
            this.writeToBufferSafe(buf);
        }
    }

    private void writeToBufferUnsafe(FloatBuffer buf) {
        long addr = MemoryUtil.memAddress(buf);

        Unsafe unsafe = UnsafeUtil.instance();
        unsafe.putFloat(addr + 0, this.get(0, 0));
        unsafe.putFloat(addr + 4, this.get(1, 0));
        unsafe.putFloat(addr + 8, this.get(2, 0));
        unsafe.putFloat(addr + 12, this.get(3, 0));
        unsafe.putFloat(addr + 16, this.get(0, 1));
        unsafe.putFloat(addr + 20, this.get(1, 1));
        unsafe.putFloat(addr + 24, this.get(2, 1));
        unsafe.putFloat(addr + 28, this.get(3, 1));
        unsafe.putFloat(addr + 32, this.get(0, 2));
        unsafe.putFloat(addr + 36, this.get(1, 2));
        unsafe.putFloat(addr + 40, this.get(2, 2));
        unsafe.putFloat(addr + 44, this.get(3, 2));
        unsafe.putFloat(addr + 48, this.get(0, 3));
        unsafe.putFloat(addr + 52, this.get(1, 3));
        unsafe.putFloat(addr + 56, this.get(2, 3));
        unsafe.putFloat(addr + 60, this.get(3, 3));
    }

    private void writeToBufferSafe(FloatBuffer buf) {
        buf.put(0, this.get(0, 0));
        buf.put(1, this.get(1, 0));
        buf.put(2, this.get(2, 0));
        buf.put(3, this.get(3, 0));
        buf.put(4, this.get(0, 1));
        buf.put(5, this.get(1, 1));
        buf.put(6, this.get(2, 1));
        buf.put(7, this.get(3, 1));
        buf.put(8, this.get(0, 2));
        buf.put(9, this.get(1, 2));
        buf.put(10, this.get(2, 2));
        buf.put(11, this.get(3, 2));
        buf.put(12, this.get(0, 3));
        buf.put(13, this.get(1, 3));
        buf.put(14, this.get(2, 3));
        buf.put(15, this.get(3, 3));
    }
}
