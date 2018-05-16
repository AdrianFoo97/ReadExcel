
import java.util.ArrayList;

/**
 *
 * @author a80052136
 */
public class Summary {
    public static String getSummaryStr(ArrayList<BIS> passList) {
        String fopSub = Integer.toString(countPenSub("1-FOP", passList));
        String optiSub = Integer.toString(countPenSub("2-OPTI", passList));
        String bstrSub = Integer.toString(countPenSub("3-BSTR", passList));
        String fmSub = Integer.toString(countPenSub("4-FM", passList));
        String prefixSub = Integer.toString(countPenSub("5-PREFIX", passList));

        String fopResub = Integer.toString(countPenResub("1-FOP", passList));
        String optiResub = Integer.toString(countPenResub("2-OPTI", passList));
        String bstrResub = Integer.toString(countPenResub("3-BSTR", passList));
        String fmResub = Integer.toString(countPenResub("4-FM", passList));
        String prefixResub = Integer.toString(countPenResub("5-PREFIX", passList));
        
        String strReturn = "Below are the latest BIS Report submission status:\n\n"
                + "1-FOP\t\tPending\t\t" + fopSub + "\t\tSubmission\t\t" + 
                fopResub + "\t\tResubmission\n" +
                "2-OPTI\t\tPending\t\t" + optiSub + "\t\tSubmission\t\t" + 
                optiResub + "\t\tResubmission\n" +
                "3-BSTR\t\tPending\t\t" + bstrSub + "\t\tSubmission\t\t" + 
                bstrResub + "\t\tResubmission\n" +
                "4-FM\t\tPending\t\t" + fmSub + "\t\tSubmission\t\t" + 
                fmResub + "\t\tResubmission\n" +
                "5-PREFIX\t\tPending\t\t" + prefixSub + "\t\tSubmission\t\t" + 
                prefixResub + "\t\tResubmission\n\n" +
                "Attachment can find the details acceptance plan information.\n\n" + 
                "Thank you.";
        
        return strReturn;
    }
    
    // Count the total pending submission for a category
    public static int countPenSub(String category, ArrayList<BIS> passList) {
        int total = 0;
        for (BIS bis: passList) {
            if (bis.stepSubmitted.equalsIgnoreCase("null") &&
                    bis.getCategory().equalsIgnoreCase(category)) {
                total += 1;
            }
        }
        return total;
    }
    
    // Count the total pending resubmission for a category
    public static int countPenResub(String category, ArrayList<BIS> passList) {
        int total = 0;
        for (BIS bis: passList) {
            if (!bis.getRejected().equalsIgnoreCase("null") &&
                    bis.getCategory().equalsIgnoreCase(category) &&
                    bis.getResubmitted().equalsIgnoreCase("null")) {
                total += 1;
            }
        }
        return total;
    }
}
