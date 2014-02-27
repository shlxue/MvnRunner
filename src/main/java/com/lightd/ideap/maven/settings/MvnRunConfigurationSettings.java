package com.lightd.ideap.maven.settings;

import com.intellij.ide.util.PropertiesComponent;
import com.lightd.ideap.maven.MvnBundle;

public class MvnRunConfigurationSettings implements Cloneable {

    private static final PropertiesComponent component = PropertiesComponent.getInstance();
    private boolean setupOnlyByMvn;
    private int forkCount;
    private boolean reuseForks;

    public boolean isSetupOnlyBy() {
        return setupOnlyByMvn;
    }

    public void setSetupOnlyByMvn(boolean setupOnlyByMvn) {
        this.setupOnlyByMvn = setupOnlyByMvn;
    }

    public int getForkCount() {
        return forkCount;
    }

    public void setForkCount(int forkCount) {
        this.forkCount = forkCount;
    }

    public boolean isReuseForks() {
        return reuseForks;
    }

    public void setReuseForks(boolean reuseForks) {
        this.reuseForks = reuseForks;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MvnRunConfigurationSettings) || getClass() != obj.getClass())
            return false;

        final MvnRunConfigurationSettings settings = (MvnRunConfigurationSettings) obj;
        return settings.isSetupOnlyBy() == isSetupOnlyBy() &&
                settings.getForkCount() == getForkCount() &&
                settings.isReuseForks() == isReuseForks();
    }

    public void readSettings() {
        setupOnlyByMvn = component.getBoolean(MvnBundle.message("settings.key.only.mvn"), true);
        forkCount = (byte)component.getOrInitInt(MvnBundle.message("settings.key.fork.count"), Runtime.getRuntime().availableProcessors());
        reuseForks = component.getBoolean(MvnBundle.message("settings.key.reuse.forks"), true);
    }

    protected void saveSettings() {
        component.setValue(MvnBundle.message("settings.key.only.mvn"), Boolean.toString(isSetupOnlyBy()));
        component.setValue(MvnBundle.message("settings.key.fork.count"), Integer.toString(forkCount));
        component.setValue(MvnBundle.message("settings.key.reuse.forks"), Boolean.toString(reuseForks));
    }
}
