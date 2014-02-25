package com.lightd.ideap.maven.execution;

import com.intellij.execution.PsiLocation;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.junit.JUnitUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPackage;
import com.lightd.ideap.maven.MvnBundle;
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
    protected boolean isSameParameters(List<String> paramters, List<String> configParameters) {
        if (isTestAll) {
            String prefix = MvnBundle.message("mvn.param.test.object", "");
            for (String parameter : configParameters) {
                if (parameter.startsWith(prefix)) {
                    return false;
                }
            }
        }
        return true;
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
                mvnTestParam = MvnBundle.message("java.class.name", psiJavaFile.getPackageName(), psiClass.getName());
                if (psiMethod != null) {
                    mvnTestParam = MvnBundle.message("mvn.method.test.suffix", mvnTestParam, psiMethod.getName());
                }
            }
            testParameters.add(MvnBundle.message("mvn.param.test.object", mvnTestParam));
        }
        testParameters.add(MvnBundle.message("mvn.param.skip"));
        testParameters.add(MvnBundle.message("mvn.param.fork.mode"));

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
}
