package com.lightd.ideap.maven.actions;

import com.intellij.ide.actions.QuickSwitchSchemeAction;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.lightd.ideap.maven.MvnBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.model.MavenConstants;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;

public class MvnQuickListPopupAction extends QuickSwitchSchemeAction implements DumbAware {

    public MvnQuickListPopupAction() {
        super.myActionPlace = "MavenBuildGroup";
    }

    @Override
    protected boolean isEnabled() {
        return true;
    }

    @Override
    protected String getPopupTitle(AnActionEvent e) {
        MavenProject mavenProject = MvnExecuteAction.getProject(e.getDataContext());
        if (mavenProject == null) return null;
        String moduleName = mavenProject.getMavenId().getArtifactId();
        if (moduleName != null && moduleName.length() > 20)
            moduleName = moduleName.substring(0, 17) + "...";
        return MvnBundle.message("maven.quick.list.popup.title", moduleName);
    }

    @Override
    protected void fillActions(Project project, @NotNull DefaultActionGroup group, @NotNull DataContext dataContext) {
        if (project == null || !MavenActionUtil.hasProject(dataContext) ||
                MvnExecuteAction.getProject(dataContext) == null) {
            return;
        }
        addLifecycleActions(group);
        addPomActions(group);
    }

    private void addLifecycleActions(DefaultActionGroup group) {
        addSeparator(group, null);
        addSeparator(group, MvnBundle.message("maven.quick.list.popup.lifecycle"));
        for (String phase : MavenConstants.BASIC_PHASES) {
            addAction("Maven." + StringUtil.wordsToBeginFromUpperCase(phase), group, phase);
        }
    }

    private void addPomActions(DefaultActionGroup group) {
        addSeparator(group, null);
        addSeparator(group, "POM");

        addAction("Maven.Pom.Open", group);
    }

    private void addAction(final String actionId, final DefaultActionGroup toGroup) {
        addAction(actionId, toGroup, null);
    }

    private void addAction(final String actionId, final DefaultActionGroup toGroup, String phase) {
        final AnAction action = ActionManager.getInstance().getAction(actionId);

        if (action != null) {
            toGroup.add(action);
            if (phase != null && action instanceof MvnExecuteAction) {
                ((MvnExecuteAction) action).setPhase(phase);
            }
        }
    }

    private void addSeparator(final DefaultActionGroup toGroup, @Nullable final String title) {
        final Separator separator = title == null ? new Separator() : new Separator(title);
        toGroup.add(separator);
    }
}
