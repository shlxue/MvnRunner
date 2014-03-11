package com.lightd.ideap.maven.navigation.actions;

import com.intellij.ide.util.gotoByName.ContributorsBasedGotoByModel;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.openapi.project.Project;
import com.lightd.ideap.maven.MvnBundle;
import com.lightd.ideap.maven.navigation.PomNavigationContributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GotoPomModel extends ContributorsBasedGotoByModel {
    protected GotoPomModel(@NotNull Project project) {
        super(project, new ChooseByNameContributor[]{new PomNavigationContributor(project)});
    }

    @Override
    public String getPromptText() {
        return MvnBundle.message("prompt.gotopom.enter.pom.name");
    }

    @Override
    public String getNotInMessage() {
        return MvnBundle.message("label.non.pom.files.found");
    }

    @Override
    public String getNotFoundMessage() {
        return MvnBundle.message("label.no.files.found");
    }

    @Nullable
    @Override
    public String getCheckBoxName() {
        return MvnBundle.message("checkbox.include.non.project.pom.files");
    }

    @Deprecated
    @Override
    public char getCheckBoxMnemonic() {
        return 'n';
    }

    @Override
    public boolean loadInitialCheckBoxState() {
        return false;
    }

    @Override
    public void saveInitialCheckBoxState(boolean b) {
    }

    @NotNull
    @Override
    public String[] getSeparators() {
        return new String[]{":"};
    }

    @Nullable
    @Override
    public String getFullName(Object o) {
        return getElementName(o);
    }

    @Override
    public boolean willOpenEditor() {
        return true;
    }
}
