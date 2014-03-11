package com.lightd.ideap.maven.navigation;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import icons.MavenIcons;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.dom.references.MavenPsiElementWrapper;
import org.jetbrains.idea.maven.model.MavenCoordinate;

import javax.swing.*;

public class PomWrapper extends MavenPsiElementWrapper {
    private final MavenCoordinate coordinate;
    private final String rootPath;

    PomWrapper(PsiElement wrappeeElement, MavenCoordinate coordinate, String rootPath) {
        super(wrappeeElement, null);
        this.coordinate = coordinate;
        this.rootPath = rootPath;
    }

    @Override
    public String getName() {
        return coordinate.getGroupId() + ":" + coordinate.getArtifactId();
    }

    @Override
    public Icon getIcon() {
        return MavenIcons.MavenLogo;
    }

    @Override
    public PsiFile getWrappee() {
        return (PsiFile) super.getWrappee();
    }

    @Nullable
    @Override
    public String getLocationString() {
        String localPath = null;
        if (getWrappee().getPresentation() != null) {
            localPath = getWrappee().getPresentation().getLocationString();
            if (localPath != null && localPath.length() > rootPath.length()) {
                return "." + localPath.substring(rootPath.length());
            }
        }
        return localPath;
    }

    @Override
    public boolean isValid() {
        return getWrappee().isValid();
    }

    @Override
    public PsiFile getContainingFile() {
        return getWrappee().getContainingFile();
    }

    public VirtualFile getVirtualFile() {
        return getWrappee().getVirtualFile();
    }
}
