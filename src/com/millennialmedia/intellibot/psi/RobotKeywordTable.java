package com.millennialmedia.intellibot.psi;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RobotKeywordTable {
    private Map<IElementType, Set<String>> myType2KeywordsTable = new HashMap<IElementType, Set<String>>();

    public RobotKeywordTable() {
        for (IElementType type : RobotTokenTypes.KEYWORDS.getTypes()) {
            myType2KeywordsTable.put(type, new HashSet<String>());
        }
    }

    public void put(IElementType type, String keyword) {
        if (RobotTokenTypes.KEYWORDS.contains(type)) {
            Set<String> keywords = getKeywords(type);
            if (keywords == null) {
                keywords = new HashSet<String>(1);
                myType2KeywordsTable.put(type, keywords);
            }
            keywords.add(keyword);
        }
    }

    public Set<String> getKeywordsOfType(RobotElementType type) {
        return myType2KeywordsTable.get(type);
    }

    @Nullable
    public Set<String> getKeywords(final IElementType type) {
        return myType2KeywordsTable.get(type);
    }
}