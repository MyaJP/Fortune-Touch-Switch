package com.miyabi.fortunetouchswitch.client;

import net.minecraft.client.renderer.rendertype.RenderSetup;

import java.util.Map;

/** RenderSetup に足した処理（RenderSetupMixin で実装） */
public interface RenderSetupDuck {
    Map<String, Object> fortune_touch_switch$getTextures();

    /** テクスチャの割り当てだけ替えた写し */
    RenderSetup fortune_touch_switch$copyWithTextures(Map<String, Object> textures);

    void fortune_touch_switch$setTextures(Map<String, Object> textures);
}
