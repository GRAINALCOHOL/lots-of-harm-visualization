package grainalcohol.lhv.client.effect;

import net.minecraft.util.Util;

public abstract class BaseEffect implements Effect {
    protected final long creationTimeMs;

    /**
     * 第一次开始时的时间
     */
    protected Long startTimeMs;

    /**
     * 总暂停时长，用于冻结动画
     */
    private long pausedDurationTimeMs;
    /**
     * 开始暂停时的时间，start时置空
     */
    private Long pauseStartTimeMs;

    protected BaseEffect(boolean active) {
        this.creationTimeMs = Util.getMeasuringTimeMs();
        this.startTimeMs = null;
        if (active) this.start();
    }

    protected BaseEffect() {
        this(false);
    }

    @Override
    public final void start() {
        if (this.pauseStartTimeMs != null) {
            this.pausedDurationTimeMs += Util.getMeasuringTimeMs() - this.pauseStartTimeMs;
            this.pauseStartTimeMs = null;
        }
        if (this.startTimeMs == null) {
            this.startTimeMs = Util.getMeasuringTimeMs();
        }
    }

    @Override
    public final void pause() {
        if (this.pauseStartTimeMs == null) {
            this.pauseStartTimeMs = Util.getMeasuringTimeMs();
        }
    }

    @Override
    public final void restart() {
        this.reset();
        this.start();
    }

    @Override
    public void reset() {
        this.pausedDurationTimeMs = 0L;
        this.pauseStartTimeMs = null;
        this.startTimeMs = null;
    }

    @Override
    public int getHeadMs(int textLength) {
        return 0;
    }

    @Override
    public int getTailMs(int textLength) {
        return 0;
    }

    private long virtualNow() {
        long now = Util.getMeasuringTimeMs();
        long totalPaused = this.pausedDurationTimeMs;
        if (this.pauseStartTimeMs != null) {
            totalPaused += now - this.pauseStartTimeMs;
        }
        return now - totalPaused;
    }

    /**
     * @return 获取对象生命周期的时间（毫秒）
     */
    protected long lifecycleTimeMs() {
        return Util.getMeasuringTimeMs() - creationTimeMs;
    }

    /**
     * @return 获取动画启用的总时长，用于计算动画状态
     */
    protected long activeTimeMs() {
        if (startTimeMs == null) {
            return 0;
        }
        return virtualNow() - startTimeMs;
    }
}
