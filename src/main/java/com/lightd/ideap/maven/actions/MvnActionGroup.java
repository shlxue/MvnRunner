package com.lightd.ideap.maven.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.idea.maven.utils.actions.MavenActionGroup;

public class MvnActionGroup extends MavenActionGroup {

    @Override
    protected boolean isAvailable(AnActionEvent e) {
        return MvnModuleContextAction.getProject(e.getDataContext()) != null;
    }
}
