import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.FormElement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;

public class Session {

    Map<String, String> cookies;

    public Session(String username, String password) throws IOException {
        Document doc = Jsoup.connect("https://www.members.eyp.org/user/login").get();
        FormElement loginForm = doc.select("#user-login").forms().get(0);
        Connection loginConn = loginForm.submit();

        // Find username and password field.
        Connection.KeyVal usernameField = null;
        Connection.KeyVal passwordField = null;
        for (Connection.KeyVal field : loginConn.request().data()) {
            if (field.key().equals("name"))
                usernameField = field;
            else if (field.key().equals("pass"))
                passwordField = field;
        }
        if (usernameField == null || passwordField == null) {
            throw new RuntimeException("Username or password field missing on form.");
        }
        usernameField.value(username);
        passwordField.value(password);
        Connection.Response resp = loginConn.timeout(60000).execute();
        cookies = resp.cookies();
    }

    public String searchName(String name) throws IOException {
        URL url = new URL("https://www.members.eyp.org/eyp/profile/autocomplete/" + encode(name));
        URLConnection conn = url.openConnection();
        // Set the cookie value to send

        conn.setRequestProperty("Cookie", "has_js=1; SSESSbebf61918e5469f2d0f571b3b2af3693=" + cookies.get("SSESSbebf61918e5469f2d0f571b3b2af3693"));
        // Send the request to the server
        conn.connect();
        StringBuilder answer = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            answer.append(line);
        }
        reader.close();
        return answer.toString();
    }

    /**
     * @param person person we aer searching for
     * @param conference the conference to check presence of
     * @return 1 if person has conference, 0 if it doesn't, -1 if the person's page cannot be accessed
     */
    public int nameHasConference(String person, String conference) throws IOException {
        try {
            URL url = new URL("https://www.members.eyp.org/users/" + encode(person));
            URLConnection conn = url.openConnection();
            // Set the cookie value to send
            conn.setRequestProperty("Cookie", "has_js=1; SSESSbebf61918e5469f2d0f571b3b2af3693=" + cookies.get("SSESSbebf61918e5469f2d0f571b3b2af3693"));
            // Send the request to the server
            conn.connect();
            StringBuilder answer = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                answer.append(line);
            }
            reader.close();

            return answer.toString().contains(conference) ? 1 : 0;
        } catch (Exception e) {
            return -1;
        }
    }



    public String encode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
