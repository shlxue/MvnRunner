package com.lightd.ideap.maven.navigation.actions;

import com.intellij.featureStatistics.FeatureUsageTracker;
import com.intellij.ide.actions.GotoActionBase;
import com.intellij.ide.util.gotoByName.ChooseByNamePopup;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.pom.Navigatable;
import com.lightd.ideap.maven.navigation.PomWrapper;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;

public class GotoPomAction extends GotoActionBase {

    @Override
    protected void gotoActionPerformed(AnActionEvent e) {
        FeatureUsageTracker.getInstance().triggerFeatureUsed("navigation.popup.file");
        final Project project = e.getData(CommonDataKeys.PROJECT);
        if (project != null) {
            final GotoPomModel gotoPomModel = new GotoPomModel(project);
            showNavigationPopup(e, gotoPomModel, new GotoPomActionCallback(project), null, false);
        }
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        Presentation presentation = e.getPresentation();
        presentation.setEnabled(MavenActionUtil.hasProject(e.getDataContext()));
    }

    class GotoPomActionCallback extends GotoActionCallback<FileType> {
        private final Project project;

        GotoPomActionCallback(Project project) {
            this.project = project;
        }

        @Override
        public void elementChosen(final ChooseByNamePopup popup, final Object element) {
            if (element == null) return;
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (!(element instanceof PomWrapper)) {
                        return;
                    }
                    PomWrapper wrapper = (PomWrapper) element;
                    if (wrapper.getVirtualFile() == null) return;
                    Navigatable n = new OpenFileDescriptor(project, wrapper.getVirtualFile(), popup.getLinePosition(),
                            popup.getColumnPosition()).setUseCurrentWindow(popup.isOpenInCurrentWindowRequested());

                    if (!n.canNavigate()) return;
                    n.navigate(true);
                }
            }, ModalityState.NON_MODAL);
        }
    }
}
