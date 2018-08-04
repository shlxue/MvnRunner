package com.lightd.ideap.maven.execution.actions;

import com.intellij.execution.impl.EditorHyperlinkSupport;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.FoldRegion;
import com.intellij.openapi.editor.FoldingModel;
import com.intellij.openapi.editor.actions.ToggleUseSoftWrapsToolbarAction;
import com.intellij.openapi.editor.impl.softwrap.SoftWrapAppliancePlaces;
import com.lightd.ideap.maven.MvnCommandFolding;
import com.lightd.ideap.maven.actions.MvnModuleContextAction;

import java.util.Objects;

class MvnSoftWrapsAction extends ToggleUseSoftWrapsToolbarAction {

    private final MvnCommandFolding commandFolding;

    MvnSoftWrapsAction(MvnCommandFolding commandFolding) {
        super(SoftWrapAppliancePlaces.CONSOLE);
        this.commandFolding = commandFolding;
    }

    @Override
    public boolean isSelected(AnActionEvent e) {
        boolean selected = super.isSelected(e);
        if (selected  && !(getLastState(e.getPresentation()))) {
            setSelected(e, true);
        }
        return selected;
    }

    @Override
    public void setSelected(AnActionEvent event, final boolean state) {
        super.setSelected(event, state);
        Editor editor = getEditor(event);
        if (editor == null) return;

        String text = EditorHyperlinkSupport.getLineText(editor.getDocument(), 0, false);

        final String placeholder = commandFolding.getPlaceHolder(Objects.requireNonNull(MvnModuleContextAction.getEventProject(event)), text);
        if (placeholder == null) return;
        final FoldingModel foldingModel = editor.getFoldingModel();
        FoldRegion[] foldRegions = foldingModel.getAllFoldRegions();
        if (!state && foldRegions.length <= 0) return;
        Runnable foldTask = null;

        final int endFoldRegionOffset = editor.getDocument().getLineEndOffset(0);
        Runnable addCollapsedFoldRegionTask = () -> {
            FoldRegion foldRegion = foldingModel.addFoldRegion(0, endFoldRegionOffset, placeholder);
            if (foldRegion != null) {
                foldRegion.setExpanded(false);
            }
        };
        if (foldRegions.length <= 0 || state) {
            if (endFoldRegionOffset > 0)
                foldTask = addCollapsedFoldRegionTask;
        }
        else {
            final FoldRegion foldRegion = foldRegions[0];
            if (foldRegion.getStartOffset() == 0 && foldRegion.getEndOffset() == endFoldRegionOffset) {
                foldTask = () -> foldRegion.setExpanded(true);
            }
        }

        if (foldTask != null)
            foldingModel.runBatchFoldingOperation(foldTask);
    }

    private boolean getLastState(Presentation presentation) {
        Object rs = presentation.getClientProperty(SELECTED_PROPERTY);
        if (rs instanceof Boolean)
            return (Boolean)rs;
        return false;
    }
}
