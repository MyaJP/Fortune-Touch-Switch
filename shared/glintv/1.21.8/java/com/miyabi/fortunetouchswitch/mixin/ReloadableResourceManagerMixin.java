package com.miyabi.fortunetouchswitch.mixin;

import com.miyabi.fortunetouchswitch.client.GlintTextures;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ReloadableResourceManager.class)
public abstract class ReloadableResourceManagerMixin {
    // リソースパックを読み込み直したら、色付きのキラキラも作り直す
    @Inject(method = "createReload", at = @At("RETURN"))
    private void fortune_touch_switch$reload(CallbackInfoReturnable<?> cir) {
        GlintTextures.invalidate();
    }
}
