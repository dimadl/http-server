package by.bsuir.httpserver.session;

import by.bsuir.httpserver.config.Configuration;
import by.bsuir.httpserver.entity.ResponceHeader;
import org.springframework.util.MimeTypeUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dzmitry_Dydyshka on 11/3/2015.
 */
public class ClientSession implements Runnable {

    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private Configuration configuration;

    public ClientSession(Socket socket) {

        this.socket = socket;
        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
            configuration = new Configuration();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {

        try {
            String header = readHeader();
            System.out.println(header + "\n");
            String url = getURIFromHeader(header);
            int code = send(url);
            System.out.println("Responce code: " + code);
            System.out.println("Resource: " + url + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    private String readHeader() throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder builder = new StringBuilder();
        String ln;

        while (true) {
            ln = reader.readLine();
            if (ln == null || ln.isEmpty()) {
                break;
            }
            builder.append(ln + System.getProperty("line.separator"));
        }
        return builder.toString();
    }

    private String getURIFromHeader(String header) {

        String uri = null;
        String pattern = "\\/([a-z-\\/]+\\.[a-z]{2,5})";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(header);
        if (m.find()) {
            uri = m.group(0);
        } else {
            System.err.println("Bad header");
        }

        return configuration.getDestination() + uri;
    }

    private int send(String url) {

        int code = 200;

        byte[] encoded = new byte[0];
        try {
            encoded = Files.readAllBytes(Paths.get(url));
        } catch (IOException e) {
            code = 404;
            try {
                encoded = Files.readAllBytes(Paths.get(configuration.getDestination() + "/error/404error.html"));
            } catch (IOException e1) {

            }
        }
        String s = new String(encoded);

        String mime = "text/plain";

        String[] res = url.split("\\.");
        boolean isImage = false;

        String ext = res[res.length - 1];
        if (ext.equalsIgnoreCase("html")) {
            mime = MimeTypeUtils.TEXT_HTML_VALUE;
        }
        else if (ext.equalsIgnoreCase("htm")) {
            mime = MimeTypeUtils.TEXT_HTML_VALUE;
        }
        else if (ext.equalsIgnoreCase("gif")) {
            mime = MimeTypeUtils.IMAGE_GIF_VALUE;
            isImage = true;
        } else if (ext.equalsIgnoreCase("jpg")) {
            mime = MimeTypeUtils.IMAGE_JPEG_VALUE;
            isImage = true;
        } else if (ext.equalsIgnoreCase("png")) {
            mime = MimeTypeUtils.IMAGE_PNG_VALUE;
            isImage = true;
        } else if (ext.equalsIgnoreCase("jpeg")) {
            mime = MimeTypeUtils.IMAGE_JPEG_VALUE;
            isImage = true;
        } else if (ext.equalsIgnoreCase("css")) {
            mime = "text/css";
        }

        String result = new ResponceHeader(code, mime).getHeaderAsStirng() + s;

        try {

            if (isImage) {
                BufferedImage read = ImageIO.read(new File(url));
                ImageIO.write(read, ext, out);
            } else {
                out.write(result.getBytes());
            }

            out.flush();
        } catch (IOException e) {
            code = 500;
        }

        return code;
    }

}
