package com.orsay.fileexplorer.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.orsay.fileexplorer.R;
import com.orsay.fileexplorer.tools.Utils;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class DepFilesActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dep_files);

        // Get the title
        setTitle((String) getIntent().getSerializableExtra(getResources().getString(R.string.title)));

        HashMap<String, Integer> depFiles;
        // Get the HashMap for most used files
        if (getTitle().equals(getResources().getString(R.string.title_most_used_files))) {
            depFiles = (HashMap<String, Integer>) Utils.loadObject(this, FileExplorerActivity.PATH_MOST_USED_FILES);
        }
        // Get the HashMap for most recent files
        else {
            depFiles = (HashMap<String, Integer>) Utils.loadObject(this, FileExplorerActivity.PATH_MOST_RECENT_FILES);
        }

        if (depFiles != null) {
            // HashMap not empty
            if (!depFiles.isEmpty()) {
                // Sort bye descending values
                depFiles = Utils.sortByDescendingValues(depFiles);

                // Create an array of files with the good size
                int size = depFiles.size();
                File[] arrayFiles = size < FileExplorerActivity.LIMIT ? new File[size]
                        : new File[FileExplorerActivity.LIMIT];

            /*
             Transform the HashMap to a classic array to create the list view with the latter.
            */
                Set<String> keys = depFiles.keySet();
                Iterator<String> it = keys.iterator();
                int index = 0;
                while (it.hasNext() && index < arrayFiles.length) {
                    arrayFiles[index++] = new File((String) it.next());
                }

                // Create the list view
                Utils.createListViewFiles(this, arrayFiles, (ListView) findViewById(R.id.listViewDepFiles),
                        (TextView) findViewById(R.id.textViewNoDepFiles),
                        null, null, null);
            }
        }
    }
}
