package com.miyabi.fortunetouchswitch.mixin;

import com.miyabi.fortunetouchswitch.client.RenderSetupDuck;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(RenderSetup.class)
public abstract class RenderSetupMixin implements Cloneable, RenderSetupDuck {
    @Shadow @Final @Mutable
    Map<String, Object> textures;

    @Override
    public Map<String, Object> fortune_touch_switch$getTextures() {
        return this.textures;
    }

    @Override
    public void fortune_touch_switch$setTextures(Map<String, Object> textures) {
        this.textures = textures;
    }

    // ほかの設定はそのままで、テクスチャの割り当てだけ替えた写しを作る
    @Override
    public RenderSetup fortune_touch_switch$copyWithTextures(Map<String, Object> textures) {
        try {
            Object copy = super.clone();
            ((RenderSetupDuck) copy).fortune_touch_switch$setTextures(textures);
            return (RenderSetup) copy;
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }
    }
}
