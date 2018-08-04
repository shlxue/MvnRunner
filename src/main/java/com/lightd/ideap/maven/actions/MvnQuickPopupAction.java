package com.lightd.ideap.maven.actions;

import com.intellij.ide.actions.QuickSwitchSchemeAction;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class MvnQuickPopupAction extends QuickSwitchSchemeAction implements DumbAware {

    protected MvnQuickPopupAction() {
        super();
        super.myActionPlace = "MavenBuildGroup";
    }

    @Override
    protected boolean isEnabled() {
        return true;
    }

    @Override
    protected String getPopupTitle(AnActionEvent e) {
        return getPopupTitle(getModuleName(e.getDataContext()));
    }

    @Override
    protected void fillActions(Project project, @NotNull DefaultActionGroup toGroup, @NotNull DataContext context) {
        if (project == null || !MavenActionUtil.hasProject(context) ||
                MvnModuleContextAction.getProject(context) == null) {
            return;
        }

        buildActions(toGroup, MvnModuleContextAction.getProject(context));
    }

    @Override
    protected JBPopupFactory.ActionSelectionAid getAidMethod() {
        return JBPopupFactory.ActionSelectionAid.ALPHA_NUMBERING;
    }

    protected abstract String getPopupTitle(String moduleName);

    protected abstract void buildActions(final DefaultActionGroup toGroup, final MavenProject mavenProject);


    protected final void addActionGroup(final DefaultActionGroup toGroup, String groupName, AnAction... actions) {
        addActionGroup(toGroup, groupName, Arrays.asList(actions));
    }

    protected final void addActionGroup(final DefaultActionGroup toGroup, String groupName, Collection<AnAction> actions) {
        addSeparator(toGroup, null);
        if (!StringUtil.isEmptyOrSpaces(groupName))
            addSeparator(toGroup, groupName);
        toGroup.addAll(actions);
    }

    protected final AnAction addPopupGroup(String groupName, AnAction... actions) {
        DefaultActionGroup actionGroup = new DefaultActionGroup(groupName, true);
        actionGroup.addAll(actions);
        return actionGroup;
    }

    protected final void addActionGroup(final DefaultActionGroup toGroup, String groupName, String... actionIds) {
        List<AnAction> actions = new ArrayList<>(actionIds.length);
        for (String actionId : actionIds) {
            final AnAction action = ActionManager.getInstance().getAction(actionId);
            if (action != null) actions.add(action);
        }
        addActionGroup(toGroup, groupName, actions.toArray(new AnAction[actions.size()]));
    }

    protected void addSeparator(final DefaultActionGroup toGroup, final String title) {
        final Separator separator = title == null ? new Separator() : new Separator(title);
        toGroup.add(separator);
    }

    private String getModuleName(DataContext context) {
        MavenProject mavenProject = MvnModuleContextAction.getProject(context);
        String moduleName = "";
        if (mavenProject != null) {
            moduleName = mavenProject.getMavenId().getArtifactId();
            if (moduleName != null && moduleName.length() > 20)
                moduleName = moduleName.substring(0, 17) + "...";
        }
        if (StringUtil.isEmptyOrSpaces(moduleName)) moduleName = "...";
        return moduleName;
    }
}
