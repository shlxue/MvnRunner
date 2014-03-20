package com.lightd.ideap.maven.settings;

import com.lightd.ideap.maven.MvnBundle;

import javax.swing.*;

class MvnRunConfigurationSettingsPanel extends JPanel {

    private final MvnRunConfigurationSettings settings;
    private JCheckBox cbShowPomLocation;
    private JCheckBox cbReuseForks;
    private JSpinner spForkCount;

    public MvnRunConfigurationSettingsPanel(MvnRunConfigurationSettings settings) {
        super();
        this.settings = settings;
        initComponents();
    }

    private void initComponents() {
        cbShowPomLocation = new JCheckBox();
        final JLabel label = new JLabel();
        spForkCount = new JSpinner();
        cbReuseForks = new JCheckBox();

        cbShowPomLocation.setText(MvnBundle.message("panel.show.pom.location.text"));

        int cpuCores = Runtime.getRuntime().availableProcessors();
        label.setText(MvnBundle.message("panel.fork.count.text", cpuCores));
        spForkCount.setModel(new SpinnerNumberModel(cpuCores, 0, cpuCores * 3, 1));

        cbReuseForks.setText(MvnBundle.message("panel.reuse.forks.text"));

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(label, GroupLayout.PREFERRED_SIZE, 270, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(spForkCount, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addComponent(cbShowPomLocation))
                                .addGap(20, 30, 80)
                                .addComponent(cbReuseForks, GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                                .addGap(20, 30, 50))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(cbShowPomLocation)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(cbReuseForks)
                                        .addComponent(spForkCount, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }

    void apply() {
        settings.setForkCount((Integer) spForkCount.getValue());
        settings.setReuseForks(cbReuseForks.isSelected());
        settings.setShowPomLocation(cbShowPomLocation.isSelected());
    }

    void reset() {
        spForkCount.setValue(settings.getForkCount());
        cbReuseForks.setSelected(settings.isReuseForks());
        cbShowPomLocation.setSelected(settings.isShowPomLocation());
    }

    boolean isModified() {
        return settings.isReuseForks() != cbReuseForks.isSelected() ||
                settings.getForkCount() != (Integer) spForkCount.getValue() ||
                settings.isShowPomLocation() != cbShowPomLocation.isSelected();
    }
}
