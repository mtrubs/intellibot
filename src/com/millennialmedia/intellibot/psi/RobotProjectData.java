package com.millennialmedia.intellibot.psi;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.MultiMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.millennialmedia.intellibot.psi.element.DefinedVariable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author
 * @since 2019-10-31
 */

public class RobotProjectData {
    private Collection<DefinedVariable> GLOBAL_DEFAULT_VARIABLES = null;
    private Collection<DefinedVariable> BUILT_IN_VARIABLES = null;
    private final Map<String, PsiElement> FILE_CACHE = new HashMap<String, PsiElement>();
    private final MultiMap<PsiElement, String> FILE_NAMES = MultiMap.createSet();

    public static RobotProjectData getInstance(Project project) {
        return ServiceManager.getService(project, RobotProjectData.class);
    }

    public Collection<DefinedVariable> projectGlobalDefaultVariables() {
        return this.GLOBAL_DEFAULT_VARIABLES;
    }
    public synchronized void setProjectGlobalDefaultVariables(Collection<DefinedVariable> variables) {
        this.GLOBAL_DEFAULT_VARIABLES = variables;
    }

    public Collection<DefinedVariable> builtInVariables() {
        return this.BUILT_IN_VARIABLES;
    }
    public synchronized void setBuiltInVariables(Collection<DefinedVariable> variables) {
        this.BUILT_IN_VARIABLES = variables;
    }

    @Nullable
    public PsiElement getFromCache(@NotNull String value) {
//        PsiElement element = FILE_CACHE.get(value);
        // evict the element if it is from an old instance
//        as in service, will getProject().isDisposed() happen?
//        if (element != null && element.getProject().isDisposed()) {
//            evict(element);
//            return null;
//        }
        return FILE_CACHE.get(value);
    }

    public synchronized void addToCache(@NotNull PsiElement element, @NotNull String value) {
        //if (element != null && !element.getProject().isDisposed()) {
            FILE_CACHE.put(value, element);
            //FILE_NAMES.putValue(element, value);
        //}
    }

//    public synchronized void evict(@Nullable PsiElement element) {
//        if (element != null) {
//            Collection<String> keys = FILE_NAMES.remove(element);
//            if (keys != null) {
//                for (String key : keys) {
//                    FILE_CACHE.remove(key);
//                }
//            }
//        }
//    }
}
