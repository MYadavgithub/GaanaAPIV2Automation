package utils;
import common.FileActions;
import config.Constants;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.collections.Lists;
import org.testng.internal.Utils;
import org.testng.xml.XmlSuite;

public class EmailableReportListener implements IReporter {
    private String report_folder = null;
    private String report_titile = "GGL NOIDA";
    private String report_name = "Search And Recommendation Test Report";
	private String file_name = "EmailerReport.html";
    protected PrintWriter writer;
    protected final List<SuiteResult> suiteResults = Lists.newArrayList();
    private static Logger log = LoggerFactory.getLogger(EmailableReportListener.class);
    private final StringBuilder buffer = new StringBuilder();   // Reusable buffer

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outDir) {
        outDir = System.getProperty("user.dir");
        report_folder = outDir+Constants.CUSTOM_REPORT_FOLDER;
        FileActions.checkFolderExists(outDir, Constants.CUSTOM_REPORT_FOLDER);
        writer = createWriter(report_folder);
        for (ISuite suite : suites) {
            suiteResults.add(new SuiteResult(suite));
        }
        writeDocumentStart();
        writeHead();
        writeBody();
        writeDocumentEnd();
        writer.close();
    }

    protected PrintWriter createWriter(String outdir) {
		try {
            new File(outdir).mkdirs();
            return new PrintWriter(new BufferedWriter(new FileWriter(new File(outdir, file_name))));
        } catch (IOException e) {
            log.error("Unable to create output file for "+report_name+".");
            e.printStackTrace();
        }
        return null;
    }
    
    protected void writeReportTitle(String title) {
        writer.println("<center><h1>"+ title +"</h1></center>");
	}

    protected void writeDocumentStart() {
        writer.println("<span style=\"font-size:5px;\"> </span>");
        writer.println("<span style=\"font-size:5px;\"> </span>");
        writer.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
    }

    protected void writeHead() {
        writer.println("<head>");
        writer.println("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"/>");
        writer.println("<title>"+report_titile+"</title>");
        writeStylesheet();
        writer.println("</head>");
    }
    

    protected void writeStylesheet() {
    	writer.println("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css\">");
        writer.print("<style type=\"text/css\">");
        writer.print("table {margin-bottom:10px;border-collapse:collapse;empty-cells:show}");
        writer.print("#summary {margin-top:30px; margin-left: 90px; margin-right: 90px;}");
        writer.print("#m {margin-left: 90px; font-size: 16px; font-style: oblique;}");
        writer.print("h1 {font-size:30px}");
        writer.print("body {width:100%;}");
        writer.print("th,td {padding: 8px}");
        writer.print("th {vertical-align:bottom}");
        writer.print("td {vertical-align:top}");
        writer.print("table a {font-weight:bold;color:#0D1EB6;}");
        writer.print(".easy-overview {margin-left: auto; margin-right: auto;} ");
        writer.print(".easy-test-overview tr:first-child {background: #3092c0; color: white; font-family: serif;}");
        writer.print(".stripe td {background-color: #E6EBF9}");
        writer.print(".num {text-align:right}");
        writer.print(".passedodd td {background-color: #3F3}");
        writer.print(".passedeven td {background-color: #0A0}");
        writer.print(".skippedodd td {background-color: #DDD}");
        writer.print(".skippedeven td {background-color: #CCC}");
        writer.print(".failedodd td,.attn {background-color: #F33}");
        writer.print(".failedeven td,.stripe .attn {background-color: #D00}");
        writer.print(".stacktrace {font-family:monospace}");
        writer.print(".totop {font-size:85%;text-align:center;border-bottom:2px solid #000}");
        writer.print(".invisible {display:none}");
        writer.print(".tbl2 {margin-left: auto; margin-right: auto; background-color: antiquewhite;}");
        writer.print(".result {margin-left: 90px; margin-right: 1500px;}");
        writer.println("</style>");
    }

    protected void writeBody() {
        writer.println("<body>");
        writeReportTitle(report_name);
        writeSuiteSummary();
        writeScenarioSummary();
        writeScenarioDetails();
        writer.println("</body>");
    }

    protected void writeDocumentEnd() {
        writer.println("</html>");
    }

    protected void writeSuiteSummary() {
        NumberFormat integerFormat = NumberFormat.getIntegerInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");

        int totalTestsCount = 0;
        int totalPassedTests = 0;
        int totalSkippedTests = 0;
        int totalFailedTests = 0;
        long totalDuration = 0;

        writer.println("<div class=\"easy-test-overview\">");
        writer.println("<table class=\"table-bordered easy-overview\">");
        writer.print("<th>Test Name</th>");
        writer.print("<th>Test Count</th>");
        writer.print("<th>Passed</th>");
        writer.print("<th>Skipped</th>");
        writer.print("<th>Failed</th>");
        writer.print("<th>Start Time</th>");
        writer.print("<th>End Time</th>");
        writer.print("<th>Total Time<br /><span style= font-size:10px;>(hh:mm:ss)</span></th>");
        writer.print("<th>Included Groups</th>");
        writer.print("<th>Excluded Groups</th>");
        writer.println("</tr>");

        int testIndex = 0;
        for(SuiteResult suiteResult : suiteResults) {
            writer.print("<tr><th colspan=\"11\">");
            writer.println("<center><h6><b><I>"+ Utils.escapeHtml(suiteResult.getSuiteName()) +"<I><b></h6></center>");
            writer.println("</th></tr>");

            for (TestResult testResult : suiteResult.getTestResults()) {
            	int testsCount = testResult.getTestCount();
                int passedTests = testResult.getPassedTestCount();
                int skippedTests = testResult.getSkippedTestCount();
                int failedTests = testResult.getFailedTestCount();
               
                Date startTime = testResult.getTestStartTime();
                Date endTime = testResult.getTestEndTime();
                long duration = testResult.getDuration();

                writer.print("<tr");
                if ((testIndex % 2) == 1) {
                    writer.print("class=\"stripe\"");
                }
                writer.print(">");

                buffer.setLength(0);
                writeTableData(buffer.append("<a href=\"#t\").append(testIndex).append(\"\">").append(Utils.escapeHtml(testResult.getTestName())).append("</a>").toString());
                writeTableData(integerFormat.format(testsCount), "num");
                writeTableData(integerFormat.format(passedTests), "num");
                writeTableData(integerFormat.format(skippedTests), (skippedTests > 0 ? "num attn" : "num"));
                writeTableData(integerFormat.format(failedTests), (failedTests > 0 ? "num attn" : "num"));
                writeTableData(dateFormat.format(startTime),  "num");
                writeTableData(dateFormat.format(endTime),  "num");
                writeTableData(convertTimeToString(duration), "num");
                
                String include_groups =  testResult.getIncludedGroups().toString().trim();
                if(!include_groups.isEmpty()){
                    writeTableData(testResult.getIncludedGroups());
                }else{
                    writeTableData("Not Found");
                }

                String exclude_groups =  testResult.getExcludedGroups().toString().trim();
                if(!exclude_groups.isEmpty()){
                    writeTableData(testResult.getExcludedGroups());
                }else{
                    writeTableData("Not Found");
                }
                
                writer.println("</tr>");
                totalTestsCount +=testsCount;
                totalPassedTests += passedTests;
                totalSkippedTests += skippedTests;
                totalFailedTests += failedTests;
                totalDuration += duration;
                testIndex++;
            }
        }
		
        // Print totals if there was more than one test
        if (testIndex > 1) {
            writer.print("<tr>");
            writer.print("<th>Total</th>");
            writeTableHeader(integerFormat.format(totalTestsCount), "num");
            writeTableHeader(integerFormat.format(totalPassedTests), "num");
            writeTableHeader(integerFormat.format(totalSkippedTests), (totalSkippedTests > 0 ? "num attn" : "num"));
            writeTableHeader(integerFormat.format(totalFailedTests), (totalFailedTests > 0 ? "num attn" : "num"));
            writeTableHeader("-", "num");
            writeTableHeader("-", "num");
            // writer.print("<th colspan=\"2\"></th>");
            writeTableHeader(convertTimeToString(totalDuration), "num");
            writeTableHeader("-", "num");
            writeTableHeader("-", "num");
            writer.println("</tr>");
        }

        writer.println("</table>");
        writer.println("</div>");
    }

    /**
     * Writes a summary of all the test scenarios.
     */
    protected void writeScenarioSummary() {
    	writer.print("<div class=\"easy-test-summary\">");
        writer.print("<table class=\"table-bordered tbl2\" id='summary'>");
        writer.print("<thead class= \"easy-test-overview\">");
        writer.print("<tr>");
        writer.print("<th>Class Name</th>");
        writer.print("<th>Method Name</th>");
        writer.print("<th>Short Exception</th>");
        writer.print("<th>Screenshot</th>");
        writer.print("<th>Start Time</th>");
        writer.print("<th>End Time</th>");
        writer.print("</tr>");
        writer.print("</thead>");

        int testIndex = 0;
        int scenarioIndex = 0;
        for (SuiteResult suiteResult : suiteResults) {
            writer.print("<tbody><tr><th colspan=\"6\">");
            writer.print("<center><h6><b><I>"+ Utils.escapeHtml(suiteResult.getSuiteName()) +"<I></b></h6></center>");
            writer.print("</th></tr></tbody>");

            for (TestResult testResult : suiteResult.getTestResults()) {
                writer.printf("<tbody id=\"t%d\">", testIndex);
                String testName = Utils.escapeHtml(testResult.getTestName());
                int startIndex = scenarioIndex;
                scenarioIndex += writeScenarioSummary(testName + " &#8212; failed (configuration methods)", testResult.getFailedConfigurationResults(), "failed", scenarioIndex);
                scenarioIndex += writeScenarioSummary(testName + " &#8212; failed", testResult.getFailedTestResults(), "failed", scenarioIndex);
                scenarioIndex += writeScenarioSummary(testName + " &#8212; skipped (configuration methods)", testResult.getSkippedConfigurationResults(), "skipped", scenarioIndex);
                scenarioIndex += writeScenarioSummary(testName + " &#8212; skipped", testResult.getSkippedTestResults(), "skipped", scenarioIndex);
                scenarioIndex += writeScenarioSummary(testName + " &#8212; passed", testResult.getPassedTestResults(), "passed", scenarioIndex);

                if (scenarioIndex == startIndex) {
                    writer.print("<tr><th colspan=\"4\" class=\"invisible\"/></tr>");
                }
                writer.println("</tbody>");
                testIndex++;
            }
        }
        writer.println("</table>");
        writer.println("</div>");
    }

    /**
     * Writes the scenario summary for the results of a given state for a single
     * test.
     */
    private int writeScenarioSummary(String description, List<ClassResult> classResults, String cssClassPrefix, int startingScenarioIndex) {
        int scenarioCount = 0;
        if (!classResults.isEmpty()) {
            writer.print("<tr><th colspan=\"6\">");
            writer.print(description);
            writer.print("</th></tr>");
            int scenarioIndex = startingScenarioIndex;
            int classIndex = 0;
            for (ClassResult classResult : classResults) {
                String cssClass = cssClassPrefix + ((classIndex % 2) == 0 ? "even" : "odd");
                buffer.setLength(0);
                int scenariosPerClass = 0;
                int methodIndex = 0;
                for (MethodResult methodResult : classResult.getMethodResults()) {
                    List<ITestResult> results = methodResult.getResults();
                    int resultsCount = results.size();
                    assert resultsCount > 0;

                    ITestResult firstResult = results.iterator().next();
                    String methodName = Utils.escapeHtml(firstResult.getMethod().getMethodName());
                    long start = firstResult.getStartMillis();
                    long end = firstResult.getEndMillis();
                    
                    String shortException="";
    				String failureScreenShot="";
                    Throwable exception=firstResult.getThrowable();
                    boolean hasThrowable = exception != null;
                    if(hasThrowable){
                        String str = Utils.shortStackTrace(exception, true);
                        Scanner scanner = new Scanner(str);
                        shortException = scanner.nextLine();
                        scanner.close();
                        List<String> msgs = Reporter.getOutput(firstResult);
                        boolean hasReporterOutput = msgs.size() > 0;
                        if(hasReporterOutput){
                            for (String info : msgs) {
                                failureScreenShot+=info+"<br/>";
                            }
                        }
                    }
    					
    				DateFormat formatter = new SimpleDateFormat("hh:mm:ss a");
    				Calendar startTime = Calendar.getInstance();
    				startTime.setTimeInMillis(start);
    				Calendar endTime = Calendar.getInstance();
    				endTime.setTimeInMillis(end);
                    // The first method per class shares a row with the class
                    if (methodIndex > 0) {
                        buffer.append("<tr class=\"\"").append(cssClass).append("\">");  // header
                    }

                    // Write the timing information with the first scenario per
                    // method
                    if(failureScreenShot.isEmpty()){
                        failureScreenShot = "Not Found";
                    }

                    buffer.append("<td><a href=\"#m\"")
                        .append(scenarioIndex).append(">")
                        .append(methodName)
                        .append("</a></td>").append("<td rowspan=\"")
                        .append("\">").append(shortException)
                        .append("</td>").append("<td rowspan=\"")
                        .append("\">").append(failureScreenShot)
                        .append("</td>").append("<td rowspan=\"")
                        .append("\">").append(formatter.format(startTime.getTime()))
                        .append("</td>").append("<td rowspan=\"")
                        .append("\">").append(formatter.format(endTime.getTime())).append("</td></tr>");
                    scenarioIndex++;

                    // Write the remaining scenarios for the method
                    for (int i = 1; i < resultsCount; i++) {
                        buffer.append("<tr class=\"").append(cssClass).append("\">").append("<td><a href=\"#m\"")
                            .append(scenarioIndex).append("\">").append(methodName).append("</a></td></tr>");
                        scenarioIndex++;
                    }

                    scenariosPerClass += resultsCount;
                    methodIndex++;
                }

                // Write the test results for the class
                writer.print("<tr class=\"");
                writer.print(cssClass);
                writer.print("\">");
                writer.print("<td rowspan=\"");
                writer.print(scenariosPerClass);
                writer.print("\">");
                writer.print(Utils.escapeHtml(classResult.getClassName()));
                writer.print("</td>");
                writer.print(buffer);
                classIndex++;
            }
            scenarioCount = scenarioIndex - startingScenarioIndex;
        }
        return scenarioCount;
    }

    /**
     * Writes the details for all test scenarios.
     */
    protected void writeScenarioDetails() {
        int scenarioIndex = 0;
        for (SuiteResult suiteResult : suiteResults) {
            for (TestResult testResult : suiteResult.getTestResults()) {
                writer.print("<h4 style = \"margin-left: 90px\">");
                writer.print("<b>"+Utils.escapeHtml(testResult.getTestName())+"</b>");
                writer.print("</h4>");

                scenarioIndex += writeScenarioDetails(testResult.getFailedConfigurationResults(), scenarioIndex);
                scenarioIndex += writeScenarioDetails(testResult.getFailedTestResults(), scenarioIndex);
                scenarioIndex += writeScenarioDetails(testResult.getSkippedConfigurationResults(), scenarioIndex);
                scenarioIndex += writeScenarioDetails(testResult.getSkippedTestResults(), scenarioIndex);
                scenarioIndex += writeScenarioDetails(testResult.getPassedTestResults(), scenarioIndex);
            }
        }
    }

    /**
     * Writes the scenario details for the results of a given state for a single
     * test.
     */
    private int writeScenarioDetails(List<ClassResult> classResults, int startingScenarioIndex) {
        int scenarioIndex = startingScenarioIndex;
        for (ClassResult classResult : classResults) {
            String className = classResult.getClassName();
            for (MethodResult methodResult : classResult.getMethodResults()) {
                List<ITestResult> results = methodResult.getResults();
                assert !results.isEmpty();
                String label = Utils.escapeHtml(className+ " : "+ results.iterator().next().getMethod().getMethodName());
                for (ITestResult result : results) {
                    writeScenario(scenarioIndex, label, result);
                    scenarioIndex++;
                }
            }
        }
        return scenarioIndex - startingScenarioIndex;
    }

    /**
     * Writes the details for an individual test scenario
     */
    private void writeScenario(int scenarioIndex, String label, ITestResult result) {
        writer.print("<h3 id=\"m\""+scenarioIndex+">");
        writer.print(label);
        writer.print("</h3><table class=\"table-bordered result\">");
        boolean hasRows = false;

        // Write test parameters (if any)
        Object[] parameters = result.getParameters();
        int parameterCount = (parameters == null ? 0 : parameters.length);
        if (parameterCount > 0) {
            writer.print("<tr class=\"param\">");
            for (int i = 1; i <= parameterCount; i++) {
                writer.print("<th>Parameter #");
                writer.print(i);
                writer.print("</th>");
            }
            writer.print("</tr><tr class=\"param stripe\">");
            for (Object parameter : parameters) {
                writer.print("<td>");
                writer.print(Utils.escapeHtml(Utils.toString(parameter)));
                writer.print("</td>");
            }
            writer.print("</tr>");
            hasRows = true;
        }

        // Write reporter messages (if any)
        List<String> reporterMessages = Reporter.getOutput(result);
        if (!reporterMessages.isEmpty()) {
            writer.print("<tr><th");
            if (parameterCount > 1) {
                writer.printf(" colspan=\"%d\"", parameterCount);
            }
            writer.print(">Messages</th></tr>");

            writer.print("<tr><td");
            if (parameterCount > 1) {
                writer.printf(" colspan=\"%d\"", parameterCount);
            }
            writer.print(">");
            writeReporterMessages(reporterMessages);
            writer.print("</td></tr>");
            hasRows = true;
        }

        // Write exception (if any)
        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            writer.print("<tr><th");
            if (parameterCount > 1) {
                writer.printf(" colspan=\"%d\"", parameterCount);
            }
            writer.print(">");
            writer.print((result.getStatus() == ITestResult.SUCCESS ? "Expected Exception" : "Exception"));
            writer.print("</th></tr>");
            writer.print("<tr><td");
            if (parameterCount > 1) {
                writer.printf(" colspan=\"%d\"", parameterCount);
            }
            writer.print(">");
            writeStackTrace(throwable);
            writer.print("</td></tr>");
            hasRows = true;
        }

        if (!hasRows) {
            writer.print("<tr><th");
            if (parameterCount > 1) {
                writer.printf("colspan=\"%d\"", parameterCount);
            }
            writer.print("class=\"invisible\"/></tr>");
        }

        writer.print("</table>");
        writer.println("<p class=\"totop\"><a href=\"#summary\">back to summary</a></p>");
    }

    protected void writeReporterMessages(List<String> reporterMessages) {
        writer.print("<div class=\"messages\">");
        Iterator<String> iterator = reporterMessages.iterator();
        assert iterator.hasNext();
        if (Reporter.getEscapeHtml()) {
        	writer.print(Utils.escapeHtml(iterator.next()));
        } else {
        	writer.print(iterator.next());
        }
        while (iterator.hasNext()) {
            writer.print("<br/>");
            if (Reporter.getEscapeHtml()) {
            	writer.print(Utils.escapeHtml(iterator.next()));
            } else {
            	writer.print(iterator.next());
            }
        }
        writer.print("</div>");
    }

    protected void writeStackTrace(Throwable throwable) {
        writer.print("<div class=\"stacktrace\">");
        writer.print(Utils.shortStackTrace(throwable, true));
        writer.print("</div>");
    }

    /**
     * Writes a TH element with the specified contents and CSS class names.
     * @param html the HTML contents
     * @param cssClasses the space-delimited CSS classes or null if there are no classes to apply
     */
    protected void writeTableHeader(String html, String cssClasses) {
        writeTag("th", html, cssClasses);
    }

    /**
     * Writes a TD element with the specified contents.
     * @param html the HTML contents
     */
    protected void writeTableData(String html) {
        writeTableData(html, null);
    }

    /**
     * Writes a TD element with the specified contents and CSS class names.
     * @param html the HTML contents
     * @param cssClasses the space-delimited CSS classes or null if there are no classes to apply
     */
    protected void writeTableData(String html, String cssClasses) {
        writeTag("td", html, cssClasses);
    }

    /**
     * Writes an arbitrary HTML element with the specified contents and CSS
     * class names.
     * @param tag the tag name
     * @param html the HTML contents
     * @param cssClasses the space-delimited CSS classes or null if there are no classes to apply
     */
    protected void writeTag(String tag, String html, String cssClasses) {
        writer.print("<");
        writer.print(tag);
        if (cssClasses != null) {
            writer.print(" class=\"\"");
            writer.print(cssClasses);
            writer.print("");
        }
        writer.print(">");
        writer.print(html);
        writer.print("</");
        writer.print(tag);
        writer.print(">");
    }

    /**
     * Groups {@link TestResult}s by suite.
     */
    protected static class SuiteResult {
        private final String suiteName;
        private final List<TestResult> testResults = Lists.newArrayList();

        public SuiteResult(ISuite suite) {
            suiteName = suite.getName();
            for (ISuiteResult suiteResult : suite.getResults().values()) {
                testResults.add(new TestResult(suiteResult.getTestContext()));
            }
        }

        public String getSuiteName() {
            return suiteName;
        }

        /**
         * @return the test results (possibly empty)
         */
        public List<TestResult> getTestResults() {
            return testResults;
        }
    }

    /**
     * Groups {@link ClassResult}s by test, type (configuration or test), and
     * status.
     */
    protected static class TestResult {
        /**
         * Orders test results by class name and then by method name (in
         * lexicographic order).
         */
        protected static final Comparator<ITestResult> RESULT_COMPARATOR = new Comparator<ITestResult>() {
            @Override
            public int compare(ITestResult o1, ITestResult o2) {
                int result = o1.getTestClass().getName().compareTo(o2.getTestClass().getName());
                if (result == 0) {
                    result = o1.getMethod().getMethodName().compareTo(o2.getMethod().getMethodName());
                }
                return result;
            }
        };

        private final String testName;
        private final Date testStartTime;
        private final Date testEndTime;
        private final List<ClassResult> failedConfigurationResults;
        private final List<ClassResult> failedTestResults;
        private final List<ClassResult> skippedConfigurationResults;
        private final List<ClassResult> skippedTestResults;
        private final List<ClassResult> passedTestResults;
        private final int failedTestCount;
        private final int skippedTestCount;
        private final int passedTestCount;
        private final int testCount;
        private final long duration;
        private final String includedGroups;
        private final String excludedGroups;

        public TestResult(ITestContext context) {
            testName = context.getName();

            Set<ITestResult> failedConfigurations = context.getFailedConfigurations().getAllResults();
            Set<ITestResult> failedTests = context.getFailedTests().getAllResults();
            Set<ITestResult> skippedConfigurations = context.getSkippedConfigurations().getAllResults();
            Set<ITestResult> skippedTests = context.getSkippedTests().getAllResults();
            Set<ITestResult> passedTests = context.getPassedTests().getAllResults();

            failedConfigurationResults = groupResults(failedConfigurations);
            failedTestResults = groupResults(failedTests);
            skippedConfigurationResults = groupResults(skippedConfigurations);
            skippedTestResults = groupResults(skippedTests);
            passedTestResults = groupResults(passedTests);
            
            testStartTime = context.getStartDate();
            testEndTime = context.getEndDate();

            failedTestCount = failedTests.size();
            skippedTestCount = skippedTests.size();
            passedTestCount = passedTests.size();
            testCount = context.getAllTestMethods().length;
            duration = context.getEndDate().getTime() -context.getStartDate().getTime();
            includedGroups = formatGroups(context.getIncludedGroups());
            excludedGroups = formatGroups(context.getExcludedGroups());
        }

        /**
         * Groups test results by method and then by class.
         */
        protected List<ClassResult> groupResults(Set<ITestResult> results) {
            List<ClassResult> classResults = Lists.newArrayList();
            if (!results.isEmpty()) {
                List<MethodResult> resultsPerClass = Lists.newArrayList();
                List<ITestResult> resultsPerMethod = Lists.newArrayList();

                List<ITestResult> resultsList = Lists.newArrayList(results);
                Collections.sort(resultsList, RESULT_COMPARATOR);
                Iterator<ITestResult> resultsIterator = resultsList.iterator();
                assert resultsIterator.hasNext();

                ITestResult result = resultsIterator.next();
                resultsPerMethod.add(result);

                String previousClassName = result.getTestClass().getName();
                String previousMethodName = result.getMethod().getMethodName();
                while (resultsIterator.hasNext()) {
                    result = resultsIterator.next();

                    String className = result.getTestClass().getName();
                    if (!previousClassName.equals(className)) {
                        // Different class implies different method
                        assert !resultsPerMethod.isEmpty();
                        resultsPerClass.add(new MethodResult(resultsPerMethod));
                        resultsPerMethod = Lists.newArrayList();

                        assert !resultsPerClass.isEmpty();
                        classResults.add(new ClassResult(previousClassName,resultsPerClass));
                        resultsPerClass = Lists.newArrayList();

                        previousClassName = className;
                        previousMethodName = result.getMethod().getMethodName();
                    } else {
                        String methodName = result.getMethod().getMethodName();
                        if (!previousMethodName.equals(methodName)) {
                            assert !resultsPerMethod.isEmpty();
                            resultsPerClass.add(new MethodResult(resultsPerMethod));
                            resultsPerMethod = Lists.newArrayList();
                            previousMethodName = methodName;
                        }
                    }
                    resultsPerMethod.add(result);
                }
                assert !resultsPerMethod.isEmpty();
                resultsPerClass.add(new MethodResult(resultsPerMethod));
                assert !resultsPerClass.isEmpty();
                classResults.add(new ClassResult(previousClassName, resultsPerClass));
            }
            return classResults;
        }

        public String getTestName() {
            return testName;
        }
        
        public Date getTestStartTime() {
            return testStartTime;
          }
        
        public Date getTestEndTime() {
            return testEndTime;
          }
        

        /**
         * @return the results for failed configurations (possibly empty)
         */
        public List<ClassResult> getFailedConfigurationResults() {
            return failedConfigurationResults;
        }

        /**
         * @return the results for failed tests (possibly empty)
         */
        public List<ClassResult> getFailedTestResults() {
            return failedTestResults;
        }

        /**
         * @return the results for skipped configurations (possibly empty)
         */
        public List<ClassResult> getSkippedConfigurationResults() {
            return skippedConfigurationResults;
        }

        /**
         * @return the results for skipped tests (possibly empty)
         */
        public List<ClassResult> getSkippedTestResults() {
            return skippedTestResults;
        }

        /**
         * @return the results for passed tests (possibly empty)
         */
        public List<ClassResult> getPassedTestResults() {
            return passedTestResults;
        }

        public int getFailedTestCount() {
            return failedTestCount;
        }

        public int getSkippedTestCount() {
            return skippedTestCount;
        }

        public int getPassedTestCount() {
            return passedTestCount;
        }

        public long getDuration() {
            return duration;
        }

        public String getIncludedGroups() {
            return includedGroups;
        }

        public String getExcludedGroups() {
            return excludedGroups;
        }
        
        public int getTestCount() {
            return testCount;
        }

        /**
         * Formats an array of groups for display.
         */
        protected String formatGroups(String[] groups) {
            if (groups.length == 0) {
                return "";
            }

            StringBuilder builder = new StringBuilder();
            builder.append(groups[0]);
            for (int i = 1; i < groups.length; i++) {
                builder.append(", ").append(groups[i]);
            }
            return builder.toString();
        }
    }

    /**
     * Groups {@link MethodResult}s by class.
     */
    protected static class ClassResult {
        private final String className;
        private final List<MethodResult> methodResults;

        /**
         * @param className the class name
         * @param methodResults the non-null, non-empty {@link MethodResult} list
         */
        public ClassResult(String className, List<MethodResult> methodResults) {
            this.className = className;
            this.methodResults = methodResults;
        }

        public String getClassName() {
            return className;
        }

        /**
         * @return the non-null, non-empty {@link MethodResult} list
         */
        public List<MethodResult> getMethodResults() {
            return methodResults;
        }
    }

    /**
     * Groups test results by method.
     */
    protected static class MethodResult {
        private final List<ITestResult> results;

        /**
         * @param results
         *            the non-null, non-empty result list
         */
        public MethodResult(List<ITestResult> results) {
            this.results = results;
        }

        /**
         * @return the non-null, non-empty result list
         */
        public List<ITestResult> getResults() {
            return results;
        }
    }
    
	
	/* Convert long type milliseconds to format hh:mm:ss */
	public String convertTimeToString(long miliSeconds) {
		int hrs = (int) TimeUnit.MILLISECONDS.toHours(miliSeconds) % 24;
		int min = (int) TimeUnit.MILLISECONDS.toMinutes(miliSeconds) % 60;
		int sec = (int) TimeUnit.MILLISECONDS.toSeconds(miliSeconds) % 60;
		return String.format("%02d:%02d:%02d", hrs, min, sec);
	}
		
}