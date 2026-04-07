package xyz.breadloaf.forgetmechunk.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.light.LightingProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.breadloaf.forgetmechunk.SmoothChunks;

import java.util.ArrayDeque;
import java.util.Deque;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPacketListenerMixin {
    @Unique
    private static final Deque<ChunkSectionPos> smoothchunks$reloadQueue = new ArrayDeque<>();

    @Redirect(
        method = "updateLighting",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/chunk/light/LightingProvider;setSectionStatus(Lnet/minecraft/util/math/ChunkSectionPos;Z)V"
        )
    )
    private void smoothchunks$onSetSectionStatus(LightingProvider instance, ChunkSectionPos pos, boolean notReady) {
        if (notReady) {
            synchronized (smoothchunks$reloadQueue) {
                smoothchunks$reloadQueue.add(pos);
                int limit = SmoothChunks.getConfig().chunkRetentionLimit;
                while (smoothchunks$reloadQueue.size() > limit) {
                    ChunkSectionPos oldest = smoothchunks$reloadQueue.poll();
                    if (oldest != null) {
                        instance.setSectionStatus(oldest, true);
                    }
                }
            }
        } else {
            synchronized (smoothchunks$reloadQueue) {
                smoothchunks$reloadQueue.remove(pos);
                instance.setSectionStatus(pos, false);
            }
        }
    }
}
