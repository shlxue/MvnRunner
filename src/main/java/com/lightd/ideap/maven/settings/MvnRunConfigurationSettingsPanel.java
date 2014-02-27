package com.lightd.ideap.maven.settings;

import com.lightd.ideap.maven.MvnBundle;

import javax.swing.*;

class MvnRunConfigurationSettingsPanel extends JPanel {

    private final MvnRunConfigurationSettings settings;
    private JCheckBox cbSetupOnlyMvn;
    private JCheckBox cbReuseForks;
    private JSpinner spForkCount;

    public MvnRunConfigurationSettingsPanel(MvnRunConfigurationSettings settings) {
        super();
        this.settings = settings;
        initComponents();
    }

    private void initComponents() {
        cbSetupOnlyMvn = new JCheckBox();
        final JLabel label = new JLabel();
        spForkCount = new JSpinner();
        cbReuseForks = new JCheckBox();

        cbSetupOnlyMvn.setText(MvnBundle.message("panel.only.mvn.text"));

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
                                                .addComponent(label, GroupLayout.PREFERRED_SIZE, 263, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(spForkCount, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addComponent(cbSetupOnlyMvn))
                                .addGap(18, 18, 18)
                                .addComponent(cbReuseForks, GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                                .addGap(11, 11, 11))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(cbSetupOnlyMvn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(cbReuseForks)
                                        .addComponent(spForkCount, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }

    void apply() {
        settings.setSetupOnlyByMvn(cbSetupOnlyMvn.isSelected());
        settings.setForkCount((Integer)spForkCount.getValue());
        settings.setReuseForks(cbReuseForks.isSelected());
    }

    void reset() {
        cbSetupOnlyMvn.setSelected(settings.isSetupOnlyBy());
        spForkCount.setValue(settings.getForkCount());
        cbReuseForks.setSelected(settings.isReuseForks());
    }

    boolean isModified() {
        return settings.isSetupOnlyBy() != cbSetupOnlyMvn.isSelected() ||
                settings.isReuseForks() != cbReuseForks.isSelected() ||
                settings.getForkCount() != (Integer)spForkCount.getValue();
    }
}
