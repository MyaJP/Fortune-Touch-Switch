package com.miyabi.fortunetouchswitch.client;

import com.miyabi.fortunetouchswitch.FortuneTouchSwitch;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
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

    private static final Logger LOGGER = LogUtils.getLogger();

    /** 色の倍率（赤, 緑, 青）。キラキラの明るさにこれを掛ける */
    private static final float[] FORTUNE = {0.30F, 1.00F, 0.40F};
    private static final float[] SILK_TOUCH = {0.92F, 0.95F, 1.00F};

    private static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(FortuneTouchSwitch.MODID, "dynamic/" + name);
    }

    private static final ResourceLocation VANILLA_ITEM = ResourceLocation.withDefaultNamespace("textures/misc/enchanted_glint_item.png");
    private static final ResourceLocation VANILLA_ENTITY = ResourceLocation.withDefaultNamespace("textures/misc/enchanted_glint_entity.png");

    public static final ResourceLocation FORTUNE_ITEM = id("glint_fortune_item");
    public static final ResourceLocation FORTUNE_ENTITY = id("glint_fortune_entity");
    public static final ResourceLocation SILK_ITEM = id("glint_silk_touch_item");
    public static final ResourceLocation SILK_ENTITY = id("glint_silk_touch_entity");

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
        make(resources, VANILLA_ITEM, FORTUNE_ITEM, FORTUNE);
        make(resources, VANILLA_ENTITY, FORTUNE_ENTITY, FORTUNE);
        make(resources, VANILLA_ITEM, SILK_ITEM, SILK_TOUCH);
        make(resources, VANILLA_ENTITY, SILK_ENTITY, SILK_TOUCH);
    }

    private static void make(ResourceManager resources, ResourceLocation from, ResourceLocation to, float[] tint) {
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
            DynamicTexture texture = new DynamicTexture(img);
            // バニラのキラキラと同じく、なめらかに（ぼかして）貼る
            texture.setFilter(true, false);
            Minecraft.getInstance().getTextureManager().register(to, texture);
        } catch (Exception e) {
            LOGGER.warn("Failed to recolor enchantment glint {}", from, e);
        }
    }
}
