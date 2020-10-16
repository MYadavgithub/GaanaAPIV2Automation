package utils;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
}
