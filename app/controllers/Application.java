package controllers;

import play.*;
import play.api.libs.json.Json;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render("use a bitch"));
    }

    public static Result search() {
        return ok(play.libs.Json.parse("{\"toto\": \"not implemented yet\"}"));
    }
}
