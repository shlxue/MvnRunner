package com.lightd.ideap.maven.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import org.jetbrains.idea.maven.execution.MavenRunner;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;
import org.jetbrains.idea.maven.project.MavenGeneralSettings;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jetbrains.idea.maven.utils.actions.MavenAction;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;

import java.util.LinkedHashMap;

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

    protected final MavenGeneralSettings getGeneralSettings(Project project) {
        MavenProjectsManager projectsManager = MavenProjectsManager.getInstance(project);
        return projectsManager.getGeneralSettings();
    }

    protected final MavenRunnerSettings createRunnerSettings(Project project) {
        MavenRunnerSettings runnerSettings = MavenRunner.getInstance(project).getSettings().clone();
        runnerSettings.setMavenProperties(new LinkedHashMap<>());
        return runnerSettings;
    }
}
