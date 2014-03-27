package com.lightd.ideap.maven.execution.actions;

import com.intellij.execution.ConsoleFolding;
import com.intellij.execution.actions.ConsoleActionsPostProcessor;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.util.containers.ContainerUtil;
import com.lightd.ideap.maven.MvnCommandFolding;
import org.jetbrains.annotations.NotNull;

import java.util.List;

class SoftWrapsConsoleActionProcessor extends ConsoleActionsPostProcessor {

    @NotNull
    public AnAction[] postProcess(@NotNull ConsoleView consoleView, @NotNull AnAction[] actions) {
        List<MvnCommandFolding> list = ContainerUtil.findAll(ConsoleFolding.EP_NAME.getExtensions(), MvnCommandFolding.class);
        if (!list.isEmpty() && list.get(0).byMavenRun())
            actions[2] = new MvnSoftWrapsAction(list.get(0));
        return actions;
    }
}
