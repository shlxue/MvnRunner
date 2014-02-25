package com.lightd.ideap.maven.execution;

import com.intellij.execution.Location;
import com.intellij.execution.PsiLocation;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.ConfigurationFromContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.junit.JUnitUtil;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.lightd.ideap.maven.MvnBundle;
import com.lightd.ideap.maven.MvnRunConfiguration;
import com.lightd.ideap.maven.MvnRunConfigurationType;
import com.lightd.ideap.maven.settings.MvnRunConfigurationSettings;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.execution.MavenRunConfiguration;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class MvnRunConfigurationProducerBase extends RunConfigurationProducer<MvnRunConfiguration> {

    private static final Collection<String> MVN_OPTION_PARAMS;

    static {
        MVN_OPTION_PARAMS = Arrays.asList(MvnBundle.message("mvn.param.skip"));
    }

    protected MavenProject mavenProject;
    protected PsiPackage psiPackage;
    protected PsiClass psiClass;
    protected PsiMethod psiMethod;
    protected boolean isTestScope;
    protected boolean isTestAll;

    protected MvnRunConfigurationProducerBase() {
        super(MvnRunConfigurationType.getInstance());
    }

    @Override
    public boolean isConfigurationFromContext(MvnRunConfiguration configuration, ConfigurationContext context) {
        if (initPsiContext(context)) {
            MavenRunnerParameters parameters = configuration.getRunnerParameters();
            if (Comparing.strEqual(mavenProject.getDirectory(), parameters.getWorkingDirPath())) {
                List<String> testParameters = generateMvnParameters();
                testParameters.removeAll(MVN_OPTION_PARAMS);
                return parameters.getGoals().containsAll(testParameters) &&
                        isSameParameters(testParameters, parameters.getGoals());
            }
        }
        return false;
    }

    @Nullable
    @Override
    public ConfigurationFromContext createConfigurationFromContext(ConfigurationContext context) {
        ConfigurationFromContext config = super.createConfigurationFromContext(context);
        if (config != null) {
            MvnRunConfigurationSettings settings = new MvnRunConfigurationSettings(true);
            if (settings.isSetupOnlyBy()) {
                return new MvnConfigurationFromContextWrapper(this, config);
            }
        }
        return config;
    }

    @Override
    protected boolean setupConfigurationFromContext(MvnRunConfiguration configuration, ConfigurationContext context, Ref<PsiElement> elementRef) {
        if (context == null || !initPsiContext(context)) return false;

        final MavenRunnerParameters params = createBuildParameters(context.getLocation(), context.getDataContext());
        if (params != null) {
            configuration.setRunnerParameters(params);
        } else {
            return false;
        }
        return setupMavenContext(configuration, configuration.getRunnerParameters().getGoals());
    }

    protected abstract boolean setupMavenContext(MavenRunConfiguration config, List<String> goals);

    protected abstract List<String> generateMvnParameters();

    protected MavenRunnerParameters createBuildParameters(Location l, DataContext dataContext) {
        if (l instanceof PsiLocation) {
            Collection<String> profiles = MavenActionUtil.getProjectsManager(dataContext).getExplicitProfiles();
            return new MavenRunnerParameters(true, mavenProject.getDirectory(), null, profiles);
        }

        return null;
    }

    protected boolean initPsiContext(ConfigurationContext context) {
        mavenProject = MavenActionUtil.getMavenProject(context.getDataContext());
        if (mavenProject == null) return false;
        MavenProjectsManager projectsManager = MavenProjectsManager.getInstance(context.getProject());
        if (!projectsManager.isMavenizedModule(context.getModule())) return false;

        Project project = context.getProject();
        PsiElement psiElement = context.getPsiLocation();
        if (psiElement == null) {
            return false;
        }
        psiPackage = null;
        psiClass = null;
        psiMethod = null;
        isTestAll = false;
        if (psiElement instanceof PsiDirectory) {
            psiPackage = JavaDirectoryService.getInstance().getPackage((PsiDirectory) psiElement);
            isTestAll = psiPackage == null || StringUtil.isEmpty(psiPackage.getQualifiedName());
            isTestScope = true;
        } else if (psiElement.getContainingFile() instanceof PsiJavaFile) {
            PsiJavaFile psiJavaFile = (PsiJavaFile) psiElement.getContainingFile();
            String name = MvnBundle.message("java.class.name", psiJavaFile.getPackageName(), psiJavaFile.getName());
            if (name.endsWith(".java")) {
                name = name.substring(0, name.length() - 5);
            }
            psiClass = JavaPsiFacade.getInstance(project).findClass(name, GlobalSearchScope.projectScope(project));
            isTestScope = psiClass != null && JUnitUtil.isTestClass(psiClass);
            if (isTestScope) {
                psiMethod = JUnitUtil.getTestMethod(context.getPsiLocation());
            }
        } else {
            return false;
        }

        return true;
    }

    protected String generateName(PsiClass psiClass, PsiMethod psiMethod) {
        String name = psiClass.getName();
        if (psiMethod != null) {
            name += "." + psiMethod.getName();
        }
        return name;
    }

    protected boolean isSameParameters(List<String> paramters, List<String> configParameters) {
        return true;
    }
}
