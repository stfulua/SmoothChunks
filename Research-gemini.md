# SmoothChunks Research & Plan (Minecraft 1.21.1)

## Findings
- **Vanilla Behavior**: In Minecraft 1.21.1, the client unloads lighting data via `LightingProvider.setSectionStatus(ChunkSectionPos pos, boolean notReady)` when a chunk is unloaded. This triggers expensive re-calculations when the player re-enters the chunk, causing "chunk border stutter."
- **Original Mod Logic**: The original 1.19 mod targeted `ClientPlayNetworkHandler.method_38546` (which was `onUnloadChunk` or `onChunkData` depending on sub-version) and redirected the `setSectionStatus` call to do nothing.
- **1.21.1 Mapping Changes**:
  - `ClientPlayNetworkHandler` has been refactored. The primary chunk unload logic now flows through `ClientChunkManager.ClientChunkMap.unloadChunk`.
  - The Intermediary name for `onUnloadChunk` in `ClientPlayNetworkHandler` is now `method_38541`.
- **Memory Fix Strategy**:
  - A **FIFO (First-In-First-Out) Queue** is the most efficient way to solve the memory leak. 
  - Instead of blocking *all* unloads, we buffer the last `X` unloads. When the queue overflows, the oldest lighting data is finally released.
  - This keeps recent chunks smooth for PVP and exploration while capping RAM usage at a predictable level.

## Proposed Strategy
1. **Targeting the Core**: Instead of targeting specific network handlers, we will Mixin directly into `net.minecraft.world.chunk.light.LightingProvider#setSectionStatus(ChunkSectionPos, boolean)`. This ensures ALL light unloads (from networking, manager logic, or other mods) are intercepted and managed by our buffer.
2. **Buffer Implementation**:
   - `LinkedList<ChunkSectionPos>` to store pending unloads.
   - Threshold set to `3000` sections (configurable via ModMenu/ClothConfig).
   - Logic: 
     - If `notReady == true` (unloading), add to queue and cancel the original call.
     - If queue > limit, poll the oldest and call the *actual* `setSectionStatus(oldest, true)`.
     - If `notReady == false` (loading), remove the section from the queue if present (as it's now being re-used) and allow the call to proceed.
3. **Configuration**:
   - Use **Cloth Config** and **ModMenu** for a user-friendly UI.
   - Allow setting the buffer size from `0` (mod disabled) to `20000`.

## Fixed Deprecations & Build Improvements
- Updated to **Java 21**.
- Updated **Fabric Loom** and **Gradle** configuration for 1.21.1 stability.
- Modernized `build.gradle` properties (`base.archivesName`, etc.) to resolve Gradle 9.x warnings.
- Renamed project to **SmoothChunks** for final delivery.

## Final Output Target
The build will produce `build/libs/forgetmechunk-1.1.0-1.21.X.jar` (or `SmoothChunks-1.1.0-1.21.X.jar`).
