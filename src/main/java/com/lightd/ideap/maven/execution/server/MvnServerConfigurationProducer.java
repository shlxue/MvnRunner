package com.lightd.ideap.maven.execution.server;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.psi.PsiFile;
import com.lightd.ideap.maven.MvnBundle;
import com.lightd.ideap.maven.execution.MavenModuleConfigurationProducer;
import org.jetbrains.idea.maven.model.MavenConstants;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.model.MavenPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * maven plugin support
 */
public abstract class MvnServerConfigurationProducer extends MavenModuleConfigurationProducer {

    protected MavenPlugin plugin;

    @Override
    protected boolean isContext(ConfigurationContext context) {
        if (super.isContext(context) &&
                MavenConstants.TYPE_WAR.equals(mavenProject.getPackaging()) && plugin != null) {
            PsiFile psiFile = CommonDataKeys.PSI_FILE.getData(context.getDataContext());
            return psiFile != null && MavenConstants.POM_XML.equals(psiFile.getName());
        }
        return false;
    }

    @Override
    protected boolean initContext(ConfigurationContext context) {
        plugin = null;
        if (super.initContext(context)) {
            for (MavenId mavenId : getPluginMavenId()) {
                plugin = mavenProject.findPlugin(mavenId.getGroupId(), mavenId.getArtifactId());
                if (plugin != null)
                    return true;
            }
        }
        return false;
    }
    @Override
    protected List<String> generateMvnParameters() {
        List<String> parameters = new ArrayList<String>();
        parameters.add(getStartGoal());
        return parameters;
    }

    @Override
    protected boolean isSameParameters(List<String> parameters, List<String> configParameters) {
        return parameters.contains(getStartGoal()) && configParameters.contains(getStartGoal());
    }

    protected String getPortInfo() {
        return "default";
    }

    @Override
    protected String generateName() {
        return MvnBundle.message("mvn.server.config.name", getServerName(), getPortInfo(), mavenProject.getMavenId().getArtifactId());
    }

    protected String getServerName() {
        String name = plugin.getArtifactId().substring(0, plugin.getArtifactId().length() - 13);
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        return name;
    }

    protected final String getProperty(String key) {
        return mavenProject.getProperties().getProperty(key);
    }

    protected abstract List<MavenId> getPluginMavenId();

    protected abstract String getStartGoal();
}
