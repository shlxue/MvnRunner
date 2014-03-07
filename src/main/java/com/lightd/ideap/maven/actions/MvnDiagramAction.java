package com.lightd.ideap.maven.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public abstract class MvnDiagramAction extends MvnModuleContextAction {
    private final AnAction diagramAction = ActionManager.getInstance().getAction("ShowUmlDiagramPopup");

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        Presentation presentation = e.getPresentation();
        presentation.setVisible(diagramAction != null);
        presentation.setEnabled(isAvailable(e));
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        AnActionEvent moduleEvent = rebuildContext(e);
        moduleEvent.setInjectedContext(true);
        diagramAction.actionPerformed(moduleEvent);
    }

    @Override
    protected boolean isAvailable(AnActionEvent e) {
        return diagramAction != null && super.isAvailable(e);
    }

    private AnActionEvent rebuildContext(AnActionEvent e) {
        assert e.getProject() != null;
        MavenElementDataContext context = new MavenElementDataContext(e.getDataContext());
        wrap(e.getProject(), context, e);
        return new AnActionEvent(e.getInputEvent(), context, e.getPlace(), e.getPresentation(), e.getActionManager(), e.getModifiers());
    }

    protected abstract void wrap(@NotNull Project project, MockElement mockElement, AnActionEvent event);

    interface MockElement {
        void mock(String key, Object element, Collection<String> contextKeys);
    }

    class MavenElementDataContext implements DataContext, MockElement {
        private String elementKey;
        private Object element;
        private Collection<String> contextKeys;
        private final DataContext context;

        MavenElementDataContext(DataContext context) {
            this.context = context;
        }

        @Override
        public void mock(String key, Object element, Collection<String> contextKeys) {
            this.elementKey = key;
            this.element = element;
            this.contextKeys = contextKeys;
        }

        @Nullable
        @Override
        public Object getData(@NonNls String key) {
            if (!contextKeys.contains(key)) return null;
            Object data = context.getData(key);
            if (data == null && elementKey.equals(key)) data = element;
            return data;
        }
    }
}
