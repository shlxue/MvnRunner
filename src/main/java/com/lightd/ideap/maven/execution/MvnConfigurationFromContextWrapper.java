package com.lightd.ideap.maven.execution;

import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.ConfigurationFromContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

class MvnConfigurationFromContextWrapper extends ConfigurationFromContext {
    private final RunConfigurationProducer configurationProducer;
    private final ConfigurationFromContext config;

    public MvnConfigurationFromContextWrapper(RunConfigurationProducer producer, ConfigurationFromContext config) {
        this.configurationProducer = producer;
        this.config = config;
    }

    @NotNull
    @Override
    public RunnerAndConfigurationSettings getConfigurationSettings() {
        return config.getConfigurationSettings();
    }

    @Override
    public void setConfigurationSettings(RunnerAndConfigurationSettings runnerAndConfigurationSettings) {
        config.setConfigurationSettings(runnerAndConfigurationSettings);
    }

    @NotNull
    @Override
    public PsiElement getSourceElement() {
        return config.getSourceElement();
    }

    @Override
    public boolean isPreferredTo(ConfigurationFromContext other) {
        return config.isPreferredTo(other);
    }

    @Override
    public boolean isProducedBy(Class<? extends RunConfigurationProducer> producerClass) {
        return MvnRunConfigurationProducerBase.class.isInstance(configurationProducer);
    }

    @Override
    public void onFirstRun(ConfigurationContext context, Runnable startRunnable) {
        this.config.onFirstRun(context, startRunnable);
    }
}
