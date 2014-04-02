package com.lightd.ideap.maven.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
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

    public static final String defaultGroup = "org.apache.maven.plugins";
    public static final List<String> defaultPlugins;
    private static final Map<MavenId, MavenPluginInfo> plugins = new HashMap<MavenId, MavenPluginInfo>();

    static {
        String[] corePhases = new String[]{"clean", "compiler", "deploy", "failsafe", "install", "resources", "site", "surefire", "verifier"};
        for (int i = 0; i < corePhases.length; i++) {
            corePhases[i] = "maven-" + corePhases[i] + "-plugin";
        }
        defaultPlugins = Arrays.asList(corePhases);
    }

    @Override
    protected String getPopupTitle(String moduleName) {
        return MvnBundle.message("maven.quick.popup.goal.title", moduleName);
    }

    @Override
    protected void buildActions(DefaultActionGroup toGroup, MavenProject mavenProject) {
        Map<MavenPlugin, AnAction[]> pluginActions = buildAllPlugins(mavenProject);

        List<AnAction> defaultPluginGroups = new ArrayList<AnAction>();
        List<AnAction> customPluginGroups = new ArrayList<AnAction>();
        for (Map.Entry<MavenPlugin, AnAction[]> plgActions : pluginActions.entrySet()) {
            String name = plgActions.getKey().getArtifactId();
            AnAction popupGroup = addPopupGroup(name, plgActions.getValue());
            if (defaultPlugins.contains(name))
                defaultPluginGroups.add(popupGroup);
            else
                customPluginGroups.add(popupGroup);
        }
        if (!defaultPluginGroups.isEmpty())
            addActionGroup(toGroup, "Default Plugins", defaultPluginGroups);
        if (!customPluginGroups.isEmpty())
            addActionGroup(toGroup, "Custom Plugins", customPluginGroups);
    }

    private Map<MavenPlugin, AnAction[]> buildAllPlugins(MavenProject mavenProject) {
        File localRepository = mavenProject.getLocalRepository();
        MvnRunConfigurationSettings settings = MvnRunConfigurationSettings.getInstance();
        boolean withPrefix = settings.isWithPrefix();
        boolean ignoreDefault = settings.isIgnoreCorePlugin();

        Map<MavenPlugin, AnAction[]> pluginActions = new TreeMap<MavenPlugin, AnAction[]>(new MavenPluginComparator());
        for (MavenPlugin plugin : mavenProject.getDeclaredPlugins()) {
            loadPluginInfo(localRepository, plugin.getMavenId());
            if (ignoreDefault && skipPlugin(plugin.getMavenId()))
                continue;

            AnAction[] actions = buildPluginActions(plugin.getMavenId(), withPrefix);
            if (actions.length > 0) {
                pluginActions.put(plugin, actions);
            }
        }
        return pluginActions;
    }

    private void loadPluginInfo(File repos, final MavenId mavenId) {
        if (!plugins.containsKey(mavenId)) {
            MavenPluginInfo info = MavenArtifactUtil.readPluginInfo(repos, mavenId);
            plugins.put(mavenId, info);
        }
    }

    private boolean skipPlugin(final MavenId mavenId) {
        if (defaultGroup.equals(mavenId.getGroupId())) {
            if (defaultPlugins.contains(mavenId.getArtifactId())) return true;
        }
        MavenPluginInfo pluginInfo = plugins.get(mavenId);
        return pluginInfo == null || pluginInfo.getMojos().isEmpty();
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

    class MavenPluginComparator implements Comparator<MavenPlugin> {
        @Override
        public int compare(MavenPlugin o1, MavenPlugin o2) {
            if (defaultPlugins.contains(o1.getArtifactId()) && !defaultPlugins.contains(o2.getArtifactId()))
                return -1;
            if (!defaultPlugins.contains(o1.getArtifactId()) && defaultPlugins.contains(o2.getArtifactId()))
                return 1;
            return o1.getArtifactId().compareTo(o2.getArtifactId());
        }
    }
}
