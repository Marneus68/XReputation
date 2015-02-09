package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import play.data.Form;
import play.libs.F;
import play.mvc.*;
import play.libs.ws.WS;
import services.crawlers.GoogleSearcher;
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
        //google async parser
        F.Promise<JsonNode> googlePromisePageOne = WS.url("http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=" +  encode_query + "&rsz=8&start=1")
                .get()
                .map(GoogleSearcher.SearchOnGoogle(userInfo));
        F.Promise<JsonNode> googlePromisePageTwo = WS.url("http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=" +  encode_query + "&rsz=8&start=5")
                .get()
                .map(GoogleSearcher.SearchOnGoogle(userInfo));
        //facebook async parser incoming

        //list of parsers promises
        return F.Promise.sequence(googlePromisePageOne, googlePromisePageTwo).flatMap(new F.Function<List<JsonNode>, F.Promise<Result>>() {
            @Override
            public F.Promise<Result> apply(List<JsonNode> jsonNodes) throws Throwable {
                List<String> urls = jsonNodes.get(0).findValuesAsText("url");
                urls.addAll(jsonNodes.get(1).findValuesAsText("url"));
                int[] i = {0};
                List<F.Promise<JsonNode>> listOfPromise = new ArrayList<F.Promise<JsonNode>>();
                urls.forEach((url) -> {
                    listOfPromise.add(WS.url(url).get().map(GoogleSearcher.parseHtml(url, userInfo, jsonNodes.get(0))));
                    i[0]++;
                });
                return F.Promise.sequence(listOfPromise).map(new F.Function<List<JsonNode>, Result>() {
                    @Override
                    public Result apply(List<JsonNode> jsonNodes) throws Throwable {
                        return ok(play.libs.Json.parse(userInfo.jsonUser()));
                    }
                });
            }
        });
    }


}
