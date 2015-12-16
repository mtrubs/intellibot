## IntelliJ/PyCharm Plugin for Robot Automation Framework

This is a work in progress (the product of a 24 hour hack match).
Related feature request to JetBrains: [IDEA-97678](http://youtrack.jetbrains.com/issue/IDEA-97678).
For Robot Framework files ending with ".robot", this currently supports:

1. Syntax highlighting

   ![Syntax Highlighting](/readme/demo_complete.png)
2. Code folding
3. Auto-completion (Same File, Robot Resource files, Python Library files, Keywords, Variables)

   ![Auto-complete](/readme/keyword_recommendation.png)
4. Click into imported Resources, Libraries and their defined keywords

   ![Click into imported files](/readme/jump_to_source.png)
5. Undefined keywords and code inspections

   ![Undefined Keywords](/readme/undefined_keyword.png)
5. File Structure

   ![File Structure](/readme/structure.png)

### Installation & Usage

Note that this plugin should work in either IntelliJ or PyCharm, but that PyCharm is far less used (and thus tested) personally.

The plugin is now hosted in the JetBrains [repositories](http://plugins.jetbrains.com/plugin/7386?pr=github).
This means you can install it directly form your IDE.
Just search for 'Intellibot' under 'Browse Repositories...'.

You can also install the plugin manually.
To do so you can either download and compile the project yourself, or download the intellibot.jar file in the project.
You can then install this plugin to your IDE by using the 'Install plugin from disk...' option.
This version may be slightly ahead of the JetBrains repository though potentially slightly less stable.

The plugin will, by default, operate against any *.robot file in the project.
If you are using PyCharm then any Python libraries should be detected.
If you are using IntelliJ then you can install the Python plugin; see the [wiki page](https://github.com/millennialmedia/intellibot/wiki/Python-Interpreter).