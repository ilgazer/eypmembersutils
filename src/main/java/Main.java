

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.FormElement;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException, KeyManagementException, NoSuchAlgorithmException {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);


        Scanner input2 = new Scanner(System.in);
        System.out.println("Username:");
        String username = input2.next();
        System.out.println("Password");
        String password = input2.next();

        Session session = new Session(username, password);
        String lineNew;

        Scanner input = new Scanner(System.in);
        while (input.hasNextLine()) {
            lineNew = input.nextLine();
            if (lineNew.isEmpty()) {
                break;
            }
            String answer = session.searchName(lineNew);
            if (!answer.equals("[]")) {
                String name = mergeHalf(StringEscapeUtils.unescapeJava(answer
                        .split("\"")[1]
                        .split("\\(")[0])
                        .split(" "))
                        .toLowerCase();
                int resp=session.nameHasConference(name, "2018-04-12 NS EYP Turkey Ankara");
                if (resp==0) {
                    System.out.println(StringEscapeUtils.unescapeJava(answer.toString().split("\"")[1]).split("\\(")[0]);
                } else if(resp==-1) {
                    System.out.println("!" + StringEscapeUtils.unescapeJava(answer.toString().split("\"")[1]).split("\\(")[0]);
                }
            }
        }
    }

    static String mergeHalf(String[] ss) {
        String ret = "";
        for (int i = (ss.length / 2); i <= ss.length - 1; i++) {
            ret += ss[i];
            ret += " ";
        }
        return ret.trim().replace(" ", "-");
    }
}
