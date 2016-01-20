package com.millennialmedia.intellibot.ide.structure;

import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewModelBase;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.Filter;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.millennialmedia.intellibot.psi.element.KeywordDefinition;
import com.millennialmedia.intellibot.psi.element.RobotFile;
import com.millennialmedia.intellibot.psi.element.VariableDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author mrubino
 * @since 2015-11-23
 */
public class RobotStructureViewModel extends StructureViewModelBase implements StructureViewModel.ElementInfoProvider, StructureViewModel.ExpandInfoProvider {

    private static final Filter[] FILTERS = new Filter[]{
            new RobotTypeFilter("SHOW_TEST_CASES", RobotViewElementType.TestCase),
            new RobotTypeFilter("SHOW_KEYWORDS", RobotViewElementType.Keyword),
            new RobotTypeFilter("SHOW_VARIABLES", RobotViewElementType.Variable),
            new RobotTypeFilter("SHOW_HEADINGS", RobotViewElementType.Heading)
    };

    public RobotStructureViewModel(@NotNull PsiFile psiFile, @Nullable Editor editor) {
        this(psiFile, editor, new RobotStructureViewElement(psiFile));
        withSorters(
                Sorter.ALPHA_SORTER,
                RobotTypeSorter.getInstance()
        );
    }

    public RobotStructureViewModel(@NotNull PsiFile psiFile, @Nullable Editor editor, @NotNull StructureViewTreeElement element) {
        super(psiFile, editor, element);
    }


    @Override
    public boolean isAlwaysShowsPlus(StructureViewTreeElement element) {
        return element.getValue() instanceof RobotFile;
    }

    @Override
    public boolean isAlwaysLeaf(StructureViewTreeElement element) {
        final Object value = element.getValue();
        return value instanceof KeywordDefinition || value instanceof VariableDefinition;
    }

    @Override
    public boolean shouldEnterElement(Object element) {
        return false;
    }

    @NotNull
    @Override
    public Filter[] getFilters() {
        return FILTERS;
    }

    @Override
    public boolean isAutoExpand(@NotNull StructureViewTreeElement element) {
        return element.getValue() instanceof RobotFile;
    }

    @Override
    public boolean isSmartExpand() {
        return false;
    }
}
