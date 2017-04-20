package org.zstack.pubCloud;

import java.util.ArrayList;
import java.util.List;

import org.zstack.header.vm.ECSNode;
import org.zstack.utils.gson.JSONList;

public class testA {
	 
     public static void main(String args[]){	 
    	 String data = "{\"nodes\": [{\"node\": null, \"uuid\": \"3e5ce7d81a9dc3b0d3417a53bbeab5d7afd810b2\", \"driver\": null, \"state\": \"running\", \"name\": \"ds\", \"public_ips\": [], \"configuration\": null, \"private_ips\": [\"10.27.220.106\"]}, {\"node\": null, \"uuid\": \"b88dd32395bbd72eee9472fad0b91765ba9eabbc\", \"driver\": null, \"state\": \"running\", \"name\": \"ds\", \"public_ips\": [], \"configuration\": null, \"private_ips\": [\"10.28.57.39\"]}, {\"node\": null, \"uuid\": \"58adb2cedbe17f311e03d42cc0be67ff462a95d2\", \"driver\": null, \"state\": \"running\", \"name\": \"dsa\", \"public_ips\": [], \"configuration\": null, \"private_ips\": [\"10.26.25.126\"]}], \"success\": true, \"error\": \"\"}";
    	 	JSONList<ECSNode> re = JSONList.fromJson(data,ECSNode.class);
    	 	List<ECSNode> vms = new ArrayList<ECSNode>();
    	 	for(ECSNode node : vms){
    	 		System.out.println(node.getState());
    	 	}
     }
	
 	
	 
}

