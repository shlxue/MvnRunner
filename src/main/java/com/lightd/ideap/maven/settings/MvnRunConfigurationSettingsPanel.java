package com.lightd.ideap.maven.settings;

import com.lightd.ideap.maven.MvnBundle;

import javax.swing.*;
import java.awt.*;

class MvnRunConfigurationSettingsPanel extends JPanel {

    private JCheckBox cbSetupOnlyMvn;

    public MvnRunConfigurationSettingsPanel() {
        initComponents();
    }

    private void initComponents() {
        this.setLayout(new GridLayout());
        cbSetupOnlyMvn = new JCheckBox();
        cbSetupOnlyMvn.setText(MvnBundle.message("panel.only.mvn.text"));
        add(cbSetupOnlyMvn);
    }

    void getData(MvnRunConfigurationSettings settings) {
        settings.setSetupOnlyByMvn(cbSetupOnlyMvn.isSelected());
    }

    void setData(MvnRunConfigurationSettings settings) {
        cbSetupOnlyMvn.setSelected(settings.isSetupOnlyBy());
    }

    boolean isModified(MvnRunConfigurationSettings settings) {
        MvnRunConfigurationSettings panelSettings = new MvnRunConfigurationSettings(false);
        getData(panelSettings);
        return !panelSettings.equals(settings);
    }
}
