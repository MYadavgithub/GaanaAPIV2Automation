package utils;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {
    
    public static String getCurrentDateTime() {
		Calendar currentDate = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy:HH.mm.ss");
		return formatter.format(currentDate.getTime());
	}

	public String removeQoutesForCsvData(String val){
        String updated = null;
        Pattern p = Pattern.compile("\"\"([^\"\"]*)\"");
        Matcher m = p.matcher(val);
        String rep = "";
        int count = 0;
        while (m.find()) {
            String ex_val = m.group(1);
            if(ex_val.length() > 1){
                rep = "\\"+"\""+ex_val+"\\"+"\"";
                if(count == 0){
                    updated = val.replace("\"\""+ex_val+"\"\"", rep);
                }else{
                    updated =  updated.replace("\"\""+ex_val+"\"\"", rep);
                }
                count++;
            }
        }
        if(updated == null){
            updated = val;
        }
        return updated;
    }

    /**
     * validate date is between last and after 10 years or not
     * @param date
     * @return
     */
    public static boolean validateYears(String date){
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
        LocalDateTime actual_date = LocalDate.parse(date, format).atStartOfDay();
        LocalDateTime previous = actual_date.minusYears(10);
        LocalDateTime after = actual_date.plusYears(10);
        if(actual_date.isAfter(previous) &&  actual_date.isBefore(after)){
            return true;
        }
        return false;
    }

    public static String generateRandomDeviceId(){
        UUID randomId = UUID.randomUUID();
        return "GM"+randomId.toString().toUpperCase().replaceAll("-", "").substring(0, 12);
    }
}
