package com.lightd.ideap.maven;

import com.intellij.compiler.options.CompileStepBeforeRun;
import com.intellij.compiler.options.CompileStepBeforeRunNoErrorCheck;
import com.intellij.execution.*;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.impl.DefaultJavaProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;
import org.jetbrains.idea.maven.project.MavenGeneralSettings;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jetbrains.idea.maven.utils.MavenUtil;

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

    public static void runConfiguration(Project project,
                                        @NotNull MavenRunnerParameters params,
                                        @Nullable MavenGeneralSettings settings,
                                        @Nullable MavenRunnerSettings runnerSettings) {
        runConfiguration(project, params, settings, runnerSettings, null);
    }

    public static void runConfiguration(Project project,
                                        @NotNull MavenRunnerParameters params,
                                        @Nullable MavenGeneralSettings settings,
                                        @Nullable MavenRunnerSettings runnerSettings,
                                        @Nullable ProgramRunner.Callback callback) {
        RunnerAndConfigurationSettings configSettings = createRunnerAndConfigurationSettings(settings,
                runnerSettings,
                params,
                project);

        ProgramRunner runner = DefaultJavaProgramRunner.getInstance();
        Executor executor = DefaultRunExecutor.getRunExecutorInstance();
        ExecutionEnvironment env = new ExecutionEnvironment(executor, runner, configSettings, project);

        try {
           runner.execute(env, callback);
        }
        catch (ExecutionException e) {
            MavenUtil.showError(project, "Failed to execute Maven goal", e);
        }
    }

    private static RunnerAndConfigurationSettings createRunnerAndConfigurationSettings(@Nullable MavenGeneralSettings generalSettings,
                                                                                      @Nullable MavenRunnerSettings runnerSettings,
                                                                                      MavenRunnerParameters params,
                                                                                      Project project) {
        MvnRunConfigurationType type = ConfigurationTypeUtil.findConfigurationType(MvnRunConfigurationType.class);
        final RunnerAndConfigurationSettings settings = RunManagerEx.getInstanceEx(project).createRunConfiguration(generateName(project, params), type.myFactory);
        MvnRunConfiguration runConfiguration = (MvnRunConfiguration)settings.getConfiguration();
        runConfiguration.setRunnerParameters(params);
        runConfiguration.setGeneralSettings(generalSettings);
        runConfiguration.setRunnerSettings(runnerSettings);
        return settings;
    }

    private static String generateName(Project project, MavenRunnerParameters runnerParameters) {
        String name = "";
        for (MavenProject mavenProject : MavenProjectsManager.getInstance(project).getProjects()) {
            if (project.getBasePath().equals(mavenProject.getDirectory())) {
                name = mavenProject.getMavenId().getArtifactId();
                break;
            }
        }
        StringBuilder param = new StringBuilder();
        for (String s : runnerParameters.getGoals()) {
            param.append(s).append(",");
        }
        if (param.length() > 40) param.setLength(37);
        if (param.charAt(param.length() - 1) == ',') param.setLength(param.length() - 1);
        return MvnBundle.message("mvn.build.name", name, param);
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
        public Icon getIcon(@NotNull RunConfiguration config) {
            return config.getIcon();
        }

        @Override
        public void configureBeforeRunTaskDefaults(Key<? extends BeforeRunTask> providerID, BeforeRunTask task) {
            if (providerID == CompileStepBeforeRun.ID || providerID == CompileStepBeforeRunNoErrorCheck.ID) {
                task.setEnabled(false);
            }
        }
    }
}
