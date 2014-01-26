IntelliJ/PyCharm Plugin for Robot Automation Framework

This is a work in progress (the product of a 24 hour hack match).
Related Feature request to JetBrains: http://youtrack.jetbrains.com/issue/IDEA-97678.
For Robot Framework files ending with ".robot", this currently supports:

1. Syntax highlighting
2. Code folding
3. Auto-completion (Same File, Robot Resource files, Python Library files)

![Syntax highlighting, code folding, local keyword completion](/examples/local_keyword_auto_complete.png)

4. Click into imported Resources, Libraries and their defined keywords

![Click into imported files](/examples/go_to_file.png)

Note that for python library support we recently added a dependency on the Python plugin.
The plugin will work without it but you will not get python support.
In Pycharm, python files are not yet recognized.  Though the plugin should work fine otherwise.

Features still needed:

1. "Find Usages" for keywords in local file and external files
   a. From there we could do refactors
2. Finer control of keywords auto-completion (where in a file the completions should appear)
3. Better detection/use of arguments (particularly inline keyword arguments)
4. Better detection/use of variables