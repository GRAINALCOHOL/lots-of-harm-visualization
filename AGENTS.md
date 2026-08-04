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
├── LHVMod.java                    # Server init; handleDamage() — damage classification/flags/killTime/packet send
├── LHVModMenu.java                # ModMenu integration
├── client/
│   ├── LHVModAPI.java             # Public API: register/findSourceType, initForVictim, getRendererManager, handleDamage/handleText, clearDamage
│   ├── LHVModClient.java          # Client init, default colors/ignore types, verticalOffset, attackFromOtherPlayer
│   ├── ClientEventListener.java   # HudRenderCallback → DamageRouter.render()
│   ├── ClientPacketHandler.java   # Packet → DamageRouter.handleDamage()
│   ├── display/
│   │   ├── DamageRouter.java      # Routes damage/text per UUID → Manager; initForVictim/getManager/computeRenderStream
│   │   ├── TextDisplay.java       # Per-character rendering, outline, clamp, effects, textAlpha
│   │   ├── renderer/
│   │   │   ├── WorldTextRenderer.java      # Interface: setStatus/render/isExpired
│   │   │   ├── BaseWorldTextRenderer.java  # Abstract: worldOffset rotation, render-range gate, ScreenTextRenderer delegation
│   │   │   ├── TextRenderer.java           # Single StyledText world renderer
│   │   │   ├── ListTextRenderer.java       # ArrayDeque<TextRenderer> pool, DamageSortMode ordering
│   │   │   ├── ScreenTextRenderer.java     # world→screen, depth scale/alpha, off-screen retain
│   │   │   └── damage/
│   │   │       ├── DamageRenderer.java        # Interface: WorldTextRenderer<DamageInfo>
│   │   │       ├── BaseDamageRenderer.java    # Abstract: DecimalValue accumulate, color/critical, rainbow-on-infinity
│   │   │       ├── SingleRenderer.java        # LATEST: replace damage
│   │   │       ├── MergeRenderer.java         # MERGE: accumulative
│   │   │       └── ListRenderer.java          # ALL: ArrayDeque<SingleRenderer> pool
│   │   ├── manager/
│   │   │   ├── RendererManager.java          # Interface: handleDamage/handleText/render/isExpired/getLatestWorldPos
│   │   │   └── RendererManagerImpl.java      # Impl: DAMAGES + TEXTS maps, RENDERER_ORDER, computeRenderOrder
│   │   └── func/
│   │       ├── DamageHandler.java        # (Double, Boolean) → merge/replace logic
│   │       ├── CriticalHandler.java      # Critical flag handling
│   │       └── TextDisplayHandler.java   # Callback type for TextDisplaySlot events
│   ├── wrapper/
│   │   ├── TextDisplaySlot.java     # Lifecycle, position/scale/alpha delegation, onCreated/onChanged handlers
│   │   └── StyledText.java          # Text + ColorScheme
│   ├── text/
│   │   ├── TextProvider.java        # Interface: provide(DamageContext) → StyledText
│   │   └── TextProviders.java       # Registry: killTime/DIED_FLAG → instantKill/kill display
│   └── effect/
│       ├── Effect.java             # Interface for all effects (incl. restartIfFinished)
│       ├── BaseEffect.java         # Abstract base with lifecycle management
│       ├── CharSetting.java        # Per-character mutable fields (x, y, rot, scale, alpha, color)
│       ├── DisplayContext.java     # Context struct passed during render
│       ├── QuadColorField.java     # Uniform / gradient color field
│       └── effects/
│           ├── BounceEffect.java
│           ├── FlashInEffect.java
│           ├── PulseEffect.java
│           ├── SettleEffect.java
│           ├── ShrinkEffect.java
│           ├── SimpleTypewriterEffect.java
│           ├── SpringEffect.java
│           └── SweepEffect.java
├── common/
│   ├── dto/
│   │   ├── config/
│   │   │   ├── BasicConfig.java        # renderMode/damageSortMode/trackEntity/ranges + createRenderer/isInRenderRange/isInReceiveRange
│   │   │   ├── FormatConfig.java       # formatMode/unitSystem/roundingMode/decimalPlaces/infinity/nan + SymbolConfig + createFormatter
│   │   │   ├── DisplayConfig.java      # kill/instantKill/colors/criticalTemplate/OutlineSetting/OffsetSetting/depth refs/effectTemplate/damageTypeColors
│   │   │   ├── OffsetSetting.java      # screen/world offset ranges + computeScreenOffset/computeWorldOffset
│   │   │   ├── OutlineSetting.java     # enabled/color/width
│   │   │   └── SymbolConfig.java       # useGrouping/separators/prefixes/suffixes
│   │   ├── DamageInfo.java         # Per-hit: damageAmount, isCritical, damageColor only
│   │   ├── DamageContext.java      # Packet data: sourceType/attackerUuid/victimUuid/damageTypeId/killTime/damageFlags
│   │   ├── DecimalValue.java       # IEEE754-style decimal: FINITE/±∞/NaN via BigDecimal
│   │   └── ScreenPosition.java     # x, y, cameraDepth (FOV-baked) + depthToScale/Alpha, offsetWithDepth()
│   ├── source/
│   │   ├── SourceType.java         # Interface: getId/getBasicConfig/getFormatConfig/getDisplayConfig
│   │   ├── SourceTypes.java        # Static registry: PLAYER/ENTITY/ENVIRONMENT; register/getSourceType
│   │   ├── PlayerSourceType.java   # Impl (EffectTemplates.SPRING)
│   │   ├── EntitySourceType.java   # Impl (EffectTemplates.COMMON)
│   │   └── EnvironmentSourceType.java # Impl (EffectTemplates.COMMON)
│   ├── enums/
│   │   ├── RenderMode.java         # MERGE, ALL, LATEST
│   │   ├── DamageSortMode.java     # LATEST, OLDEST
│   │   ├── EntitySortMode.java     # RANDOM, NEAREST, FARTHEST
│   │   ├── SourceSortMode.java     # LATEST, OLDEST
│   │   ├── FormatMode.java         # SCIENTIFIC, UNIT, AUTO, RAW — each has Function<FormatConfig, DamageFormatter>
│   │   └── UnitSystem.java         # SHORT_SCALE, METRIC_PREFIX, LONG_SCALE
│   ├── format/
│   │   ├── DamageFormatter.java    # Base: DecimalFormat patterns; format(DecimalValue), ∞/NaN handled
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
│   ├── template/
│   │   ├── EffectTemplate.java     # Interface: getId + onCreated/onChanged TextDisplayHandler
│   │   ├── EffectTemplates.java    # Registry: SPRING/COMMON, register/getTemplate
│   │   ├── CommonTemplate.java     # flash+shrink+typewriter+settle
│   │   └── SpringTemplate.java     # + pulse+spring
│   ├── network/
│   │   └── DamageS2CPacket.java    # Server→Client custom packet definition
│   └── util/
│       ├── ScreenUtil.java         # worldToScreen() with FOV-baked cameraDepth
│       ├── NetworkUtil.java        # Vec3d read/write helpers for packet byte buf
│       ├── ColorUtil.java          # Color manipulation helpers (lerp, luminance, brightness)
│       └── FovCache.java           # Cached render FOV + zoom magnification
├── config/
│   ├── PlayerConfig.java           # YACL config → PlayerSourceType (config/lhv/player.json5)
│   ├── EntityConfig.java           # YACL config → EntitySourceType (config/lhv/entity.json5)
│   ├── EnvConfig.java              # YACL config → EnvironmentSourceType (config/lhv/env.json5)
│   ├── GlobalConfig.java           # entitySortMode/sourceSortMode/receiveOtherPlayer/test modes/ignoreDamageTypes
│   └── LHVConfigSupplier.java      # Interface: getBasicConfig/getFormatConfig/getDisplayConfig/clearCache
├── flag/
│   ├── FlagContext.java            # DamageSource/victim/damageAmount/isCritical
│   ├── FlagProvider.java           # FunctionalInterface: provide(FlagContext) → Set<String>
│   └── FlagProviders.java          # Registry: compute(), DIED_FLAG provider
├── internal/
│   ├── LHVGlyphRenderer.java       # Interface for glyph render mixin
│   ├── CriticalArgController.java  # Critical argument control
│   └── DamageTimeAccessor.java     # Accessor for first-damage-time tracking
└── mixin/
    ├── DamageSourceMixin.java      # Server-side damage source tracking
    ├── LivingEntityMixin.java      # Implements DamageTimeAccessor; applyDamage hook → LHVMod.handleDamage
    ├── PlayerEntityMixin.java      # Player applyDamage hook → LHVMod.handleDamage
    ├── client/
    │   ├── GlyphRendererMixin.java # Per-character scale/position via lhv$render
    │   ├── GlyphRendererAccessor.java # Accessor for GlyphRenderer internals
    │   ├── FontAccess.java         # Accessor for FontStorage lookup
    │   └── GameRendererFovMixin.java # Captures actual render FOV into FovCache
    └── accessor/
        └── WorldEntityLookupInvoker.java  # Accessor for entity lookup
```

## Data Flow

```
Server (mixin hooks)
  LivingEntityMixin / PlayerEntityMixin.afterApplyDamage
    → LHVMod.handleDamage(victim, source, amount)
      → resolveCritical(source)   [CriticalArgController + playerex arrow]
      → FlagContext + FlagProviders.compute → damageFlags  [DIED_FLAG if victim.isDead()]
      → classify attacker → sourceType (PLAYER/ENTITY/ENVIRONMENT) + attackerUuid
      → avaritia infinity sword → damageAmount = +∞, add DIED_FLAG
      → resolveKillTime(victim)  [first-damage-time via DamageTimeAccessor; 10s reset; -1 if alive]
      → new DamageContext(...) → DamageS2CPacket.sendToAllPlayers(world, ctx)

ClientPacketHandler → DamageRouter.handleDamage(DamageContext)
  → GlobalConfig.shouldIgnore(damageTypeId) / world/player null checks
  → receiveOtherPlayer filter: attackerUuid != null && !receiveOtherPlayer && attackFromOtherPlayer → skip
  → entity lookup by victimUuid (WorldEntityLookupInvoker); null → skip
  → isInReceiveRange filter (max distance only)
  → DamageInfo(damageAmount, isCritical, displayConfig.findColor(damageTypeId))
  → initForVictim(victimUuid, entity.getYaw(), entity.getPos())
  → Router.handleDamage(sourceType, victimUuid, damageInfo)
      → manager.handleDamage → DAMAGES.getOrCreate(basicConfig.createRenderer(sourceType)) → setStatus(damageInfo)
  → Router.handleText(sourceType, victimUuid, TextProviders.compute(damageContext))
      → killTime ∈ [0, 20] → instantKillDisplay; DIED_FLAG → killDisplay; else empty
      → manager.handleText → TEXTS.getOrCreate(new ListTextRenderer) → setStatus(texts)
  → RENDERER_ORDER.remove(sourceType); RENDERER_ORDER.add(sourceType)

HudRenderCallback → DamageRouter.render(DrawContext, tickDelta)
  → remove expired managers
  → skip if FIRST_PERSON && victimUuid == localPlayer
  → computeRenderStream (EntitySortMode: RANDOM / NEAREST / FARTHEST by squared distance)
  → entity != null → manager.render(drawContext, lerpedPos, lerpedYaw)
      → latestWorldPos/latestYaw = lerped values
      → computeRenderOrder (SourceSortMode on RENDERER_ORDER: LATEST / OLDEST)
      → renderFor(TEXTS) then renderFor(DAMAGES) for each SourceType
          → if trackEntity: render at current lerped pos/yaw; else at latestWorldPos/latestYaw
          → getRendererPos(worldPos)  [adds verticalOffset only]
          → getYawDelta(victimYaw)    [victimYawOnCreation - victimYaw]
          → WorldTextRenderer.render(drawContext, rendererPos, yawDelta)
            → BaseWorldTextRenderer: isInRenderRange(worldPos, playerPos) gates drawing
              → ScreenTextRenderer.render(drawContext, renderPos)
                → ScreenUtil.worldToScreen → ScreenPosition (cameraDepth ÷ zoomMagnification)
                → if on-screen:
                    setScreenPos / setScale(depthToScale) / setAlpha(depthToAlpha)
                  else if hasBeenOnScreen && retainWhenOffScreen:
                    latestScreenPos, setScale(depthToScale * 0.6), setAlpha(depthToAlpha)
                  else:
                    setAlpha(0)  [invisible]
                → TextDisplaySlot.render → TextDisplay.render
                  → per-character effects + GlyphRendererMixin
    → entity == null → manager.render(drawContext)  [latestWorldPos/latestYaw]
```

### Render Order Layers (newest on top)

| Layer | Structure | Order |
|---|---|---|
| damage ListRenderer | `ArrayDeque<SingleRenderer>` pool | `setStatus` appends; `computeRenderList` by `DamageSortMode` (LATEST/OLDEST) |
| text ListTextRenderer | `ArrayDeque<TextRenderer>` pool | Same pattern as damage `ListRenderer` |
| RendererManagerImpl | `DAMAGES` + `TEXTS` maps, `LinkedHashSet<SourceType> RENDERER_ORDER` | `computeRenderOrder()` by `SourceSortMode` (LATEST/OLDEST) |
| DamageRouter | `HashMap<UUID, RendererManager>` | `computeRenderStream()` by `EntitySortMode` (RANDOM/NEAREST/FARTHEST via squared distance) |

## Key Fixes / Decisions

- **`handleDamage()` bug**: `BaseDamageRenderer` handler was called with `this.damageAmount`/`this.isCritical` (fields, init 0) instead of `damageInfo.getDamageAmount()`/`damageInfo.isCritical()`. Fixed.
- **Yaw delta units**: `getRenderPos()` was passing raw degrees to `Math.cos/sin` which expect radians. Fixed with `Math.toRadians(yawDelta)`.
- **NPE in off-screen branch**: `updateWorldPos()` used `screenPosition` (null) instead of `latestScreenPos` for `depthToAlpha` in the `else if` branch. Fixed.
- **Router receive vs render range**: Router used `isInRenderRange` (min+max) instead of `isInReceiveRange` (max only), dropping damage outside min distance entirely. Fixed — router only filters receive, renderer does the visibility check.
- **Outline concentric sampling**: Outer ring 16 dirs + inner rings at step=1.0 for smooth outline
- **Per-glyph scale**: `GlyphRendererMixin.lhv$render()` applies `setting.widthScale/heightScale` via center-based vertex transform
- **ScreenOffset non-linear**: `OffsetSetting.normalOffset()` uses quadratic distribution (exponent=2)
- **`depthToScale()`**: `clamp(refDist / cameraDepth, minScale, maxScale)` — configurable per SourceType
- **`depthToAlpha()`**: `clamp(cameraDepth / disToAlphaRef, minAlpha, maxAlpha)` — close = transparent, far = opaque
- **`offsetWithDepth()`**: Linear depth decay `clamp(refDist / cameraDepth, 0, 1)`
- **`cameraDepth`**: Horizontal forward depth in camera space (ignores Y), NOT Euclidean distance. `Vec3d dot camera.getHorizontalPlane()` — only XZ forward component.
- **`BounceEffect` pivot**: Pivot was `textWidth/2` (screen origin-relative), not `screenX` (actual text center). Fixed.
- **Server damage logic centralized in `LHVMod`**: Mixins slimmed to just hook `applyDamage` and call `LHVMod.handleDamage()` — source classification, flags, killTime, avaritia handling all moved server-side. Packets are now broadcast via `sendToAllPlayers`; clients filter with `receiveOtherPlayer` + `attackerUuid`.
- **killTime instant-kill**: `DamageTimeAccessor` tracks per-entity first-damage time (reset after 10s idle). `killTime ∈ [0, 20]` ticks → `instantKillDisplay`; otherwise `DIED_FLAG` → `killDisplay`.
- **`DecimalValue`**: IEEE754-style accumulator (BigDecimal for finite, enum for ±∞/NaN) used by Merge/Single renderers. `add()` implements special-value rules (e.g. inf + -inf = NaN); infinity triggers rainbow effect.
- **Renderer abstraction**: `WorldTextRenderer`/`BaseWorldTextRenderer`/`ScreenTextRenderer` separate world-space transform + projection from damage logic; damage renderers live in `renderer/damage/`. `ListRenderer` is an `ArrayDeque<SingleRenderer>` pool (was `TreeMap<Long>`), ordered per-frame by `DamageSortMode`.
- **FOV baked into cameraDepth**: `GameRendererFovMixin` captures the actual render FOV (spyglass/TACZ zoom included); `ScreenUtil` divides `cameraDepth` by `getZoomMagnification()` (pow 0.7 compression). `depthToScale/Alpha/offsetWithDepth` become zoom-aware automatically; `ScreenPosition` dropped its 4th `fovMagnification` component.
- **`EffectTemplate` pattern**: Templates (SPRING/COMMON) provide `onCreated`/`onChanged` handlers; `TextDisplay.getOrPutEffect()` + `restartIfFinished()` restarts Bounce/Sweep when text changes.
- **`SourceType` interface pattern**: Each SourceType implements `getBasicConfig/getFormatConfig/getDisplayConfig` backed by its own config supplier; `SourceTypes` static registry.
- **`FormatMode` → `DamageFormatter`**: Each FormatMode holds a `Function<FormatConfig, DamageFormatter>`, allowing clean map-style delegation instead of switch chains
- **`GlobalConfig.ignoreDamageTypes`**: Stored as `Set<String>` of damage type IDs; filters at router entry before any rendering logic
- **`TextProviders`**: Plain `List<TextProvider>` registry (no priority system); kill/instant-kill display is the only active provider

## Config System

Three per-source-type configs (PlayerConfig, EntityConfig, EnvConfig) implement `LHVConfigSupplier` and expose three sub-DTOs, saved to `config/lhv/{player,entity,env}.json5`. Each YACL config has fields in three categories: **basic** (13), **format** (15), **display** (13).

- **BasicConfig** (6): `renderMode`, `damageSortMode`, `trackEntity`, `maxReceiveRange`, `minVisibleRange`, `maxVisibleRange` — plus `createRenderer(sourceType)` (MERGE/LATEST/ALL), `isInRenderRange`, `isInReceiveRange`, `copy()`
- **FormatConfig** (7): `formatMode`, `unitSystem`, `roundingMode`, `retainDecimalPlaces`, `infinityDisplay`, `nanDisplay`, `symbolConfig` — plus `createFormatter()` (delegates to `FormatMode`)
  - **SymbolConfig**: `useGrouping`, `groupingSeparator`, `decimalSeparator`, `exponentSeparator`, positive/negative `prefix`/`suffix`
- **DisplayConfig** (17): `killDisplay`, `instantKillDisplay`, `defaultColor`, `criticalColor`, `criticalFormatTemplate`, `outlineSetting`, `displayDuration`, `retainWhenOffScreen`, `depthToScaleRef`, `minScale`, `maxScale`, `depthToAlphaRef`, `minAlpha`, `maxAlpha`, `offsetSetting`, `damageTypeColors`, `effectTemplate` — plus `findColor(damageTypeId)`, `copy()`
  - **OutlineSetting**: `enabled`, `color`, `width`
  - **OffsetSetting**: `screenOffsetRangeX/Y`, `offsetRangeX/Y/Z` + `computeScreenOffset`/`computeWorldOffset`

Note: YACL field region ≠ DTO assignment (e.g. `criticalFormatTemplate` is declared in the format region but wired into `DisplayConfig`). All DTOs have deep-copy constructors + `copy()`; `DamageTypeColors` defaults come from `LHVModClient.getDefaultColors()` (40+ entries).

`GlobalConfig` stores `entitySortMode`, `sourceSortMode`, `receiveOtherPlayer`, `bigNumberTestMode`, `infinityTestMode`, and `ignoreDamageTypes: Set<String>` (default: `minecraft:out_of_world`, `minecraft:in_wall`, `minecraft:cramming`) at `config/lhv/global.json5`, plus `shouldIgnore()`.

`SourceType` interface links each type to its config supplier via `getBasicConfig/getFormatConfig/getDisplayConfig`; `SourceTypes` provides static lookup and `register()` (the `LHVModAPI` entry point). EffectTemplate assignment: PLAYER → SPRING, ENTITY/ENV → COMMON.

All fields are actively consumed in render/format paths.

## Code Conventions

- No Javadoc on implementation methods unless explicitly required; code is self-documenting
- `@Contract`, `@NotNull`, `@Nullable` from JetBrains annotations
- Lombok `@Getter`, `@AllArgsConstructor` on DTOs
- No wildcard imports; explicit imports throughout
- Don't add `AGENTS.md` or any `*.md` documentation unless explicitly requested
