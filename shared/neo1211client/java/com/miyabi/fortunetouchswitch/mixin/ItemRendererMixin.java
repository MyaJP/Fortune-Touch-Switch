package com.miyabi.fortunetouchswitch.mixin;

import com.miyabi.fortunetouchswitch.client.GlintColors;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Unique
    private static final ThreadLocal<GlintColors.Color> fortune_touch_switch$previous = new ThreadLocal<>();

    // アイテムを描く間だけ、そのアイテムのキラキラの色を覚えておく
    @Inject(method = "render", at = @At("HEAD"))
    private void fortune_touch_switch$beginRender(ItemStack stack, ItemDisplayContext ctx, boolean leftHand, PoseStack pose,
                                                 MultiBufferSource buffers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        fortune_touch_switch$previous.set(GlintColors.set(GlintColors.colorOf(stack)));
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void fortune_touch_switch$endRender(ItemStack stack, ItemDisplayContext ctx, boolean leftHand, PoseStack pose,
                                               MultiBufferSource buffers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        GlintColors.set(fortune_touch_switch$previous.get());
        fortune_touch_switch$previous.remove();
    }

    @Inject(method = "getFoilBuffer", at = @At("HEAD"), cancellable = true)
    private static void fortune_touch_switch$foil(MultiBufferSource buffers, RenderType type, boolean isItem, boolean glint,
                                                 CallbackInfoReturnable<VertexConsumer> cir) {
        GlintColors.Color c = GlintColors.current();
        if (c == null || !glint) return;
        RenderType g = net.minecraft.client.Minecraft.useShaderTransparency() && type == net.minecraft.client.renderer.Sheets.translucentItemSheet()
                ? c.glintTranslucent : (isItem ? c.glint : c.entityGlint);
        cir.setReturnValue(VertexMultiConsumer.create(buffers.getBuffer(g), buffers.getBuffer(type)));
    }

    @Inject(method = "getFoilBufferDirect", at = @At("HEAD"), cancellable = true)
    private static void fortune_touch_switch$foilDirect(MultiBufferSource buffers, RenderType type, boolean noEntity, boolean glint,
                                                       CallbackInfoReturnable<VertexConsumer> cir) {
        GlintColors.Color c = GlintColors.current();
        if (c == null || !glint) return;
        cir.setReturnValue(VertexMultiConsumer.create(buffers.getBuffer(noEntity ? c.glint : c.entityGlintDirect), buffers.getBuffer(type)));
    }
}
