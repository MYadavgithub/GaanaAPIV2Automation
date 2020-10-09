package utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import java.util.concurrent.atomic.AtomicInteger;

public class RetryAnalyzer implements IRetryAnalyzer {

	private static int MAX_RETRY_COUNT = 3;
	AtomicInteger count = new AtomicInteger(MAX_RETRY_COUNT);
	private static Logger log = LoggerFactory.getLogger(IRetryAnalyzer.class);

	public boolean isRetryAvailable() {
		return (count.intValue() > 0);
	}

	public boolean retry(ITestResult result) {
		boolean retry = false;
		if (isRetryAvailable()) {
			log.info("Going to retry test case: " + result.getMethod() + ", "+ (MAX_RETRY_COUNT - count.intValue() + 1) + " out of " + MAX_RETRY_COUNT);
			retry = true;
			count.decrementAndGet();
		}
		return retry;
	}
}