package com.lightd.ideap.maven;

public enum RunType {
    None("mvn_run"),
    Test("run_test"),
    Main("run_main"),
    Jetty("run_jetty"),
    Tomcat("run_tomcat"),
    PhaseClean("phase_clean"),
    PhaseCompile("phase_compile"),
    PhaseDeploy("phase_deploy"),
    PhaseInstall("phase_install"),
    PhasePackage("phase_package"),
    PhaseTest("phase_test");

    private String value;

    RunType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("/images/%s.png", value);
    }

    public static RunType to(String value) {
        for (RunType type : RunType.values()) {
            if (type.getValue().equals(value)) return type;
        }
        return null;
    }

    public static RunType toPhase(String value) {
        return to("phase_" + value);
    }
}
