import com.poiji.bind.Poiji;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class Analyzer {

    HashMap<String, HashSet<String>> employeeSubmissions = new HashMap<>();
    Map<String, Integer> employeeLateCounts = new TreeMap<>();
    HashMap<String, Record> records = new HashMap<>();
    HashMap<String, Double> totalAttempts = new HashMap<>();

    public Analyzer(String fileName) {
        for (Record r : Poiji.fromExcel(new File(fileName), Record.class)) {
            if (totalAttempts.containsKey(r.username)) {
                totalAttempts.put(r.username, totalAttempts.get(r.username) + 1.0);
            } else {
                totalAttempts.put(r.username, 1.0);
            }

            if (r.jobCode.contains("Holiday") || r.jobCode.contains("Leave") || r.jobCode.contains("Vacation") || r.jobCode.contains("Sick")) {
                continue;
            } else {
                if (r.compareDates()) {
                    records.put(r.username, r);
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                    String simpleJobDate = sdf.format(r.dateJob.toDate());

                    if (employeeSubmissions.containsKey(r.username)) {
                        if (!employeeSubmissions.get(r.username).contains(simpleJobDate)) {
                            employeeSubmissions.get(r.username).add(simpleJobDate);
                            if (employeeLateCounts.containsKey(r.username)) {
                                employeeLateCounts.put(r.username, employeeLateCounts.get(r.username) + 1);
                            } else {
                                employeeLateCounts.put(r.username, 1);
                            }
                        }
                    } else {
                        HashSet<String> temp = new HashSet<>();
                        temp.add(simpleJobDate);
                        employeeSubmissions.put(r.username, temp);

                        if (employeeLateCounts.containsKey(r.username)) {
                            employeeLateCounts.put(r.username, employeeLateCounts.get(r.username) + 1);
                        } else {
                            employeeLateCounts.put(r.username, 1);
                        }
                    }
                }
            }
        }
    }

    public Record getRecord(String username) {
        return records.get(username);
    }

}
