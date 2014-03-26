package com.lightd.ideap.maven.settings;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.options.UnnamedConfigurable;
import com.intellij.openapi.project.Project;
import com.lightd.ideap.maven.MvnBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.project.AdditionalMavenImportingSettings;

import javax.swing.*;

public class MvnRunConfigurable implements AdditionalMavenImportingSettings, SearchableConfigurable {

    private static MvnRunConfigurable instance;
    private MvnRunConfigurationSettings settings = new MvnRunConfigurationSettings();
    private MvnRunConfigurationSettingsPanel settingsPanel;

    public static MvnRunConfigurable getInstance() {
        if (instance == null) {
            instance = new MvnRunConfigurable();
        }
        return instance;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return MvnBundle.message("configurable.display.name");
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return getId();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        settings.readSettings();
        settingsPanel = new MvnRunConfigurationSettingsPanel(settings);
        return settingsPanel;
    }

    @Override
    public boolean isModified() {
        return settingsPanel != null && settingsPanel.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        if (settingsPanel != null) {
            settingsPanel.apply();
            settings.saveSettings();
        }
    }

    @Override
    public void reset() {
        if (settingsPanel != null) {
            settingsPanel.reset();
        }
    }

    @Override
    public void disposeUIResources() {
        settingsPanel = null;
    }

    @Nullable
    @Override
    public Runnable enableSearch(String s) {
        return null;
    }

    @NotNull
    @Override
    public String getId() {
        return MvnBundle.message("configurable.id");
    }

    @Override
    public UnnamedConfigurable createConfigurable(Project project) {
        return this;
    }
}
