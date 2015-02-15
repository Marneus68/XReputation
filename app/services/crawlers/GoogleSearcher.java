package services.crawlers;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;
import org.apache.stanbol.enhancer.engines.htmlextractor.impl.DOMBuilder;
import org.jsoup.Jsoup;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import play.libs.F;
import play.libs.XPath;
import play.libs.ws.*;
import services.persons.XUser;
import services.xmlutilities.Xpath20;

import javax.xml.xpath.*;


/**
 * Created by madalien on 31/01/15.
 */
public class GoogleSearcher {

    /**
     * Later implement async version
     */

    public static  F.Function<WSResponse, List<String>> SearchOnGoogle(XUser curUser ){
        return new F.Function<WSResponse, List<String>>(){
            @Override
            public List<String> apply(WSResponse response) throws Throwable {

                //System.out.println("in google json "+response.asJson());
                List<String> tabUrls = response.asJson().findValuesAsText("url");
                return tabUrls;
            }
        };
    }

    public static F.Function<WSResponse, String> parseHtml(String htmlUrl, XUser curUser){
        return new F.Function<WSResponse, String>(){
            @Override
            public String apply(WSResponse response) throws Throwable {

                System.out.println("in google "+htmlUrl);
                org.jsoup.nodes.Document html = Jsoup.parse(response.getBody());
                List<String> userLinks = new ArrayList<>();
                String fb = curUser.facebook;
                String lk = curUser.linkedin;
                String cp = curUser.company;
                boolean isSetFb = fb != null? !userLinks.add(fb):false;
                boolean isSetLk = lk != null? !userLinks.add(lk):false;
                boolean isSetCP = cp != null? !userLinks.add(cp):false;
                // uses Xpath 1.0 see twitter for Xpath 2.0
                StringBuilder xpathQuery = new StringBuilder("//a[");
                int itr = 0;
                for(String link: userLinks) {
                    if(itr == 0)
                        xpathQuery.append("contains(@href,'" + link + "')");
                    else
                        xpathQuery.append("or contains(@href,'" + link + "')");
                    itr++;
                }
                xpathQuery.append("]");
                NodeList elements = null;
                try {
                    elements = XPath.selectNodes(xpathQuery.toString(), DOMBuilder.jsoup2DOM(html));
                }catch (Exception e){
                    System.out.println("parsing error"+e.getMessage());
                    return " ";
                }
                if(elements == null){
                    System.out.println("elements are null"+elements);
                    return " ";
                }
                for(int i =0; i < elements.getLength(); i++){
                    Node aTag = elements.item(i);
                    String curLink = ((org.w3c.dom.Element)aTag).getAttribute("href");
                    System.out.println("element is "+ curLink);
                    if(curLink.contains(lk) && !isSetLk) {
                        curUser.addRedirection("2", htmlUrl);
                        System.out.println("current link in html node " + ": " + curLink);
                        isSetLk = true;
                    }
                    else if(curLink.contains(fb) && !isSetFb) {
                        curUser.addRedirection("3", htmlUrl);
                        System.out.println("current link in html node " + ": " + curLink);
                        isSetFb = true;
                    }
                    else if(curLink.contains(cp) && !isSetCP) {
                        curUser.addRedirection("4", htmlUrl);
                        System.out.println("current link in html node " + ": " + curLink);
                        isSetCP = true;
                    }
                }
                return "GoogleParseDone";
            }
        };
    }

}
