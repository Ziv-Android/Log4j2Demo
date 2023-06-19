import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static void initLog1() {
        Configurator.setLevel(LogManager.getRootLogger().getName(), Level.DEBUG);
    }

    private static void showLog() {
        logger.error("error 错误信息, 系统出现错误");
        logger.warn("warn 警告信息, 可能会发生问题, 不会影响系统运行");
        logger.info("info 运行信息, 数据连接、网络连接、I0操作等等");
        logger.debug("debug 调试信息, 一般在开发中使用, 记录程序变量参数传递信息等等");
        logger.trace("trace 追踪信息, 记录程序所有的流程信息");
    }
}
