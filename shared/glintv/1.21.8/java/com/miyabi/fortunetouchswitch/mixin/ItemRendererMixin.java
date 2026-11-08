package com.miyabi.fortunetouchswitch.mixin;

import com.miyabi.fortunetouchswitch.client.GlintColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    // キラキラを重ねて描くときに、色を変えたものを使う
    @Redirect(method = {"getFoilBuffer", "getCompassFoilBuffer", "getSpecialFoilBuffer"}, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/RenderType;glint()Lnet/minecraft/client/renderer/RenderType;"))
    private static RenderType fortune_touch_switch$glint() {
        return GlintColors.glint();
    }

    @Redirect(method = {"getFoilBuffer", "getSpecialFoilBuffer"}, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/RenderType;glintTranslucent()Lnet/minecraft/client/renderer/RenderType;"))
    private static RenderType fortune_touch_switch$glintTranslucent() {
        return GlintColors.glintTranslucent();
    }

    @Redirect(method = "getFoilBuffer", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/RenderType;entityGlint()Lnet/minecraft/client/renderer/RenderType;"))
    private static RenderType fortune_touch_switch$entityGlint() {
        return GlintColors.entityGlint();
    }
}
