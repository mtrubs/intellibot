package com.millennialmedia.intellibot.psi.ref;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyFunction;
import com.millennialmedia.intellibot.psi.element.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author mrubino
 */
public class RobotKeywordReference extends PsiReferenceBase<KeywordInvokable> {

    public RobotKeywordReference(@NotNull KeywordInvokable element) {
        this(element, false);
    }

    public RobotKeywordReference(KeywordInvokable element, boolean soft) {
        super(element, soft);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        KeywordInvokable element = getElement();
        String actualKeyword = element.getPresentableText();

        // all files we import are based off the file we are currently in
        RobotFile currentFile = PsiTreeUtil.getParentOfType(getElement(), RobotFile.class);

        Set<PsiFile> robotFiles = new HashSet<PsiFile>();
        Set<RobotPythonClass> pythonClasses = new HashSet<RobotPythonClass>();
        addImports(currentFile, robotFiles, pythonClasses);

        PsiElement psiElement = resolveRobotKeyword(robotFiles, actualKeyword);
        if (psiElement == null) {
            psiElement = resolvePythonKeyword(pythonClasses, actualKeyword);
        }
        return psiElement;
    }

    private static final String ROBOT_BUILT_IN = "BuiltIn";

    private void addRobotBuiltIn(Collection<RobotPythonClass> pythonClasses) {
        PyClass builtIn = PythonResolver.findClass(ROBOT_BUILT_IN, myElement.getProject());
        if (builtIn != null) {
            // TODO: what if there is more than one class found?
            pythonClasses.add(new RobotPythonClass(ROBOT_BUILT_IN, builtIn));
        }
    }

    private void addImports(RobotFile currentFile, Collection<PsiFile> robotFiles, Collection<RobotPythonClass> pythonClasses) {
        // TODO: better way to search for these files
        // TODO: better way to search for matches in each file
        if (currentFile == null) {
            return;
        }
        // add our own file, since we could use keywords in it too
        robotFiles.add(currentFile);
        addRobotBuiltIn(pythonClasses);

        Heading[] headings = PsiTreeUtil.getChildrenOfType(currentFile, Heading.class);
        if (headings == null) {
            return;
        }
        for (Heading heading : headings) {
            if (heading.containsImports()) {
                Import[] imports = PsiTreeUtil.getChildrenOfType(heading, Import.class);
                if (imports == null) {
                    continue;
                }
                for (Import eachImport : imports) {
                    Argument importData = PsiTreeUtil.getChildOfType(eachImport, Argument.class);
                    if (importData == null) {
                        continue;
                    }
                    String text = importData.getPresentableText();
                    if (eachImport.isResource()) {
                        //TODO: crude, we want to look up the actual file in the future, this is quick and dirty
                        String[] path = text.split("/");
                        PsiFile[] files = FilenameIndex.getFilesByName(myElement.getProject(), path[path.length - 1],
                                GlobalSearchScope.allScope(myElement.getProject()));
                        Collections.addAll(robotFiles, files);
                    } else if (eachImport.isLibrary()) {
                        PyClass lib = PythonResolver.findClass(text, myElement.getProject());
                        if (lib != null) {
                            pythonClasses.add(new RobotPythonClass(text, lib));
                        }
                    }
                }
            }
        }
    }

    private PsiElement resolveRobotKeyword(Collection<PsiFile> robotFiles, String actualKeyword) {
        //find the actual keyword to link to
        for (PsiFile file : robotFiles) {
            if (file == null) {
                continue;
            }
            Heading[] headings = PsiTreeUtil.getChildrenOfType(file, Heading.class);
            if (headings != null) {
                for (Heading heading : headings) {
                    if (heading.containsKeywordDefinitions()) {
                        KeywordDefinition[] definitions = PsiTreeUtil.getChildrenOfType(heading, KeywordDefinition.class);
                        if (definitions == null) {
                            continue;
                        }
                        for (KeywordDefinition definition : definitions) {
                            if (definition.matches(actualKeyword)) {
                                return definition;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private PsiElement resolvePythonKeyword(@NotNull Collection<RobotPythonClass> pythonClasses, @NotNull String keyword) {
        for (RobotPythonClass pythonClass : pythonClasses) {
            PyFunction function = pythonClass.findMethodByKeyword(keyword);
            if (function != null) {
                return function;
            }
        }
        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        // TODO: does this come into play with inline arguments? I bet you it does
        return EMPTY_ARRAY;
    }
}
