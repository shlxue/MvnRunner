package com.lightd.ideap.maven.settings;

import com.intellij.ide.util.PropertiesComponent;

public class MvnRunConfigurationSettings implements Cloneable {

    public final String Key_ForkCount = "forkCount";
    public final String Key_ReuseForks = "reuseForks";
    public final String Key_ShowPomLocation = "showPomLocation";
    public final String Key_WithPrefix = "withPrefix";
    public final String Key_IgnoreCorePlugin = "ignoreCorePlugin";

    private static MvnRunConfigurationSettings instance;
    private int forkCount;
    private boolean reuseForks;
    private boolean showPomLocation;
    private boolean withPrefix;
    private boolean ignoreCorePlugin;

    public static MvnRunConfigurationSettings getInstance() {
        if (instance == null) {
            instance = new MvnRunConfigurationSettings();
            instance.readSettings();
        }
        return instance;
    }

    MvnRunConfigurationSettings() {
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

    public boolean isShowPomLocation() {
        return showPomLocation;
    }

    public void setShowPomLocation(boolean showPomLocation) {
        this.showPomLocation = showPomLocation;
    }

    public boolean isWithPrefix() {
        return withPrefix;
    }

    public void setWithPrefix(boolean withPrefix) {
        this.withPrefix = withPrefix;
    }

    public boolean isIgnoreCorePlugin() {
        return ignoreCorePlugin;
    }

    public void setIgnoreCorePlugin(boolean ignoreCorePlugin) {
        this.ignoreCorePlugin = ignoreCorePlugin;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MvnRunConfigurationSettings) || getClass() != obj.getClass())
            return false;

        final MvnRunConfigurationSettings settings = (MvnRunConfigurationSettings) obj;
        return settings.getForkCount() == getForkCount() &&
                settings.isReuseForks() == isReuseForks() &&
                settings.isShowPomLocation() == isShowPomLocation() &&
                settings.isWithPrefix() == isWithPrefix() &&
                settings.isIgnoreCorePlugin() == isIgnoreCorePlugin();
    }

    public void readSettings() {
        final PropertiesComponent component = PropertiesComponent.getInstance();
        forkCount = (byte)component.getInt(Key_ForkCount, Runtime.getRuntime().availableProcessors());
        reuseForks = component.getBoolean(Key_ReuseForks, true);
        showPomLocation = component.getBoolean(Key_ShowPomLocation, false);
        withPrefix = component.getBoolean(Key_WithPrefix, false);
        ignoreCorePlugin = component.getBoolean(Key_IgnoreCorePlugin, false);
    }

    protected void saveSettings() {
        final PropertiesComponent component = PropertiesComponent.getInstance();
        component.setValue(Key_ForkCount, Integer.toString(forkCount));
        component.setValue(Key_ReuseForks, Boolean.toString(reuseForks));
        component.setValue(Key_ShowPomLocation, Boolean.toString(showPomLocation));
        component.setValue(Key_WithPrefix, Boolean.toString(withPrefix));
        component.setValue(Key_IgnoreCorePlugin, Boolean.toString(ignoreCorePlugin));
        instance = null;
    }
}
