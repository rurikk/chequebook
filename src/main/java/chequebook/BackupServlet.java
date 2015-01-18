package chequebook;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Created by rurik
 */
@WebServlet(BackupServlet.CONTEXT + "/*")
@MultipartConfig
public class BackupServlet extends HttpServlet {
    public static final String CONTEXT = "/backup";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (checkAccess(req)) {
            File file = Bank.dataFile;
            byte[] bytes = Files.readAllBytes(file.toPath());
            res.setContentLength(bytes.length);
            res.setContentType("application/octet-stream");
            res.setHeader("Content-Disposition", "attachment; filename=\"checkbook-backup-" + now() + ".bin\"");
            ServletOutputStream os = res.getOutputStream();
            os.write(bytes);
            os.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (checkAccess(req)) {
            for (Part part : req.getParts()) {
                part.write(Bank.dataFile.getAbsolutePath());
            }
            Bank.load();
        }
    }

    private boolean checkAccess(HttpServletRequest req) {
        return Bank.instance.isAdmin(req.getRequestURI().substring(CONTEXT.length() + 1));
    }

    private static String now() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm").withZone(ZoneId.systemDefault()).format(Instant.now());
    }
}
