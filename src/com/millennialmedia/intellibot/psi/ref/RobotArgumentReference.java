package com.millennialmedia.intellibot.psi.ref;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.millennialmedia.intellibot.ide.config.RobotOptionsProvider;
import com.millennialmedia.intellibot.psi.element.*;
import com.millennialmedia.intellibot.psi.util.PerformanceCollector;
import com.millennialmedia.intellibot.psi.util.PerformanceEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Scott Albertine
 */
public class RobotArgumentReference extends PsiReferenceBase<Argument> {

    public RobotArgumentReference(@NotNull Argument element) {
        this(element, false);
    }

    private RobotArgumentReference(Argument element, boolean soft) {
        super(element, soft);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        PsiElement parent = getElement().getParent();
        // TODO: potentially unsafe cast
        PerformanceCollector debug = new PerformanceCollector((PerformanceEntity) getElement(), "resolve");
        PsiElement result = null;
        // we only want to attempt to resolve a resource/library for the first argument
        if (parent instanceof Import) {
            PsiElement secondChild = getSecondChild(parent);
            if (secondChild == getElement()) {
                Import importElement = (Import) parent;
                if (importElement.isResource()) {
                    result = resolveResource();
                } else if (importElement.isLibrary() || importElement.isVariables()) {
                    result = resolveLibrary();
                }
            //} else {
                //result = resolveVariable();
            }
        } else if (parent instanceof KeywordStatement) {
            PsiElement reference = resolveKeyword();
            result = reference == null ? resolveVariable() : reference;
        }
        debug.complete();
        return result;
    }

    private static PsiElement getSecondChild(PsiElement element) {
        if (element == null) {
            return null;
        }
        PsiElement[] children = element.getChildren();
        return children.length > 0 ? children[0] : null;
    }

    private PsiElement resolveKeyword() {
        Argument element = getElement();
        String keyword = element.getPresentableText();
        // all files we import are based off the file we are currently in
        return ResolverUtils.resolveKeywordFromFile(keyword, element.getContainingFile());
    }

    private PsiElement resolveVariable() {
        Argument element = getElement();
        String variable = element.getPresentableText();
        return ResolverUtils.resolveVariableFromFile(variable, element.getContainingFile());
        /*
        String text = getElement().getPresentableText();
        PsiElement parent = getElement().getParent();
        PsiElement containingStatement = parent.getParent();
        if (containingStatement instanceof VariableDefinition) {
            parent = containingStatement;
            containingStatement = containingStatement.getParent();
        }
        if (containingStatement instanceof KeywordDefinition) {
            // we want to go backwards to get the latest setter
            PsiElement[] children = containingStatement.getChildren();
            boolean seenParent = false;
            for (int i = children.length - 1; i >= 0; i--) {
                PsiElement child = children[i];
                // skip everything until we go past ourselves
                if (child == parent) {
                    seenParent = true;
                    continue;
                }
                if (!seenParent) {
                    continue;
                }
                // now start checking for definitions
                if (child instanceof DefinedVariable) {
                    // ${x}  some keyword results
                    if (((DefinedVariable) child).matches(text)) {
                        return child;
                    }
                } else if (child instanceof KeywordStatement) {
                    PsiElement reference = walkKeyword((KeywordStatement) child, text);
                    if (reference != null) {
                        return reference;
                    }
                }
            }
            for (DefinedVariable variable : ((KeywordDefinition) containingStatement).getDeclaredVariables()) {
                if (variable.matches(text)) {
                    return variable.reference();
                }
            }
        }
        PsiFile file = getElement().getContainingFile();
        if (file instanceof RobotFile) {
            Collection<DefinedVariable> fileVariables = ((RobotFile) file).getDefinedVariables();
            for (DefinedVariable variable : fileVariables) {
                if (variable.matches(text)) {
                    return variable.reference();
                }
            }
            // TODO: __init__ files...
            // TODO: global variables: ~/.robot-env/lib/python2.7/site-packages/robot/variables/__init__.py
        }
        return null;
        */
    }

    /**
     * Walks the keyword tree looking for global variable setting keywords.
     * This only includes variables that are set in this manner as everything else
     * is out of scope.
     *
     * @param statement the keyword statement to find a variable in.
     * @param text      the variable text we are looking for.
     * @return the matching definition if it exists; null otherwise.
     */
    @Nullable
    private PsiElement walkKeyword(@Nullable KeywordStatement statement, String text) {
        if (statement == null) {
            return null;
        } else if (!RobotOptionsProvider.getInstance(getElement().getProject()).allowGlobalVariables()) {
            return null;
        }
        // set test variable  ${x}  ${y}
        DefinedVariable variable = statement.getGlobalVariable();
        if (variable != null && variable.matches(text)) {
            return variable.reference();
        } else {
            KeywordInvokable invokable = statement.getInvokable();
            if (invokable != null) {
                PsiReference reference = invokable.getReference();
                if (reference != null) {
                    PsiElement resolved = reference.resolve();
                    if (resolved instanceof KeywordDefinition) {
                        List<KeywordInvokable> keywords = ((KeywordDefinition) resolved).getInvokedKeywords();
                        Collections.reverse(keywords);
                        for (KeywordInvokable invoked : keywords) {
                            PsiElement parent = invoked.getParent();
                            if (parent instanceof KeywordStatement) {
                                PsiElement result = walkKeyword((KeywordStatement) parent, text);
                                if (result != null) {
                                    return result;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    private PsiElement resolveLibrary() {
        return RobotFileManager.findPython(getElement().getPresentableText(),
                getElement().getProject(), getElement());
    }

    private PsiElement resolveResource() {
        return RobotFileManager.findRobot(getElement().getPresentableText(),
                getElement().getProject(), getElement());
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return EMPTY_ARRAY;
    }
}
