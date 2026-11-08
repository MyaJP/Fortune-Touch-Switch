package com.miyabi.fortunetouchswitch.mixin;

import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RenderType.class)
public interface RenderTypeAccessor {
    @Accessor("state")
    RenderSetup fortune_touch_switch$getState();

    @Accessor("name")
    String fortune_touch_switch$getName();

    @Invoker("create")
    static RenderType fortune_touch_switch$create(String name, RenderSetup state) {
        throw new AssertionError();
    }
}
