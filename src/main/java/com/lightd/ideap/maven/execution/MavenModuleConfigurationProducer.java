package com.lightd.ideap.maven.execution;

import com.intellij.execution.*;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.ConfigurationFromContext;
import com.intellij.execution.actions.ConfigurationFromContextImpl;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.lightd.ideap.maven.MvnRunConfiguration;
import com.lightd.ideap.maven.MvnRunConfigurationType;
import com.lightd.ideap.maven.RunType;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.model.MavenExplicitProfiles;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * maven module support
 */
public abstract class MavenModuleConfigurationProducer extends RunConfigurationProducer<MvnRunConfiguration> {

    protected MavenProject mavenProject;
    protected PsiElement sourceElement;
    protected PsiFile psiFile;

    protected MavenModuleConfigurationProducer() {
        super(Objects.requireNonNull(MvnRunConfigurationType.getInstance()));
    }

    @Nullable
    @Override
    public ConfigurationFromContext createConfigurationFromContext(ConfigurationContext context) {
        RunnerAndConfigurationSettings settings = cloneTemplateConfiguration(context);
        sourceElement = context.getPsiLocation();
        final Ref<PsiElement> ref = new Ref<>(context.getPsiLocation());
        if (setupConfigurationFromContext((MvnRunConfiguration) settings.getConfiguration(), context, ref)) {
            return new ConfigurationFromContextImpl(this, settings, ref.get());
        }
        return null;
    }

    @Nullable
    @Override
    public RunnerAndConfigurationSettings findExistingConfiguration(ConfigurationContext context) {
        final RunManager runManager = RunManager.getInstance(context.getProject());
        final List<RunnerAndConfigurationSettings> configurations = getConfigurationSettingsList(runManager);
        for (RunnerAndConfigurationSettings config : configurations) {
            if (!(config.getConfiguration() instanceof MvnRunConfiguration)) continue;
            if (isConfigurationFromContext((MvnRunConfiguration) config.getConfiguration(), context)) {
                return config;
            }
        }
        return null;
    }


    @Override
    public boolean isConfigurationFromContext(MvnRunConfiguration config, ConfigurationContext context) {
        final Location contextLocation = context.getLocation();
        if (contextLocation == null) {
            return false;
        }
        Location location = JavaExecutionUtil.stepIntoSingleClass(contextLocation);
        if (location == null) {
            return false;
        }
        final PsiElement element = location.getPsiElement();
        if (element instanceof PsiClass) {
            MavenRunnerParameters parameters = config.getRunnerParameters();
            if (isSameConfigByElement(config.getRunnerParameters().getGoals(), (PsiClass) element) &&
                    Comparing.strEqual(mavenProject.getDirectory(), parameters.getWorkingDirPath())) {
                List<String> testParameters = generateMvnParameters();
                return isSameParameters(testParameters, parameters.getGoals());
            }
        }
        return false;
    }

    private boolean isSameConfigByElement(List<String> goals, PsiClass psiClass) {
        String runClass = "=" + psiClass.getQualifiedName();
        return goals.stream()
                .map(s -> s.endsWith(runClass))
                .findAny().orElse(false);
    }

    @Override
    protected boolean setupConfigurationFromContext(MvnRunConfiguration config, ConfigurationContext context, Ref<PsiElement> ref) {
        if (context == null) return false;
        if (!initContext(context) || !isContext(context)) return false;

        final MavenRunnerParameters params = createMavenParameters(context.getLocation(), context.getDataContext());
        if (params != null) {
            config.setRunnerParameters(params);
            return setupMavenContext(config, config.getRunnerParameters().getGoals());
        }
        return false;
    }

    protected boolean initContext(ConfigurationContext context) {
        mavenProject = MavenActionUtil.getMavenProject(context.getDataContext());
        psiFile = LangDataKeys.PSI_FILE.getData(context.getDataContext());
        return true;
    }

    protected boolean isContext(ConfigurationContext context) {
        boolean isContext = mavenProject != null;
        if (isContext) {
            MavenProjectsManager projectsManager = MavenProjectsManager.getInstance(context.getProject());
            isContext = projectsManager.isMavenizedModule(context.getModule());
        }
        return isContext;
    }

    protected MavenRunnerParameters createMavenParameters(Location l, DataContext dataContext) {
        if (l instanceof PsiLocation) {
            MavenExplicitProfiles profiles = MavenActionUtil.getProjectsManager(dataContext).getExplicitProfiles();
            return new MavenRunnerParameters(true, mavenProject.getDirectory(), mavenProject.getFile().getName(), Collections.emptyList(), profiles);
        }
        return null;
    }

    protected boolean setupMavenContext(MvnRunConfiguration config, List<String> goals) {
        config.setName(generateName());
        config.setRunType(getRunType());
        goals.addAll(generateMvnParameters());
        return true;
    }

    protected abstract String generateName();

    protected abstract RunType getRunType();

    protected abstract List<String> generateMvnParameters();

    protected abstract boolean isSameParameters(List<String> parameters, List<String> configParameters);

}
