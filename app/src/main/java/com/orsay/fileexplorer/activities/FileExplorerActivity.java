package com.orsay.fileexplorer.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.orsay.fileexplorer.R;
import com.orsay.fileexplorer.tools.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


public class FileExplorerActivity extends Activity {
    private final static String ROOT_DIRECTORY = "/";

    private static HashMap<String, Integer> mostUsedFiles;
    private static HashMap<String, Integer> mostRecentFiles;
    private static ArrayList<String> favorites;

    public final static int LIMIT = 3;
    public final static String PATH_MOST_USED_FILES = "most_used_files";
    public final static String PATH_MOST_RECENT_FILES = "most_recent_files";
    public final static String PATH_FAVORITES = "favorites";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_explorer);

        // We create the two HashMaps and the ArrayList
        createHashMapsAndArrayList();

        // Get the current directory
        File currentDirectory = getCurrentDirectory();

        // Get all sub files
        File [] arrayFiles = currentDirectory.listFiles();

        // Current directory is not empty
        if (arrayFiles != null) {
            // Folder is not empty
            ((TextView) findViewById(R.id.textViewEmptyFolder)).setText("");

            // Create the list view
            Utils.createListViewFiles(this, arrayFiles, (ListView) findViewById(R.id.listViewFiles),
                                      (TextView) findViewById(R.id.textViewEmptyFolder),
                                      mostUsedFiles, mostRecentFiles, favorites);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_file_explorer_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.most_used_files:
                startActivityDepFiles(getResources().getString(R.string.title_most_used_files));
                return true;

            case R.id.most_common_files:
                startActivityDepFiles(getResources().getString(R.string.title_most_recent_files));
                return true;

            case R.id.favorite_files:
                startActivity(new Intent(this, FavoritesActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Returns the current directory.
     * @return Returns the current
     */
    private File getCurrentDirectory() {
        // Get the current directory
        File currentDirectory = (File) getIntent().getSerializableExtra(getResources().getResourceName(R.string.directory));

        /*
            The current directory is null when we open the application.
            So the current directory is the root directory
        */
        return currentDirectory == null ? new File(ROOT_DIRECTORY) : currentDirectory;
    }

    /**
     * Start the DepFiles activity
     * @param title
     */
    private void startActivityDepFiles(String title) {
        Intent intent = new Intent(this, DepFilesActivity.class);
        intent.putExtra(getResources().getString(R.string.title), title);
        startActivity(intent);
    }

    /**
     * Create the HashMaps and the array list
     */
    private void createHashMapsAndArrayList() {
        // We have to load the three HashMaps if they have been saved
        File tmpFile;

        // Most used files
        tmpFile = getFileStreamPath(PATH_MOST_USED_FILES);
        if (tmpFile.exists()) {
            mostUsedFiles = (HashMap<String, Integer>) Utils.loadObject(this, PATH_MOST_USED_FILES);
        }
        else {
            mostUsedFiles = new HashMap<String, Integer>();
        }

        // Most recent files
        tmpFile = getFileStreamPath(PATH_MOST_RECENT_FILES);
        if (tmpFile.exists()) {
            mostRecentFiles = (HashMap<String, Integer>) Utils.loadObject(this, PATH_MOST_RECENT_FILES);
        }
        else {
            mostRecentFiles = new HashMap<String, Integer>();
        }

        // Favorites
        tmpFile = getFileStreamPath(PATH_FAVORITES);
        if (tmpFile.exists()) {
            favorites = (ArrayList<String>) Utils.loadObject(this, PATH_FAVORITES);
        }
        else {
            favorites = new ArrayList<String>();
        }
    }
}
