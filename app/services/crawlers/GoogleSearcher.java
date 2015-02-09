package services.crawlers;
import java.lang.Iterable;
import java.lang.String;

import akka.dispatch.Mapper;
import akka.dispatch.OnSuccess;
import com.fasterxml.jackson.databind.JsonNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import play.libs.F;
import play.libs.ws.*;
import services.persons.XUser;


/**
 * Created by madalien on 31/01/15.
 */
public class GoogleSearcher {

    /**
     * Later implement async version
     */

    public static  F.Function<WSResponse, JsonNode> SearchOnGoogle(XUser curUser){
        return new F.Function<WSResponse, JsonNode>(){
            @Override
            public JsonNode apply(WSResponse response) throws Throwable {
                return response.asJson();
            }
        };
    }

    public static F.Function<WSResponse, JsonNode> parseHtml(String htmlUrl, XUser curUser, JsonNode nodeIn){
        return new F.Function<WSResponse, JsonNode>(){
            @Override
            public JsonNode apply(WSResponse response) throws Throwable {
                org.jsoup.nodes.Document html = Jsoup.parse(response.getBody());
                String fb = curUser.facebook;
                String tw = curUser.twitter;
                String lk = curUser.linkedin;
                boolean isSetFb = false;
                boolean isSetTw = false;
                boolean isSetLk = false;
                //Xpath request "//a[contains(@href,'"+fb+"') or contains(@href,'"+tw+"') or contains(@href,'"+lk+"')]";
                Elements all= html.select("a[href*="+fb+"], [href*="+tw+"], [href*="+lk+"]");
                for(Element aTag : all){
                    String curLink = aTag.attr("href");
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
                curUser.saveDomToXml();
                return nodeIn;
            }
        };
    }

}
