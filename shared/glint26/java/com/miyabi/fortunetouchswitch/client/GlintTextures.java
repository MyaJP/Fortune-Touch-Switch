package com.miyabi.fortunetouchswitch.client;

import com.miyabi.fortunetouchswitch.FortuneTouchSwitch;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;

import java.io.InputStream;
import java.util.Optional;

/**
 * バニラのキラキラのテクスチャを読み込んで、色だけ変えたものをメモリ上に作る。バニラの画像そのものはMODに入れていない。
 * 最初に使うときと、リソースパックを読み込み直したあとに作り直す。
 * リソースパックでキラキラを変えている場合は、その模様がそのまま緑や白になる。
 */
public final class GlintTextures {
    private GlintTextures() {}

    static final Logger LOGGER = LogUtils.getLogger();

    /** 色の倍率（赤, 緑, 青）。キラキラの明るさにこれを掛ける */
    private static final float[] FORTUNE = {0.30F, 1.00F, 0.40F};
    private static final float[] SILK_TOUCH = {0.92F, 0.95F, 1.00F};

    public static final Identifier FORTUNE_ITEM = Identifier.fromNamespaceAndPath(FortuneTouchSwitch.MODID, "dynamic/glint_fortune_item");
    public static final Identifier SILK_ITEM = Identifier.fromNamespaceAndPath(FortuneTouchSwitch.MODID, "dynamic/glint_silk_touch_item");

    private static final Identifier VANILLA_GLINT = Identifier.withDefaultNamespace("textures/misc/enchanted_glint_item.png");

    private static volatile boolean dirty = true;

    /** リソースが読み込み直されたら呼ぶ（次に使うときに作り直す） */
    public static void invalidate() {
        dirty = true;
    }

    /** 描画の前に呼ぶ。必要なら作る（描画のスレッドで呼ぶこと） */
    public static void ensure() {
        if (!dirty) return;
        dirty = false;
        ResourceManager resources = Minecraft.getInstance().getResourceManager();
        make(resources, VANILLA_GLINT, FORTUNE_ITEM, FORTUNE);
        make(resources, VANILLA_GLINT, SILK_ITEM, SILK_TOUCH);
    }

    private static void make(ResourceManager resources, Identifier from, Identifier to, float[] tint) {
        Optional<Resource> res = resources.getResource(from);
        if (res.isEmpty()) return;
        try (InputStream in = res.get().open()) {
            NativeImage img = NativeImage.read(in);
            for (int y = 0; y < img.getHeight(); y++) {
                for (int x = 0; x < img.getWidth(); x++) {
                    int argb = img.getPixel(x, y);
                    int a = argb >>> 24 & 0xFF, r = argb >>> 16 & 0xFF, g = argb >>> 8 & 0xFF, b = argb & 0xFF;
                    // バニラの紫の明るさ（いちばん明るい色の成分）をそのまま使って、色だけ付け直す
                    int v = Math.max(r, Math.max(g, b));
                    int nr = Math.min(255, Math.round(v * tint[0]));
                    int ng = Math.min(255, Math.round(v * tint[1]));
                    int nb = Math.min(255, Math.round(v * tint[2]));
                    img.setPixel(x, y, a << 24 | nr << 16 | ng << 8 | nb);
                }
            }
            Minecraft.getInstance().getTextureManager().register(to, new GlintTexture(to::toString, img));
        } catch (Exception e) {
            LOGGER.warn("Failed to recolor enchantment glint {}", from, e);
        }
    }
}
