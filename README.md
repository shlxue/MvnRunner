MvnRunner
=========

a IntelliJ plugin for maven module, run unit testing and main method, by mvn.

插件做了什么？
---------
针对 maven 管理的 Java 项目，在 Run 和 Debug 两个菜单项上新增 maven 的处理方式，根据上下文直接调用 maven 的原生命令来进行单元测试或 main 方法的运行，你仍然可以使用原有的处理方式运行。

需求
----
IntelliJ 13 及更高版本

安装
----
1. 下载编译好的压缩包: [MvnRunner.tar.gz](https://github.com/ShlXue/MvnRunner/releases/download/v0.1.rc1/MvnRunner_v0.1.3.tar.gz)
2. 解压后得到插件 jar 包，在 IDE 中选择: Plugins --> Install plugin from disk...

功能
----
1. 新增 Run 及 Debug 两个菜单项的处理方式，以原生的 mvn 命令来执行相同的操作。
2. 支持单个方法及单个类的单元测试。
3. 支持包的单元测试。
4. 支持 main 方法的运行。
5. maven 启动方式是否优先可配置(Preferences/Maven/Importing)
6. 允许配置 fork 模式(配置均属 application level)
7. 为 maven 构建添加 Quick switch popup menu(shortcut: ^M)
8. maven 构建菜单添加到 Build main menu 中

FAQ
----
1. 为什么要为这些菜单项增加 maven 的处理方式？
	* IntelliJ 默认的处理方式无法正确的处理 maven 的级联依赖 jar 包。
	* 部分 maven 插件依赖运行期读取信息的情况 IntelliJ 默认执行方式无法正确处理。
2. 为什么需要这个插件？
	* 你不用再为了运行单元测试而去修改 pom.xml，让 pom 每天都保持清爽吧。
	* IntelliJ 平台为 maven 项目提供更完美的支持。
3. 类似插件: [MavenRunHelper](https://github.com/krasa/MavenRunHelper)
    * 该插件新增了 Maven 常用操作的菜单项。

Screen Snapshot
----

1. All Tests
	
	![All_tests_popup_menu](https://raw.github.com/ShlXue/MvnRunner/master/docs/images/all_tests_popup_menu.png)
2. Test package

	![Test_package_popup_menu](https://raw.github.com/ShlXue/MvnRunner/master/docs/images/test_package_popup_menu.png)
3. Run/Debug Configurations

	![Run_Configurations](https://raw.github.com/ShlXue/MvnRunner/master/docs/images/run_configurations.png)
4. Preferences

	![Preferences](https://raw.github.com/ShlXue/MvnRunner/master/docs/images/Preferences.png)
5. Quick switch popup menu

    ![Quick switch popup menu](https://raw.github.com/ShlXue/MvnRunner/master/docs/images/quick_switch_popup_menu.png)
6. Build menu

    ![Build menu](https://raw.github.com/ShlXue/MvnRunner/master/docs/images/maven_build_menu.png)