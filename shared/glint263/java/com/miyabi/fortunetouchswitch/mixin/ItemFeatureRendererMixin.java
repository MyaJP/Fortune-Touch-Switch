package com.miyabi.fortunetouchswitch.mixin;

import com.miyabi.fortunetouchswitch.client.GlintColors;
import com.miyabi.fortunetouchswitch.client.GlintHolder;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.geometry.BakedQuad;
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

    @Inject(method = "prepareMainSubmit", at = @At("HEAD"))
    private void fortune_touch_switch$begin(ItemFeatureRenderer.Submit submit, CallbackInfo ci) {
        this.fortune_touch_switch$color = ((GlintHolder) (Object) submit).fortune_touch_switch$getColor();
    }

    @Inject(method = "prepareMainSubmit", at = @At("RETURN"))
    private void fortune_touch_switch$end(ItemFeatureRenderer.Submit submit, CallbackInfo ci) {
        this.fortune_touch_switch$color = null;
    }

    // 26.3 ではキラキラはアイテム本体と一緒に描かれるので、その描画の種類のキラキラのテクスチャだけ色を変えたものを使う
    @Redirect(method = "prepareMainSubmit", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/resources/model/geometry/BakedQuad$MaterialInfo;itemGlintRenderType()Lnet/minecraft/client/renderer/rendertype/RenderType;"))
    private RenderType fortune_touch_switch$glint(BakedQuad.MaterialInfo material) {
        return GlintColors.recolor(material.itemGlintRenderType(), this.fortune_touch_switch$color, "GlintSampler");
    }

    @Redirect(method = "prepareMainSubmit", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/resources/model/geometry/BakedQuad$MaterialInfo;itemGlintSpecialRenderType()Lnet/minecraft/client/renderer/rendertype/RenderType;"))
    private RenderType fortune_touch_switch$glintSpecial(BakedQuad.MaterialInfo material) {
        return GlintColors.recolor(material.itemGlintSpecialRenderType(), this.fortune_touch_switch$color, "GlintSampler");
    }
}
