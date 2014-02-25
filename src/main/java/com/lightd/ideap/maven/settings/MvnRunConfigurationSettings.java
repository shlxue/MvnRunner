package com.lightd.ideap.maven.settings;

import com.intellij.ide.util.PropertiesComponent;
import com.lightd.ideap.maven.MvnBundle;

public class MvnRunConfigurationSettings implements Cloneable {
    private static final String SETUP_ONLY_BY_MVN = MvnBundle.message("settings.key.only.mvn");
    private final PropertiesComponent component;
    private boolean setupOnlyByMvn;

    public MvnRunConfigurationSettings(boolean loadProperties) {
        component = loadProperties ? PropertiesComponent.getInstance() : null;
        if (component != null) {
            setupOnlyByMvn = component.getBoolean(SETUP_ONLY_BY_MVN, true);
        }
    }

    public boolean isSetupOnlyBy() {
        return setupOnlyByMvn;
    }

    public void setSetupOnlyByMvn(boolean setupOnlyByMvn) {
        this.setupOnlyByMvn = setupOnlyByMvn;
        saveSettings();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MvnRunConfigurationSettings) || getClass() != obj.getClass())
            return false;

        final MvnRunConfigurationSettings settings = (MvnRunConfigurationSettings) obj;
        return settings.isSetupOnlyBy() == isSetupOnlyBy();
    }

    private void saveSettings() {
        if (component != null) {
            component.setValue(SETUP_ONLY_BY_MVN, Boolean.toString(isSetupOnlyBy()));
        }
    }
}
