package com.millennialmedia.intellibot.ide.structure;

import com.intellij.ide.util.treeView.smartTree.ActionPresentation;
import com.intellij.ide.util.treeView.smartTree.ActionPresentationData;
import com.intellij.ide.util.treeView.smartTree.Filter;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import org.jetbrains.annotations.NotNull;

/**
 * @author mrubino
 * @since 2015-12-15
 */
class RobotTypeFilter implements Filter {

    @NotNull
    private final String name;
    @NotNull
    private final RobotViewElementType type;

    RobotTypeFilter(@NotNull String name, @NotNull RobotViewElementType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public boolean isVisible(TreeElement treeElement) {
        if (treeElement instanceof RobotStructureViewElement) {
            RobotViewElementType type = ((RobotStructureViewElement) treeElement).getType();
            return this.type != type;
        }
        return false;
    }

    @Override
    public boolean isReverted() {
        return true;
    }

    @NotNull
    @Override
    public ActionPresentation getPresentation() {
        return new ActionPresentationData(
                this.type.getMessage(),
                null,
                this.type.getIcon(null)
        );
    }

    @NotNull
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
