package com.orsay.fileexplorer.tools;


import android.app.Activity;
import android.content.Context;
import android.widget.ListView;
import android.widget.TextView;

import com.orsay.fileexplorer.engine.FilesAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Utils {
    /**
     * Sort an HashMap by descending values.
     * @param map
     * @return
     */
    public static HashMap sortByDescendingValues(HashMap map) {
        List list = new LinkedList(map.entrySet());
        // Defined Custom Comparator here
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
            }
        });

        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }

     /**
     * Create the list view files to display the files in
     * the application.
     *
     * @param activity
     * @param arrayFiles
     * @param listView
     * @param mostUsedFiles
     * @param mostRecentFiles
     * @param favorites
     */
    public static void createListViewFiles(Activity activity, File [] arrayFiles, ListView listView, TextView textView,
                                           HashMap<String, Integer> mostUsedFiles, HashMap<String, Integer> mostRecentFiles,
                                           ArrayList<String> favorites) {
        // Empty folder
        textView.setText("");

        // Sort the list files by alphabetical order
        List listFiles = Arrays.asList(arrayFiles);
        Collections.sort(listFiles);

        // Add this list to an adapter
        ArrayList<File> files = new ArrayList<File>();
        files.addAll(listFiles);
        FilesAdapter adapter = new FilesAdapter(activity, files, mostUsedFiles,
                                                mostRecentFiles, favorites);

        // Creation of the list view
        ListView listViewFiles = listView;
        listViewFiles.setAdapter(adapter);
    }

    /**
     * Serialize an object
     * @param context
     * @param path
     * @param object
     */
    public static void saveObject(Context context, String path, Object object) {
        try {
            FileOutputStream fos = context.openFileOutput(path , Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(object);
            os.close();
        }
        catch (Exception e) {}
    }

    /**
     * Deserialize an object
     * @param context
     * @param path
     * @return
     */
    public static Object loadObject(Context context, String path) {
        try {
            FileInputStream fis = context.openFileInput(path);
            ObjectInputStream is = new ObjectInputStream(fis);
            Object object = is.readObject();
            is.close();

            return object;
        }
        catch (Exception e) {
            return null;
        }
    }
}
