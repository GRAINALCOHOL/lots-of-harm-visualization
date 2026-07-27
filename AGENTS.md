# Lots Of Harm Visualization (LHV) — Agent Reference

## Project Overview

**Mod ID**: `lhv`
**Minecraft Version**: 1.20.1
**Mod Loader**: Fabric
**Java Version**: 17
**Package**: `grainalcohol.lhv`

A damage number display mod using direct Minecraft font rendering (no ETA). Features per-character effects, configurable display modes (MERGE/ALL/LATEST), and depth-based scaling/alpha. The server sends custom packets (DamageS2CPacket), so the mod must be installed on both sides.

**No need to run Gradle after writing code — Minecraft mods are not unit-testable and cannot be frequently built locally.**

## Directory Structure

```
grainalcohol.lhv/
├── LHVMod.java                    # Server-side initializer (empty)
├── LHVModMenu.java                # ModMenu integration
├── internal/
│   ├── LHVGlyphRenderer.java      # Interface for glyph render mixin
│   └── CriticalArgController.java # Critical argument control
├── client/
│   ├── LHVModClient.java          # Client init, offset/range/color computation
│   ├── ClientEventListener.java   # HudRenderCallback → DamageRouter.render()
│   ├── ClientPacketHandler.java   # Packet → DamageRouter.handleDamage()
│   ├── display/
│   │   ├── DamageRouter.java      # Routes damage per UUID → Manager (HashMap)
│   │   ├── TextDisplay.java       # Per-character rendering, outline, clamp, effects, textAlpha
│   │   ├── renderer/
│   │   │   ├── DamageRenderer.java        # Interface: handleDamage(DamageInfo)
│   │   │   ├── BaseDamageRenderer.java    # Abstract: slot mgmt, world→screen, depth scale/alpha
│   │   │   ├── SingleRenderer.java        # LATEST/ALL: replace damage
│   │   │   ├── MergeRenderer.java         # MERGE: accumulative
│   │   │   └── ListRenderer.java          # ALL: TreeMap<Long, SingleRenderer> by timestamp
│   │   ├── manager/
│   │   │   ├── RendererManager.java          # Interface: handleDamage / render / isExpired
│   │   │   └── RendererManagerImpl.java      # Impl: RENDERERS (EnumMap), RENDERER_ORDER, yaw tracking
│   │   └── func/
│   │       ├── DamageHandler.java        # (Double, Boolean) → merge/replace logic
│   │       └── TextDisplayHandler.java   # Callback type for TextDisplaySlot events
│   ├── wrapper/
│   │   ├── TextDisplaySlot.java     # Lifecycle, position/scale/alpha delegation
│   │   └── StyledText.java          # Text + ColorScheme
│   ├── subtext/
│   │   ├── SubTextProviders.java    # Kill display etc.
│   │   ├── SubTextProvider.java     # Interface for subtext computation
│   │   └── SubTextPriority.java     # Priority ordering for subtext providers
│   └── effect/
│       ├── Effect.java             # Interface for all effects
│       ├── BaseEffect.java         # Abstract base with lifecycle management
│       ├── CharSetting.java        # Per-character mutable fields (x, y, rot, scale, alpha, color)
│       ├── DisplayContext.java     # Context struct passed during render
│       ├── QuadColorField.java      # Uniform / gradient color field
│       └── effects/
│           ├── BounceEffect.java
│           ├── FadeInEffect.java
│           ├── FlashInEffect.java
│           ├── PulseEffect.java
│           ├── SettleEffect.java
│           ├── ShrinkEffect.java
│           ├── SimpleTypewriterEffect.java
│           ├── SpringEffect.java
│           └── SweepEffect.java
├── common/
│   ├── dto/
│   │   ├── LHVConfig.java          # Config DTO (sourceType, renderMode, ranges, offsets, format, colors, outline, scale, alpha, damageTypeColors...)
│   │   ├── DamageInfo.java         # Per-hit: damageAmount, isCritical, subText, damageColor only
│   │   ├── DamageContext.java      # Raw damage data from packet (victimUuid, etc.) implements FabricPacket
│   │   └── ScreenPosition.java     # x, y, cameraDepth + depthToScale/Alpha, offsetWithDepth()
│   ├── enums/
│   │   ├── SourceType.java         # PLAYER, ENTITY, ENVIRONMENT — each linked to config supplier
│   │   ├── RenderMode.java         # MERGE, ALL, LATEST
│   │   ├── FormatMode.java         # SCIENTIFIC, UNIT, AUTO, RAW — each has Supplier<DamageFormatter>
│   │   └── UnitSystem.java         # SHORT_SCALE, METRIC_PREFIX, LONG_SCALE
│   ├── format/
│   │   ├── DamageFormatter.java    # Base: DecimalFormat patterns, formatting orchestration
│   │   ├── ScientificFormatter.java    # Always scientific notation
│   │   ├── UnitFormatter.java         # Always unit suffix (K/M/B/T…)
│   │   ├── AutoFormatter.java         # Smart: unit for < 1T, scientific beyond
│   │   ├── RawFormatter.java          # Always raw decimal
│   │   └── unit/
│   │       ├── Units.java             # Interface for unit definitions
│   │       ├── DecimalUnit.java       # Single unit entry (name, size)
│   │       ├── ShortScaleUnits.java   # Short-scale units provider
│   │       ├── LongScaleUnits.java    # Long-scale units provider
│   │       └── MetricPrefixUnits.java # SI prefix units provider
│   ├── network/
│   │   └── DamageS2CPacket.java    # Server→Client custom packet definition
│   └── util/
│       ├── ScreenUtil.java         # worldToScreen() projection (with occlusion check support)
│       ├── NetworkUtil.java        # Vec3d read/write helpers for packet byte buf
│       ├── ColorUtil.java          # Color manipulation helpers (lerp, luminance, brightness)
│       └── FovCache.java           # Cached FOV tanHalf values for projection
├── config/
│   ├── PlayerConfig.java           # YACL config for SourceType.PLAYER (saved to config/lhv/player.json5)
│   ├── EntityConfig.java           # YACL config for SourceType.ENTITY (saved to config/lhv/entity.json5)
│   ├── EnvConfig.java              # YACL config for SourceType.ENVIRONMENT (saved to config/lhv/env.json5)
│   ├── GlobalConfig.java           # Global YACL settings (bigNumberTestMode, ignoreDamageTypes)
│   └── LHVConfigSupplier.java      # Interface: toConfig() → LHVConfig
└── mixin/
    ├── DamageSourceMixin.java      # Server-side damage source tracking
    ├── LivingEntityMixin.java      # Server-side entity death hook
    ├── PlayerEntityMixin.java      # Server-side player damage hook
    ├── client/
    │   ├── GlyphRendererMixin.java # Per-character scale/position via lhv$render
    │   ├── GlyphRendererAccessor.java # Accessor for GlyphRenderer internals
    │   └── FontAccess.java         # Accessor for FontStorage lookup
    └── accessor/
        └── WorldEntityLookupInvoker.java  # Accessor for entity lookup
```

## Data Flow

```
Server → Packet → ClientPacketHandler
  → DamageRouter.handleDamage(DamageContext)
    → ignoreType / world/player null checks
    → entity = ((WorldEntityLookupInvoker) client.world).invokeGetEntityLookup().get(victimUuid)
    → isInReceiveRange filter (max distance only)
    → LHVModClient.findDamageColor / SubTextProviders.compute
    → creates DamageInfo (per-hit only: damageAmount, isCritical, subText, damageColor)
    → Router.handleDamage(SourceType, UUID, victimYaw, worldPos, DamageInfo)
      → MANAGERS.computeIfAbsent(uuid) → new RendererManagerImpl(verticalOffset, victimYaw, worldPos)
        → manager.handleDamage(sourceType, victimYaw, worldPos, damageInfo)
          → latestWorldPos/latestYaw = current values
          → RENDERERS.computeIfAbsent(sourceType, k → config.createRenderer())
          → renderer.handleDamage(damageInfo)
            → handler.accept(damageInfo.getDamageAmount(), damageInfo.isCritical())
            → mainSlot.setText(getStyledDamage(damageInfo.getDamageColor()))
            → subSlot.setText(damageInfo.getSubText())  [if non-null]
            → initialized = true
          → RENDERER_ORDER.remove(sourceType); RENDERER_ORDER.add(sourceType)

HudRenderCallback → DamageRouter.render(DrawContext, tickDelta)
  → MANAGERS.entrySet()
    → isExpired() → iterator.remove()
    → skip if FIRST_PERSON && victimUuid == localPlayer
    → entity = ((WorldEntityLookupInvoker) client.world).invokeGetEntityLookup().get(uuid)
    → entity != null → manager.render(drawContext, lerpedPos, lerpedYaw)
      → RendererManagerImpl.render(drawContext, lerpedPos, lerpedYaw)
        → RENDERER_ORDER iteration (newest SourceType last → on top)
          → if expired: replace with null; continue
          → if isTrackEntity: update latestWorldPos/latestYaw from lerped values
          → getRendererPos(worldPos)  [adds verticalOffset only]
          → getYawDelta(victimYaw)    [victimYawOnCreation - victimYaw]
          → renderer.render(drawContext, rendererPos, yawDelta)
            → updateWorldPos(worldPos, yawDelta)   [always runs, regardless of range]
              → getRenderPos(worldPos, yawDelta)  [rotates worldOffset by Math.toRadians(yawDelta)]
              → ScreenUtil.worldToScreen() → ScreenPosition
              → if on-screen:
                  setScreenPos / setScale(depthToScale) / setAlpha(depthToAlpha)
                else if hasBeenOnScreen && retainWhenOffScreen:
                  use latestScreenPos, setScale(depthToScale, 0.6f), mainSlot.setAlpha, subSlot.setAlpha(0)
                else:
                  setInvisible()  [alpha = 0]
            → if isInRenderRange(worldPos, playerPos):  [gates only slot drawing, not updateWorldPos]
              → mainSlot.render() → subSlot.render()
                → TextDisplay.render() → per-character effects + GlyphRendererMixin
    → entity == null → manager.render(drawContext)  [use last known pos/yaw]
      → same iteration, always uses latestWorldPos/latestYaw
```

### Three-Layer Render Order (newest on top)

| Layer | Structure | Order |
|---|---|---|---|
| ListRenderer | `TreeMap<Long, SingleRenderer>` | Keyed by `Util.getMeasuringTimeMs()`, ascending → newest last |
| RendererManagerImpl | `LinkedHashSet<SourceType> RENDERER_ORDER` | `remove + add` on each `handleDamage` → newest SourceType last |
| DamageRouter | `HashMap<UUID, RendererManager>` | No ordering (random) |

## Key Fixes / Decisions

- **`handleDamage()` bug**: `BaseDamageRenderer` handler was called with `this.damageAmount`/`this.isCritical` (fields, init 0) instead of `damageInfo.getDamageAmount()`/`damageInfo.isCritical()`. Fixed.
- **Yaw delta units**: `getRenderPos()` was passing raw degrees to `Math.cos/sin` which expect radians. Fixed with `Math.toRadians(yawDelta)`.
- **NPE in off-screen branch**: `updateWorldPos()` used `screenPosition` (null) instead of `latestScreenPos` for `depthToAlpha` in the `else if` branch. Fixed.
- **Router receive vs render range**: Router used `isInRenderRange` (min+max) instead of `isInReceiveRange` (max only), dropping damage outside min distance entirely. Fixed — router only filters receive, renderer does the visibility check.
- **Outline concentric sampling**: Outer ring 16 dirs + inner rings at step=1.0 for smooth outline
- **Per-glyph scale**: `GlyphRendererMixin.lhv$render()` applies `setting.widthScale/heightScale` via center-based vertex transform
- **ScreenOffset non-linear**: `shapedOffset()` uses quadratic distribution (exponent=2)
- **`depthToScale()`**: `clamp(refDist / cameraDepth, minScale, maxScale)` — now configurable per SourceType
- **`depthToAlpha()`**: `clamp(cameraDepth / disToAlphaRef, minAlpha, maxAlpha)` — close = transparent, far = opaque
- **`offsetWithDepth()`**: Linear depth decay `clamp(refDist / cameraDepth, 0, 1)`
- **`cameraDepth`**: Horizontal forward depth in camera space (ignores Y), NOT Euclidean distance. `Vec3d dot camera.getHorizontalPlane()` — only XZ forward component.
- **`BounceEffect` pivot**: Pivot was `textWidth/2` (screen origin-relative), not `screenX` (actual text center). Fixed.
- **`SourceType.getConfig()` pattern**: Each SourceType links to its own config supplier via method reference (`PlayerConfig::getConfig`), enabling per-source-type independent settings
- **`FormatMode` → `DamageFormatter`**: Each FormatMode holds a `Supplier<DamageFormatter>`, allowing clean map-style delegation instead of switch chains
- **`GlobalConfig.ignoreDamageTypes`**: Stored as `Set<String>` of damage type IDs; filters at router entry before any rendering logic
- **`SubTextProviders` priority system**: Uses `EnumMap<SubTextPriority, List<SubTextProvider>>` with priority-based short-circuit; kill display is the only active provider

## Config System

Three per-source-type configs (PlayerConfig, EntityConfig, EnvConfig) implement `LHVConfigSupplier` and produce `LHVConfig` instances. Each YACL config has 31 fields organized in four categories, mirrored in `LHVConfig`:

- **General** (12): `renderMode`, `minVisibleRange`, `maxVisibleRange`, `trackEntity`, `retainWhenOffScreen`, `displayDuration`, `maxReceiveRange`, `screenOffsetRangeX/Y`, `offsetRangeX/Y/Z`
- **Format** (14): `formatMode`, `retainDecimalPlaces`, `infinityDisplay`, `nanDisplay`, `unitSystem`, `useGrouping`, `groupingSeparator`, `decimalSeparator`, `exponentSeparator`, `positivePrefix`, `negativePrefix`, `positiveSuffix`, `negativeSuffix`, `roundingMode`
- **Custom** (15): `killDisplay`, `defaultColor`, `criticalColor`, `criticalFormat`, `outlineEnable`, `outlineColor`, `outlineWidth`, `depthToScaleRef`, `minScale`, `maxScale`, `depthToAlphaRef`, `minAlpha`, `maxAlpha`, `punchyEffectEnable`, `damageTypeColors` (`Map<String, String>`, 40+ default entries keyed by damage type ID)

`GlobalConfig` stores `bigNumberTestMode: boolean` and `ignoreDamageTypes: Set<String>` (default: `minecraft:out_of_world`, `minecraft:in_wall`, `minecraft:cramming`) at `config/lhv/global.json5`.

`SourceType` enum links each type to its config supplier: `PLAYER(PlayerConfig::getConfig)` → `LHVConfig`.

All fields are actively consumed in render/format paths.

## Code Conventions

- No Javadoc on implementation methods unless explicitly required; code is self-documenting
- `@Contract`, `@NotNull`, `@Nullable` from JetBrains annotations
- Lombok `@Getter`, `@AllArgsConstructor` on DTOs
- Record types for value objects (`ScreenPosition`)
- Mixin method prefix best practice: `lhv$` for all Mixin-added methods
- `@Unique` on all Mixin-added members
- No wildcard imports; explicit imports throughout
- Don't add `AGENTS.md` or any `*.md` documentation unless explicitly requested
