package com.miyabi.fortunetouchswitch.mixin;

import com.miyabi.fortunetouchswitch.client.GlintColors;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    // キラキラを重ねて描くときに、色を変えたものを使う
    @Redirect(method = {"getSpecialFoilBuffer", "getFoilBuffer"}, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;glint()Lnet/minecraft/client/renderer/rendertype/RenderType;"))
    private static RenderType fortune_touch_switch$glint() {
        return GlintColors.recolor(RenderTypes.glint(), GlintColors.drawing, "Sampler0");
    }

    @Redirect(method = {"getSpecialFoilBuffer", "getFoilBuffer"}, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;glintTranslucent()Lnet/minecraft/client/renderer/rendertype/RenderType;"))
    private static RenderType fortune_touch_switch$glintTranslucent() {
        return GlintColors.recolor(RenderTypes.glintTranslucent(), GlintColors.drawing, "Sampler0");
    }

    @Redirect(method = "getFoilBuffer", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;entityGlint()Lnet/minecraft/client/renderer/rendertype/RenderType;"))
    private static RenderType fortune_touch_switch$entityGlint() {
        return GlintColors.recolor(RenderTypes.entityGlint(), GlintColors.drawing, "Sampler0");
    }
}
