package utils;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CommonUtils {
    
    public static String getCurrentDateTime() {
		Calendar currentDate = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy:HH.mm.ss");
		return formatter.format(currentDate.getTime());
    }
}
