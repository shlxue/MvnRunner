package com.lightd.ideap.maven.execution;

import com.intellij.execution.PsiLocation;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.junit.JUnitUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPackage;
import com.intellij.util.text.VersionComparatorUtil;
import com.lightd.ideap.maven.MvnBundle;
import com.lightd.ideap.maven.settings.MvnRunConfigurable;
import com.lightd.ideap.maven.settings.MvnRunConfigurationSettings;
import org.jetbrains.idea.maven.execution.MavenRunConfiguration;

import java.util.ArrayList;
import java.util.List;

public class MvnTestConfigurationProducer extends MvnRunConfigurationProducerBase {

    @Override
    protected boolean initPsiContext(ConfigurationContext context) {
        return super.initPsiContext(context) && isTestScope &&
                (isTestAll || psiPackage == null || hasTestClass(psiPackage));
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
    protected boolean setupMavenContext(MavenRunConfiguration configuration, List<String> goals) {
        if (isTestScope && (psiMethod == null || JUnitUtil.isTestMethod(PsiLocation.fromPsiElement(psiMethod)))) {
            configuration.setName(generateName(psiClass, psiMethod));
            goals.addAll(generateMvnParameters());
            return true;
        }
        return false;
    }

    @Override
    protected String generateName(PsiClass psiClass, PsiMethod psiMethod) {
        String moduleName = mavenProject.getDisplayName();
        if (isTestAll) {
            return MvnBundle.message("action.all.tests.text", moduleName);
        }
        if (psiPackage != null) {
            return MvnBundle.message("mvn.config.in.package.name", psiPackage.getQualifiedName(), moduleName);
        }
        return super.generateName(psiClass, psiMethod);
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
        testParameters.add(MvnBundle.message("mvn.param.skip"));
        MvnRunConfigurationSettings settings = MvnRunConfigurable.getInstance().getSettings();
        if (isForking()) {
            testParameters.add(MvnBundle.message("mvn.param.fork.count", settings.getForkCount()));
            testParameters.add(MvnBundle.message("mvn.param.reuse.forks", settings.isReuseForks()));
        } else {
            testParameters.add(MvnBundle.message("mvn.param.fork.mode",
                    getForkMode(settings.getForkCount(), settings.isReuseForks())));
        }

        return testParameters;
    }

    private boolean hasTestClass(PsiPackage pack) {
        for (PsiClass aClass : pack.getClasses()) {
            if (JUnitUtil.isTestClass(aClass)) {
                return true;
            }
        }
        for (PsiPackage aPackage : pack.getSubPackages()) {
            if (hasTestClass(aPackage)) {
                return true;
            }
        }
        return false;
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
        return forkCount == 0 ? "never" : MvnBundle.message("mvn.param.fork.thread", forkCount) ;
    }
}
