package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import play.data.Form;
import play.libs.F;
import play.mvc.*;
import play.libs.ws.WS;
import services.crawlers.GoogleSearcher;
import services.crawlers.TwitterSearcher;
import services.persons.XUser;
import views.html.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class Application extends Controller {

    public static Result index() {
        return ok(index.render("use a bitch"));
    }

    public static F.Promise<Result> postForm() {
        Form<XUser> currentUser = Form.form(XUser.class).bindFromRequest();
        String encode_query = null;
        XUser userInfo = currentUser.get();
        try {
            userInfo.createXmlDom();
            encode_query = URLEncoder.encode(userInfo.firstName+" "+userInfo.lastName, "UTF-8");
        }catch(Exception e){
            System.out.println("Exception Xml utils or url Encoder: " + e.getCause());
        }
        userInfo.saveBasicAttrToDom();
        List <F.Promise<List<String>>> googleUrlsToParse = new ArrayList<F.Promise<List<String>>>();
        //google async parser
        for(int i = 0; i < 2; i++){
            googleUrlsToParse.add(WS.url("http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=" + encode_query + "&rsz=8&start=" + i)
                    .get()
                    .map(GoogleSearcher.SearchOnGoogle(userInfo)));
        }
        //twitter parser
        List <F.Promise<List<String>>> twitterPageToParse = new ArrayList<F.Promise<List<String>>>();
        if (userInfo.twitter.startsWith("@")) {
            twitterPageToParse.add(WS.url("https://twitter.com/search?q="+URLEncoder.encode(userInfo.twitter))
                    .get()
                    .map(TwitterSearcher.SearchOnTwitter()));
        }
        //list of parsers promises
        List <F.Promise<List<String>>> mainSearchList = new ArrayList<F.Promise<List<String>>>(googleUrlsToParse);
        mainSearchList.addAll(twitterPageToParse);
        return F.Promise.sequence(mainSearchList).flatMap(new F.Function<List<List<String>>, F.Promise<Result>>() {
            @Override
            public F.Promise<Result> apply(List<List<String>> urlsToParse) throws Throwable {
                List<F.Promise<String>> listOfPromise = new ArrayList<F.Promise<String>>();
                // subList to avoid a list copy
                List<List<String>> googleUrlsList = null;
                if (userInfo.twitter.startsWith("@")) {
                    googleUrlsList =  urlsToParse.subList(0, urlsToParse.size()-2);
                }
                else {
                    googleUrlsList =  urlsToParse;
                }
                //google Parsing
                for( List<String> urls: googleUrlsList) {
                    urls.forEach((url) -> {
                        listOfPromise.add(WS.url(url).get().map(GoogleSearcher.parseHtml(url, userInfo)));
                    });
                }

                return F.Promise.sequence(listOfPromise).map(new F.Function<List<String>, Result>() {
                    @Override
                    public Result apply(List<String> parsersStatus) throws Throwable {
                        System.out.println("at end");
                        if (userInfo.twitter.startsWith("@")) {
                            TwitterSearcher.parsetwitterHtml(urlsToParse.get(urlsToParse.size() - 1).get(0), userInfo);
                        }
                        userInfo.saveDomToXml();
                        return ok(play.libs.Json.parse(userInfo.jsonUser()));
                    }
                });
            }
        });

    }


}
