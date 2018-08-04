package com.lightd.ideap.maven;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public interface MvnCommandFolding {
    void placeMaven(String jdkHome, String mainClass, String goalStr);

    String getPlaceHolder(@NotNull Project project, String text);

    boolean byMavenRun();
}
