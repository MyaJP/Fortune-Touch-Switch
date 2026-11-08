package com.miyabi.fortunetouchswitch.mixin;

import com.miyabi.fortunetouchswitch.client.GlintColors;
import com.miyabi.fortunetouchswitch.client.GlintHolder;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemFeatureRenderer.class)
public abstract class ItemFeatureRendererMixin {
    @Unique
    private GlintColors.Color fortune_touch_switch$color;

    @Inject(method = "prepareFoilSubmit", at = @At("HEAD"))
    private void fortune_touch_switch$begin(ItemFeatureRenderer.Submit submit, CallbackInfo ci) {
        this.fortune_touch_switch$color = ((GlintHolder) (Object) submit).fortune_touch_switch$getColor();
    }

    @Inject(method = "prepareFoilSubmit", at = @At("RETURN"))
    private void fortune_touch_switch$end(ItemFeatureRenderer.Submit submit, CallbackInfo ci) {
        this.fortune_touch_switch$color = null;
    }

    // キラキラを重ねて描くときに、色を変えたものを使う
    @Redirect(method = "getFoilBuffer", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;glint()Lnet/minecraft/client/renderer/rendertype/RenderType;"))
    private RenderType fortune_touch_switch$glint() {
        return GlintColors.recolor(RenderTypes.glint(), this.fortune_touch_switch$color, "Sampler0");
    }

    @Redirect(method = "getFoilBuffer", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;glintTranslucent()Lnet/minecraft/client/renderer/rendertype/RenderType;"))
    private RenderType fortune_touch_switch$glintTranslucent() {
        return GlintColors.recolor(RenderTypes.glintTranslucent(), this.fortune_touch_switch$color, "Sampler0");
    }
}
