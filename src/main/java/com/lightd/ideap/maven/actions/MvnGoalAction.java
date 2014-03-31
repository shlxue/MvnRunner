package com.lightd.ideap.maven.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.lightd.ideap.maven.MvnRunConfigurationType;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;
import org.jetbrains.idea.maven.project.MavenGeneralSettings;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.utils.MavenPluginInfo;

import java.util.Arrays;

class MvnGoalAction extends MvnModuleContextAction {

    private final MavenPluginInfo.Mojo mojo;
    private final boolean withPrefix;

    public MvnGoalAction(MavenPluginInfo.Mojo mojo, boolean withPrefix) {
        this.withPrefix = withPrefix;
        this.mojo = mojo;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project =  e.getRequiredData(PlatformDataKeys.PROJECT);
        final MavenProject mavenProject = getProject(e.getDataContext());

        MavenRunnerParameters parameters = createParameters(mavenProject);
        MavenGeneralSettings settings = getGeneralSettings(e.getProject());
        MavenRunnerSettings runnerSettings = createRunnerSettings(e.getProject());
        MvnRunConfigurationType.runConfiguration(project, parameters, settings, runnerSettings);
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        Presentation presentation = e.getPresentation();
        presentation.setText(withPrefix ? mojo.getDisplayName() : mojo.getGoal());
    }

    private MavenRunnerParameters createParameters(MavenProject project) {
        System.out.println(project.getDirectory());
        return new MavenRunnerParameters(true, project.getDirectory(), Arrays.asList(mojo.getDisplayName()), null);
    }
}
