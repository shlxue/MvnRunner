package com.lightd.ideap.maven.navigation;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentIterator;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWithId;
import com.intellij.util.indexing.FileBasedIndex;

import java.util.BitSet;

public abstract class IdFilter {
    public static final Logger LOG = Logger.getInstance("#com.intellij.ide.util.gotoByName.DefaultFileNavigationContributor");

    public static IdFilter getProjectIdFilter(Project project, boolean includeNonProjectItems) {
        long started = System.currentTimeMillis();
        final BitSet idSet = new BitSet();

        ContentIterator iterator = new ContentIterator() {
            @Override
            public boolean processFile(VirtualFile fileOrDir) {
                int id = ((VirtualFileWithId) fileOrDir).getId();
                if (id < 0) id = -id; // workaround for encountering invalid files, see EA-49915, EA-50599
                idSet.set(id);
                ProgressManager.checkCanceled();
                return true;
            }
        };

        if (!includeNonProjectItems) {
            ProjectRootManager.getInstance(project).getFileIndex().iterateContent(iterator);
        } else {
            FileBasedIndex.getInstance().iterateIndexableFiles(iterator, project, null);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Done filter " + (System.currentTimeMillis() - started) + ":" + idSet.size());
        }
        return new IdFilter() {
            @Override
            public boolean containsFileId(int id) {
                return id >= 0 && idSet.get(id);
            }
        };
    }

    public abstract boolean containsFileId(int id);
}
