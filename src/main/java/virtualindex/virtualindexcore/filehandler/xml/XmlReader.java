package virtualindex.virtualindexcore.filehandler.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlReader {
	
	String path = "";
	
	public XmlReader(String path)
	{
		this.path =path;
	}
	
	public String getNodeContent(String nodeName) throws ParserConfigurationException, SAXException, IOException
	{
		File fXmlFile = new File(path);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName("artifactId");		
		Node nNode = nList.item(0);	 	
		return ((NodeList) nNode).item(0).getTextContent();

		
	}

}
