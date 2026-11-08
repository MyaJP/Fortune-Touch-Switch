package com.miyabi.fortunetouchswitch.mixin;

import com.miyabi.fortunetouchswitch.client.GlintColors;
import com.miyabi.fortunetouchswitch.client.GlintHolder;
import com.miyabi.fortunetouchswitch.client.GlintTextures;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemModelResolver.class)
public abstract class ItemModelResolverMixin {
    // アイテムの描画の情報を作るときに、キラキラの色も覚えさせる
    @Inject(method = "appendItemLayers", at = @At("RETURN"))
    private void fortune_touch_switch$color(ItemStackRenderState output, ItemStack item, ItemDisplayContext displayContext,
                                            Level level, net.minecraft.world.entity.LivingEntity owner, int seed, CallbackInfo ci) {
        GlintColors.Color c = GlintColors.colorOf(item);
        if (c == null) return;
        GlintTextures.ensure();
        ((GlintHolder) output).fortune_touch_switch$setColor(c);
    }
}
