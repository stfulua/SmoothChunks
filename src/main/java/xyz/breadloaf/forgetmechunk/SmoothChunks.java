package xyz.breadloaf.forgetmechunk;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

public class SmoothChunks implements ModInitializer {
    @Override
    public void onInitialize() {
        AutoConfig.register(SmoothChunksConfig.class, GsonConfigSerializer::new);
    }

    public static SmoothChunksConfig getConfig() {
        return AutoConfig.getConfigHolder(SmoothChunksConfig.class).getConfig();
    }
}
