package services.xmlutilities;

/**
 * Created by madalien on 13/02/15.
 */
import javax.xml.xpath.*;
import net.sf.saxon.lib.NamespaceConstant;
public  class Xpath20 {
    public static  XPathFactory xpf;
    public static void init(){
        System.setProperty("javax.xml.xpath.XPathFactory:"+ NamespaceConstant.OBJECT_MODEL_SAXON, "net.sf.saxon.xpath.XPathFactoryImpl");
        try {
            xpf = XPathFactory.newInstance(NamespaceConstant.OBJECT_MODEL_SAXON);
        } catch (XPathFactoryConfigurationException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

}
