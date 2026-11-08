package com.miyabi.fortunetouchswitch;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(FortuneTouchSwitch.MODID)
public class FortuneTouchSwitch {
    public static final String MODID = "fortune_touch_switch";
    public static final org.slf4j.Logger LOGGER = com.mojang.logging.LogUtils.getLogger();

    public static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MODID);

    /** 今は効いていない方のエンチャント（シルクタッチか幸運のどちらか1つ） */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemEnchantments>> INACTIVE =
            COMPONENTS.registerComponentType("inactive_enchantment",
                    b -> b.persistent(ItemEnchantments.CODEC).networkSynchronized(ItemEnchantments.STREAM_CODEC));

    /** 先に付いた方（"silk_touch" か "fortune"）。両方そろったとき、最初に効く方になる */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> FIRST =
            COMPONENTS.registerComponentType("first_enchantment",
                    b -> b.persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8));

    public FortuneTouchSwitch(IEventBus modBus) {
        COMPONENTS.register(modBus);
        NeoForge.EVENT_BUS.register(SwitchEvents.class);
    }
}
