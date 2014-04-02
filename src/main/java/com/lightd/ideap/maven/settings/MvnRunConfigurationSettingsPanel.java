package com.lightd.ideap.maven.settings;

import com.intellij.ui.IdeBorderFactory;
import com.lightd.ideap.maven.MvnBundle;

import javax.swing.*;
import java.awt.*;

class MvnRunConfigurationSettingsPanel extends JPanel {

    private final MvnRunConfigurationSettings settings;
    private JCheckBox cbShowPomLocation;
    private JCheckBox cbWithPrefix;
    private JCheckBox cbIgnoreCorePlugin;
    private JCheckBox cbReuseForks;
    private JSpinner spForkCount;

    public MvnRunConfigurationSettingsPanel(MvnRunConfigurationSettings settings) {
        super(new GridBagLayout());
        this.settings = settings;
        initComponents();
    }

    private void initComponents() {
        this.setBorder(IdeBorderFactory.createTitledBorder(MvnBundle.message("panel.runner.layout.title"), false));

        cbShowPomLocation = new JCheckBox(MvnBundle.message("panel.show.pom.location.text"));
        final JLabel label = new JLabel();
        spForkCount = new JSpinner();
        cbReuseForks = new JCheckBox(MvnBundle.message("panel.reuse.forks.text"));
        cbWithPrefix = new JCheckBox(MvnBundle.message("panel.with.prefix.text"));
        cbIgnoreCorePlugin = new JCheckBox(MvnBundle.message("panel.ignore.core.plugin.text"));

        int cpuCores = Runtime.getRuntime().availableProcessors();
        label.setText(MvnBundle.message("panel.fork.count.text", cpuCores));
        spForkCount.setModel(new SpinnerNumberModel(cpuCores, 0, cpuCores * 3, 1));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        this.add(cbShowPomLocation, gbc);

        gbc = new GridBagConstraints();
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        this.add(cbWithPrefix, gbc);

        gbc = new GridBagConstraints();
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        this.add(cbIgnoreCorePlugin, gbc);

        gbc = new GridBagConstraints();
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 26, 0, 0);
        this.add(label, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        this.add(spForkCount, gbc);

        gbc = new GridBagConstraints();
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        this.add(cbReuseForks, gbc);
    }

    void apply() {
        settings.setForkCount((Integer) spForkCount.getValue());
        settings.setReuseForks(cbReuseForks.isSelected());
        settings.setShowPomLocation(cbShowPomLocation.isSelected());
        settings.setWithPrefix(cbWithPrefix.isSelected());
        settings.setIgnoreCorePlugin(cbIgnoreCorePlugin.isSelected());
    }

    void reset() {
        spForkCount.setValue(settings.getForkCount());
        cbReuseForks.setSelected(settings.isReuseForks());
        cbShowPomLocation.setSelected(settings.isShowPomLocation());
        cbWithPrefix.setSelected(settings.isWithPrefix());
        cbIgnoreCorePlugin.setSelected(settings.isIgnoreCorePlugin());
    }

    boolean isModified() {
        return settings.isReuseForks() != cbReuseForks.isSelected() ||
                settings.getForkCount() != (Integer) spForkCount.getValue() ||
                settings.isShowPomLocation() != cbShowPomLocation.isSelected() ||
                settings.isWithPrefix() != cbWithPrefix.isSelected() ||
                settings.isIgnoreCorePlugin() != cbIgnoreCorePlugin.isSelected();
    }
}
