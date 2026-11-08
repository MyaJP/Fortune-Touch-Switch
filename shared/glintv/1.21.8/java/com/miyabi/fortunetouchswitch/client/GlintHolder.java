package com.miyabi.fortunetouchswitch.client;

import org.jetbrains.annotations.Nullable;

/** アイテムの描画の情報にキラキラの色を持たせる（ItemStackRenderState と ItemFeatureRenderer.Submit） */
public interface GlintHolder {
    @Nullable
    GlintColors.Color fortune_touch_switch$getColor();

    void fortune_touch_switch$setColor(@Nullable GlintColors.Color color);
}
