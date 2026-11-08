package com.miyabi.fortunetouchswitch.client;

import com.miyabi.fortunetouchswitch.FortuneTouchSwitch;
import com.miyabi.fortunetouchswitch.mixin.RenderTypeAccessor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * シルクタッチか幸運が付いた道具は、エンチャントのキラキラの色を変える。
 * シルクタッチが効いていれば白、幸運が効いていれば緑（両方付いた道具は、今効いている方の色）。
 * バニラのキラキラの描画の種類をそのまま写して、キラキラのテクスチャだけ色を変えたもの（GlintTextures）に差し替える。
 */
public final class GlintColors {
    private GlintColors() {}

    public enum Color {
        FORTUNE(GlintTextures.FORTUNE_ITEM),
        SILK_TOUCH(GlintTextures.SILK_ITEM);

        public final Identifier texture;
        private final Map<RenderType, RenderType> recolored = new IdentityHashMap<>();

        Color(Identifier texture) {
            this.texture = texture;
        }
    }

    /** 今描いているアイテムのキラキラの色（null ならバニラのまま） */
    private static final ThreadLocal<Color> CURRENT = new ThreadLocal<>();

    @Nullable
    public static Color current() { return CURRENT.get(); }

    /** 描画の段階で、今描いているアイテムのキラキラの色（1.21.11〜26.1 で使う。描画のスレッドだけ） */
    @Nullable
    public static Color drawing;

    @Nullable
    public static Color set(@Nullable Color color) {
        Color prev = CURRENT.get();
        CURRENT.set(color);
        return prev;
    }

    /** このアイテムのキラキラの色。シルクタッチも幸運も効いていなければ null（エンチャントの本はバニラのまま） */
    @Nullable
    public static Color colorOf(ItemStack stack) {
        if (stack.isEmpty() || stack.is(Items.ENCHANTED_BOOK)) return null;
        for (Holder<Enchantment> h : stack.getEnchantments().keySet()) {
            if (h.is(Enchantments.SILK_TOUCH)) return Color.SILK_TOUCH;
            if (h.is(Enchantments.FORTUNE)) return Color.FORTUNE;
        }
        return null;
    }

    /**
     * base の描画の種類のうち、sampler という名前のテクスチャ（キラキラ）だけを color のものに替えた描画の種類。
     * 一度作ったものは使い回す。
     */
    public static RenderType recolor(RenderType base, @Nullable Color color, String sampler) {
        if (color == null) return base;
        RenderType r = color.recolored.get(base);
        if (r == null) {
            r = make(base, color, sampler);
            color.recolored.put(base, r);
        }
        return r;
    }

    private static RenderType make(RenderType base, Color color, String sampler) {
        try {
            RenderTypeAccessor acc = (RenderTypeAccessor) base;
            RenderSetupDuck setup = (RenderSetupDuck) (Object) acc.fortune_touch_switch$getState();
            Map<String, Object> textures = new HashMap<>(setup.fortune_touch_switch$getTextures());
            if (!textures.containsKey(sampler)) return base;
            // テクスチャの割り当てを1つ作るために、同じ名前で色付きのテクスチャだけを持つ設定を作って、そこから取り出す
            RenderSetup donor = RenderSetup.builder(RenderPipelines.GLINT).withTexture(sampler, color.texture).createRenderSetup();
            textures.put(sampler, ((RenderSetupDuck) (Object) donor).fortune_touch_switch$getTextures().get(sampler));
            RenderSetup copy = setup.fortune_touch_switch$copyWithTextures(textures);
            return RenderTypeAccessor.fortune_touch_switch$create(
                    acc.fortune_touch_switch$getName() + "_" + FortuneTouchSwitch.MODID + "_" + color.name().toLowerCase(Locale.ROOT), copy);
        } catch (Throwable e) {
            GlintTextures.LOGGER.warn("Failed to make colored glint for {}", base, e);
            return base;
        }
    }
}
