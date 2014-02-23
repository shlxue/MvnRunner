package com.lightd.ideap.maven.execution;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.openapi.util.Comparing;
import com.intellij.psi.util.PsiMethodUtil;
import org.jetbrains.idea.maven.execution.MavenRunConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MvnRunConfigurationProducer extends MvnRunConfigurationProducerBase {

    @Override
    public boolean isConfigurationFromContext(MavenRunConfiguration configuration, ConfigurationContext context) {
        if (super.isConfigurationFromContext(configuration, context)) {
            String name = getName(psiClass, psiMethod);
            if (Comparing.strEqual(name, configuration.getName())) {
                Collection<String> mvnParameters = configuration.getRunnerParameters().getGoals();
                Collection<String> parameters = generateMvnParameters();
                parameters.removeAll(MVN_OPTION_PARAMS);
                return mvnParameters.containsAll(parameters);
            }
        }
        return false;
    }

    @Override
    protected boolean setupMavenContext(MavenRunConfiguration config, List<String> goals) {
        if (psiMethod != null && PsiMethodUtil.isMainMethod(psiMethod)) {
            config.setName(getName(psiClass, psiMethod));
            goals.addAll(generateMvnParameters());
            return true;
        }
        return false;
    }

    @Override
    protected boolean initPsiContext(ConfigurationContext context) {
        if (super.initPsiContext(context) && !isTestScope) {
            psiMethod = PsiMethodUtil.findMainMethod(psiClass);
            return psiMethod != null;
        }
        return false;
    }

    protected Collection<String> generateMvnParameters() {
        Collection<String> parameters = new ArrayList<String>(3);
        parameters.add(isTestScope ? MVN_TEST_COMPILE : MVN_COMPILE);
        parameters.add(MVN_EXEC_JAVA);
        parameters.add(MVN_EXEC_MAIN + psiClass.getQualifiedName());
        if (isTestScope) {
            parameters.add(MVN_EXEC_TEST_CLASSPATH);
        }
        return parameters;
    }
}
