package com.lightd.ideap.maven.navigation;

import com.intellij.navigation.ChooseByNameContributorEx;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.ArrayUtil;
import com.intellij.util.CommonProcessors;
import com.intellij.util.Processor;
import com.intellij.util.indexing.FindSymbolParameters;
import com.intellij.util.indexing.IdFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import java.util.List;

public class PomNavigationContributor implements ChooseByNameContributorEx, DumbAware {
    private final Project project;

    public PomNavigationContributor(Project project) {
        this.project = project;
    }

    @Override
    public void processNames(@NotNull final Processor<String> processor, @NotNull GlobalSearchScope scope, IdFilter filter) {
        List<MavenProject> mavenProjects = MavenProjectsManager.getInstance(project).getRootProjects();
        if (mavenProjects.size() > 0) {
            addProjectNames(processor, mavenProjects.get(0));
        }
    }

    private void addProjectNames(final Processor<String> processor, MavenProject rootProject) {
        MavenId id = rootProject.getMavenId();
        processor.process(String.format("%s:%s:%s", id.getGroupId(), id.getArtifactId(), id.getVersion()));
        for (MavenProject mavenProject : MavenProjectsManager.getInstance(project).getModules(rootProject)) {
            addProjectNames(processor, mavenProject);
        }
    }

    @Override
    public void processElementsWithName(@NotNull String name,
                                        @NotNull final Processor<NavigationItem> processor,
                                        @NotNull FindSymbolParameters parameters) {
        String[] names = name.split(":");
        if (!StringUtil.isEmptyOrSpaces(parameters.getCompletePattern())) {
            String[] pattern = parameters.getCompletePattern().split(":");

            if (pattern.length == 1 && !name.contains(pattern[0]) ||
                    pattern.length > 1 && (!StringUtil.containsIgnoreCase(names[0], pattern[0]) ||
                            !StringUtil.containsIgnoreCase(names[1], pattern[1]))) {
                return;
            }
        }
        MavenId mavenId = new MavenId(names[0], names[1], names[2]);
        MavenProject p = MavenProjectsManager.getInstance(project).findProject(mavenId);
        if (p != null) {
            processor.process(new PomWrapper(PsiManager.getInstance(project).findFile(p.getFile()), mavenId, project.getBasePath()));
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
}
