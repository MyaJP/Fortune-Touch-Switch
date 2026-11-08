package com.miyabi.fortunetouchswitch.mixin;

import com.miyabi.fortunetouchswitch.client.GlintColors;
import com.miyabi.fortunetouchswitch.client.GlintHolder;
import net.minecraft.client.renderer.SubmitNodeStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SubmitNodeStorage.ItemSubmit.class)
public abstract class StorageItemSubmitMixin implements GlintHolder {
    @Unique
    private GlintColors.Color fortune_touch_switch$color;

    @Override
    public GlintColors.Color fortune_touch_switch$getColor() {
        return this.fortune_touch_switch$color;
    }

    @Override
    public void fortune_touch_switch$setColor(GlintColors.Color color) {
        this.fortune_touch_switch$color = color;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void fortune_touch_switch$init(CallbackInfo ci) {
        this.fortune_touch_switch$color = GlintColors.current();
    }
}
