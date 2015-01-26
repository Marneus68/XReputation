package controllers;

import play.*;
import play.api.libs.json.Json;
import play.data.Form;
import play.mvc.*;


import services.persons.XUser;
import views.html.*;

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
        return ok(play.libs.Json.parse("{\"Input\": \""+result+"\"}"));
    }


}
