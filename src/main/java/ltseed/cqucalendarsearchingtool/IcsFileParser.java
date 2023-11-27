package ltseed.cqucalendarsearchingtool;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.RandomUidGenerator;
import net.fortuna.ical4j.util.UidGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;

public class IcsFileParser {

    public static final File ICS_FOLDER = new File("E:\\SERVER\\ics-out");
    public static final ZoneId CQ;
    static {
        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
        TimeZone timezone = registry.getTimeZone("Asia/Chongqing");
        CQ = timezone.toZoneId();
    }

    public static void outputIcsFileFromClasses(List<Class> classes, String name) throws IOException {
        List<VEvent> list = new ArrayList<>();
        for (Class aClass : classes) {
            list.addAll(aClass.exportToEvents());
        }
        list.addAll(getWeekAlarm());
        outputIcsFileFromEvent(list, name);
    }

    public static void outputIcsFileFromEvent(List<VEvent> events, String name) throws IOException {
        File new_file = new File(ICS_FOLDER,name+".ics");
        //noinspection ResultOfMethodCallIgnored
        new_file.getParentFile().mkdirs();
        new_file.createNewFile();
        Calendar calendar = new Calendar().withDefaults().getFluentTarget();
        List<Property> plist = new ArrayList<>();
        plist.add(new Method("REQUEST"));
        plist.add(new ProdId("-//CQU Classes Calendar//iCal4j 1.0//EN"));
        plist.add(new XProperty("X-WR-CALNAME", "学生课程日历"));
        plist.add(new XProperty("X-WR-CALDESC", "包含所有课程安排的学生个性化日历"));
        plist.add(new Version("2.0","2.0"));
        plist.add(new CalScale("GREGORIAN"));
        calendar.setPropertyList(new PropertyList(plist));
        calendar.setComponentList(new ComponentList<>(events));
        calendar.validate();
        FileOutputStream file_out = new FileOutputStream(new_file);
        CalendarOutputter out = new CalendarOutputter();
        out.output(calendar, file_out);
    }

    public static List<VEvent> getWeekAlarm(){
        List<VEvent> week_alarm = new ArrayList<>();
        for (int i = 1; i <= 18; i++) {
            ZoneId tz = getZoneId();
            UidGenerator ug = new RandomUidGenerator();
            Uid uid = ug.generateUid();
            String eventSummary = "第"+i+"周";
            LocalDate time = LocalDate.ofInstant(Instant.ofEpochMilli(ClassTime.FIRST_DAY.getTime()+
                    (i-1)* ChronoUnit.WEEKS.getDuration().toMillis()), tz);
            VEvent event = new VEvent();
            List<Property> pros = new ArrayList<>();
            pros.add(uid);
            DtStart<LocalDate> e = new DtStart<>(time);
            e.add(Value.DATE);
            pros.add(e);
            pros.add(new Summary(eventSummary));
            event.setPropertyList(new PropertyList(pros));
            week_alarm.add(event);
        }
        return week_alarm;
    }

    public static VEvent getClassEvent(String name, ClassTime time, int week, String location, String description){
        ZoneId tz = getZoneId();
        LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochMilli(time.getStartTime(week).getTime()), tz);
        LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochMilli(time.getEndTime(week).getTime()), tz);
        VEvent event = new VEvent();
        List<Property> pros = new ArrayList<>();
        pros.add(new DtStart<>(start));
        pros.add(new DtEnd<>(end));
        return formEvent(name, location, description, event, pros);
    }

    public static VEvent getDayEvent(String name, int week, int week_day, String location, String description) {
        ZoneId tz = getZoneId();
        LocalDate d_time = LocalDate.ofInstant(Instant.ofEpochMilli(ClassTime.FIRST_DAY.getTime()+
                (week-1)* ChronoUnit.WEEKS.getDuration().toMillis() +
                (week_day-1)* ChronoUnit.DAYS.getDuration().toMillis()), tz);
        VEvent event = new VEvent();
        List<Property> pros = new ArrayList<>();

        DtStart<LocalDate> e = new DtStart<>(d_time);
        e.add(Value.DATE);
        pros.add(e);
        return formEvent(name, location, description, event, pros);
    }

    private static VEvent formEvent(String name, String location, String description, VEvent event, List<Property> pros) {
        pros.add(new Summary(name));
        pros.add(new Uid(new RandomUidGenerator().generateUid().getValue()));
        if(location != null)
            pros.add(new Location(location));
        if(description != null)
            pros.add(new Description(description));
        event.setPropertyList(new PropertyList(pros));
        return event;
    }

    private static ZoneId getZoneId() {
        return CQ;
    }
}
