package com.lightd.ideap.maven.execution;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.lightd.ideap.maven.MvnBundle;

import java.util.Collection;

public abstract class JavaElementConfigurationProducer extends MavenModuleConfigurationProducer {

    protected PsiClass psiClass;
    protected PsiPackage psiPackage;

    @Override
    protected boolean initContext(ConfigurationContext context) {
        psiClass = null;
        psiPackage = null;
        if (super.initContext(context)) {
            psiClass = getPsiClass();
            if (psiClass == null) {
                psiPackage = getPsiPackage(context.getDataContext());
            }
            return true;
        }
        return false;
    }

    @Override
    protected String generateName() {
        return psiClass.getName();
    }

    private PsiClass getPsiClass() {
        if (psiFile != null && JavaFileType.INSTANCE.equals(psiFile.getFileType())) {
            PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile.getContainingFile();
            String name = getJavaClassName(psiJavaFile.getPackageName(), psiJavaFile.getName());
            Project project = psiFile.getProject();
            return JavaPsiFacade.getInstance(project).findClass(name, GlobalSearchScope.projectScope(project));
        }
        return null;
    }

    private PsiPackage getPsiPackage(DataContext dataContext) {
        PsiElement psiElement = CommonDataKeys.PSI_ELEMENT.getData(dataContext);
        if (psiElement instanceof PsiDirectory) {
            return JavaDirectoryService.getInstance().getPackage((PsiDirectory) psiElement);
        }
        return null;
    }

    protected final String findByPrefix(Collection<String> collection, String prefix) {
        for (String parameter : collection) {
            if (parameter.startsWith(prefix)) {
                return parameter;
            }
        }
        return "";
    }

    protected String getJavaClassName(String packageName, String className) {
        String name = MvnBundle.message("java.class.name", packageName, className);
        if (name.startsWith(".")) name = name.substring(1);
        if (name.endsWith(".java")) {
            name = name.substring(0, name.length() - 5);
        }
        return name;
    }
}
