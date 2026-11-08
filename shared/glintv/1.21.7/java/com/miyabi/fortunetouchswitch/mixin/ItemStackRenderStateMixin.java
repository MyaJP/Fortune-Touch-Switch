package com.miyabi.fortunetouchswitch.mixin;

import com.miyabi.fortunetouchswitch.client.GlintColors;
import com.miyabi.fortunetouchswitch.client.GlintHolder;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStackRenderState.class)
public abstract class ItemStackRenderStateMixin implements GlintHolder {
    @Unique
    private GlintColors.Color fortune_touch_switch$color;
    @Unique
    private GlintColors.Color fortune_touch_switch$previous;

    @Override
    public GlintColors.Color fortune_touch_switch$getColor() {
        return this.fortune_touch_switch$color;
    }

    @Override
    public void fortune_touch_switch$setColor(GlintColors.Color color) {
        this.fortune_touch_switch$color = color;
    }

    @Inject(method = "clear", at = @At("HEAD"))
    private void fortune_touch_switch$clear(CallbackInfo ci) {
        this.fortune_touch_switch$color = null;
    }

    // 描く間だけ色を覚えておく（ItemRendererMixin がそれを見てキラキラを差し替える）
    @Inject(method = "render", at = @At("HEAD"))
    private void fortune_touch_switch$begin(CallbackInfo ci) {
        this.fortune_touch_switch$previous = GlintColors.drawing;
        GlintColors.drawing = this.fortune_touch_switch$color;
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void fortune_touch_switch$end(CallbackInfo ci) {
        GlintColors.drawing = this.fortune_touch_switch$previous;
        this.fortune_touch_switch$previous = null;
    }
}
