package com.lightd.ideap.maven.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.lightd.ideap.maven.MvnRunConfigurationType;
import org.jetbrains.idea.maven.execution.MavenRunner;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;
import org.jetbrains.idea.maven.project.MavenGeneralSettings;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jetbrains.idea.maven.utils.actions.MavenAction;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;

import java.util.Arrays;
import java.util.LinkedHashMap;

public class MvnExecuteAction extends MavenAction {
    private String phase;

    public void setPhase(String phase) {
        this.phase = phase;
    }

    @Override
    protected boolean isAvailable(AnActionEvent e) {
        return super.isAvailable(e) && getProject(e.getDataContext()) != null;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project =  e.getRequiredData(CommonDataKeys.PROJECT);
        final MavenProject mavenProject = getProject(e.getDataContext());

        MavenRunnerParameters parameters = createParameters(mavenProject);
        MavenGeneralSettings settings = getGeneralSettings(e.getProject());
        MavenRunnerSettings runnerSettings = createRunnerSettings(e.getProject());
        MvnRunConfigurationType.runConfiguration(project, parameters, settings, runnerSettings);
    }

    protected static MavenProject getProject(DataContext context) {
        MavenProject project = MavenActionUtil.getMavenProject(context);
        if (project == null && !MavenActionUtil.getMavenProjects(context).isEmpty()) {
            project = MavenActionUtil.getMavenProjects(context).get(0);
        }
        return project;
    }

    private MavenRunnerParameters createParameters(MavenProject project) {
        return new MavenRunnerParameters(true, project.getDirectory(), Arrays.asList(phase), null);
    }

    private MavenGeneralSettings getGeneralSettings(Project project) {
        MavenProjectsManager projectsManager = MavenProjectsManager.getInstance(project);
        return projectsManager.getGeneralSettings();
    }

    private MavenRunnerSettings createRunnerSettings(Project project) {
        MavenRunnerSettings runnerSettings = MavenRunner.getInstance(project).getSettings().clone();
        runnerSettings.setMavenProperties(new LinkedHashMap<String, String>());
        return runnerSettings;
    }

}
