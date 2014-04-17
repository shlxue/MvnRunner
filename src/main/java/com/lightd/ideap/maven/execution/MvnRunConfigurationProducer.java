package com.lightd.ideap.maven.execution;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.junit.JUnitUtil;
import com.intellij.psi.util.PsiMethodUtil;
import com.lightd.ideap.maven.MvnBundle;
import com.lightd.ideap.maven.RunType;

import java.util.ArrayList;
import java.util.List;

public class MvnRunConfigurationProducer extends JavaElementConfigurationProducer {
    protected boolean isTestScope;

    @Override
    protected boolean initContext(ConfigurationContext context) {
        isTestScope = true;
        if (super.initContext(context) && psiClass != null) {
            isTestScope = JUnitUtil.isTestClass(psiClass);
            return true;
        }
        return false;
    }

    @Override
    protected boolean isContext(ConfigurationContext context) {
        return super.isContext(context) && !isTestScope &&
                psiClass != null && PsiMethodUtil.hasMainMethod(psiClass);
    }

    @Override
    protected String generateName() {
        return super.generateName() + ".main";
    }

    @Override
    protected RunType getRunType() {
        return RunType.Main;
    }

    @Override
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

    @Override
    protected boolean isSameParameters(List<String> parameters, List<String> configParameters) {
        String mvnExec = MvnBundle.message("mvn.param.exec");
        if (parameters.contains(mvnExec) && configParameters.contains(mvnExec)) {
            String prefix = MvnBundle.message("mvn.param.exec.main", "");
            String param = findByPrefix(parameters, prefix);
            return param.length() > 0 && param.equals(findByPrefix(configParameters, prefix));
        }
        return false;
    }
}
