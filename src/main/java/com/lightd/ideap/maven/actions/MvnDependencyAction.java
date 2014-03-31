package com.lightd.ideap.maven.actions;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.utils.MavenDataKeys;

import java.util.Arrays;
import java.util.Collection;

public class MvnDependencyAction extends MvnDiagramAction {
    private static final String MavenExtPluginId = "org.jetbrains.idea.maven.ext";
    private static final Collection<String> moduleKeys = Arrays.asList(
            PlatformDataKeys.PROJECT.getName(),
            LangDataKeys.MODULE_CONTEXT.getName());
    private static final Collection<String> pomKeys = Arrays.asList(
            PlatformDataKeys.CONTEXT_COMPONENT.getName(),
            PlatformDataKeys.PROJECT.getName(),
            MavenDataKeys.MAVEN_PROJECTS_TREE.getName(),
            LangDataKeys.FILE_EDITOR.getName(),
            PlatformDataKeys.VIRTUAL_FILE.getName(),
            LangDataKeys.PSI_FILE.getName());

    @Override
    protected void wrap(@NotNull Project project, MockElement mockElement, AnActionEvent e) {
        MavenProject mavenProject = MvnModuleContextAction.getProject(e.getDataContext());
        if (!initPomElement(project, e.getDataContext(), mockElement)) {
            Object module = ModuleUtilCore.findModuleForFile(mavenProject.getFile(), project);
            mockElement.mock(LangDataKeys.MODULE_CONTEXT.getName(), module, moduleKeys);
        }
    }

    private boolean initPomElement(Project project, DataContext context, MockElement mockElement) {
        if (!checkMavenExtPlugin()) return false;
        if (MavenDataKeys.MAVEN_PROJECTS_TREE.getData(context) == null) {
            PsiFile psiFile = DataKeys.PSI_FILE.getData(context);
            if (psiFile == null || !("pom.xml".equalsIgnoreCase(psiFile.getName()))) return false;
            Object editor = DataKeys.FILE_EDITOR.getData(context);
            if (editor == null) {
                FileEditorManager editorManager = FileEditorManager.getInstance(project);
                if (editorManager.getSelectedEditors().length > 0) {
                    editor = editorManager.getSelectedEditors()[0];
                } else if (editorManager.getSelectedEditors().length > 0)
                    editor = editorManager.getAllEditors()[0];
            }
            mockElement.mock(LangDataKeys.FILE_EDITOR.getName(), editor, pomKeys);
        } else
            mockElement.mock(LangDataKeys.FILE_EDITOR.getName(), null, pomKeys);
        return true;
    }

    private boolean checkMavenExtPlugin() {
        IdeaPluginDescriptor descriptor = PluginManager.getPlugin(PluginId.getId(MavenExtPluginId));
        return descriptor != null && descriptor.isEnabled();
    }
}
