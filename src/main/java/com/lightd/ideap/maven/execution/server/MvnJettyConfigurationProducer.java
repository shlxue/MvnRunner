package com.lightd.ideap.maven.execution.server;

import com.intellij.openapi.util.text.StringUtil;
import com.lightd.ideap.maven.MvnRunConfiguration;
import com.lightd.ideap.maven.RunType;
import org.jdom.Content;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.jetbrains.idea.maven.model.MavenId;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class MvnJettyConfigurationProducer extends MvnServerConfigurationProducer {
    private static final String PLUGIN_GROUP = "org.eclipse.jetty";
    private static final String PLUGIN_ARTIFACT = "jetty-maven-plugin";
    private static final String PLUGIN_GROUP_MORTBAY = "org.mortbay.jetty";
    private static final String PLUGIN_GROUP_KOHSUKE = "org.kohsuke.jetty";

    @Override
    protected boolean setupMavenContext(MvnRunConfiguration config, List<String> goals) {
        if (canStop()) {
            config.setStopGoal("jetty:stop");
        }
        return super.setupMavenContext(config, goals);
    }

    @Override
    protected RunType getRunType() {
        return RunType.Jetty;
    }

    protected String getStartGoal() {
        return "jetty:start";
    }

    @Override
    protected List<MavenId> getPluginMavenId() {
        return Arrays.asList(new MavenId(PLUGIN_GROUP, PLUGIN_ARTIFACT, null),
                new MavenId(PLUGIN_GROUP_MORTBAY, PLUGIN_ARTIFACT, null),
                new MavenId(PLUGIN_GROUP_KOHSUKE, PLUGIN_ARTIFACT, null));
    }

    @Override
    protected String getPortInfo() {
        String portInfo = getProperty("jetty.port");
        if (!StringUtil.isEmptyOrSpaces(portInfo)) return portInfo;
        try {
            final String portPath = "connectors/connector/port";
            List list = XPath.selectNodes(plugin.getConfigurationElement(), portPath);
            for (Object e : list) {
                Content content = (Content) e;
                if (!StringUtil.isEmptyOrSpaces(content.getValue())) {
                    return content.getValue();
                }
            }
        } catch (JDOMException ignore) {
        }
        return super.getPortInfo();
    }

    private boolean canStop() {
        Properties properties = mavenProject.getProperties();

        Element e = plugin.getConfigurationElement();
        for (String config : stopConfigs) {
            if (properties.containsKey(config)) continue;
            if (e == null || StringUtil.isEmptyOrSpaces(e.getChildText(config))) {
                return false;
            }
        }
        return true;
    }

    private static final List<String> stopConfigs = Arrays.asList("stopKey", "stopPort");
}
