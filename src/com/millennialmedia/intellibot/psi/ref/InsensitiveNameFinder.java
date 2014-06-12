package com.millennialmedia.intellibot.psi.ref;

import com.intellij.util.Processor;
import com.jetbrains.python.psi.PyElement;

/**
 * Same as NameFinder in PythonClassImpl except this is case-insensitive.
 *
 * @author mrubino
 * @since 2014-06-11
 */
public class InsensitiveNameFinder<T extends PyElement> implements Processor<T> {

    private T myResult;
    private final String[] myNames;

    public InsensitiveNameFinder(String... names) {
        this.myNames = names;
        this.myResult = null;
    }

    public T getResult() {
        return this.myResult;
    }

    public boolean process(T target) {
        final String targetName = target.getName();
        for (String name : this.myNames) {
            // this is the one change; equalsIgnoresCase vs equals
            if (name.equalsIgnoreCase(targetName)) {
                this.myResult = target;
                return false;
            }
        }
        return true;
    }
}
