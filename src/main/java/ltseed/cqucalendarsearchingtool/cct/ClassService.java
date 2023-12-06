package ltseed.cqucalendarsearchingtool.cct;

import javax.ws.rs.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static ltseed.cqucalendarsearchingtool.cct.Student.requestStudentClasses;

@Path("/class")
public class ClassService {
    @GET
    @Path("/all/{id}")
    @Produces("class-list/List<Class>")
    public List<Class> getAllClazz(@PathParam("id") String id) {
        try {
            return Objects.requireNonNull(requestStudentClasses(id)).classes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @GET
    @Path("/ics/{id}")
    @Produces("class-ics/FileInputStream")
    public FileInputStream getClazzFile(@PathParam("id") String id) {
        try {
            IcsFileParser.outputIcsFileFromClasses(Objects.requireNonNull(requestStudentClasses(id)).classes,id);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        String name = id + ".ics";
        try {
            File file = new File(IcsFileParser.ICS_FOLDER, name);
            return new FileInputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}