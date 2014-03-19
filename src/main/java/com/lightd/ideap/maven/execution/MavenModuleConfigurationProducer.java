package com.lightd.ideap.maven.execution;

import com.intellij.execution.Location;
import com.intellij.execution.PsiLocation;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.lightd.ideap.maven.MvnRunConfiguration;
import com.lightd.ideap.maven.MvnRunConfigurationType;
import com.lightd.ideap.maven.RunType;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;

import java.util.Collection;
import java.util.List;

/**
 * maven module support
 */
public abstract class MavenModuleConfigurationProducer extends RunConfigurationProducer<MvnRunConfiguration> {

    protected MavenProject mavenProject;
    protected PsiFile psiFile;

    protected MavenModuleConfigurationProducer() {
        super(MvnRunConfigurationType.getInstance());
    }

    @Override
    public boolean isConfigurationFromContext(MvnRunConfiguration config, ConfigurationContext context) {
        if (isContext(context)) {
            MavenRunnerParameters parameters = config.getRunnerParameters();
            if (Comparing.strEqual(mavenProject.getDirectory(), parameters.getWorkingDirPath())) {
                List<String> testParameters = generateMvnParameters();
                return isSameParameters(testParameters, parameters.getGoals());
            }
        }
        return false;
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
        psiFile = CommonDataKeys.PSI_FILE.getData(context.getDataContext());
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
            Collection<String> profiles = MavenActionUtil.getProjectsManager(dataContext).getExplicitProfiles();
            return new MavenRunnerParameters(true, mavenProject.getDirectory(), null, profiles);
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
