package com.lightd.ideap.maven;

import com.intellij.execution.*;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.execution.MavenRunConfiguration;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class MvnRunConfiguration extends MavenRunConfiguration {
    private static final String TEST_PREFIX = MvnBundle.message("mvn.param.test.object", "");
    private static final String PACKAGE_PATTERN = "\\.\\*\\*\\.\\*$";
    private RunType runType;
    private String stopGoal;

    @Override
    public JavaParameters createJavaParameters(@Nullable Project project) throws ExecutionException {
        JavaParameters parameters = super.createJavaParameters(project);
        foldMavenCommand(parameters);
        return parameters;
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        super.readExternal(element);
        String value = element.getAttributeValue("runType");
        if (value != null) runType = RunType.to(value);
        stopGoal = element.getAttributeValue("stopGoal");
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
        if (runType != null) element.setAttribute("runType", runType.getValue());
        if (stopGoal != null) element.setAttribute("stopGoal", stopGoal);
    }

    @Override
    public RunProfileState getState(@NotNull Executor executor, final @NotNull ExecutionEnvironment env) throws ExecutionException {
        if (DefaultDebugExecutor.EXECUTOR_ID.equals(executor.getId())) {
            List<String> goals = null;
            if (RunType.Test.equals(runType)) {
                RunnerAndConfigurationSettings settings = env.getRunnerAndConfigurationSettings();
                if (settings != null && settings.getConfiguration() instanceof MvnRunConfiguration) {
                    goals = disableFork(((MvnRunConfiguration) settings.getConfiguration()).getGoals());
                }
            }
            if ((RunType.Jetty.equals(runType) || RunType.Tomcat.equals(runType)) || goals != null) {
                return new DebugServerCommandLineState(env, this, goals);
            }
        }
        return super.getState(executor, env);
    }

    protected MvnRunConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(project, factory, name);
    }

    public String getStopGoal() {
        return stopGoal;
    }

    public void setStopGoal(String stopGoal) {
        this.stopGoal = stopGoal;
    }

    @Override
    public boolean isGeneratedName() {
        return isTest() || super.isGeneratedName();
    }

    @Nullable
    @Override
    public String getActionName() {
        if (isTest()) {
            if (isAllTest())
                return MvnBundle.message("action.all.tests.text", getModuleName());
            if (isPackageTest())
                return MvnBundle.message("action.test.package.text", getPackageName());
        }
        return super.getActionName();
    }

    public void setRunType(RunType runType) {
        this.runType = runType;
    }

    @Override
    public Icon getIcon() {
        if (runType != null) {
            return IconLoader.getIcon(runType.toString());
        }
        return MvnBundle.MAVEN_RUN_ICON;
    }

    private String getModuleName() {
        MavenProjectsManager projectsManager = MavenProjectsManager.getInstance(getProject());
        String workingDirPath = getRunnerParameters().getWorkingDirPath();
        for (MavenProject mavenProject : projectsManager.getProjects()) {
            if (StringUtil.equals(workingDirPath, mavenProject.getDirectory())) {
                return mavenProject.getDisplayName();
            }
        }
        return null;
    }

    private String getPackageName() {

        for (String s : getGoals()) {
            if (s.startsWith(TEST_PREFIX)) {
                return s.substring(TEST_PREFIX.length(), s.length() - 5);
            }
        }
        return null;
    }

    private List<String> getGoals() {
        List<String> goals = getRunnerParameters().getGoals();
        return goals != null ? goals : Collections.<String>emptyList();
    }

    private boolean isTest() {
        return getGoals().contains(MvnBundle.message("mvn.param.test"));
    }

    private boolean isPackageTest() {
        return isMatch(getGoals(), TEST_PREFIX, PACKAGE_PATTERN);
    }

    private boolean isAllTest() {
        return isMatch(getGoals(), TEST_PREFIX, null, false);
    }

    private boolean isMatch(List<String> list, String prefix, String regex) {
        return isMatch(list, prefix, regex, true);
    }

    private boolean isMatch(List<String> list, String prefix, String regex, boolean match) {
        String param = null;
        for (String s : list) {
            if (s.startsWith(prefix)) {
                param = s;
                break;
            }
        }

        if (regex != null) {
            Pattern pattern = Pattern.compile(regex);
            if (param != null && pattern.matcher(param).find()) {
                return match;
            }
        } else if (param != null)
            return match;
        return !match;
    }

    private static List<String> disableFork(List<String> goals) {
        List<String> bakGoals = new ArrayList<String>(goals.size());
        List<String> clone = new ArrayList<String>(goals.size());
        bakGoals.addAll(goals);
        clone.addAll(goals);
        goals.clear();
        boolean changed = false;
        for (String s : clone) {
            if (s.startsWith("-DreuseForks=") || s.startsWith("-DthreadCount=")) continue;
            if (s.startsWith("-Dfork")) {
                changed = true;
                String[] keyValue = s.split("=");
                goals.add(keyValue[0] + "=" + (keyValue[0].length()>10 ? "0" : "never"));
            } else
                goals.add(s);
        }
        return changed ? bakGoals : null;
    }

    private void foldMavenCommand(JavaParameters params) {
        if (params == null) {
            return;
        }
        List<MvnCommandFolding> list = ContainerUtil.findAll(ConsoleFolding.EP_NAME.getExtensions(), MvnCommandFolding.class);
        if (list.isEmpty()) return;
        try {
            String jdkPath = params.getJdkPath();
            String goalStr = params.getProgramParametersList().getParametersString();
            for (MvnCommandFolding folding : list) {
                folding.placeMaven(jdkPath, params.getMainClass(), goalStr);
            }
        } catch (CantRunException ignore) {
        }
    }
}
