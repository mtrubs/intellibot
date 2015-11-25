package com.millennialmedia.intellibot.ide.structureview;


import com.intellij.icons.AllIcons;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.navigation.ColoredItemPresentation;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.millennialmedia.intellibot.psi.element.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author mrubino
 * @since 2015-11-23
 */
public class RobotStructureViewElement implements StructureViewTreeElement {

    private static final String UNKNOWN = "Unknown";
    private static final StructureViewTreeElement[] EMPTY = {};

    private PsiElement element;
    private ElementIcon elementIcon;

    private enum ElementIcon {
        File {
            @Nullable
            @Override
            protected Icon getIcon(PsiElement element) {
                return element.getIcon(0);
            }
        },
        Keyword {
            @Nullable
            @Override
            protected Icon getIcon(PsiElement element) {
                return AllIcons.Nodes.Method;
            }
        },
        TestCase {
            @Nullable
            @Override
            protected Icon getIcon(PsiElement element) {
                return AllIcons.RunConfigurations.Junit;
            }
        },
        Variable {
            @Nullable
            @Override
            protected Icon getIcon(PsiElement element) {
                return AllIcons.Nodes.Variable;
            }
        };

        @Nullable
        protected abstract Icon getIcon(PsiElement element);
    }

    protected RobotStructureViewElement(PsiElement element) {
        this(element, ElementIcon.File);
    }

    private RobotStructureViewElement(PsiElement element, ElementIcon elementIcon) {
        this.element = element;
        this.elementIcon = elementIcon;
    }

    protected StructureViewTreeElement createChild(PsiElement element, ElementIcon elementIcon) {
        return new RobotStructureViewElement(element, elementIcon);
    }

    @Override
    public PsiElement getValue() {
        return this.element;
    }

    @Override
    public void navigate(boolean requestFocus) {
        // TODO: implement
        //this.element.navigate(requestFocus);
    }

    @Override
    public boolean canNavigate() {
        // TODO: implement
        return false;
        //return this.element.canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        // TODO: implement
        //return this.element.canNavigateToSource();
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RobotStructureViewElement) {
            String name = getDisplayName();
            String otherName = ((RobotStructureViewElement) o).getDisplayName();
            return name.equals(otherName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getDisplayName().hashCode();
    }

    @NotNull
    public StructureViewTreeElement[] getChildren() {
        if (this.element instanceof RobotFile) {
            Collection<StructureViewTreeElement> children = new ArrayList<StructureViewTreeElement>();
            Heading[] headings = PsiTreeUtil.getChildrenOfType(this.element, Heading.class);
            if (headings != null) {
                for (Heading heading : headings) {
                    for (DefinedKeyword keyword : heading.getDefinedKeywords()) {
                        if (keyword instanceof KeywordDefinition) {
                            children.add(createChild((KeywordDefinition) keyword, ElementIcon.Keyword));
                        }
                    }
                    for (DefinedKeyword keyword : heading.getTestCases()) {
                        if (keyword instanceof KeywordDefinition) {
                            children.add(createChild((KeywordDefinition) keyword, ElementIcon.TestCase));
                        }
                    }
                    for (DefinedVariable variable : heading.getDefinedVariables()) {
                        if (variable instanceof VariableDefinition) {
                            children.add(createChild((VariableDefinition) variable, ElementIcon.Variable));
                        }
                    }
                }
            }
            return children.toArray(new StructureViewTreeElement[children.size()]);
        } else {
            return EMPTY;
        }
    }

    @NotNull
    private String getDisplayName() {
        if (this.element instanceof RobotFile) {
            return ((RobotFile) this.element).getName();
        } else if (this.element instanceof RobotStatement) {
            return ((RobotStatement) this.element).getPresentableText();
        } else {
            return UNKNOWN;
        }
    }

    @Nullable
    private Icon getDisplayIcon() {
        return this.elementIcon.getIcon(this.element);
    }

    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        return new ColoredItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return getDisplayName();
            }

            @Nullable
            @Override
            public TextAttributesKey getTextAttributesKey() {
                return null;
            }

            @Nullable
            @Override
            public String getLocationString() {
                return null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean open) {
                return getDisplayIcon();
            }
        };
    }
}
