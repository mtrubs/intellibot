package com.millennialmedia.intellibot.ide.structure;


import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.navigation.ColoredItemPresentation;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.pom.Navigatable;
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

    private final PsiElement element;
    private final RobotViewElementType type;

    protected RobotStructureViewElement(PsiElement element) {
        this(element, RobotViewElementType.File);
    }

    private RobotStructureViewElement(PsiElement element, RobotViewElementType type) {
        this.element = element;
        this.type = type;
    }

    protected StructureViewTreeElement createChild(PsiElement element, RobotViewElementType type) {
        return new RobotStructureViewElement(element, type);
    }

    @Override
    public PsiElement getValue() {
        return this.element;
    }

    @Override
    public void navigate(boolean requestFocus) {
        if (this.element instanceof Navigatable) {
            ((Navigatable) this.element).navigate(requestFocus);
        }
    }

    @Override
    public boolean canNavigate() {
        return this.type != RobotViewElementType.File &&
                this.element instanceof Navigatable && ((Navigatable) this.element).canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return this.type != RobotViewElementType.File &&
                this.element instanceof Navigatable && ((Navigatable) this.element).canNavigateToSource();
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
                    children.add(createChild(heading, RobotViewElementType.Heading));
                    for (DefinedKeyword keyword : heading.getDefinedKeywords()) {
                        if (keyword instanceof KeywordDefinition) {
                            children.add(createChild((KeywordDefinition) keyword, RobotViewElementType.Keyword));
                        }
                    }
                    for (DefinedKeyword keyword : heading.getTestCases()) {
                        if (keyword instanceof KeywordDefinition) {
                            children.add(createChild((KeywordDefinition) keyword, RobotViewElementType.TestCase));
                        }
                    }
                    for (DefinedVariable variable : heading.getDefinedVariables()) {
                        if (variable instanceof VariableDefinition) {
                            children.add(createChild((VariableDefinition) variable, RobotViewElementType.Variable));
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

    @NotNull
    public RobotViewElementType getType() {
        return this.type;
    }

    @Nullable
    private Icon getDisplayIcon() {
        return this.type.getIcon(this.element);
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
