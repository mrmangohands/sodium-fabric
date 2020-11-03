package me.jellysquid.mods.sodium.mixin.core.matrix;

import me.jellysquid.mods.sodium.client.util.UnsafeUtil;
import me.jellysquid.mods.sodium.client.util.math.Matrix4fExtended;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import sun.misc.Unsafe;

import java.nio.BufferUnderflowException;
import java.nio.FloatBuffer;

@Mixin(Matrix4f.class)
public class MixinMatrix4f implements Matrix4fExtended {
    @Final
    @Shadow
    private float[] components;

    @Override
    public void translate(float x, float y, float z) {
        this.components[12] = this.components[0] * x + this.components[4] * y + this.components[8] * z + this.components[12];
        this.components[13] = this.components[1] * x + this.components[5] * y + this.components[9] * z + this.components[13];
        this.components[14] = this.components[2] * x + this.components[6] * y + this.components[10] * z + this.components[14];
        this.components[15] = this.components[3] * x + this.components[7] * y + this.components[11] * z + this.components[15];
    }

    @Override
    public float transformVecX(float x, float y, float z) {
        return (this.components[0] * x) + (this.components[4] * y) + (this.components[8] * z) + (this.components[12] * 1.0f);
    }

    @Override
    public float transformVecY(float x, float y, float z) {
        return (this.components[1] * x) + (this.components[5] * y) + (this.components[9] * z) + (this.components[13] * 1.0f);
    }

    @Override
    public float transformVecZ(float x, float y, float z) {
        return (this.components[2] * x) + (this.components[6] * y) + (this.components[10] * z) + (this.components[14] * 1.0f);
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
        float tcomponents5 = 1.0F - xx;
        float tcomponents10 = 1.0F - xx;

        float xw = x * w;

        float tcomponents6 = 2.0F * xw;
        float tcomponents9 = 2.0F * -xw;

        float components4 = this.components[4] * tcomponents5 + this.components[8] * tcomponents6;
        float components8 = this.components[4] * tcomponents9 + this.components[8] * tcomponents10;
        float components5 = this.components[5] * tcomponents5 + this.components[9] * tcomponents6;
        float components9 = this.components[5] * tcomponents9 + this.components[9] * tcomponents10;
        float components6 = this.components[6] * tcomponents5 + this.components[10] * tcomponents6;
        float components10 = this.components[6] * tcomponents9 + this.components[10] * tcomponents10;
        float components7 = this.components[7] * tcomponents5 + this.components[11] * tcomponents6;
        float components11 = this.components[7] * tcomponents9 + this.components[11] * tcomponents10;

        this.components[4] = components4;
        this.components[8] = components8;
        this.components[5] = components5;
        this.components[9] = components9;
        this.components[6] = components6;
        this.components[10] = components10;
        this.components[7] = components7;
        this.components[11] = components11;
    }

    private void rotateY(Quaternion quaternion) {
        float y = quaternion.getC();
        float w = quaternion.getA();

        float yy = 2.0F * y * y;
        float tcomponents0 = 1.0F - yy;
        float tcomponents10 = 1.0F - yy;
        float yw = y * w;
        float tcomponents2 = 2.0F * -yw;
        float tcomponents8 = 2.0F * yw;

        float components0 = this.components[0] * tcomponents0 + this.components[8] * tcomponents2;
        float components8 = this.components[0] * tcomponents8 + this.components[8] * tcomponents10;
        float components1 = this.components[1] * tcomponents0 + this.components[9] * tcomponents2;
        float components9 = this.components[1] * tcomponents8 + this.components[9] * tcomponents10;
        float components2 = this.components[2] * tcomponents0 + this.components[10] * tcomponents2;
        float components10 = this.components[2] * tcomponents8 + this.components[10] * tcomponents10;
        float components3 = this.components[3] * tcomponents0 + this.components[11] * tcomponents2;
        float components11 = this.components[3] * tcomponents8 + this.components[11] * tcomponents10;

        this.components[0] = components0;
        this.components[8] = components8;
        this.components[1] = components1;
        this.components[9] = components9;
        this.components[2] = components2;
        this.components[10] = components10;
        this.components[3] = components3;
        this.components[11] = components11;
    }

    private void rotateZ(Quaternion quaternion) {
        float z = quaternion.getD();
        float w = quaternion.getA();

        float zz = 2.0F * z * z;
        float tcomponents0 = 1.0F - zz;
        float tcomponents5 = 1.0F - zz;
        float zw = z * w;
        float tcomponents1 = 2.0F * zw;
        float tcomponents4 = 2.0F * -zw;

        float components0 = this.components[0] * tcomponents0 + this.components[4] * tcomponents1;
        float components4 = this.components[0] * tcomponents4 + this.components[4] * tcomponents5;
        float components1 = this.components[1] * tcomponents0 + this.components[5] * tcomponents1;
        float components5 = this.components[1] * tcomponents4 + this.components[5] * tcomponents5;
        float components2 = this.components[2] * tcomponents0 + this.components[6] * tcomponents1;
        float components6 = this.components[2] * tcomponents4 + this.components[6] * tcomponents5;
        float components3 = this.components[3] * tcomponents0 + this.components[7] * tcomponents1;
        float components7 = this.components[3] * tcomponents4 + this.components[7] * tcomponents5;

        this.components[0] = components0;
        this.components[4] = components4;
        this.components[1] = components1;
        this.components[5] = components5;
        this.components[2] = components2;
        this.components[6] = components6;
        this.components[3] = components3;
        this.components[7] = components7;
    }

    private void rotateXYZ(Quaternion quaternion) {
        float x = quaternion.getB();
        float y = quaternion.getC();
        float z = quaternion.getD();
        float w = quaternion.getA();

        float xx = 2.0F * x * x;
        float yy = 2.0F * y * y;
        float zz = 2.0F * z * z;
        float tcomponents0 = 1.0F - yy - zz;
        float tcomponents5 = 1.0F - zz - xx;
        float tcomponents10 = 1.0F - xx - yy;
        float xy = x * y;
        float yz = y * z;
        float zx = z * x;
        float xw = x * w;
        float yw = y * w;
        float zw = z * w;
        float tcomponents1 = 2.0F * (xy + zw);
        float tcomponents4 = 2.0F * (xy - zw);
        float tcomponents2 = 2.0F * (zx - yw);
        float tcomponents8 = 2.0F * (zx + yw);
        float tcomponents6 = 2.0F * (yz + xw);
        float tcomponents9 = 2.0F * (yz - xw);

        float components0 = this.components[0] * tcomponents0 + this.components[4] * tcomponents1 + this.components[8] * tcomponents2;
        float components4 = this.components[0] * tcomponents4 + this.components[4] * tcomponents5 + this.components[8] * tcomponents6;
        float components8 = this.components[0] * tcomponents8 + this.components[4] * tcomponents9 + this.components[8] * tcomponents10;
        float components1 = this.components[1] * tcomponents0 + this.components[5] * tcomponents1 + this.components[9] * tcomponents2;
        float components5 = this.components[1] * tcomponents4 + this.components[5] * tcomponents5 + this.components[9] * tcomponents6;
        float components9 = this.components[1] * tcomponents8 + this.components[5] * tcomponents9 + this.components[9] * tcomponents10;
        float components2 = this.components[2] * tcomponents0 + this.components[6] * tcomponents1 + this.components[10] * tcomponents2;
        float components6 = this.components[2] * tcomponents4 + this.components[6] * tcomponents5 + this.components[10] * tcomponents6;
        float components10 = this.components[2] * tcomponents8 + this.components[6] * tcomponents9 + this.components[10] * tcomponents10;
        float components3 = this.components[3] * tcomponents0 + this.components[7] * tcomponents1 + this.components[11] * tcomponents2;
        float components7 = this.components[3] * tcomponents4 + this.components[7] * tcomponents5 + this.components[11] * tcomponents6;
        float components11 = this.components[3] * tcomponents8 + this.components[7] * tcomponents9 + this.components[11] * tcomponents10;

        this.components[0] = components0;
        this.components[4] = components4;
        this.components[8] = components8;
        this.components[1] = components1;
        this.components[5] = components5;
        this.components[9] = components9;
        this.components[2] = components2;
        this.components[6] = components6;
        this.components[10] = components10;
        this.components[3] = components3;
        this.components[7] = components7;
        this.components[11] = components11;
    }

    /**
     * @reason Optimize
     * @author JellySquid
     */
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
        unsafe.putFloat(addr + 0, this.components[0]);
        unsafe.putFloat(addr + 4, this.components[1]);
        unsafe.putFloat(addr + 8, this.components[2]);
        unsafe.putFloat(addr + 12, this.components[3]);
        unsafe.putFloat(addr + 16, this.components[4]);
        unsafe.putFloat(addr + 20, this.components[5]);
        unsafe.putFloat(addr + 24, this.components[6]);
        unsafe.putFloat(addr + 28, this.components[7]);
        unsafe.putFloat(addr + 32, this.components[8]);
        unsafe.putFloat(addr + 36, this.components[9]);
        unsafe.putFloat(addr + 40, this.components[10]);
        unsafe.putFloat(addr + 44, this.components[11]);
        unsafe.putFloat(addr + 48, this.components[12]);
        unsafe.putFloat(addr + 52, this.components[13]);
        unsafe.putFloat(addr + 56, this.components[14]);
        unsafe.putFloat(addr + 60, this.components[15]);
    }

    private void writeToBufferSafe(FloatBuffer buf) {
        buf.put(0, this.components[0]);
        buf.put(1, this.components[1]);
        buf.put(2, this.components[2]);
        buf.put(3, this.components[3]);
        buf.put(4, this.components[4]);
        buf.put(5, this.components[5]);
        buf.put(6, this.components[6]);
        buf.put(7, this.components[7]);
        buf.put(8, this.components[8]);
        buf.put(9, this.components[9]);
        buf.put(10, this.components[10]);
        buf.put(11, this.components[11]);
        buf.put(12, this.components[12]);
        buf.put(13, this.components[13]);
        buf.put(14, this.components[14]);
        buf.put(15, this.components[15]);
    }
}
