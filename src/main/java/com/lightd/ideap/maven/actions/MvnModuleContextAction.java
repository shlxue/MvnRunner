package com.lightd.ideap.maven.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.utils.actions.MavenAction;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;

public abstract class MvnModuleContextAction extends MavenAction {

    static MavenProject getProject(DataContext context) {
        MavenProject project = MavenActionUtil.getMavenProject(context);
        if (project == null && !MavenActionUtil.getMavenProjects(context).isEmpty()) {
            project = MavenActionUtil.getMavenProjects(context).get(0);
        }
        return project;
    }

    @Override
    protected boolean isAvailable(AnActionEvent e) {
        return super.isAvailable(e) && getProject(e.getDataContext()) != null;
    }
}
