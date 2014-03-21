package com.lightd.ideap.maven.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.lightd.ideap.maven.MvnRunConfigurationType;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;
import org.jetbrains.idea.maven.project.MavenGeneralSettings;
import org.jetbrains.idea.maven.project.MavenProject;

import java.util.Arrays;

public class MvnPhaseGoalAction extends MvnModuleContextAction {
    private String phase;

    public String getPhase() {
        if (phase == null && getTemplatePresentation().getText() != null) {
            phase = getTemplatePresentation().getText().toLowerCase();
        }
        return phase;
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

    private MavenRunnerParameters createParameters(MavenProject project) {
        return new MavenRunnerParameters(true, project.getDirectory(), Arrays.asList(getPhase()), null);
    }
}
