package utils;
import java.util.Date;
import java.net.InetAddress;
import java.net.UnknownHostException;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import common.GlobalConfigHandler;

public class ExtentManager {

    private static ExtentReports extent;

    public static ExtentReports createExtentReportInstance() {
        String path = System.getProperty("user.dir") + "/target/surefire-reports/report.html";
        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(path);
        htmlReporter.config().setAutoCreateRelativePathMedia(false);
        htmlReporter.config().setEncoding("utf-8");
        htmlReporter.config().setDocumentTitle("GGL");
        htmlReporter.config().setReportName("GGL Api Test Suite Report");
        htmlReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");
        htmlReporter.config().setTheme(Theme.STANDARD);
        htmlReporter.config().setCSS(".step-details > img { border: 2px solid #ccc; display: block; margin-top: 5px;height: 30px;width: 50px;}");
        extent = new ExtentReports();
        try {
            extent.setSystemInfo("Organization Name", "GGL");
            extent.setSystemInfo("QA Name", "Umesh Shukla");
            extent.setSystemInfo("Os Name", System.getProperty("os.name"));
            extent.setSystemInfo("User Name", System.getProperty("user.name"));
            extent.setSystemInfo("Host Name", InetAddress.getLocalHost().getHostName());
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("Execution Context", GlobalConfigHandler.getEnv());
            extent.setReportUsesManualConfiguration(true);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        extent.attachReporter(htmlReporter);
        return extent;
    }

    public static String getReportName() {
        Date date = new Date();
        String fileName = "GGL_Report" + "_" + date.toString().replace(":", "_").replace(" ", "_");
        return fileName;
    }
}