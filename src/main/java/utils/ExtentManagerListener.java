package utils;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;

public class ExtentManagerListener implements ITestListener {
    
    ExtentTest parent;
    private static ExtentReports extent = ExtentManager.createExtentReportInstance();
    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<ExtentTest>(); // for multiple classes handelling.
    
    @Override
    public void onTestStart(ITestResult result) {
        parent = extent.createTest(result.getTestClass().getName()+ " :: "+result.getMethod().getMethodName());
        ExtentTest child = parent.createNode(result.getMethod().getMethodName());
        parent.getModel().setStartTime(getTime(result.getStartMillis()));
        extentTest.set(parent);
        extentTest.set(child);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        extentTest.get().pass("Test Passed");
        String logText = "<b>Test Method "+result.getMethod().getMethodName()+" Successful.</b>";
        Markup markup = MarkupHelper.createLabel(logText, ExtentColor.GREEN);
        extentTest.get().log(Status.PASS, markup);
        parent.getModel().setEndTime(getTime(result.getEndMillis()));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String exception_msg = Arrays.toString(result.getThrowable().getStackTrace());
        StringBuilder failMsgFormat = new StringBuilder();
        failMsgFormat.append("<details><summary><b><font color=red>"+"Exception Occured, click to see details : "+ "</font></b></summary>");
        failMsgFormat.append(exception_msg.replaceAll(",", "<br>")+"</details> \n");
        extentTest.get().fail(failMsgFormat.toString());
        String logText = "<b>Test Method "+methodName+" Failed.</b>";
        Markup markup = MarkupHelper.createLabel(logText, ExtentColor.RED);
        extentTest.get().log(Status.FAIL, markup);
        parent.getModel().setEndTime(getTime(result.getEndMillis()));
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        extentTest.get().pass("Test Skipped");
        String logText = "<b>Test Method "+result.getMethod().getMethodName()+" Skipped.</b>";
        Markup markup = MarkupHelper.createLabel(logText, ExtentColor.GREY);
        extentTest.get().log(Status.SKIP, markup);
        parent.getModel().setEndTime(getTime(result.getEndMillis()));
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
       
    }

    @Override
    public void onStart(ITestContext context) {
    
    }

    @Override
    public void onFinish(ITestContext context) {
        if(extent != null){
            extent.flush();
        }
    }

    public Date getTime(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.getTime();      
    }
}