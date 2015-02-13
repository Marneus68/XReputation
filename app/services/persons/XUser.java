package services.persons;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import play.api.libs.json.JsArray;
import play.api.libs.json.JsValue;
import play.api.libs.json.Json;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created by madalien on 26/01/15.
 */
public class XUser {
    public String firstName;
    public String lastName;
    public String twitter;
    public String facebook;
    public String linkedin;
    private Document xmlTree;
    private String filePath = "public/res/person.xml";
    private DocumentBuilderFactory factory;
    private DocumentBuilder db;
    private Document doc;
    private Element redirects;

    public void xmlToDoc(String filename) throws Exception {
        factory = DocumentBuilderFactory.newInstance();
        db = factory.newDocumentBuilder();
        doc = db.parse(new File(this.filePath));

    }

    public  void createXmlDom() throws ParserConfigurationException {
        factory = DocumentBuilderFactory.newInstance();
        db = factory.newDocumentBuilder();
        doc = db.newDocument();
    }

    public void loadDom(){
        try{
            xmlToDoc(filePath);
        }catch (Exception fileExp){
            System.err.println("file not exist"+ fileExp.getMessage());
        }
    }

    public String jsonUser(){
        StringBuilder jsonString = new StringBuilder("{");
        jsonString.append(" \"firstName\" : \""+ doc.getElementsByTagName("firstName").item(0).getTextContent()+"\",");
        jsonString.append(" \"lastName\" : \""+ doc.getElementsByTagName("lastName").item(0).getTextContent()+"\" ,");
        jsonString.append(" \"links\" :[");
        NodeList userLinks = doc.getElementsByTagName("link");
        int linksArrayLenght = userLinks.getLength();
        for(int i = 0; i < linksArrayLenght; i++ ){
            Node curLink = userLinks.item(i);
            jsonString.append(  "{  \"id\" : \""+((Element) curLink).getAttribute("ID")+"\" ,");
            jsonString.append(  "   \"source\" : \""+((Element) curLink).getAttribute("name")+"\" ,");
            jsonString.append(  "  \"value\" : \""+curLink.getTextContent()+"\" }");
            if(i != linksArrayLenght-1){
                jsonString.append(",");
            }
        }
        jsonString.append("],");

        jsonString.append(" \"redirects\" :[");
        NodeList redirectLinks = redirects.getElementsByTagName("redirect");
        int redirectsArrayLenght = redirectLinks.getLength();
        for(int i = 0; i < redirectsArrayLenght; i++ ){
            Node curLink = redirectLinks.item(i);
            jsonString.append(  "{ \"target\" : \""+((Element) curLink).getAttribute("target")+"\" ,");
            jsonString.append(  "   \"OK\" : \""+((Element) curLink).getAttribute("reputation")+"\" ,");
            jsonString.append(  "   \"id\" : \""+((Element) curLink).getAttribute("target")+"\" ,");
            jsonString.append(  "   \"value\" : \""+curLink.getTextContent()+"\" }");
            if(i != redirectsArrayLenght-1){
                jsonString.append(",");
            }
        }
        jsonString.append("]");

        jsonString.append("}");
        JsValue userJson = Json.parse(jsonString.toString());

        return jsonString.toString();
    }
    public  void saveBasicAttrToDom(){
        Element root = doc.createElement("user");
        Element fName = doc.createElement("firstName");
        fName.setTextContent(firstName);

        Element lName = doc.createElement("lastName");
        lName.setTextContent(lastName);

        Element twitterL = doc.createElement("link");
        twitterL.setTextContent(twitter);
        twitterL.setAttribute("ID", "1");
        twitterL.setAttribute("name", "twitter");

        Element linkedinL = doc.createElement("link");
        linkedinL.setTextContent(linkedin);
        linkedinL.setAttribute("ID", "2");
        linkedinL.setAttribute("name", "linkedin");

        Element facebookL = doc.createElement("link");
        facebookL.setTextContent(facebook);
        facebookL.setAttribute("ID", "3");
        facebookL.setAttribute("name", "facebook");

        root.appendChild(fName);
        root.appendChild(lName);
        root.appendChild(twitterL);
        root.appendChild(linkedinL);
        root.appendChild(facebookL);
        redirects = doc.createElement("redirects");
        root.appendChild(redirects);
        doc.appendChild(root);

    }

    public  void addRedirection(String parentId, String redirectUrl){
        Element redirect = doc.createElement("redirect");
        redirect.setAttribute("target", parentId);
        redirect.setAttribute("reputation", "OK");
        redirect.setTextContent(redirectUrl);
        redirects.appendChild(redirect);
    }

    public void saveDomToXml() {
        System.out.println("saving...");
        File fileStr = new File(filePath);
        Result resultat = new StreamResult(fileStr);
        Source source = new DOMSource(doc);
        Transformer transfo = null;
        try {
            transfo = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException e) {
            System.err.println("cannot create an xml transformer");
            System.exit(1);
        }

        transfo.setOutputProperty(OutputKeys.METHOD, "xml");
        transfo.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transfo.setOutputProperty(OutputKeys.ENCODING, "utf-8");
        transfo.setOutputProperty(OutputKeys.INDENT, "yes");

        System.out.println("transforming");
        try {
            transfo.transform(source, resultat);
        } catch (TransformerException e) {
            System.err.println("Failed to save the files: " + e.getMessage());
            System.out.println("Failed to save the file ex: " + e.getException());
            System.exit(1);
        }
        
    }
}

