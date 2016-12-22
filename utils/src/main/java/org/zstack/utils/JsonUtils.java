package org.zstack.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Created by xing5 on 2016/5/18.
 */
public class JsonUtils {

	public static List<PubCloud> getPubCloudConf(String fileName){
		DocumentBuilder builder;
		Document doc = null;
		List<PubCloud>  pubClouds = new ArrayList<PubCloud>();
		
		pubClouds = new ArrayList<PubCloud>();
		try {
			File xmlFile = new File(fileName);
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			builder = builderFactory.newDocumentBuilder();
			doc = builder.parse(xmlFile);
			doc.getDocumentElement().normalize();
		} catch (SAXException | IOException e1) {
			e1.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		NodeList nList = doc.getElementsByTagName("pubCloud");
		for (int i = 0; i < nList.getLength(); i++) {
			Node node = nList.item(i);
			Element ele = (Element) node;
			
			PubCloud tmpCloud = new PubCloud();
			tmpCloud.setName(ele.getElementsByTagName("name").item(0).getTextContent());
			
			NodeList invernts = ele.getElementsByTagName("InstanceModel");
			
			
			
			List<InstanceMode> instanceMDs = new ArrayList<InstanceMode>();
			if(invernts.getLength()!=0){
				for(int j = 0;j<invernts.getLength();j++){
					Node temnode = invernts.item(i);
					Element temele = (Element) temnode;
					InstanceMode temMode = new InstanceMode();
					temMode.setCpuNum(temele.getElementsByTagName("cpuNum").item(0).getTextContent());
					temMode.setDiskSize(temele.getElementsByTagName("image").item(0).getTextContent() );
					temMode.setImage(temele.getElementsByTagName("diskSize").item(0).getTextContent());
					instanceMDs.add(temMode);
				}
				tmpCloud.setInstanceMD(instanceMDs);
			}
			

			NodeList accountInfos = ele.getElementsByTagName("value");
			List<String> infos = new ArrayList<String>();
			if(accountInfos.getLength()!=0){
				for(int j = 0;j<accountInfos.getLength();j++){
					infos.add( accountInfos.item(j).getTextContent());
				}
				tmpCloud.setAccountInfo(infos);
			}
			pubClouds.add(tmpCloud);	 
		}
		return pubClouds;
		
	}
}
