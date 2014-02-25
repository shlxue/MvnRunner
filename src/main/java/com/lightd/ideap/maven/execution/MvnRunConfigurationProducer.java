package com.lightd.ideap.maven.execution;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.psi.util.PsiMethodUtil;
import com.lightd.ideap.maven.MvnBundle;
import org.jetbrains.idea.maven.execution.MavenRunConfiguration;

import java.util.ArrayList;
import java.util.List;

public class MvnRunConfigurationProducer extends MvnRunConfigurationProducerBase {

    @Override
    protected boolean setupMavenContext(MavenRunConfiguration config, List<String> goals) {
        if (psiMethod != null && PsiMethodUtil.isMainMethod(psiMethod)) {
            config.setName(generateName(psiClass, psiMethod));
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

    protected List<String> generateMvnParameters() {
        List<String> parameters = new ArrayList<String>();
        if (isTestScope)
            parameters.add(MvnBundle.message("mvn.param.test.compile"));
        else
            parameters.add(MvnBundle.message("mvn.param.compile"));
        parameters.add(MvnBundle.message("mvn.param.exec"));
        parameters.add(MvnBundle.message("mvn.param.exec.main", psiClass.getQualifiedName()));
        if (isTestScope)
            parameters.add(MvnBundle.message("mvn.param.test.classpath.scope"));
        return parameters;
    }
}
