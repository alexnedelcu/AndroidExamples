package com.example.androidexamples;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

public class ExampleListActivity extends Activity {

	private List<Map<String, String>> groups = new ArrayList<Map<String, String>>();
	private List<List<Map<String, String>>> children = new ArrayList<List<Map<String, String>>>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.example_list);
		
		// Adding group names
		final String[] groupNames = new String [] {
				"GUI", 
				"Camera",
				"Data exchange"
				}; 
		String[] groupDescriptions = new String [] {
				"View class - text, buttons, lists",
				"Taking pictures and videos",
				"Sending, receiving and streaming data"
				};
		final Map<String, ArrayList<String[]>> childData = new HashMap<String, ArrayList<String[]>> ();
		
		// Adding children under GUI
		childData.put("GUI", new ArrayList<String[]>());
		childData.get("GUI").add(new String[] {
				"Button and Intents",
				"Transferring data between screens",
				"com.example.androidexamples.MainActivity"
			});
		childData.get("GUI").add(new String[] {
				"ExpandableListView",
				"Manipulating ExpandableListViews data",
				"com.example.androidexamples.ExampleListActivity"
			});
		

		// Adding children under Camera
		childData.put("Camera", new ArrayList<String[]>());
		childData.get("Camera").add(new String[] {
				"Pictures",
				"Taking and storing pictures",
				"com.example.androidexamples.HardwareCameraActivity"
			});
		
		// Adding children under Data exchange
		childData.put("Data exchange", new ArrayList<String[]>());
		childData.get("Data exchange").add(new String[] {
				"Send file",
				"Send MPEG4 file through HTTP PUT",
				"com.example.androidexamples.DataExchangeSendHTTPPUT"
			});
		
		// parsing data from the input above to the one needed for adapter
		for (int i=0; i< groupNames.length ; i++) {
			Map<String, String> group = new HashMap <String, String>();
			group.put("TITLE", groupNames[i]);
			group.put("DESC", groupDescriptions[i]);
			
			ArrayList< Map<String, String> > childList  = new ArrayList< Map<String, String> >();
			for (int j=0; j<childData.get(groupNames[i]).size(); j++) {
				Map<String, String> child = new HashMap <String, String>();
				child.put("TITLE", childData.get(groupNames[i]).get(j)[0]);
				child.put("DESC", childData.get(groupNames[i]).get(j)[1]);

				
				childList.add(child);
			}
			groups.add(group);
			children.add(childList);
		}
		

		ExpandableListAdapter listAdapter = new SimpleExpandableListAdapter(
				this,
				groups,
				android.R.layout.simple_expandable_list_item_1, // shows title only
				new String[] {"TITLE", "DESC"},
                new int[] { android.R.id.text1, android.R.id.text2 },
                children, 
				android.R.layout.simple_expandable_list_item_2, // shows title and description under
				new String[] {"TITLE", "DESC"},
                new int[] { android.R.id.text1, android.R.id.text2 }
			); // plugging in data into the adapter
		
		
		ExpandableListView listExample = (ExpandableListView) findViewById(R.id.list_examples);
		listExample.setAdapter(listAdapter); // links the adapter to the ExpandableListView
		
		// implementing click listeners on children items
		listExample.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
				
				try {
					// change the screen to the new activity
					String activityName = childData.get(groupNames[groupPosition]).get(childPosition)[2];
					Intent intent;
					
					intent = new Intent(getApplicationContext(), Class.forName(activityName));
					startActivity(intent);
				} catch (ClassNotFoundException e2) {

					// showing error dialog if the class does not exist
					Util.showAlert(ExampleListActivity.this, R.string.dialog_message, R.string.dialog_title);
					e2.printStackTrace();
				}

				return false;
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.example_list, menu);
		

		return true;
	}

}
