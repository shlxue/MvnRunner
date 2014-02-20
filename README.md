MvnRunner
=========

a IntelliJ plugin for maven module, run unit testing or main method, by mvn.

插件做了什么？
---------
针对 maven 管理的 Java 项目，替换 IntelliJ 中 Run 和 Debug 两个菜单项的处理方式，直接调用 maven 的原生命令来进行单元测试与 main 方法的运行。

需求
----
IntelliJ 13 及更高版本

安装
----
1. 下载编译好的压缩包: [MvnRunner.tar.gz](https://github.com/ShlXue/MvnRunner/releases/download/v0.1/MvnRunner_v0.1.1.tar.gz)
2. 解压后得到插件 jar 包，在 IDE 中选择: Plugins --> Install plugin from disk...

功能
----
1. 直接替换 Run 及 Debug 两个菜单项的处理方式，以原生的 mvn 命令来执行相同的操作。
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
3. 类似插件: [MavenRunHelper](https://github.com/krasa/MavenRunHelper)
    * 该插件新增了 Maven 常用操作的菜单项。