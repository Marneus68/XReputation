package services.crawlers;
import java.lang.Iterable;
import java.lang.String;

import akka.dispatch.Mapper;
import akka.dispatch.OnSuccess;
import com.fasterxml.jackson.databind.JsonNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import play.api.libs.json.Json;
import play.libs.F;
import play.libs.XML;
import play.libs.XPath;
import play.libs.ws.WSRequestHolder;
import play.libs.ws.*;
import play.libs.F.Promise;
import scala.Function1;
import scala.collection.immutable.*;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.runtime.BoxedUnit;
import services.persons.XUser;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import static akka.dispatch.Futures.sequence;
//import scala.concurrent.ExecutionContext.Implicits.*;

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




                /*

                java.lang.Iterable<scala.concurrent.Future<Integer>> listOfFutureInts = new ArrayList<scala.concurrent.Future<Integer>>();

                Future<Iterable<Integer>> futureListOfInts = sequence(listOfFutureInts, global);
                Future<Long> futureSum = futureListOfInts.map(
                        new Mapper<Iterable<Integer>, Long>() {
                            public Long apply(Iterable<Integer> ints) {
                                long sum = 0;
                                for (Integer i : ints)
                                    sum += i;
                                return sum;
                            }
                        }, global);

                futureSum.onSuccess(new OnSuccess<Long>() {
                    @Override
                    public void onSuccess(Long result) throws Throwable {
                        System.out.println(futureSum);
                    }
                }, global);*/
            }
        };
    }

    public static F.Function<WSResponse, JsonNode> parseHtml(String htmlUrl, XUser curUser, JsonNode nodeIn){
        /*** Incoming Use ws instead of Jsoup for performance purposes async sequence of arrays
         *  org.jsoup.nodes.Document html = null;
                try {
                    html = Jsoup.connect(htmlUrl).get();
                } catch (IOException e) {
                    System.out.println("Soup error"+ e.getMessage());
                    return (JsonNode)Json.parse("{\"toto\":\"toto\"}");
                }
         * */
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
