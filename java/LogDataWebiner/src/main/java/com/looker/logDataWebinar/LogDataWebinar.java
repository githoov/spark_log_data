package com.looker.logDataWebinar;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function0;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.flume.FlumeUtils;
import org.apache.spark.streaming.flume.SparkFlumeEvent;

/**
 * @author kaidul
 *
 */
@SuppressWarnings({ "serial", "deprecation" })
public class LogDataWebinar {

    private static final Encoder<LogLine> logEncoder = Encoders.bean(LogLine.class);
    private static final Logger logger = Logger.getLogger(LogDataWebinar.class);
    
    private static Properties properties = new Properties();
    private static JavaSparkContext sparkContext;
    private static SQLContext sqlContext;

    private static final String CONFIG_FILE = "config.properties";
    private static final String APP_NAME_KEY = "spark.appname";
    private static final String CHECKPOINT_DIR = "spark.checkpoint.dir";
    private static final String CHECKPOINT_ENABLED = "spark.checkpoint.enabled";
    private static final String BATCH_INTERVAL_KEY = "spark.stream.batch_interval";
    private static final String COALESCE_PARTITION_KEY = "dataset.coalesce_partition";
    private static final String HDFS_PATH_KEY = "hdfs.path";
    private static final String WRITE_AHEAD_LOG_ENABLED = "spark.streaming.receiver.writeAheadLog.enable";
    private static final String TRUE = Boolean.toString(Boolean.TRUE);

    private static String appName;
    private static boolean isCheckpointEnabled;
    private static String checkpointDir;
    private static String isWalEnabled;
    private static int batchInterval;
    private static int partition;
    private static String hdfsPath;

    static {

        BasicConfigurator.configure();

        try {
            InputStream input;
            File file = new File(CONFIG_FILE);
            if (file.exists()) {
                input = new FileInputStream(file);
            } else {
                input = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_FILE);
            }
            properties.load(input);
            
            logger.info("config.properties file found: " + file.getPath());

            input.close();

        } catch (Exception ex) {
            logger.fatal("Exception reading config.properties file-->", ex);
        }

        appName = properties.getProperty(APP_NAME_KEY, "Log_Data_Webinar");
        isCheckpointEnabled = Boolean.valueOf( properties.getProperty(CHECKPOINT_ENABLED, TRUE) );
        checkpointDir = properties.getProperty(CHECKPOINT_DIR, "hdfs:///user/flume-spark/checkpoint");
        isWalEnabled = properties.getProperty(WRITE_AHEAD_LOG_ENABLED, TRUE);
        batchInterval = Integer.valueOf(properties.getProperty(BATCH_INTERVAL_KEY, "10"));
        partition = Integer.valueOf(properties.getProperty(COALESCE_PARTITION_KEY, "1"));
        hdfsPath = properties.getProperty(HDFS_PATH_KEY, "hdfs:///user/flume-spark/analyzed-log");

        logger.info("Application Name: " + appName);
        logger.info("HDFS Directory: " + hdfsPath);
    }
    
    private static JavaDStream<String> createDStream(JavaStreamingContext javaStreamingContext, String hostName, int port) {
        
        JavaReceiverInputDStream<SparkFlumeEvent> flumeEventStream = FlumeUtils.createStream(javaStreamingContext, hostName, port);
        
        // Set different storage level 
//        flumeEventStream.persist(StorageLevel.MEMORY_AND_DISK_SER());
        
        JavaDStream<String> dStream = flumeEventStream.map(new Function<SparkFlumeEvent, String>() {

            @Override
            public String call(SparkFlumeEvent sparkFlumeEvent) throws Exception {

                byte[] bodyArray = sparkFlumeEvent.event().getBody().array();
                String logTxt = new String(bodyArray, "UTF-8");
                logger.info(logTxt);

                return logTxt;
            }
        });
        // dStream.print();
        
        return dStream;
    }
    
    
    private static void writeToHdfs(JavaRDD<LogLine> logRdd) {
        
        Dataset<LogLine> logDataset = sqlContext.createDataset(logRdd.collect(), logEncoder);

        // logDataset.show();
        logDataset.coalesce(partition)
                  .write()
                  .mode(SaveMode.Append)
                  .parquet(hdfsPath);
    }
    
    
    private static void processDStream(JavaDStream<String> dStream) {
        
        dStream.foreachRDD(new VoidFunction<JavaRDD<String>>() {

            @Override
            public void call(JavaRDD<String> rdd) throws Exception {

                if (rdd.isEmpty()) {
                    return;
                }

                JavaRDD<LogLine> logRdd = rdd.map(new Function<String, LogLine>() {

                    @Override
                    public LogLine call(String logText) throws Exception {

                        LogLine logLine = LogParser.parseLog(logText);
                        logger.info(logLine);

                        return logLine;
                    }

                });

                writeToHdfs(logRdd);

            }
        });
    }
    

    public static void main(String[] args) {

        if (args.length != 2) {
            logger.error("Usage: " + appName + " <host> <port>");
            System.exit(1);
        }

        final String hostName = args[0];
        final int port = Integer.parseInt(args[1]);
        
        SparkConf sparkConf = new SparkConf().setAppName(appName);
        
        if(isCheckpointEnabled) {
            sparkConf.set(WRITE_AHEAD_LOG_ENABLED, TRUE);
        } else {
            sparkConf.set(WRITE_AHEAD_LOG_ENABLED, isWalEnabled);
        }
        
        sparkContext = new JavaSparkContext(sparkConf);
        sqlContext = new SQLContext(sparkContext);
        
        JavaStreamingContext javaStreamingContext = null;
        
        if(isCheckpointEnabled) {
            
            javaStreamingContext = JavaStreamingContext.getOrCreate(checkpointDir, new Function0<JavaStreamingContext>() {

                @Override
                public JavaStreamingContext call() throws Exception {

                    JavaStreamingContext jStreamingCtx = new JavaStreamingContext(sparkContext, Durations.seconds(batchInterval));

                    jStreamingCtx.checkpoint(checkpointDir);

                    JavaDStream<String> dStream = createDStream(jStreamingCtx, hostName, port);

                    processDStream(dStream);

                    return jStreamingCtx;
                }
            });
        }
        else {
            javaStreamingContext = new JavaStreamingContext(sparkContext, Durations.seconds(batchInterval));

            JavaDStream<String> dStream = createDStream(javaStreamingContext, hostName, port);

            processDStream(dStream);
        }
        

        
        javaStreamingContext.start();

        try {
            javaStreamingContext.awaitTermination();
            
        } catch (InterruptedException ex) {
            logger.warn(ex);
        } finally {
//             System.exit(0);
        }
    }
}