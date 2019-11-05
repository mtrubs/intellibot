## IntelliJ/PyCharm Plugin for Robot Automation Framework
### Changes in IntelliBot@master.dev-0.10.143.382:
* merge patch for SeleniumLibrary/keywords 2e2947e from youwi
  * with fix
  * provide an option to switch on/off
* change 2 spaces to 4 spaces
  * insert 4 spaces after keyword
  * if type 2 spaces, expand to 4 spaces according to surrounding space number
* strip variable in library path to be part of path name
  * "Resource %{PATH_1}/file1.robot" will search "Resource PATH_1/file1.robot"
  * it is useful if there're variables in import path but there is not running environment in local
  * provide an option to switch this behaviour on/off
* will not take number like ${123} as "variable not defined"
* solve "Project Disposed" exception when close current project and open new project without quit application
* correct the behavior for Library, Resource and Variables files.
  * import both variable and keyword from Resource.
  * import only keyword from Library.
  * import only variable from Variables.
* correct the priority of variable
* will search fake resource file "\_ProjectDefaultResource\_.robot" under project root directory
  * it is useful if the variable is defined in command line "--variablefile"
  * it is processed as a Resource file
  * provide an option to switch on/off
  * the variable in it is available globally to all testcase
  * only variable in it is imported currently, keywords are ignored
* change the option TransitiveImport from on/off to maxTransitiveDepth
  * maxTransitiveDepth=1 is same as old TransitiveImport=off
  * select your balance between performance and convenience
* some optimization

### original
This is a work in progress (the product of a 24 hour hack match), though at this point I have devoted far more time than that.
Related feature request to JetBrains: [IDEA-97678](http://youtrack.jetbrains.com/issue/IDEA-97678).
**Here is a growing list of [features](https://github.com/millennialmedia/intellibot/wiki/Features).**

![Sample](/wiki/features/demo_complete.png)

### Installation & Usage

Note that this plugin should work in either IntelliJ or PyCharm, but that PyCharm is far less used (and thus tested) personally.

**The plugin is now hosted in the JetBrains [repositories](http://plugins.jetbrains.com/plugin/7386?pr=github).**
This means you can install it directly form your IDE.
Just search for 'Intellibot' under 'Browse Repositories...'.

You can also install the plugin manually.
To do so you can either download and [compile](https://github.com/millennialmedia/intellibot/wiki/Development-Setup) the project yourself.
Or download the [intellibot.jar](https://github.com/lte2000/intellibot/blob/develop/intellibot.jar) file in the project.
You can then install this plugin to your IDE by using the 'Install plugin from disk...' option.
This version may be slightly ahead of the JetBrains repository though potentially slightly less stable.

The plugin will, by default, operate against any ".robot" file in the project.
**You can add ".txt" support by following these [instructions](https://github.com/millennialmedia/intellibot/wiki/Supporting-.txt-Files).**
If you are using PyCharm then any Python libraries should be detected when you setup your interpreter.
If you are using IntelliJ then you can install the Python plugin.
Both instructions can be found in this [wiki page](https://github.com/millennialmedia/intellibot/wiki/Python-Interpreter).
