package com.lightd.ideap.maven;

import com.intellij.execution.PsiLocation;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.junit.JUnitUtil;
import com.intellij.openapi.util.Comparing;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPackage;
import org.jetbrains.idea.maven.execution.MavenRunConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MvnTestConfigurationProducer extends MvnRunConfigurationProducerBase {

    @Override
    public boolean isConfigurationFromContext(MavenRunConfiguration configuration, ConfigurationContext context) {
        if (super.isConfigurationFromContext(configuration, context)) {
            String name = getName(psiClass, psiMethod);
            if (Comparing.strEqual(configuration.getName(), name)) {
                Collection<String> testParameters = generateMvnParameters();
                testParameters.removeAll(MVN_OPTION_PARAMS);
                return configuration.getRunnerParameters().getGoals().containsAll(testParameters);
            }
            return configuration.getName() != null && Comparing.strEqual(configuration.getName(), name);
        }
        return false;
    }

    @Override
    protected boolean initPsiContext(ConfigurationContext context) {
        return super.initPsiContext(context) && isTestScope &&
                (isTestAll || psiPackage == null || hasTestClass(psiPackage));
    }

    @Override
    protected boolean setupMavenContext(MavenRunConfiguration configuration, List<String> goals) {
        if (isTestScope && (psiMethod == null || JUnitUtil.isTestMethod(PsiLocation.fromPsiElement(psiMethod)))) {
            configuration.setName(getName(psiClass, psiMethod));
            goals.addAll(generateMvnParameters());
            return true;
        }
        return false;
    }

    @Override
    protected String getName(PsiClass psiClass, PsiMethod psiMethod) {
        if (isTestAll) {
            return "All Tests in " + mavenProject.getDisplayName();
        }
        if (psiPackage != null) {
            return psiPackage.getQualifiedName();
        }
        return super.getName(psiClass, psiMethod);
    }

    protected Collection<String> generateMvnParameters() {
        Collection<String> testParameters = new ArrayList<String>(4);
        testParameters.add(MVN_TEST_COMPILE);
        testParameters.add(MVN_TEST);

        if (!isTestAll) {
            String mvnTestParam;
            if (psiPackage != null) {
                mvnTestParam = psiPackage.getQualifiedName() + ".**.*";
            } else {
                PsiJavaFile psiJavaFile = (PsiJavaFile) psiClass.getScope();
                mvnTestParam = psiJavaFile.getPackageName() + "." + psiClass.getName();
                if (psiMethod != null) {
                    mvnTestParam += "#" + psiMethod.getName();
                }
            }
            testParameters.add(MVN_TEST_PARAM + mvnTestParam);
        }
        testParameters.add(MVN_TEST_SKIP);

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
