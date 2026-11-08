package com.miyabi.fortunetouchswitch;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * シルクタッチと幸運を1つの道具に両方付けたときの扱い。
 * 効いている方は普通のエンチャントとして付けたまま、効いていない方は INACTIVE にしまっておく。
 * こうするとドロップの処理はバニラのままで済む。
 */
public final class SwitchLogic {
    static final String SILK = "silk_touch";
    static final String FORTUNE = "fortune";

    private SwitchLogic() {}

    static Holder<Enchantment> silk(HolderLookup.Provider r) {
        return r.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH);
    }

    static Holder<Enchantment> fortune(HolderLookup.Provider r) {
        return r.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE);
    }

    /** 説明欄で上に表示される方（バニラでは幸運が上） */
    static String upperInTooltip(HolderLookup.Provider r) {
        Optional<HolderSet.Named<Enchantment>> order = r.lookupOrThrow(Registries.ENCHANTMENT).get(EnchantmentTags.TOOLTIP_ORDER);
        if (order.isPresent()) {
            for (Holder<Enchantment> h : order.get()) {
                if (h.is(Enchantments.SILK_TOUCH)) return SILK;
                if (h.is(Enchantments.FORTUNE)) return FORTUNE;
            }
        }
        return FORTUNE;
    }

    /**
     * 道具の状態を整える。金床で付け足したり、エンチャントテーブルで両方付いたりしたあとに呼ぶ。
     * 変更があったら true。
     */
    static boolean normalize(ItemStack stack, HolderLookup.Provider r) {
        if (stack.isEmpty() || stack.is(Items.ENCHANTED_BOOK) || stack.is(Items.BOOK)) return false;
        Holder<Enchantment> silk = silk(r), fortune = fortune(r);
        ItemEnchantments active = stack.getEnchantments();
        int s = active.getLevel(silk), f = active.getLevel(fortune);
        @Nullable ItemEnchantments inactive = stack.get(Components.inactive());
        @Nullable String first = stack.get(Components.first());

        // どちらも効いていない（砥石で外した・エンチャントを消した）→ しまっていた方と印も消す
        if (s == 0 && f == 0) {
            boolean changed = inactive != null || first != null;
            stack.remove(Components.inactive());
            stack.remove(Components.first());
            return changed;
        }

        if (s > 0 && f > 0) {
            // 両方効いている → 先に付いた方（なければ説明欄で上の方）を残して、もう片方をしまう
            String keep = first != null ? first : upperInTooltip(r);
            Holder<Enchantment> move = keep.equals(SILK) ? fortune : silk;
            int level = Math.max(active.getLevel(move), inactive != null ? inactive.getLevel(move) : 0);
            ItemEnchantments.Mutable m = new ItemEnchantments.Mutable(active);
            m.removeIf(h -> h.equals(move));
            stack.set(DataComponents.ENCHANTMENTS, m.toImmutable());
            stack.set(Components.inactive(), single(move, level));
            if (first == null) stack.set(Components.first(), keep);
            return true;
        }

        // 片方だけ効いている
        String activeName = s > 0 ? SILK : FORTUNE;
        boolean changed = false;
        if (first == null && inactive == null) {
            stack.set(Components.first(), activeName);
            changed = true;
        }
        if (inactive != null) {
            // しまってある方が、今効いている方と同じものになっていたら（または空なら）片付ける
            Holder<Enchantment> other = s > 0 ? fortune : silk;
            if (inactive.getLevel(other) <= 0) {
                stack.remove(Components.inactive());
                changed = true;
            }
        }
        return changed;
    }

    /** 効いている方としまってある方を入れ替える。入れ替えたら、新しく効くようになった方を返す */
    @Nullable
    static Holder<Enchantment> toggle(ItemStack stack, HolderLookup.Provider r) {
        normalize(stack, r);
        @Nullable ItemEnchantments inactive = stack.get(Components.inactive());
        if (inactive == null) return null;
        Holder<Enchantment> silk = silk(r), fortune = fortune(r);
        ItemEnchantments active = stack.getEnchantments();
        Holder<Enchantment> on = active.getLevel(silk) > 0 ? silk : fortune;   // 今効いている方
        Holder<Enchantment> off = on.equals(silk) ? fortune : silk;           // しまってある方
        int onLevel = active.getLevel(on), offLevel = inactive.getLevel(off);
        if (onLevel <= 0 || offLevel <= 0) return null;

        ItemEnchantments.Mutable m = new ItemEnchantments.Mutable(active);
        m.removeIf(h -> h.equals(on));
        m.set(off, offLevel);
        stack.set(DataComponents.ENCHANTMENTS, m.toImmutable());
        stack.set(Components.inactive(), single(on, onLevel));
        return off;
    }

    static ItemEnchantments single(Holder<Enchantment> e, int level) {
        ItemEnchantments.Mutable m = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        m.set(e, level);
        return m.toImmutable();
    }
}
