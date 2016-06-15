package com.orsay.fileexplorer.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.orsay.fileexplorer.R;
import com.orsay.fileexplorer.tools.Utils;

import java.io.File;
import java.util.ArrayList;

public class FavoritesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // Get the favorites
        ArrayList<String> favorites = (ArrayList<String>) Utils.loadObject(this, FileExplorerActivity.PATH_FAVORITES);

        if (favorites != null) {
            // Some favorites exist
            if (!favorites.isEmpty()) {
                // Create an array of files with the good size
                int size = favorites.size();
                File[] arrayFiles = size < FileExplorerActivity.LIMIT ? new File[size]
                        : new File[FileExplorerActivity.LIMIT];

                // Create the files
                for (int i = 0; i < arrayFiles.length; ++i) {
                    arrayFiles[i] = new File(favorites.get(i));
                }

                // Create the list view
                Utils.createListViewFiles(this, arrayFiles, (ListView) findViewById(R.id.listViewFavorites),
                        (TextView) findViewById(R.id.textViewNoFavorites),
                        null, null, null);
            }
        }
    }
}
