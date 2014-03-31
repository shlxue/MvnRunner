package com.lightd.ideap.maven.navigation;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.EverythingGlobalScope;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FindSymbolParameters {
    private final String myCompletePattern;
    private final String myLocalPatternName;
    private final GlobalSearchScope mySearchScope;
    private final IdFilter myIdFilter;

    public FindSymbolParameters(@NotNull String pattern, @NotNull String name, @NotNull GlobalSearchScope scope, @Nullable IdFilter idFilter) {
        myCompletePattern = pattern;
        myLocalPatternName = name;
        mySearchScope = scope;
        myIdFilter = idFilter;
    }

    public String getCompletePattern() {
        return myCompletePattern;
    }

    public String getLocalPatternName() {
        return myLocalPatternName;
    }

    public @NotNull GlobalSearchScope getSearchScope() {
        return mySearchScope;
    }

    public @Nullable IdFilter getIdFilter() {
        return myIdFilter;
    }

    public static FindSymbolParameters wrap(@NotNull String pattern, @NotNull Project project, boolean searchInLibraries) {
        return new FindSymbolParameters(
                pattern,
                pattern,
                searchScopeFor(project, searchInLibraries),
                null
        );
    }

    public static GlobalSearchScope searchScopeFor(Project project, boolean searchInLibraries) {
        if (project == null) return new EverythingGlobalScope();
        return searchInLibraries? ProjectScope.getAllScope(project) : ProjectScope.getProjectScope(project);
    }

    public Project getProject() {
        return mySearchScope.getProject();
    }

    public boolean isSearchInLibraries() {
        return mySearchScope.isSearchInLibraries();
    }
}
