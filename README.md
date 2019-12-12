## IntelliJ/PyCharm Plugin for Robot Automation Framework

**INSTALLATION**  
Download the [intellibot.jar](/intellibot.jar) file in this github project.  
Then install this plugin to your IDE by using the 'Install plugin from disk...' option.

**NEW OPTIONS**  
This version of intellibot provide more options, see following picture:

![New Options](/wiki/robot_options/new_options.png)

### NEW FEATURE/ENHANCEMENT/FIX comparing to the original version

* Support FOR loop, both old and new syntax

* Variable
  * support variable in extended syntax, e.g. ${var * 10 + 3}
  * support scalar/dictionary/list variable resolve
  * will not take number like ${123} as "variable not defined"
  * improve the handling when type ${ in variable table
  * correct the priority of variable according to the robot framework user guide

* Library/Resource
  * can limit transitive import depth to improve performance
  * improve resolve library WITH NAME
  * support implemented as a directory
  * don't replace "." to "/" in library path if it contain "/"
  * don't resolve library as class if using physical path to library
  * regard library as directory if using physical path and not end with ".py",
  * follow the rule in robot framework user guide as much as possbile
  * strip variable in library path to be part of path name
    * e.g. "Resource %{PATH_1}/file1.robot" will search "Resource PATH_1/file1.robot"
    * it is useful if there're variables in import path but there is not running environment in local
    * provide an option to switch this behaviour on/off

* Correct the import behavior for Library, Resource and Variables files.
  * import both variable and keyword from Resource.
  * import only keyword from Library.
  * import only variable from Variables.

* User defined GLOBAL variables, like the one provided in command line "--variablefile"
  * using fake resource file named **_\_ProjectDefaultResource\_.robot_** under the current Pycharm project's root directory
  * it is processed as a Resource file, but only variables are imported, keywords are ignored
  * provide an option to switch on/off

* Support SeleniumLibrary keywords
  * No exception

* Autocompletion
  * keyword autocompletion support input prefixed with library and resource name
  * insert 4 spaces after keyword
  * if type 2 spaces, expand to 4 spaces according to surrounding space count

* resolve exception:
  * when type ${}
  * when keyword containing file is null
  * solve "Project Disposed" exception when close current project and open new project without quit application
  * type cast exception


### original readme
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
