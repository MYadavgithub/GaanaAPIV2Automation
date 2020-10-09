package utils;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.testng.IAnnotationTransformer;
import org.testng.IRetryAnalyzer;
import org.testng.annotations.ITestAnnotation;

public class RetryTransformListener implements IAnnotationTransformer {

	@Override
	public void transform(ITestAnnotation annotation,  @SuppressWarnings("rawtypes") Class testClass, @SuppressWarnings("rawtypes") Constructor testConstructor, Method testMethod) {
		Class<? extends IRetryAnalyzer> retry = annotation.getRetryAnalyzerClass();
		if(retry != null) {
			annotation.setRetryAnalyzer(RetryAnalyzer.class);
		}
	}
}
