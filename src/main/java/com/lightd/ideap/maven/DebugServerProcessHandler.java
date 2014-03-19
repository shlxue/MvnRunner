package com.lightd.ideap.maven;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.DefaultJavaProcessHandler;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.project.Project;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;

public class DebugServerProcessHandler extends DefaultJavaProcessHandler {
    private final Project project;
    private final MavenRunnerParameters parameters;

    public DebugServerProcessHandler(GeneralCommandLine commandLine, Project project, MavenRunnerParameters parameters) throws ExecutionException {
        super(commandLine);
        this.project = project;
        this.parameters = parameters;
    }

    @Override
    protected void doDestroyProcess() {
        MvnRunConfigurationType.runConfiguration(project, parameters, null, null, new ProgramRunner.Callback() {
            @Override
            public void processStarted(RunContentDescriptor runContentDescriptor) {
                getProcess().destroy();
            }
        });
    }

    @Override
    protected void onOSProcessTerminated(int exitCode) {
        int exit = exitCode == 255 ? 0 : exitCode;
        super.onOSProcessTerminated(exit);
    }
}
