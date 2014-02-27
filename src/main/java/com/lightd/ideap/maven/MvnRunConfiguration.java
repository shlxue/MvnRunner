package com.lightd.ideap.maven;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.execution.MavenRunConfiguration;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class MvnRunConfiguration extends MavenRunConfiguration {
    private static final String TEST_PREFIX = MvnBundle.message("mvn.param.test.object", "");
    private static final String PACKAGE_PATTERN = "\\.\\*\\*\\.\\*$";

    protected MvnRunConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(project, factory, name);
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
            if (pattern.matcher(param).find()) {
                return match;
            }
        } else if (param != null)
            return match;
        return !match;
    }
}
