MvnRunner
=========

a IntelliJ plugin for maven module, building, run unit testing and main method, by mvn.

插件做了什么？
---------
针对 maven 管理的 Java 项目，添加 maven 模块构建菜单；在 Run 和 Debug 两个菜单项上新增 maven 的处理方式，根据上下文直接调用 maven 的原生命令来进行单元测试或 main 方法的运行，你仍然可以使用原有的处理方式运行。

需求
----
IntelliJ IDEA 12 及更高版本

依赖插件(__粗体__为必选插件):

* __Maven Integration__
* UML Support
* Maven Integration Extension

安装
----
* 插件仓库安装：菜单中选择 Preferences/Plugins/Browser repositories, 查找 Maven Runner 并安装
* 磁盘安装下载地址：[JetBrains Plugin Repository](http://plugins.jetbrains.com/plugin/7409)


功能
----
1. 新增 Run 及 Debug 两个菜单项的处理方式，以原生的 mvn 命令来执行相同的操作。
* 支持单个方法、类、包及整个模块的单元测试。
* 支持 main 方法的运行。
* 支持 jetty 及 tomcat 运行，如果配置 jetty-maven-plugin 或 tomcat6/7-maven-plugin。
* 允许配置 fork 模式(配置均属 application level, Preferences/Maven/Importing)
* 为 maven 构建添加 Quick switch popup menu(shortcut: ^M)
* maven 构建菜单添加到 Build main menu 中(shortcut: ^⌘+ C/P/T/I)
* 通过 Quick switch popup menu 快速执行 plugin goals(shortcut: ^P)
* 支持快速查看 module 依赖(在 pom.xml 或 maven 项目列表上显示所有依赖，否则只显示模块依赖。 shortcut: ^⌘U)
* 通过 maven 坐标快速打开 POM 文件(shortcut: ^⌘M)

FAQ
----
1. 为什么要为这些菜单项增加 maven 的处理方式？
	* IntelliJ 默认的 Run/Debug 是解析本模块中 pom 依赖信息后，再生成 classpath 对应的列表，最终以 com.intellij 下面的 mainClass 来执行。
	* 默认处理方式无法正确的处理 maven 的级联依赖 jar 包。
	* 部分 maven 插件依赖运行期读取信息的情况 IntelliJ 默认执行方式无法正确处理。
	* 增加的 maven 处理方式是直接调用 mvn 命令中的 mainClass 来执行相同的操作。
* 如何配置让 Jetty 或 Tomcat 可以调试运行?
    * 在 pom 文件中配置 jetty-maven-plugin 或 tomcat6/7-maven-plugin。
    * 两者的启动配置完全依赖于 pom 文件中上述两个 maven 构建插件的配置。
* 添加 maven 构建菜单的目的？
	* 根据当前编辑或选择的上下文，自动选择当前 module 并构建。
	* Maven projects 上的构建操作达不到快捷的目标。
* 为什么需要这个插件？
	* 你不用再为了运行单元测试而去修改 pom.xml，让 pom 每天都保持清爽吧。
	* IntelliJ 平台为 maven 项目提供更完美的支持。

Screenshot
----

1. All Tests
	
	![All_tests_popup_menu](https://raw.github.com/ShlXue/MvnRunner/master/docs/images/all_tests_popup_menu.png)
* Test package

	![Test_package_popup_menu](https://raw.github.com/ShlXue/MvnRunner/master/docs/images/test_package_popup_menu.png)
* Folding java command

	![Folding java command](https://raw.github.com/ShlXue/MvnRunner/master/docs/images/folding_java_command.png)
* Run/Debug Configurations

	![Run_Configurations](https://raw.github.com/ShlXue/MvnRunner/master/docs/images/run_configurations.png)
* Run Jetty or Tomcat

	![Run_Jetty_Tomcat](https://raw.github.com/ShlXue/MvnRunner/master/docs/images/run_jetty_tomcat.png)
* Preferences

	![Preferences](https://raw.github.com/ShlXue/MvnRunner/master/docs/images/Preferences.png)
* Quick switch popup menu

    ![Quick switch popup menu](https://raw.github.com/ShlXue/MvnRunner/master/docs/images/quick_switch_popup_menu.png)
* Quick run plugin goals

    ![Quick run plugin goals](https://raw.github.com/ShlXue/MvnRunner/master/docs/images/quick_run_plugin_goals.png)
* Build menu

    ![Build menu](https://raw.github.com/ShlXue/MvnRunner/master/docs/images/maven_build_menu.png)
* Quick open pom

    ![Quick open pom](https://raw.github.com/ShlXue/MvnRunner/master/docs/images/quick_open_pom.png)


相关资源
----

1. [IntelliJ IDEA Build Number Ranges](http://confluence.jetbrains.com/display/IDEADEV/Build+Number+Ranges)
* [Previous IntelliJ IDEA Releases](http://confluence.jetbrains.com/display/IntelliJIDEA/Previous+IntelliJ+IDEA+Releases)
