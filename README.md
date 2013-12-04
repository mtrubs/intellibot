IntelliJ Plugin for Robot Automation Framework

This is a work in progress (the product of a 24 hour hack match).
Related Feature request to JetBrains: http://youtrack.jetbrains.com/issue/IDEA-97678.
For Robot Framework files ending with ".robot", this currently supports:

1. Syntax highlighting
2. Code folding
3. Auto-completion of some general Robot keywords, keywords defined in the same file or imported files

![Syntax highlighting, code folding, local keyword completion](/examples/local_keyword_auto_complete.png)

4. Click into imported files

![Click into imported files](/examples/go_to_file.png)

Features still needed:

1. "Find Usages" for keywords in local file and external files
2. Integration with Robot Framework Python based libraries
3. Finer control of keywords auto-completion (where in a file the completions should appear)
4. Auto-completion for keywords available from imports and the core Robot Framework keyword libraries
