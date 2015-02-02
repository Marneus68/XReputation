package controllers;

import play.*;
import play.api.libs.json.Json;
import play.data.Form;
import play.mvc.*;

import services.crawlers.GoogleSearcher;
import services.persons.XUser;
import views.html.*;
import xml.SecureSAXParserFactory;


public class Application extends Controller {

    public static Result index() {
        return ok(index.render("use a bitch"));
    }

    public static Result postForm() {
        Form<XUser> currentUser = Form.form(XUser.class).bindFromRequest();
        String firstName = currentUser.get().firstName;
        String lastName = currentUser.get().lastName;
        String twitter = currentUser.get().twitter;
        String facebook = currentUser.get().facebook;
        String linkedin = currentUser.get().linkedin;
        String result = "Fn: "+firstName+"  lN: "+lastName+"  fb: "+facebook+"  tw: "+twitter+"   linkedin: "+linkedin;
        XUser userInfo = currentUser.get();
        try {
            userInfo.createXmlDom();
        }catch(Exception e){
            result += "****exception in user settings****: "+ e.getMessage();
            System.out.println("Failed to save the file: " + e.getCause());
        }

        userInfo.saveBasicAttrToDom();
        //userInfo.saveDomToXml();
        try {
            new GoogleSearcher().SearchOnGoogle("Coulibaly Mamadou", userInfo);
        }catch (Exception e){
            System.out.println("exception"+ e.getMessage());
        }
        return ok(play.libs.Json.parse("{\"Input\": \""+result+"\"}"));
    }


}
