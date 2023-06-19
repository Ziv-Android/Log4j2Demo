# Log4j2 - (2.20.0)版Demo
## 基础原则
1. 应用中不可直接使用日志系统(Log4j、Logback)中的API，而应依赖使用日志框架（SLF4J、JCL–Jakarta Commons Logging）中的API，使用门面模式的日志框架，有利于维护和各个类的日志处理方式统一。日志框架推荐使用SLF4J
2. 所有日志文件至少保存15天，因为有些异常具备以“周”为频次发生的特点。对于当天日志，以“应用名.log”来保存，保存在`/home/用户名/software/应用名/logs/`目录下，过往日志格式为:`{logname}.log.{保存日期}.gz`，日期格式：yyyy-MM-dd。
3. 在日志输出时，字符串变量之间的拼接使用占位符的方式。
4. 对于trace/debug级别的日志输出，必须进行日志级别的开关判断。
5. **禁止直接使用System.out或System.err输出日志或使用e.printStackTrace()打印异常堆栈**。如果大量输出送往标准日志输出与标准错误输出文件这两个文件，容易造成文件大小超过操作系统大小限制。
6. 异常信息应该包括两类信息：案发现场信息和异常堆栈信息。如果不处理，那么通过关键字`throws`往上抛出。
7. 除非是海外项目或者国外服务器，需要全部英文。国内使用中文进行输出和注释，避免词不达意或解释歧义的情况。

## 引入库
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project>
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-api</artifactId>
        <version>2.20.0</version>
    </dependency>
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-core</artifactId>
        <version>2.20.0</version>
    </dependency>
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-slf4j-impl</artifactId>
        <version>2.20.0</version>
    </dependency>
</project>
```

```groovy
dependencies {
  compile group: 'org.apache.logging.log4j', name: 'log4j-api', version: '2.20.0'
  compile group: 'org.apache.logging.log4j', name: 'log4j-core', version: '2.20.0'
  compile group: 'org.apache.logging.log4j', name: 'log4j-slf4j-impl', version: '2.20.0'
}
```

## 代码动态配置
```java
public class TestLog4j {
    public static final String LOGGER_CLASS = TestLog4j.class.getName();
    private static final Logger logger = LoggerFactory.getLogger(LOGGER_CLASS);

    public static void main(String[] args) {
        initLog();
        showLog();
    }
    
    private static void initLog() {
        // 设置日志级别
        Configurator.setLevel(LogManager.getRootLogger().getName(), Level.DEBUG);

        // 创建LoggerContext实例
        LoggerContext context = (LoggerContext) LogManager.getContext(false);

        // 创建ConfigurationBuilder实例
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();

        // 创建LayoutComponentBuilder实例，设置日志输出格式
        LayoutComponentBuilder layout = builder.newLayout("PatternLayout")
                .addAttribute("pattern", "%d [%t] %-5level %logger{36} - %msg%n");

        // 创建ConsoleAppender
        AppenderComponentBuilder consoleAppender = builder.newAppender("ConsoleAppender", "CONSOLE")
                .addAttribute("target", "SYSTEM_OUT")
                .add(layout);

        // 创建FileAppender
        AppenderComponentBuilder fileAppender = builder.newAppender("FileAppender", "FILE")
                .addAttribute("fileName", "myapp.log")
                .add(layout);

        // 将Appender添加到RootLogger
        RootLoggerComponentBuilder rootLogger = builder.newRootLogger("INFO")
                .add(builder.newAppenderRef("ConsoleAppender"))
                .add(builder.newAppenderRef("FileAppender"));

        // 添加Appender和RootLogger到Configuration
        builder.add(consoleAppender);
        builder.add(fileAppender);
        builder.add(rootLogger);

        // 构建Configuration并重新配置LoggerContext
        context.start(builder.build());
    }

    private static void showLog() {
        logger.error("error 错误信息, 系统出现错误");
        logger.warn("warn 警告信息, 可能会发生问题, 不会影响系统运行");
        logger.info("info 运行信息, 数据连接、网络连接、I0操作等等");
        logger.debug("debug 调试信息, 一般在开发中使用, 记录程序变量参数传递信息等等");
        logger.trace("trace 追踪信息, 记录程序所有的流程信息");
    }
}
```

## 配置文件配置
```properties
# 控制台输出配置
log4j.appender.Console=org.apache.log4j.ConsoleAppender
log4j.appender.Console.layout=org.apache.log4j.PatternLayout
# 如果使用PatternLayout布局就要指定的打印信息的具体格式ConversionPattern
log4j.appender.Console.layout.ConversionPattern=%d [%t] %p [%c] - %m%n
#指定日志的输出级别与输出端
log4j.rootLogger=DEBUG,Console
        
#%p: 输出日志信息优先级，即DEBUG，INFO，WARN，ERROR，FATAL, 
#%d: 输出日志时间点的日期或时间，默认格式为ISO8601，也可以在其后指定格式，比如：%d{yyyy-MM-dd HH:mm:ss,SSS}，输出类似：2011-10-18 22:10:28,921 
#%r: 输出自应用启动到输出该log信息耗费的毫秒数 
#%c: 输出日志信息所属的类目，通常就是所在类的全名 
#%t: 输出产生该日志事件的线程名 
#%l: 输出日志事件的发生位置，相当于%C.%M(%F:%L)的组合,包括类目名、发生的线程，以及在代码中的行数。 
#%x: 输出和当前线程相关联的NDC(嵌套诊断环境),尤其用到像java servlets这样的多客户多线程的应用中。 
#%%: 输出一个"%"字符 
#%F: 输出日志消息产生时所在的文件名称 
#%L: 输出代码中的行号 
#%m: 输出代码中指定的消息,产生的日志具体信息 
#%n: 输出一个回车换行符，Windows平台为"\r\n"，Unix平台为"\n"输出日志信息换行
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN" monitorInterval="30">
    <Properties>
        <Property name="LOG_PATTERN">%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1} - %m%n</Property>
    </Properties>
 
    <Appenders>
        <Console name="console" target="SYSTEM_OUT" follow="true">
            <PatternLayout pattern="${LOG_PATTERN}"/>
        </Console>
    </Appenders>
 
    <Loggers>
        <Root level="info">
            <AppenderRef ref="console"/>
        </Root>
    </Loggers>
</Configuration>
```

## 参考资料
https://logging.apache.org/log4j/2.x/
https://www.slf4j.org/docs.html
https://zhuanlan.zhihu.com/p/39869648
https://www.cnblogs.com/qlqwjy/p/9275415.html