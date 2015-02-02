
package xml;

import org.xml.sax.*;
import javax.xml.parsers.*;

public class SecureSAXParserFactory extends SAXParserFactory {
    private final SAXParserFactory platformDefault = new com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl();


    public SecureSAXParserFactory() throws SAXNotSupportedException, SAXNotRecognizedException, ParserConfigurationException {
        platformDefault.setFeature("http://xml.org/sax/features/external-general-entities", true);
        platformDefault.setFeature("http://xml.org/sax/features/external-parameter-entities", true);
        platformDefault.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false);
        platformDefault.setFeature("http://apache.org/xml/features/allow-java-encodings", true);

        platformDefault.setValidating(false);
        platformDefault.setNamespaceAware(false);
    }


    public SAXParser newSAXParser() throws ParserConfigurationException, SAXException {
        return platformDefault.newSAXParser();
    }

    public void setFeature(String name, boolean value) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
        platformDefault.setFeature(name, value);
    }

    public boolean getFeature(String name) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
        return platformDefault.getFeature(name);
    }
}
