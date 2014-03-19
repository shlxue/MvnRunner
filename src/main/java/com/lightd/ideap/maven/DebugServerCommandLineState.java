package com.lightd.ideap.maven;

import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.wm.ToolWindowId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.execution.MavenResumeAction;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;

import java.util.Arrays;
import java.util.List;

public class DebugServerCommandLineState extends JavaCommandLineState {
    private final MvnRunConfiguration config;
    private final List<String> goals;

    protected DebugServerCommandLineState(@NotNull ExecutionEnvironment environment, MvnRunConfiguration config, List<String> goals) {
        super(environment);
        this.config = config;
        this.goals = goals;
    }

    @Override
    protected JavaParameters createJavaParameters() throws ExecutionException {
        return config.createJavaParameters(getEnvironment().getProject());
    }

    @NotNull
    @Override
    public ExecutionResult execute(@NotNull Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {
        DefaultExecutionResult res = (DefaultExecutionResult) super.execute(executor, runner);
        if (executor.getId().equals(ToolWindowId.RUN)
                && MavenResumeAction.isApplicable(getEnvironment().getProject(), getJavaParameters(), config)) {
            MavenResumeAction resumeAction = new MavenResumeAction(res.getProcessHandler(), runner, getEnvironment());
            res.setRestartActions(resumeAction);
        }
        return res;
    }

    @NotNull
    @Override
    protected OSProcessHandler startProcess() throws ExecutionException {
        if (config.getStopGoal() != null && goals == null) {
            MavenRunnerParameters parameters = config.getRunnerParameters().clone();
            parameters.setGoals(Arrays.asList(config.getStopGoal()));
            OSProcessHandler result = new DebugServerProcessHandler(createCommandLine(),
                    config.getProject(), parameters);
            ProcessTerminatedListener.attach(result);
            result.setShouldDestroyProcessRecursively(true);
            return result;
        }
        OSProcessHandler result = super.startProcess();
        if (goals != null) {
            result.addProcessListener(new ProcessAdapter() {
                @Override
                public void processTerminated(ProcessEvent event) {
                    super.processTerminated(event);
                    config.getRunnerParameters().setGoals(goals);
                }
            });
        }
        return result;
    }
}
