package com.miyabi.fortunetouchswitch;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.List;

/** 説明欄に、しまってある方も普通のエンチャントと同じ形で表示する（バニラと同じ並び順） */
final class TooltipHandler {
    private TooltipHandler() {}

    static void register() {
        if (!net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType().equals(net.fabricmc.api.EnvType.CLIENT)) return;
        ItemTooltipCallback.EVENT.register((stack, context, flag, tip) -> {
            ItemEnchantments inactive = stack.get(FortuneTouchSwitch.INACTIVE);
            HolderLookup.Provider r = context.registries();
            if (inactive == null || r == null) return;
            Holder<Enchantment> silk = SwitchLogic.silk(r), fortune = SwitchLogic.fortune(r);
            Holder<Enchantment> off = inactive.getLevel(silk) > 0 ? silk : fortune;
            Holder<Enchantment> on = off.equals(silk) ? fortune : silk;
            int onLevel = stack.getEnchantments().getLevel(on);
            if (onLevel <= 0) return;
            String onText = Enchantment.getFullname(on, onLevel).getString();
            for (int i = 0; i < tip.size(); i++) {
                if (tip.get(i).getString().equals(onText)) {
                    boolean offIsUpper = SwitchLogic.upperInTooltip(r).equals(off.is(Enchantments.SILK_TOUCH) ? SwitchLogic.SILK : SwitchLogic.FORTUNE);
                    tip.add(offIsUpper ? i : i + 1, Enchantment.getFullname(off, inactive.getLevel(off)));
                    return;
                }
            }
        });
    }
}
