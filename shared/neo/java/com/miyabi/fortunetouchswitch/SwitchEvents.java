package com.miyabi.fortunetouchswitch;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;

public final class SwitchEvents {
    private SwitchEvents() {}

    /** 何もないところを右クリック → シルクタッチと幸運を切り替える */
    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();
        if (stack.get(Components.inactive()) == null && !(hasBoth(stack, player))) return;
        // ブロックやエンティティを狙っているときは切り替えない
        if (player.pick(player.blockInteractionRange(), 1.0F, false).getType() != HitResult.Type.MISS) return;

        if (!player.level().isClientSide()) {
            Holder<Enchantment> now = SwitchLogic.toggle(stack, player.level().registryAccess());
            if (now == null) return;
            player.level().playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.5F, 1.6F);
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    private static boolean hasBoth(ItemStack stack, Player player) {
        HolderLookup.Provider r = player.level().registryAccess();
        ItemEnchantments e = stack.getEnchantments();
        return e.getLevel(SwitchLogic.silk(r)) > 0 && e.getLevel(SwitchLogic.fortune(r)) > 0;
    }

    /** 金床やエンチャントテーブルで両方付いたら、0.5秒ごとに整える */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide() || player.tickCount % 10 != 0) return;
        HolderLookup.Provider r = player.level().registryAccess();
        var inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty() && stack.isEnchanted()) SwitchLogic.normalize(stack, r);
            else if (!stack.isEmpty() && stack.get(Components.inactive()) != null) SwitchLogic.normalize(stack, r);
        }
    }

    /** 説明欄に、しまってある方も普通のエンチャントと同じ形で表示する（バニラと同じ並び順） */
    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        ItemEnchantments inactive = stack.get(Components.inactive());
        HolderLookup.Provider r = event.getContext().registries();
        if (inactive == null || r == null) return;
        Holder<Enchantment> silk = SwitchLogic.silk(r), fortune = SwitchLogic.fortune(r);
        Holder<Enchantment> off = inactive.getLevel(silk) > 0 ? silk : fortune;
        Holder<Enchantment> on = off.equals(silk) ? fortune : silk;
        int onLevel = stack.getEnchantments().getLevel(on);
        if (onLevel <= 0) return;

        List<Component> tip = event.getToolTip();
        String onText = Enchantment.getFullname(on, onLevel).getString();
        for (int i = 0; i < tip.size(); i++) {
            if (tip.get(i).getString().equals(onText)) {
                boolean offIsUpper = SwitchLogic.upperInTooltip(r).equals(off.is(net.minecraft.world.item.enchantment.Enchantments.SILK_TOUCH) ? SwitchLogic.SILK : SwitchLogic.FORTUNE);
                tip.add(offIsUpper ? i : i + 1, Enchantment.getFullname(off, inactive.getLevel(off)));
                return;
            }
        }
    }
}
