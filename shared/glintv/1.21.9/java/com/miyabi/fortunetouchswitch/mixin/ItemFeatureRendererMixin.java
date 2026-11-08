package com.miyabi.fortunetouchswitch.mixin;

import com.miyabi.fortunetouchswitch.client.GlintColors;
import com.miyabi.fortunetouchswitch.client.GlintHolder;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemFeatureRenderer.class)
public abstract class ItemFeatureRendererMixin {
    // アイテムを1つずつ描くところで、そのアイテムのキラキラの色を覚えておく
    @Redirect(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/SubmitNodeStorage$ItemSubmit;renderType()Lnet/minecraft/client/renderer/RenderType;"))
    private RenderType fortune_touch_switch$color(SubmitNodeStorage.ItemSubmit submit) {
        GlintColors.drawing = ((GlintHolder) (Object) submit).fortune_touch_switch$getColor();
        return submit.renderType();
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void fortune_touch_switch$end(CallbackInfo ci) {
        GlintColors.drawing = null;
    }
}
