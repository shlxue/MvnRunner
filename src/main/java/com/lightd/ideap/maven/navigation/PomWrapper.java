package com.lightd.ideap.maven.navigation;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.impl.RenameableFakePsiElement;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.model.MavenCoordinate;

import javax.swing.*;

public class PomWrapper extends RenameableFakePsiElement {
    private final MavenCoordinate coordinate;
    private final String rootPath;
    private final boolean showLocation;
    private final PsiFileSystemItem pomFile;

    PomWrapper(PsiFileSystemItem pomFile, MavenCoordinate coordinate, String rootPath, boolean showLocation) {
        super(pomFile.getParent());
        this.pomFile = pomFile;
        this.coordinate = coordinate;
        this.rootPath = rootPath;
        this.showLocation = showLocation;
    }

    @Override
    public String getTypeName() {
        return pomFile.getVirtualFile().getFileType().getName();
    }

    @Override
    public String getName() {
        return coordinate.getGroupId() + ":" + coordinate.getArtifactId();
    }

    @Override
    public Icon getIcon() {
        return pomFile.getIcon(ICON_FLAG_VISIBILITY);
    }

    @Nullable
    @Override
    public String getLocationString() {
        String localPath = null;
        if (showLocation && pomFile.getPresentation() != null) {
            localPath = pomFile.getPresentation().getLocationString();
            if (localPath != null && localPath.length() > rootPath.length()) {
                localPath = "." + localPath.substring(rootPath.length());
            }
        }
        return localPath;
    }

    @Override
    public boolean isValid() {
        return pomFile.isValid();
    }

    @Override
    public PsiFile getContainingFile() {
        if (pomFile instanceof PsiFile) {
            return (PsiFile) pomFile;
        }
        return pomFile.getContainingFile();
    }

    public VirtualFile getVirtualFile() {
        return pomFile.getVirtualFile();
    }
}
