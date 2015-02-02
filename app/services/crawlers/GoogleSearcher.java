package services.crawlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.org.apache.xpath.internal.res.XPATHErrorResources;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import play.libs.XML;
import play.libs.XPath;
import play.libs.ws.WSRequestHolder;
import play.libs.ws.*;
import play.libs.F.Promise;
import services.persons.XUser;

import java.net.URLEncoder;
import java.util.List;

/**
 * Created by madalien on 31/01/15.
 */
public class GoogleSearcher {

    /**
     * Later implement async version
     */

    public JsonNode googleResult;
    public  void SearchOnGoogle(String textToSearch, XUser curUser) throws Exception{
        String query = textToSearch;
        String charset = "UTF-8";

        WSRequestHolder req = WS.url("http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=" + URLEncoder.encode(query, charset));
        Promise<JsonNode> jsonRepPromise = req.get().map(response -> {
            JsonNode root = response.asJson();
            List<String> urls = root.findValuesAsText("url");
            int[] i={0};
            urls.forEach((url) -> {
                System.out.println("\n\n*********Entering *********"+i[0]+": "+url);
                    this.parseHtml(url, curUser);
                i[0]++;
            });
            System.out.println("end of parsing");
            return response.asJson();
        });

    }

    public void parseHtml(String htmlUrl, XUser curUser){
        WSRequestHolder req = WS.url(htmlUrl);
        Promise<Document> xmlRepPromise = req.get().map(response ->{

            String body = response.getBody();
            body = body.replaceAll("<!DOCTYPE html>", "");
            Document html =  curUser.stringToDoc(body);
            if(html == null){
                return XML.fromString("<html></html>");
            }
            String fb = curUser.facebook;
            String tw = curUser.twitter;
            String lk = curUser.linkedin;
            String query = "//a[contains(@href,'"+fb+"') or contains(@href,'"+tw+"') or contains(@href,'"+lk+"')]";
            NodeList hrefs = XPath.selectNodes(query, html);
            System.out.println("xpath "+hrefs.getLength());
            for(int i =0; i < hrefs.getLength(); i++){
                Node curNode = hrefs.item(i);
                String curLink = XPath.selectText("@href", curNode);
                System.out.println("current link in html node "+i+": "  + curLink);
                curUser.addRedirection("1", htmlUrl);
            }
            curUser.saveDomToXml();
            return html;
        });
    }

}
