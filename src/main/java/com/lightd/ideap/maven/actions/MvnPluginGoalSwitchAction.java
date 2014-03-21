package com.lightd.ideap.maven.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.util.text.StringUtil;
import com.lightd.ideap.maven.MvnBundle;
import com.lightd.ideap.maven.settings.MvnRunConfigurationSettings;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.model.MavenPlugin;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.utils.MavenArtifactUtil;
import org.jetbrains.idea.maven.utils.MavenPluginInfo;

import java.io.File;
import java.util.*;

public class MvnPluginGoalSwitchAction extends MvnQuickPopupAction {

    private static final String[] ignoreWords = new String[]{"maven-", "-maven", "-"};
    private static final String[] empties = new String[]{"", "", " "};
    public static final String defaultGroup = "org.apache.maven.plugins";
    public static final List<String> corePlugins;
    private static final Map<MavenId, MavenPluginInfo> plugins = new HashMap<MavenId, MavenPluginInfo>();

    static {
        String[] corePhases = new String[]{"clean", "compiler", "deploy", "failsafe", "install", "resources", "site", "surefire", "verifier"};
        for (int i = 0; i < corePhases.length; i++) {
            corePhases[i] = "maven-" + corePhases[i] + "-plugin";
        }
        corePlugins = Arrays.asList(corePhases);
    }

    @Override
    protected String getPopupTitle(String moduleName) {
        return MvnBundle.message("maven.quick.popup.goal.title", moduleName);
    }

    @Override
    protected void buildActions(DefaultActionGroup toGroup, MavenProject mavenProject) {
        File localRepository = mavenProject.getLocalRepository();
        MvnRunConfigurationSettings settings = MvnRunConfigurationSettings.getInstance();
        boolean withPrefix = settings.isWithPrefix();
        boolean onlyIgnoreCore = settings.isOnlyIgnoreCorePlugin();

        for (MavenPlugin plugin : mavenProject.getDeclaredPlugins()) {
            if (skipDefaultPlugin(localRepository, plugin.getMavenId(), onlyIgnoreCore))
                continue;

            AnAction[] actions = buildPluginActions(plugin.getMavenId(), withPrefix);
            if (actions.length > 0)
                addActionGroup(toGroup, getShortGroupName(plugin), actions);
        }
    }

    private boolean skipDefaultPlugin(File repos, final MavenId mavenId, boolean onlyIgnoreCore) {
        if (defaultGroup.equals(mavenId.getGroupId())) {
            if (!onlyIgnoreCore) return true;
            if (corePlugins.contains(mavenId.getArtifactId())) return true;
        }
        if (!plugins.containsKey(mavenId)) {
            MavenPluginInfo info = MavenArtifactUtil.readPluginInfo(repos, mavenId);
            plugins.put(mavenId, info);
        }
        MavenPluginInfo pluginInfo = plugins.get(mavenId);
        return pluginInfo == null || pluginInfo.getMojos().isEmpty();
    }

    private String getShortGroupName(MavenPlugin plugin) {
        String groupName = plugin.getArtifactId();
        if (groupName.endsWith("-plugin")) {
            groupName = StringUtil.replace(groupName, ignoreWords, empties);
            groupName = StringUtil.wordsToBeginFromUpperCase(groupName);
        }
        return groupName;
    }

    private AnAction[] buildPluginActions(MavenId mavenId, boolean withPrefix) {
        MavenPluginInfo pluginInfo = plugins.get(mavenId);
        if (pluginInfo == null || pluginInfo.getMojos().isEmpty()) return new AnAction[0];

        List<AnAction> actions = new ArrayList<AnAction>(pluginInfo.getMojos().size());
        for (MavenPluginInfo.Mojo mojo : pluginInfo.getMojos()) {
            actions.add(new MvnGoalAction(mojo, withPrefix));
        }
        return actions.toArray(new AnAction[actions.size()]);
    }
}
