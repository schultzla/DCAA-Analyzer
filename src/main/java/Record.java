import com.poiji.annotation.ExcelCell;
import com.poiji.annotation.ExcelRow;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;


public class Record {

    DateTime dateSubmitted, dateJob, dst;

    @ExcelRow
    private int rowIndex;

    @ExcelCell(0)
    private String submit;

    @ExcelCell(6)
    String username;

    @ExcelCell(9)
    private String job;

    @ExcelCell(12)
    String jobCode;

    @Override
    public String toString() {
        return "Record{" +
                "status='" + this.compareDates() + '\'' +
                ", dateSubmitted='" + dateSubmitted.toString() + '\'' +
                ", username='" + username + '\'' +
                ", jobDate='" + dateJob.toString() + '\'' +
                ", jobCode='" + jobCode + '\'' +
                '}';
    }

    public boolean compareDates() {
        DateTimeFormatter submitSdf = DateTimeFormat.forPattern("MM/dd/yy HH:mm").withZone(DateTimeZone.forID("America/New_York"));
        DateTimeFormatter jobSdf = DateTimeFormat.forPattern("MM/dd/yy").withZone(DateTimeZone.forID("America/New_York"));
        DateTimeFormatter dstSdf = DateTimeFormat.forPattern("MM/dd/yy").withZone(DateTimeZone.forID("America/New_York"));


        dateSubmitted = new DateTime(submitSdf.parseDateTime(this.submit));
        dateJob = new DateTime(jobSdf.parseDateTime(this.job));
        dst = new DateTime(dstSdf.parseDateTime("3/10/19"));

        dateJob = dateJob.plusDays(1);

        if (dateJob.isAfter(dst)) {
            dateJob = dateJob.plusHours(12);
        } else {
            dateJob = dateJob.plusHours(13);
        }
        dateJob = dateJob.plusMinutes(59);

        return dateSubmitted.isAfter(dateJob);
    }
}
