package services.crawlers;
import java.lang.String;
import java.util.Arrays;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import play.libs.F;
import play.libs.ws.*;
import services.persons.XUser;
/**
 * Created by madalien on 11/02/15.
 */
public class TwitterSearcher {
    public static  F.Function<WSResponse, List<String>> SearchOnTwitter(){
        return new F.Function<WSResponse, List<String>>(){
            @Override
            public List<String> apply(WSResponse response) throws Throwable {

                return Arrays.asList( Jsoup.parse(response.getBody()).html());
            }
        };
    }

    public static void parsetwitterHtml(String htmlUrl, XUser curUser){
        org.jsoup.nodes.Document html = Jsoup.parse(htmlUrl);
        //Xpath request "//a[contains(@href,'"+fb+"') or contains(@href,'"+tw+"') or contains(@href,'"+lk+"')]";
        String userTwitterLink  = curUser.twitter.substring(1, curUser.twitter.length()-1);
        Elements all= html.select("a[href^=/]"); //a[href*="+fb+"],
        int i = 0;
        for(Element aTag : all){
            //remove duplicates in front api or try here
            if(i > 100)
                curUser.addRedirection("1", aTag.attr("href"));
            else if(i > 200)
             break;
            i++;
        }
    }
}
