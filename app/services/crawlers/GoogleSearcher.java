package services.crawlers;
import java.lang.String;
import java.util.List;
import org.apache.stanbol.enhancer.engines.htmlextractor.impl.DOMBuilder;
import org.jsoup.Jsoup;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import play.libs.F;
import play.libs.XPath;
import play.libs.ws.*;
import services.persons.XUser;


/**
 * Created by madalien on 31/01/15.
 */
public class GoogleSearcher {

    /**
     * Later implement async version
     */

    public static  F.Function<WSResponse, List<String>> SearchOnGoogle(XUser curUser){
        return new F.Function<WSResponse, List<String>>(){
            @Override
            public List<String> apply(WSResponse response) throws Throwable {
                return response.asJson().findValuesAsText("url");
            }
        };
    }

    public static F.Function<WSResponse, String> parseHtml(String htmlUrl, XUser curUser){
        return new F.Function<WSResponse, String>(){
            @Override
            public String apply(WSResponse response) throws Throwable {
                org.jsoup.nodes.Document html = Jsoup.parse(response.getBody());
                String fb = curUser.facebook;
                String tw = curUser.twitter;
                String lk = curUser.linkedin;
                boolean isSetFb = false;
                boolean isSetTw = false;
                boolean isSetLk = false;
                System.out.println("in google");
                String xpathQuery = "//a[contains(@href,'" + fb + "') or contains(@href,'" + tw + "') or contains(@href,'" + lk + "')]";
                NodeList elements = null;
                try {
                    elements = XPath.selectNodes(xpathQuery, DOMBuilder.jsoup2DOM(html));
                }catch (Exception e){
                    System.out.println("parsing error"+e.getMessage());
                    return "";
                }
                if(elements == null){
                    System.out.println("elements are null"+elements);
                    return "";
                }
                for(int i =0; i < elements.getLength(); i++){
                    Node aTag = elements.item(i);
                    String curLink = ((org.w3c.dom.Element)aTag).getAttribute("href");
                    System.out.println("element is "+ curLink);
                    if(curLink.contains(tw) && !isSetTw) {
                        curUser.addRedirection("1", htmlUrl);
                        System.out.println("current link in html node " + ": " + curLink);
                        //prevent duplicates
                        isSetTw = true;
                    }
                    else if(curLink.contains(lk) && !isSetLk) {
                        curUser.addRedirection("2", htmlUrl);
                        System.out.println("current link in html node " + ": " + curLink);
                        isSetLk = true;
                    }
                    else if(curLink.contains(fb) && !isSetFb) {
                        curUser.addRedirection("3", htmlUrl);
                        System.out.println("current link in html node " + ": " + curLink);
                        isSetFb = true;
                    }
                }
                return "GoogleParseDone";
            }
        };
    }

}
