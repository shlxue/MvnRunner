package com.lightd.ideap.maven.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.project.MavenProject;

import java.util.Arrays;
import java.util.Collection;

public class MvnDiagramAction extends MvnModuleContextAction {
    private final AnAction showDiagramPopupAction = ActionManager.getInstance().getAction("ShowUmlDiagramPopup");

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        Presentation presentation = e.getPresentation();
        presentation.setVisible(showDiagramPopupAction != null);
        presentation.setEnabled(isAvailable(e));
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        AnActionEvent moduleEvent = rebuildContext(e);
        if (moduleEvent != null) {
            showDiagramPopupAction.actionPerformed(moduleEvent);
        }
    }

    @Override
    protected boolean isAvailable(AnActionEvent e) {
        return showDiagramPopupAction != null && super.isAvailable(e);
    }

    private AnActionEvent rebuildContext(AnActionEvent e) {
        assert e.getProject() != null;
        MavenProject mavenProject = MvnModuleContextAction.getProject(e.getDataContext());
        Module module = ModuleUtilCore.findModuleForFile(mavenProject.getFile(), e.getProject());
        if (module != null) {
            DataContext context = new MavenModuleDataContext(module, e.getDataContext());
            return new AnActionEvent(e.getInputEvent(), context, e.getPlace(), e.getPresentation(), e.getActionManager(), e.getModifiers());
        }
        return null;
    }

    static final Collection<String> moduleKeys = Arrays.asList(
            CommonDataKeys.PROJECT.getName(),
            LangDataKeys.MODULE_CONTEXT.getName());

    class MavenModuleDataContext implements DataContext {
        private final Module module;
        private final DataContext context;

        MavenModuleDataContext(Module module, DataContext context) {
            this.module = module;
            this.context = context;
        }

        @Nullable
        @Override
        public Object getData(@NonNls String key) {
            if (!moduleKeys.contains(key)) return null;
            Object data = context.getData(key);
            if (data == null && LangDataKeys.MODULE_CONTEXT.getName().equals(key)) {
                data = module;
            }
            return data;
        }
    }
}
