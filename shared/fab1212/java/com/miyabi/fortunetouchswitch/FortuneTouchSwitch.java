package com.miyabi.fortunetouchswitch;

import com.mojang.serialization.Codec;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.phys.HitResult;

public class FortuneTouchSwitch implements ModInitializer {
    public static final String MODID = "fortune_touch_switch";

    /** 今は効いていない方のエンチャント（シルクタッチか幸運のどちらか1つ） */
    public static final DataComponentType<ItemEnchantments> INACTIVE = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
            ResourceLocation.fromNamespaceAndPath(MODID, "inactive_enchantment"),
            DataComponentType.<ItemEnchantments>builder().persistent(ItemEnchantments.CODEC).networkSynchronized(ItemEnchantments.STREAM_CODEC).build());

    /** 先に付いた方（"silk_touch" か "fortune"）。両方そろったとき、最初に効く方になる */
    public static final DataComponentType<String> FIRST = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
            ResourceLocation.fromNamespaceAndPath(MODID, "first_enchantment"),
            DataComponentType.<String>builder().persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build());

    @Override
    public void onInitialize() {
        // 何もないところを右クリック → シルクタッチと幸運を切り替える
        UseItemCallback.EVENT.register((player, level, hand) -> {
            ItemStack stack = player.getItemInHand(hand);
            if (!canToggle(player, stack)) return InteractionResult.PASS;
            if (!level.isClientSide()) {
                Holder<Enchantment> now = SwitchLogic.toggle(stack, level.registryAccess());
                if (now == null) return InteractionResult.PASS;
                level.playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.5F, 1.6F);
            }
            return InteractionResult.SUCCESS;
        });

        // 金床やエンチャントテーブルで両方付いたら、0.5秒ごとに整える
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTickCount() % 10 != 0) return;
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                HolderLookup.Provider r = player.level().registryAccess();
                var inv = player.getInventory();
                for (int i = 0; i < inv.getContainerSize(); i++) {
                    ItemStack stack = inv.getItem(i);
                    if (!stack.isEmpty() && (stack.isEnchanted() || stack.get(INACTIVE) != null)) SwitchLogic.normalize(stack, r);
                }
            }
        });

        TooltipHandler.register();
    }

    private static boolean canToggle(Player player, ItemStack stack) {
        if (stack.isEmpty()) return false;
        HolderLookup.Provider r = player.level().registryAccess();
        ItemEnchantments e = stack.getEnchantments();
        boolean both = e.getLevel(SwitchLogic.silk(r)) > 0 && e.getLevel(SwitchLogic.fortune(r)) > 0;
        if (stack.get(INACTIVE) == null && !both) return false;
        // ブロックやエンティティを狙っているときは切り替えない
        return player.pick(player.blockInteractionRange(), 1.0F, false).getType() == HitResult.Type.MISS;
    }
}
