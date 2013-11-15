package com.millennialmedia.intellibot.psi;

import java.util.*;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotKeywordTable {
    private Map<IElementType, Set<String>> myType2KeywordsTable = new HashMap<IElementType, Set<String>>();

    public RobotKeywordTable() {
        for (IElementType type : RobotTokenTypes.KEYWORDS.getTypes()) {
            myType2KeywordsTable.put(type, new HashSet<String>());
        }
    }

    public void putAllKeywordsInto(Map<String, IElementType> target) {
        for (IElementType type : this.getTypes()) {
            final Set<String> keywords = this.getKeywords(type);
            if (keywords != null) {
                for (String keyword : keywords) {
                    target.put(keyword, type);
                }
            }
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

    public Set<String> getKeywordsOfTypes(RobotElementType... types) {
        HashSet<String> keywords = new HashSet<String>();
        for (RobotElementType type : types) {
            for (String word : myType2KeywordsTable.get(type)) {
                keywords.add(word);
            }
        }
        return keywords;
    }

    @NotNull
    public static RobotKeywordTable getKeywordsTable(PsiFile originalFile, Project project) {
        final RobotKeywordProvider provider = new RobotKeywordProvider();// todo SMA need a service here?

        // find language comment
        final String language = "dontcare"; //todo SMA right? getFeatureLanguage(originalFile);
        return provider.getKeywordsTable(language);
    }

//    @NotNull
//    public static String getFeatureLanguage(PsiFile originalFile) {
//        return originalFile instanceof RobotFile
//                ? ((RobotFile) originalFile).getLocaleLanguage()
//                : RobotFileImpl.getDefaultLocale();
//    }

    public Set<IElementType> getTypes() {
        return myType2KeywordsTable.keySet();
    }

    @Nullable
    public Set<String> getKeywords(final IElementType type) {
        return myType2KeywordsTable.get(type);
    }

    public boolean tableContainsKeyword(RobotElementType type, String keyword) {
        Set<String> alreadyKnownKeywords = getKeywords(type);
        return null != alreadyKnownKeywords && alreadyKnownKeywords.contains(keyword);
    }
}