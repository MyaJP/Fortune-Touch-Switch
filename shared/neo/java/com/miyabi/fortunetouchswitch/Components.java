package com.miyabi.fortunetouchswitch;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.enchantment.ItemEnchantments;

final class Components {
    private Components() {}
    static DataComponentType<ItemEnchantments> inactive() { return FortuneTouchSwitch.INACTIVE.get(); }
    static DataComponentType<String> first() { return FortuneTouchSwitch.FIRST.get(); }
}
