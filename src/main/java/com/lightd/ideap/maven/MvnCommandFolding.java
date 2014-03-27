package com.lightd.ideap.maven;

public interface MvnCommandFolding {
    void placeMaven(String jdkHome, String mainClass, String goalStr);

    String getPlaceHolder(String text);

    boolean byMavenRun();
}
