package com.lightd.ideap.maven;

import com.intellij.compiler.options.CompileStepBeforeRun;
import com.intellij.compiler.options.CompileStepBeforeRunNoErrorCheck;
import com.intellij.execution.BeforeRunTask;
import com.intellij.execution.RunManager;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import javax.swing.*;
import java.util.List;

public class MvnRunConfigurationType implements ConfigurationType {

    private final ConfigurationFactory myFactory;

    public static MvnRunConfigurationType getInstance() {
        ConfigurationType[] types = Extensions.getExtensions(ConfigurationType.CONFIGURATION_TYPE_EP);
        for (ConfigurationType type : types) {
            if (MvnRunConfigurationType.class.isInstance(type)) {
                return (MvnRunConfigurationType) type;
            }
        }
        return null;
    }

    private MvnRunConfigurationType() {
        myFactory = new MvnRunConfigurationFactory(this);
    }

    @Override
    public String getDisplayName() {
        return MvnBundle.message("mvn.run.configuration.name");
    }

    @Override
    public String getConfigurationTypeDescription() {
        return MvnBundle.message("mvn.run.configuration.description");
    }

    @Override
    public Icon getIcon() {
        return MvnBundle.MAVEN_RUN_ICON;
    }

    @NotNull
    @Override
    public String getId() {
        return "mvnRunConfigurationType";
    }

    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{myFactory};
    }

    private class MvnRunConfigurationFactory extends ConfigurationFactory{
        MvnRunConfigurationFactory(MvnRunConfigurationType configurationType) {
            super(configurationType);
        }
        @Override
        public RunConfiguration createTemplateConfiguration(Project project) {
            return createTemplateConfiguration(project, null);
        }

        @Override
        public RunConfiguration createTemplateConfiguration(Project project, RunManager runManager) {
            return new MvnRunConfiguration(project, this, "");
        }

        @Override
        public RunConfiguration createConfiguration(String name, RunConfiguration template) {
            MvnRunConfiguration cfg = (MvnRunConfiguration)super.createConfiguration(name, template);
            if (!StringUtil.isEmptyOrSpaces(cfg.getRunnerParameters().getWorkingDirPath())) return cfg;

            Project project = cfg.getProject();
            if (project == null) return cfg;

            MavenProjectsManager projectsManager = MavenProjectsManager.getInstance(project);

            List<MavenProject> projects = projectsManager.getProjects();
            if (projects.size() != 1) {
                return cfg;
            }

            VirtualFile directory = projects.get(0).getDirectoryFile();

            cfg.getRunnerParameters().setWorkingDirPath(directory.getPath());

            return cfg;
        }

        @Override
        public void configureBeforeRunTaskDefaults(Key<? extends BeforeRunTask> providerID, BeforeRunTask task) {
            if (providerID == CompileStepBeforeRun.ID || providerID == CompileStepBeforeRunNoErrorCheck.ID) {
                task.setEnabled(false);
            }
        }
    }
}
