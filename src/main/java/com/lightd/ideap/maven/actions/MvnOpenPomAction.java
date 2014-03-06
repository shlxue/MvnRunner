package com.lightd.ideap.maven.actions;

import com.intellij.ide.actions.BaseNavigateToSourceAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.OpenSourceUtil;
import com.lightd.ideap.maven.MvnBundle;
import org.jetbrains.idea.maven.navigator.MavenNavigationUtil;
import org.jetbrains.idea.maven.project.MavenProject;

public class MvnOpenPomAction extends BaseNavigateToSourceAction {

    protected MvnOpenPomAction() {
        super(true);
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        MavenProject project = MvnModuleContextAction.getProject(event.getDataContext());
        VirtualFile pomFile = LocalFileSystem.getInstance().findFileByPath(project.getDirectory() + "/pom.xml");
        OpenSourceUtil.navigate(true, MavenNavigationUtil.createNavigatableForPom(event.getProject(), pomFile));
    }

    @Override
    public void update(AnActionEvent event) {
        super.update(event);
        Presentation presentation = event.getPresentation();
        presentation.setText(MvnBundle.message("maven.quick.list.open.pom"));
        presentation.setEnabled(MvnModuleContextAction.getProject(event.getDataContext()) != null);
    }
}
