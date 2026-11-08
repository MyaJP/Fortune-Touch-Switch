package com.miyabi.fortunetouchswitch.client;

import com.miyabi.fortunetouchswitch.FortuneTouchSwitch;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.Nullable;

/**
 * シルクタッチか幸運が付いた道具は、エンチャントのキラキラの色を変える。
 * シルクタッチが効いていれば白、幸運が効いていれば緑（両方付いた道具は、今効いている方の色）。
 * バニラのキラキラと同じ設定で、テクスチャだけ色を変えたもの（GlintTextures）に差し替えた描画の種類を作っている。
 */
public final class GlintColors {
    private GlintColors() {}

    public enum Color {
        FORTUNE("glint_fortune", GlintTextures.FORTUNE_ITEM, GlintTextures.FORTUNE_ENTITY),
        SILK_TOUCH("glint_silk_touch", GlintTextures.SILK_ITEM, GlintTextures.SILK_ENTITY);

        public final RenderType glint, glintTranslucent, entityGlint, entityGlintDirect;

        Color(String texture, ResourceLocation tex, ResourceLocation entityTex) {
            String n = FortuneTouchSwitch.MODID + "_" + texture;
            glint = RenderType.create(n, DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 1536,
                    RenderType.CompositeState.builder()
                            .setShaderState(RenderStateShard.RENDERTYPE_GLINT_SHADER)
                            .setTextureState(new RenderStateShard.TextureStateShard(tex, true, false))
                            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                            .setCullState(RenderStateShard.NO_CULL)
                            .setDepthTestState(RenderStateShard.EQUAL_DEPTH_TEST)
                            .setTransparencyState(RenderStateShard.GLINT_TRANSPARENCY)
                            .setTexturingState(RenderStateShard.GLINT_TEXTURING)
                            .createCompositeState(false));
            glintTranslucent = RenderType.create(n + "_translucent", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 1536,
                    RenderType.CompositeState.builder()
                            .setShaderState(RenderStateShard.RENDERTYPE_GLINT_TRANSLUCENT_SHADER)
                            .setTextureState(new RenderStateShard.TextureStateShard(tex, true, false))
                            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                            .setCullState(RenderStateShard.NO_CULL)
                            .setDepthTestState(RenderStateShard.EQUAL_DEPTH_TEST)
                            .setTransparencyState(RenderStateShard.GLINT_TRANSPARENCY)
                            .setTexturingState(RenderStateShard.GLINT_TEXTURING)
                            .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
                            .createCompositeState(false));
            entityGlint = RenderType.create(n + "_entity", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 1536,
                    RenderType.CompositeState.builder()
                            .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_GLINT_SHADER)
                            .setTextureState(new RenderStateShard.TextureStateShard(entityTex, true, false))
                            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                            .setCullState(RenderStateShard.NO_CULL)
                            .setDepthTestState(RenderStateShard.EQUAL_DEPTH_TEST)
                            .setTransparencyState(RenderStateShard.GLINT_TRANSPARENCY)
                            .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
                            .setTexturingState(RenderStateShard.ENTITY_GLINT_TEXTURING)
                            .createCompositeState(false));
            entityGlintDirect = RenderType.create(n + "_entity_direct", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 1536,
                    RenderType.CompositeState.builder()
                            .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_GLINT_DIRECT_SHADER)
                            .setTextureState(new RenderStateShard.TextureStateShard(entityTex, true, false))
                            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                            .setCullState(RenderStateShard.NO_CULL)
                            .setDepthTestState(RenderStateShard.EQUAL_DEPTH_TEST)
                            .setTransparencyState(RenderStateShard.GLINT_TRANSPARENCY)
                            .setTexturingState(RenderStateShard.ENTITY_GLINT_TEXTURING)
                            .createCompositeState(false));
        }
    }

    /** 今描いているアイテムのキラキラの色（null ならバニラのまま） */
    private static final ThreadLocal<Color> CURRENT = new ThreadLocal<>();

    @Nullable
    public static Color current() { return CURRENT.get(); }

    @Nullable
    public static Color set(@Nullable Color color) {
        Color prev = CURRENT.get();
        CURRENT.set(color);
        return prev;
    }

    /** このアイテムのキラキラの色。シルクタッチも幸運も効いていなければ null（エンチャントの本はバニラのまま） */
    @Nullable
    public static Color colorOf(ItemStack stack) {
        if (stack.isEmpty() || stack.is(net.minecraft.world.item.Items.ENCHANTED_BOOK)) return null;
        for (Holder<Enchantment> h : stack.getEnchantments().keySet()) {
            if (h.is(Enchantments.SILK_TOUCH)) return Color.SILK_TOUCH;
            if (h.is(Enchantments.FORTUNE)) return Color.FORTUNE;
        }
        return null;
    }
}
