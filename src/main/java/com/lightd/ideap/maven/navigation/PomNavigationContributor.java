package com.lightd.ideap.maven.navigation;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.MinusculeMatcher;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.GlobalSearchScopes;
import com.intellij.util.ArrayUtil;
import com.intellij.util.CommonProcessors;
import com.intellij.util.Processor;
import com.lightd.ideap.maven.settings.MvnRunConfigurationSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.dom.MavenDomUtil;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PomNavigationContributor implements ChooseByNameContributor, DumbAware {
    private final Project project;
    private final boolean showPomLocation;
    private MavenProjectsManager projectsManager;

    public PomNavigationContributor(Project project) {
        this.project = project;
        showPomLocation = MvnRunConfigurationSettings.getInstance().isShowPomLocation();
    }

    public void processNames(@NotNull final Processor<String> processor, @NotNull GlobalSearchScope scope, IdFilter filter) {
        projectsManager = MavenProjectsManager.getInstance(project);
        List<MavenProject> mavenProjects = projectsManager.getProjectsTreeForTests().getRootProjects();
        if (mavenProjects.size() > 0) {
            addProjectNames(processor, mavenProjects.get(0));

            if (scope.isSearchInLibraries()) {
                for (MavenId mavenId : getNotImportPoms(mavenProjects.get(0)).keySet()) {
                    processor.process(mavenId.getKey());
                }
            }
        }
    }

    private void addProjectNames(final Processor<String> processor, MavenProject mavenProject) {
        processor.process(mavenProject.getMavenId().getKey());
        for (MavenProject subModule : projectsManager.getModules(mavenProject)) {
            addProjectNames(processor, subModule);
        }
    }

    public void processElementsWithName(@NotNull String name,
                                        @NotNull final Processor<NavigationItem> processor,
                                        @NotNull FindSymbolParameters parameters) {
        final MinusculeMatcher matcher = NameUtil.buildMatcher(parameters.getCompletePattern(), NameUtil.MatchingCaseSensitivity.FIRST_LETTER);

        if (!matcher.isStartMatch(name.substring(0, name.lastIndexOf(':')))) return;

        String[] names = name.split(":");
        MavenId mavenId = new MavenId(names[0], names[1], names[2]);
        projectsManager = MavenProjectsManager.getInstance(project);
        MavenProject p = projectsManager.findProject(mavenId);
        PsiFileSystemItem pomFile = null;
        if (p != null) {
            pomFile = PsiManager.getInstance(project).findFile(p.getFile());
        } else if (parameters.isSearchInLibraries()) {
            Map<MavenId, PsiFile> notImportPoms = getNotImportPoms(projectsManager.getProjectsTreeForTests().getRootProjects().get(0));
            pomFile = notImportPoms.get(mavenId);
        }
        if (pomFile != null) {
            PomWrapper pomWrapper = new PomWrapper(pomFile, mavenId, project.getBasePath(), showPomLocation, p != null);
            processor.process(pomWrapper);
        }
    }

    @NotNull
    @Override
    public String[] getNames(Project project, boolean includeNonProjectItems) {
        CommonProcessors.CollectProcessor<String> processor = new CommonProcessors.CollectProcessor<String>();
        GlobalSearchScope scope = includeNonProjectItems ? GlobalSearchScope.allScope(project) : GlobalSearchScope.projectScope(project);
        processNames(processor, scope, IdFilter.getProjectIdFilter(project, includeNonProjectItems));
        return ArrayUtil.toStringArray(processor.getResults());
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String name, final String pattern, Project project, boolean includeNonProjectItems) {
        CommonProcessors.CollectProcessor<NavigationItem> processor = new CommonProcessors.CollectProcessor<NavigationItem>();
        processElementsWithName(name, processor, FindSymbolParameters.wrap(pattern, project, includeNonProjectItems));
        return processor.toArray(new NavigationItem[processor.getResults().size()]);
    }

    private Map<MavenId, PsiFile> getNotImportPoms(MavenProject mavenProject) {
        GlobalSearchScope pomScope = GlobalSearchScopes.directoryScope(project, mavenProject.getDirectoryFile(), true);
        PsiFile[] psiFiles = FilenameIndex.getFilesByName(project, "pom.xml", pomScope);
        Map<MavenId, PsiFile> poms = new LinkedHashMap<MavenId, PsiFile>(psiFiles.length);
        for (PsiFile psiFile : psiFiles) {
            if (projectsManager.findProject(psiFile.getVirtualFile()) == null) {
                MavenId mavenId = MavenDomUtil.describe(psiFile);
                poms.put(mavenId, psiFile);
            }
        }
        return poms;
    }
}
