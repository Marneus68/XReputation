package services.crawlers;
import java.lang.String;
import java.util.Arrays;
import java.util.List;

import net.sf.saxon.lib.NamespaceConstant;
import net.sf.saxon.sxpath.XPathEvaluator;
import org.apache.stanbol.enhancer.engines.htmlextractor.impl.DOMBuilder;
import org.jsoup.Jsoup;
import org.w3c.dom.Node;
import play.libs.F;
import play.libs.XPath;
import play.libs.ws.*;
import services.persons.XUser;
import org.w3c.dom.NodeList;

import javax.xml.xpath.*;

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
        String userTwitterLink  = curUser.twitter.substring(1, curUser.twitter.length() - 1);
        String xpathQuery =  "//a[matches(@href,'^/[A-Z]')]";
        System.setProperty("javax.xml.xpath.XPathFactory:"+ NamespaceConstant.OBJECT_MODEL_SAXON, "net.sf.saxon.xpath.XPathFactoryImpl");
        XPathFactory xpf = null;
        XPathExpression expr = null;
        Object result = null;
        try {
            xpf = XPathFactory.newInstance(NamespaceConstant.OBJECT_MODEL_SAXON);
            javax.xml.xpath.XPath xpath = xpf.newXPath();
            expr = xpath.compile(xpathQuery);
            result = expr.evaluate( DOMBuilder.jsoup2DOM(html), XPathConstants.NODESET);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        NodeList nodes = (NodeList) result;
        NodeList elements   = (NodeList) result;
        for(int i =0; i < elements.getLength(); i++){
            Node aTag = elements.item(i);
            String curLink = ((org.w3c.dom.Element)aTag).getAttribute("href");
            //remove duplicates in front api or try here
            //System.out.println("twitter href "+curLink);
            curUser.addRedirection("1", curLink);
            i++;
        }
    }
}
