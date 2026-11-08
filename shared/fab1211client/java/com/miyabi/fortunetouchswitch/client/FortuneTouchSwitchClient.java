package com.miyabi.fortunetouchswitch.client;

import com.miyabi.fortunetouchswitch.FortuneTouchSwitch;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

public class FortuneTouchSwitchClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // バニラのキラキラを読み込んで、緑と白のものを作る（リソースパックを変えたときも作り直す）
        GlintTextures textures = new GlintTextures();
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return ResourceLocation.fromNamespaceAndPath(FortuneTouchSwitch.MODID, "glint_textures");
            }

            @Override
            public void onResourceManagerReload(ResourceManager manager) {
                textures.onResourceManagerReload(manager);
            }
        });
    }
}
