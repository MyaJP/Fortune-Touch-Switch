package com.miyabi.fortunetouchswitch.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.textures.FilterMode;
import net.minecraft.client.renderer.texture.DynamicTexture;

import java.util.function.Supplier;

/** 色を変えたキラキラのテクスチャ。バニラのキラキラと同じく、なめらかに（ぼかして）繰り返し貼る */
public final class GlintTexture extends DynamicTexture {
    public GlintTexture(Supplier<String> label, NativeImage image) {
        super(label, image);
        this.sampler = RenderSystem.getSamplerCache().getRepeat(FilterMode.LINEAR);
    }
}
