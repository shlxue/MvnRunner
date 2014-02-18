MvnRunner
=========

a IntelliJ plugin for maven module, run unit testing or main method, by mvn.

插件做了什么？
---------
针对 maven 管理的 Java 项目，替换 IntelliJ 中 Run 和 Debug 两个菜单项的处理方式，直接调用 maven 的原生命令来进行单元测试与 main 方法的运行。

功能
----
1. 直接替换 Run 及 Debug 两个菜单项的处理方式。
2. 支持单个方法及单个类的单元测试。
3. 支持包的单元测试。
4. 支持 main 方法的运行。

FAQ
----
1. 为什么要替换这些菜单项的处理方式？
	* IntelliJ 默认的处理方式无法正确的处理 maven 的级联依赖 jar 包。
	* 部分 maven 插件依赖运行期读取信息无法正确处理。
2. 为什么需要这个插件？
	* 你不用再为了运行单元测试而去修改 pom.xml。
	* IntelliJ 平台为 maven 项目提供更完美的支持。