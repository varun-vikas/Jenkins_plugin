/**
 * 
 */
package logging;

/**
 * @author archatte
 *
 */


import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.OutputStreamAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.filter.MarkerFilter;
import org.apache.logging.log4j.core.layout.JsonLayout;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.OutputStream;


public class LogUtils {

	/**
	 * 
	 */
	static {
		System.setProperty("Log4jContextSelector",
				"org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
	}
	public static final Marker analytics = MarkerFactory.getMarker("ANALYTICS");


	private static Logger logger = LogUtils.getLogger(LogUtils.class);
	private static LoggerContext context = (LoggerContext) LogManager.getContext(false);
	private static String layoutPattern = "[%d{yy-MMM-dd HH:mm:ss:SSS}] [%p] [%c{1}:%L] - %m%n";
	
	private LogUtils() {
		// TODO Auto-generated constructor stub
	}

	/**

	 */
	
	public static Logger getLogger(Class loggingClass)
	{
		return LoggerFactory.getLogger(loggingClass);
	}
	
	public static Configuration getLogContextConfig()
	{
		return context.getConfiguration();
	}
	
	public static LoggerConfig getLoggerConfig(String loggerName)
	{
		Configuration config = getLogContextConfig();
		return config.getLoggerConfig(loggerName);
	}
	
	public static LoggerConfig getRootLoggerConfig()
	{
		Configuration config = getLogContextConfig();
		
		return config.getRootLogger();
		
	}
	
	public static void setRootLogLevel(Level level)
	{
		LoggerConfig rootLogConfig = getRootLoggerConfig();
		rootLogConfig.setLevel(level);
		updateLogs();
	}
	
	public static void setLogLevel(String loggerName, Level level)
	{
		LoggerConfig logConfig = getLoggerConfig(loggerName);
		logConfig.setLevel(level);
		updateLogs();
	}
	
	public static void addHandler(OutputStream oStream)
	{
		LoggerConfig rootLoggerConfig = getRootLoggerConfig();
		//SimpleFormatter formatter = new SimpleFormatter();
        //StreamHandler sh = new StreamHandler(oStream,formatter);
        PatternLayout p = PatternLayout.newBuilder().withPattern(layoutPattern).build();
        OutputStreamAppender o = OutputStreamAppender.newBuilder().setName("customAppender").setTarget(oStream).setLayout(p).build();
        o.start();
        rootLoggerConfig.addAppender(o, Level.INFO, null);
        updateLogs();
	}

	public static void addFileAppender(String filename)  {
		String extension = FilenameUtils.getExtension(filename);
		RollingFileAppender rf = null;
		LoggerConfig rootLoggerConfig = getRootLoggerConfig();
		SizeBasedTriggeringPolicy policy = SizeBasedTriggeringPolicy.createPolicy("5MB");
		DefaultRolloverStrategy strategy = DefaultRolloverStrategy.newBuilder().withMax("20").withMin("1").withConfig(getLogContextConfig()).build();
		if(extension.contains("json")) {
			JsonLayout j = JsonLayout.createDefaultLayout();
			rf = RollingFileAppender.newBuilder().withFileName(filename + "." + extension).withFilePattern(filename + "%i." + extension).withPolicy(policy).withAppend(true).withStrategy(strategy).withLayout(j).withName(filename).build();

		}
		else
		{
			PatternLayout p = PatternLayout.newBuilder().withPattern(layoutPattern).build();
			MarkerFilter mf = MarkerFilter.createFilter("ANALYTICS", Filter.Result.DENY, Filter.Result.ACCEPT);
			rf = RollingFileAppender.newBuilder().withFileName(filename + "." + extension).withFilePattern(filename + "%i." + extension).withPolicy(policy).withAppend(true).withStrategy(strategy).withLayout(p).withName(filename).withFilter(mf).build();

		}

		rf.start();
		rootLoggerConfig.addAppender(rf, Level.INFO, null);
		updateLogs();
	}

	public static void updateLogs()
	{
		context.updateLoggers();
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LogUtils.getLogger(LogUtils.class).info("sample message");
	}

}
