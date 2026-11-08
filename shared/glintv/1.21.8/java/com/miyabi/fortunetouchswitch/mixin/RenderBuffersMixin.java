package com.miyabi.fortunetouchswitch.mixin;

import com.miyabi.fortunetouchswitch.client.GlintColors;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.SequencedMap;

@Mixin(RenderBuffers.class)
public abstract class RenderBuffersMixin {
    // バニラのキラキラと同じく、専用のバッファに入れる（アイテム本体を描いたあとに重ねて描かれる）
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", ordinal = 0,
            target = "Lnet/minecraft/client/renderer/MultiBufferSource;immediateWithBuffers(Ljava/util/SequencedMap;Lcom/mojang/blaze3d/vertex/ByteBufferBuilder;)Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;"),
            index = 0)
    private SequencedMap<RenderType, ByteBufferBuilder> fortune_touch_switch$addGlintBuffers(SequencedMap<RenderType, ByteBufferBuilder> map) {
        for (GlintColors.Color c : GlintColors.Color.values()) {
            for (RenderType t : new RenderType[]{c.glint, c.glintTranslucent, c.entityGlint}) {
                map.put(t, new ByteBufferBuilder(t.bufferSize()));
            }
        }
        return map;
    }
}
