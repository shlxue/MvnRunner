package com.lightd.ideap.maven.execution;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.junit.JUnitUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.util.text.VersionComparatorUtil;
import com.lightd.ideap.maven.MvnBundle;
import com.lightd.ideap.maven.MvnRunConfiguration;
import com.lightd.ideap.maven.RunType;
import com.lightd.ideap.maven.settings.MvnRunConfigurable;
import com.lightd.ideap.maven.settings.MvnRunConfigurationSettings;
import org.jetbrains.idea.maven.model.MavenConstants;

import java.util.ArrayList;
import java.util.List;

public class MvnTestConfigurationProducer extends JavaElementConfigurationProducer {

    protected PsiMethod psiMethod;
    protected boolean isTestAll;

    @Override
    protected RunType getRunType() {
        return RunType.Test;
    }

    @Override
    protected boolean setupMavenContext(MvnRunConfiguration config, List<String> goals) {
        boolean setup = super.setupMavenContext(config, goals);
        if (config.getRunnerSettings() != null) {
            config.getRunnerSettings().setSkipTests(false);
        }
        return setup;
    }

    @Override
    protected boolean initContext(ConfigurationContext context) {
        psiMethod = null;
        isTestAll = false;
        if (super.initContext(context)) {
            isTestAll = psiClass == null && psiPackage == null;
            isTestAll |= psiPackage != null && StringUtil.isEmpty(psiPackage.getQualifiedName());
            if (psiClass != null)
                psiMethod = JUnitUtil.getTestMethod(context.getPsiLocation());
            return true;
        }
        return false;
    }

    private boolean isPom(){
        return psiFile != null && MavenConstants.POM_XML.equals(psiFile.getName());
    }


    @Override
    protected boolean isContext(ConfigurationContext context) {
        if (super.isContext(context) && (isTestAll || psiPackage != null || psiClass != null)) {
            isPom();
            return !(psiClass != null && !JUnitUtil.isTestClass(psiClass)) && !isPom();
        }
        return false;
    }

    @Override
    protected boolean isSameParameters(List<String> parameters, List<String> configParameters) {
        String mvnTest = MvnBundle.message("mvn.param.test");
        if (parameters.contains(mvnTest) && configParameters.contains(mvnTest)) {
            String prefix = MvnBundle.message("mvn.param.test.object", "");
            return findByPrefix(parameters, prefix).equals(findByPrefix(configParameters, prefix));
        }
        return false;
    }

    @Override
    protected String generateName() {
        String moduleName = mavenProject.getMavenId().getArtifactId();
        if (isTestAll) {
            return MvnBundle.message("action.all.tests.text", moduleName);
        }
        if (psiPackage != null) {
            return MvnBundle.message("mvn.config.in.package.name", psiPackage.getQualifiedName(), moduleName);
        }
        String name = super.generateName();
        if (psiMethod != null)
            name += "." + psiMethod.getName();
        return name;
    }

    protected List<String> generateMvnParameters() {
        List<String> testParameters = new ArrayList<String>();
        testParameters.add(MvnBundle.message("mvn.param.test.compile"));
        testParameters.add(MvnBundle.message("mvn.param.test"));

        if (!isTestAll) {
            String mvnTestParam;
            if (psiPackage != null) {
                mvnTestParam = MvnBundle.message("mvn.package.test.suffix", psiPackage.getQualifiedName());
            } else {
                PsiJavaFile psiJavaFile = (PsiJavaFile) psiClass.getScope();
                mvnTestParam = getJavaClassName(psiJavaFile.getPackageName(), psiClass.getName());
                if (psiMethod != null) {
                    mvnTestParam = MvnBundle.message("mvn.method.test.suffix", mvnTestParam, psiMethod.getName());
                }
            }
            testParameters.add(MvnBundle.message("mvn.param.test.object", mvnTestParam));
        }

        if (Boolean.valueOf(mavenProject.getProperties().getProperty("maven.test.skip", "false")))
            testParameters.add(MvnBundle.message("mvn.param.skip"));
        if (psiMethod == null) {
            MvnRunConfigurationSettings settings = MvnRunConfigurable.getInstance().getSettings();
            if (isForking()) {
                testParameters.add(MvnBundle.message("mvn.param.fork.count", settings.getForkCount()));
                if (settings.getForkCount() == 1)
                    testParameters.add(MvnBundle.message("mvn.param.reuse.forks", settings.isReuseForks()));
            } else {
                testParameters.add(MvnBundle.message("mvn.param.fork.mode",
                        getForkMode(settings.getForkCount(), settings.isReuseForks())));
                if (testParameters.get(testParameters.size() - 1).contains("perthread")) {
                    testParameters.add(MvnBundle.message("mvn.param.fork.thread", settings.getForkCount()));
                }
            }
        }

        return testParameters;
    }

    private boolean isForking() {
        for (org.jetbrains.idea.maven.model.MavenPlugin plugin : mavenProject.getPlugins()) {
            if (plugin.getMavenId().equals("org.apache.maven.plugins", "maven-surefire-plugin")) {
                return "2.14".equals(VersionComparatorUtil.min(plugin.getVersion(), "2.14"));
            }
        }
        return false;
    }

    private String getForkMode(int forkCount, boolean reuseForks) {
        if (forkCount <= 1) {
            return reuseForks ? "once" : "always";
        }
        return forkCount == 0 ? "never" : "perthread";
    }
}
