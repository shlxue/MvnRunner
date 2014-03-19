package com.lightd.ideap.maven.execution.server;

import com.intellij.openapi.util.text.StringUtil;
import com.lightd.ideap.maven.MvnRunConfiguration;
import com.lightd.ideap.maven.RunType;
import org.jetbrains.idea.maven.model.MavenId;

import java.util.Arrays;
import java.util.List;

public class MvnTomcatConfigurationProducer extends MvnServerConfigurationProducer {

    private static final String PLUGIN_GROUP = "org.apache.tomcat.maven";
    private static final String PLUGIN_TOMCAT6 = "tomcat6-maven-plugin";
    private static final String PLUGIN_TOMCAT7 = "tomcat7-maven-plugin";

    @Override
    protected boolean setupMavenContext(MvnRunConfiguration config, List<String> goals) {
        return super.setupMavenContext(config, goals);
    }

    @Override
    protected RunType getRunType() {
        return RunType.Tomcat;
    }

    @Override
    protected List<MavenId> getPluginMavenId() {
        return Arrays.asList(new MavenId(PLUGIN_GROUP, PLUGIN_TOMCAT6, null),
                new MavenId(PLUGIN_GROUP, PLUGIN_TOMCAT7, null));
    }

    @Override
    protected String getPortInfo() {
        String portInfo = getProperty("maven.tomcat.port");
        if (!StringUtil.isEmptyOrSpaces(portInfo))
            return portInfo;
        return super.getPortInfo();
    }

    @Override
    protected String getStartGoal() {
        return "tomcat:run";
    }
}
