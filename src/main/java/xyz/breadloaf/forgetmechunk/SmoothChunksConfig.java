package xyz.breadloaf.forgetmechunk;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "forgetmechunk")
public class SmoothChunksConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(min = 100, max = 10000)
    public int chunkRetentionLimit = 3000;
}
