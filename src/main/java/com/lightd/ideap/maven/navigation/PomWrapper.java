package com.lightd.ideap.maven.navigation;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.impl.RenameableFakePsiElement;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.model.MavenId;

import javax.swing.*;

public class PomWrapper extends RenameableFakePsiElement {
    private final MavenId coordinate;
    private final String rootPath;
    private final boolean showLocation;
    private final boolean imported;
    private final PsiFileSystemItem pomFile;

    PomWrapper(PsiFileSystemItem pomFile, MavenId coordinate, String rootPath, boolean showLocation, boolean imported) {
        super(pomFile.getParent());
        this.pomFile = pomFile;
        this.coordinate = coordinate;
        this.rootPath = rootPath;
        this.showLocation = showLocation;
        this.imported = imported;
    }

    @Override
    public String getTypeName() {
        return pomFile.getVirtualFile().getFileType().getName();
    }

    @Override
    public String getName() {
        String artifact = coordinate.getArtifactId();
        if (artifact.startsWith(coordinate.getGroupId()))
            artifact = "." + artifact.substring(coordinate.getGroupId().length());
        return coordinate.getGroupId() + ":" + artifact;
    }

    @Override
    public Icon getIcon() {
        return imported ? pomFile.getIcon(ICON_FLAG_VISIBILITY) : AllIcons.FileTypes.Xml;
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
