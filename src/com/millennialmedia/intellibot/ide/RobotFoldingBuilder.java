package com.millennialmedia.intellibot.ide;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.tree.TokenSet;
import com.millennialmedia.intellibot.psi.RobotTokenTypes;
import com.millennialmedia.intellibot.psi.element.Heading;
import com.millennialmedia.intellibot.psi.element.KeywordDefinition;
import com.millennialmedia.intellibot.psi.element.RobotStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author mrubino
 */
public class RobotFoldingBuilder implements FoldingBuilder, DumbAware {

    private static final String ELLIPSIS = "...";

    private static final TokenSet BLOCKS_TO_FOLD = TokenSet.create(RobotTokenTypes.KEYWORD_DEFINITION, RobotTokenTypes.HEADING);

    @NotNull
    public FoldingDescriptor[] buildFoldRegions(@NotNull ASTNode node, @NotNull Document document) {
        Collection<FoldingDescriptor> descriptors = new ArrayList<FoldingDescriptor>();
        appendDescriptors(node, descriptors);
        return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
    }

    private void appendDescriptors(ASTNode node, Collection<FoldingDescriptor> descriptors) {
        if (BLOCKS_TO_FOLD.contains(node.getElementType()) &&
                node.getTextRange().getLength() >= 2 &&
                node.getPsi() instanceof RobotStatement) {
            descriptors.add(new FoldingDescriptor(node, node.getTextRange()));
        }
        ASTNode child = node.getFirstChildNode();
        while (child != null) {
            appendDescriptors(child, descriptors);
            child = child.getTreeNext();
        }
    }

    @Nullable
    public String getPlaceholderText(@NotNull ASTNode node) {
        if (node.getPsi() instanceof Heading ||
                node.getPsi() instanceof KeywordDefinition) {
            ItemPresentation presentation = ((NavigationItem) node.getPsi()).getPresentation();
            if (presentation != null) {
                return presentation.getPresentableText();
            }
        }
        return ELLIPSIS;
    }

    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return node.getPsi() instanceof Heading && ((Heading) node.getPsi()).isSettings();
    }
}
