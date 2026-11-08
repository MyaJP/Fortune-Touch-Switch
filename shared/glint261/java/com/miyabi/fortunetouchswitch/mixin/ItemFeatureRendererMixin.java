package com.miyabi.fortunetouchswitch.mixin;

import com.miyabi.fortunetouchswitch.client.GlintColors;
import com.miyabi.fortunetouchswitch.client.GlintHolder;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemFeatureRenderer.class)
public abstract class ItemFeatureRendererMixin {
    @Inject(method = "renderItem", at = @At("HEAD"))
    private void fortune_touch_switch$beginWithSubmit(net.minecraft.client.renderer.MultiBufferSource.BufferSource bufferSource,
                                                      net.minecraft.client.renderer.OutlineBufferSource outline,
                                                      SubmitNodeStorage.ItemSubmit submit, CallbackInfo ci) {
        GlintColors.drawing = ((GlintHolder) (Object) submit).fortune_touch_switch$getColor();
    }

    @Inject(method = "renderItem", at = @At("RETURN"))
    private void fortune_touch_switch$end(CallbackInfo ci) {
        GlintColors.drawing = null;
    }

    // キラキラを重ねて描くときに、色を変えたものを使う
    @Redirect(method = "getFoilRenderType", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;glint()Lnet/minecraft/client/renderer/rendertype/RenderType;"))
    private static RenderType fortune_touch_switch$glint() {
        return GlintColors.recolor(RenderTypes.glint(), GlintColors.drawing, "Sampler0");
    }

    @Redirect(method = "getFoilRenderType", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;glintTranslucent()Lnet/minecraft/client/renderer/rendertype/RenderType;"))
    private static RenderType fortune_touch_switch$glintTranslucent() {
        return GlintColors.recolor(RenderTypes.glintTranslucent(), GlintColors.drawing, "Sampler0");
    }

    @Redirect(method = "getFoilRenderType", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;entityGlint()Lnet/minecraft/client/renderer/rendertype/RenderType;"))
    private static RenderType fortune_touch_switch$entityGlint() {
        return GlintColors.recolor(RenderTypes.entityGlint(), GlintColors.drawing, "Sampler0");
    }
}
