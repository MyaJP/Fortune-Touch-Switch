package com.miyabi.fortunetouchswitch.client;

import com.miyabi.fortunetouchswitch.FortuneTouchSwitch;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import java.io.InputStream;
import java.util.Optional;

/**
 * バニラのキラキラのテクスチャを、ゲームを起動したとき（とリソースパックを変えたとき）に読み込んで、
 * 色だけ変えたものをメモリ上に作る。バニラの画像そのものはMODに入れていない。
 * リソースパックでキラキラを変えている場合は、その模様がそのまま緑や白になる。
 */
public final class GlintTextures implements ResourceManagerReloadListener {
    private static final org.slf4j.Logger LOGGER = com.mojang.logging.LogUtils.getLogger();
    /** 色の倍率（赤, 緑, 青）。キラキラの明るさにこれを掛ける */
    private static final float[] FORTUNE = {0.30F, 1.00F, 0.40F};
    private static final float[] SILK_TOUCH = {0.92F, 0.95F, 1.00F};

    public static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(FortuneTouchSwitch.MODID, "dynamic/" + name);
    }

    public static final ResourceLocation FORTUNE_ITEM = id("glint_fortune_item");
    public static final ResourceLocation FORTUNE_ENTITY = id("glint_fortune_entity");
    public static final ResourceLocation SILK_ITEM = id("glint_silk_touch_item");
    public static final ResourceLocation SILK_ENTITY = id("glint_silk_touch_entity");

    @Override
    public void onResourceManagerReload(ResourceManager resources) {
        make(resources, ItemRenderer.ENCHANTED_GLINT_ITEM, FORTUNE_ITEM, FORTUNE);
        make(resources, ItemRenderer.ENCHANTED_GLINT_ENTITY, FORTUNE_ENTITY, FORTUNE);
        make(resources, ItemRenderer.ENCHANTED_GLINT_ITEM, SILK_ITEM, SILK_TOUCH);
        make(resources, ItemRenderer.ENCHANTED_GLINT_ENTITY, SILK_ENTITY, SILK_TOUCH);
    }

    private static void make(ResourceManager resources, ResourceLocation from, ResourceLocation to, float[] tint) {
        Optional<Resource> res = resources.getResource(from);
        if (res.isEmpty()) return;
        try (InputStream in = res.get().open()) {
            NativeImage img = NativeImage.read(in);
            for (int y = 0; y < img.getHeight(); y++) {
                for (int x = 0; x < img.getWidth(); x++) {
                    int abgr = img.getPixelRGBA(x, y);
                    int a = abgr >>> 24 & 0xFF, b = abgr >>> 16 & 0xFF, g = abgr >>> 8 & 0xFF, r = abgr & 0xFF;
                    // バニラの紫の明るさ（いちばん明るい色の成分）をそのまま使って、色だけ付け直す
                    int v = Math.max(r, Math.max(g, b));
                    int nr = Math.min(255, Math.round(v * tint[0]));
                    int ng = Math.min(255, Math.round(v * tint[1]));
                    int nb = Math.min(255, Math.round(v * tint[2]));
                    img.setPixelRGBA(x, y, a << 24 | nb << 16 | ng << 8 | nr);
                }
            }
            Minecraft.getInstance().getTextureManager().register(to, new DynamicTexture(img));
        } catch (Exception e) {
            LOGGER.warn("Failed to recolor enchantment glint {}", from, e);
        }
    }
}
