package com.lightd.ideap.maven.actions;

import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.util.text.StringUtil;
import com.lightd.ideap.maven.MvnBundle;
import org.jetbrains.idea.maven.model.MavenConstants;
import org.jetbrains.idea.maven.project.MavenProject;

public class MvnQuickListPopupAction extends MvnQuickPopupAction {

    @Override
    protected String getPopupTitle(String moduleName) {
        return MvnBundle.message("maven.quick.list.popup.title", moduleName);
    }

    @Override
    protected void buildActions(DefaultActionGroup toGroup, MavenProject mavenProject) {
        addLifecycleActions(toGroup);
        addPomActions(toGroup);
    }

    private void addLifecycleActions(DefaultActionGroup toGroup) {
        String groupName = MvnBundle.message("maven.quick.list.popup.lifecycle");
        String[] actionIds = new String[MavenConstants.BASIC_PHASES.size()];
        for (int i = 0; i < actionIds.length; i++) {
            actionIds[i] = "Maven." + StringUtil.wordsToBeginFromUpperCase(MavenConstants.BASIC_PHASES.get(i));
        }
        addActionGroup(toGroup, groupName, actionIds);
    }

    private void addPomActions(DefaultActionGroup toGroup) {
        addActionGroup(toGroup, "POM", "Maven.Pom.Open", "Maven.Pom.Diagram");
    }
}
