# IntelliJ/PyCharm Plugin for Robot Automation Framework

This is a work in progress (the product of a 24 hour hack match).
Related feature request to JetBrains: http://youtrack.jetbrains.com/issue/IDEA-97678.
For Robot Framework files ending with ".robot", this currently supports:

1. Syntax highlighting
2. Code folding
3. Auto-completion (Same File, Robot Resource files, Python Library files)

![Syntax highlighting, code folding, local keyword completion](/examples/local_keyword_auto_complete.png)

4. Click into imported Resources, Libraries and their defined keywords

![Click into imported files](/examples/go_to_file.png)

## Installation & Usage

Note that this plugin should work in either IntelliJ or PyCharm, but that PyCharm is far less used (and thus tested) personally.

The plugin is now hosted in the JetBrains repositories.
This means you can install it directly form your IDE.
Just search for 'Intellibot' under 'Browse Repositories...'.
http://plugins.jetbrains.com/plugin/7386?pr=github

You can also install the plugin manually.
To do so you can either download and compile the project your self, or download the intellibot.jar file in the project.
You can then install this plugin to your IDE but using the 'Install plugin from disk...' option.

The plugin will operate against any *.robot file in the project.
If you are using PyCharm then any Python libraries should be detected.
If you are using IntelliJ then you can to install the 'Python' plugin ('Install JetBrains Plugin...') to get python support.

## Features still needed:

1. "Find Usages" for keywords in local file and external files
   a. From there we could do re-factors
2. Finer control of keywords auto-completion (where in a file the completions should appear)
3. Better detection/use of arguments (particularly inline keyword arguments)
4. Better detection/use of variables