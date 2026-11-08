package com.miyabi.fortunetouchswitch.mixin;

import com.miyabi.fortunetouchswitch.client.GlintColors;
import com.miyabi.fortunetouchswitch.client.GlintTextures;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Unique
    private static GlintColors.Color fortune_touch_switch$previous;

    // アイテムを描く間だけ、そのアイテムのキラキラの色を覚えておく
    @Inject(method = "render", at = @At("HEAD"))
    private void fortune_touch_switch$beginRender(ItemStack stack, ItemDisplayContext ctx, boolean leftHand, PoseStack pose,
                                                 MultiBufferSource buffers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        GlintColors.Color c = GlintColors.colorOf(stack);
        if (c != null) GlintTextures.ensure();
        fortune_touch_switch$previous = GlintColors.drawing;
        GlintColors.drawing = c;
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void fortune_touch_switch$endRender(ItemStack stack, ItemDisplayContext ctx, boolean leftHand, PoseStack pose,
                                               MultiBufferSource buffers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        GlintColors.drawing = fortune_touch_switch$previous;
        fortune_touch_switch$previous = null;
    }

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
