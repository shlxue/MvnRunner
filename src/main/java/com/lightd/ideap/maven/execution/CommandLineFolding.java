package com.lightd.ideap.maven.execution;

import com.intellij.execution.ConsoleFolding;
import com.intellij.openapi.project.Project;
import com.lightd.ideap.maven.MvnCommandFolding;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class CommandLineFolding  extends ConsoleFolding implements MvnCommandFolding {
    private String jdkHome;
    private String mainClass;
    private String goalStr;

    @Override
    public boolean shouldFoldLine(@NotNull Project project, @NotNull String line) {
        if (jdkHome != null && line.startsWith(jdkHome) && line.contains(mainClass)) {
            jdkHome = null;
            return true;
        }
        return false;
    }

    @Override
    public String getPlaceHolder(@NotNull Project project, String text) {
        return getPlaceholderText(project, Collections.singletonList(text));
    }

    @Override
    public boolean byMavenRun() {
        return mainClass != null;
    }

    @Override
    @Nullable
    public String getPlaceholderText(@NotNull Project project, @NotNull List<String> lines) {
        if (!lines.isEmpty() && lines.get(0).contains(mainClass) && lines.get(0).endsWith(goalStr))
            return "mvn " + goalStr;
        return null;
    }

    @Override
    public void placeMaven(String jdkHome, String mainClass, String goalStr) {
        this.jdkHome = jdkHome;
        this.mainClass = mainClass;
        this.goalStr = goalStr;
    }
}
