package com.miyabi.fortunetouchswitch.client;

import com.miyabi.fortunetouchswitch.FortuneTouchSwitch;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterRenderBuffersEvent;

@EventBusSubscriber(modid = FortuneTouchSwitch.MODID, value = Dist.CLIENT)
public final class ClientSetup {
    private ClientSetup() {}

    // バニラのキラキラと同じく、専用のバッファに入れる（アイテム本体を描いたあとに重ねて描かれる）
    @SubscribeEvent
    public static void onRegisterRenderBuffers(RegisterRenderBuffersEvent event) {
        for (GlintColors.Color c : GlintColors.Color.values()) {
            event.registerRenderBuffer(c.glint);
            event.registerRenderBuffer(c.glintTranslucent);
            event.registerRenderBuffer(c.entityGlint);
            event.registerRenderBuffer(c.entityGlintDirect);
        }
    }

    // バニラのキラキラを読み込んで、緑と白のものを作る（リソースパックを変えたときも作り直す）
    @SubscribeEvent
    public static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new GlintTextures());
    }
}
