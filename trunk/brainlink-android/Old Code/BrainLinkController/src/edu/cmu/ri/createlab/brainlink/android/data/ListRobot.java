package edu.cmu.ri.createlab.brainlink.android.data;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


import org.xml.sax.Attributes;

import android.app.Activity;
import android.content.res.AssetManager;
import android.sax.Element;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Xml;

public class ListRobot {

	public static final String KEY_ROBOTLIST_NAME = "name";
	public static final String KEY_ROBOTLIST_DESCRIPTION = "description";
	public static final String KEY_ROBOTLIST_ICON = "icon";
	public static final String KEY_ROBOTLIST_BRAINLINK = "brainlink";
	
	public static final String[] FIELDS = new String[] {
		KEY_ROBOTLIST_NAME, KEY_ROBOTLIST_DESCRIPTION, KEY_ROBOTLIST_ICON, KEY_ROBOTLIST_BRAINLINK
		};
	
	
	public static List<ListRobotItem>loadList(Activity act) {
		final List<ListRobotItem> robotlist = new ArrayList<ListRobotItem>();
		
		try {
			final AssetManager am = act.getAssets();
			InputStreamReader reader = new InputStreamReader(am.open("Robots.xml"));
			
			RootElement root = new RootElement("robots");
			Element element = root.getChild("item");
			element.setStartElementListener(new StartElementListener() {
				
				@Override
				public void start(Attributes attributes) {
					ListRobotItem item = new ListRobotItem();
					robotlist.add(item);
					
				}
			});
			for(final String field : FIELDS) {
				element.getChild(field).setEndTextElementListener(new EndTextElementListener() {
					
					@Override
					public void end(String body) {
						robotlist.get(robotlist.size()-1).putString(field, body);
					}
				});
			}
			Xml.parse(reader, root.getContentHandler());
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		return robotlist;
	}
	

}
