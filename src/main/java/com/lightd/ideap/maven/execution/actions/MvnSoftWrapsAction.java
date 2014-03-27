package com.lightd.ideap.maven.execution.actions;

import com.intellij.execution.impl.EditorHyperlinkSupport;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.FoldRegion;
import com.intellij.openapi.editor.FoldingModel;
import com.intellij.openapi.editor.actions.ToggleUseSoftWrapsToolbarAction;
import com.intellij.openapi.editor.impl.softwrap.SoftWrapAppliancePlaces;
import com.lightd.ideap.maven.MvnCommandFolding;

class MvnSoftWrapsAction extends ToggleUseSoftWrapsToolbarAction {

    private final MvnCommandFolding commandFolding;

    public MvnSoftWrapsAction(MvnCommandFolding commandFolding) {
        super(SoftWrapAppliancePlaces.CONSOLE);
        this.commandFolding = commandFolding;
    }

    @Override
    public void setSelected(AnActionEvent event, final boolean state) {
        super.setSelected(event, state);
        Editor editor = getEditor(event);
        if (editor == null) return;

        String text = EditorHyperlinkSupport.getLineText(editor.getDocument(), 0, false);
        if (text == null) return;

        final String placeholder = commandFolding.getPlaceHolder(text);
        final FoldingModel foldingModel = editor.getFoldingModel();
        FoldRegion[] foldRegions = foldingModel.getAllFoldRegions();
        Runnable foldTask = null;

        final int endFoldRegionOffset = editor.getDocument().getLineEndOffset(0);
        Runnable addCollapsedFoldRegionTask = new Runnable() {
            @Override
            public void run() {
                FoldRegion foldRegion = foldingModel.addFoldRegion(0, endFoldRegionOffset, placeholder);
                if (foldRegion != null) {
                    foldRegion.setExpanded(false);
                }
            }
        };
        if (foldRegions.length <= 0) {
            if (!state) return;
            foldTask = addCollapsedFoldRegionTask;
        }
        else {
            final FoldRegion foldRegion = foldRegions[0];
            if (foldRegion.getStartOffset() == 0 && foldRegion.getEndOffset() == endFoldRegionOffset) {
                foldTask = new Runnable() {
                    @Override
                    public void run() {
                        foldRegion.setExpanded(!state);
                    }
                };
            }
            else if (state)
                foldTask = addCollapsedFoldRegionTask;
        }

        if (foldTask != null)
            foldingModel.runBatchFoldingOperation(foldTask);
    }
}
