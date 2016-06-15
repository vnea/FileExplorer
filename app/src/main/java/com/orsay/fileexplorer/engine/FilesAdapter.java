package com.orsay.fileexplorer.engine;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.orsay.fileexplorer.R;
import com.orsay.fileexplorer.activities.FileExplorerActivity;
import com.orsay.fileexplorer.tools.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class FilesAdapter extends ArrayAdapter<File> {
    // View lookup cache
    private static class ViewHolder {
        ImageView imageViewFile;
        TextView textViewFileName;
    }

    private Context context;
    private HashMap<String, Integer> mostUsedFiles;
    private HashMap<String, Integer> mostRecentFiles;
    private ArrayList<String> favorites;

    public FilesAdapter(Context context, ArrayList<File> files, HashMap<String, Integer> mostUsedFiles,
                        HashMap<String, Integer> mostRecentFiles, ArrayList<String> favorites) {
        super(context, R.layout.item_file, files);
        this.context = context;

        this.mostUsedFiles = mostUsedFiles;
        this.mostRecentFiles = mostRecentFiles;
        this.favorites = favorites;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        File currentFile = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_file, parent, false);
            viewHolder.imageViewFile = (ImageView) convertView.findViewById(R.id.imageViewFile);
            viewHolder.textViewFileName = (TextView) convertView.findViewById(R.id.textViewFileName);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        /*
            Update background color of each item.
            Favorite = yellow
            No favorite = white
            We need to do this because when we scroll the listview,
            background color of each item is not correct when we add
            a file into favorites.
        */
        if (favorites != null) {
            if (favorites.contains(currentFile.getAbsolutePath())) {
                convertView.setBackgroundColor(Color.YELLOW);
            }
            else {
                convertView.setBackgroundColor(Color.WHITE);
            }
        }


        // Directory
        if (currentFile.isDirectory()) {
            manageDirectory(viewHolder.imageViewFile, convertView, currentFile);
        }
        // Classic file
        else {
            manageFile(viewHolder.imageViewFile, convertView, currentFile);
        }

        // Set the name of the file
        viewHolder.textViewFileName.setText(currentFile.getName());

        // Return the completed view to render on screen
        return convertView;
    }

    /**
     * Set the directory image and the OnClickListener.
     * @param imageViewFile
     * @param convertView
     * @param currentFile
     */
    private void manageDirectory(ImageView imageViewFile, View convertView, final File currentFile) {
        // Set directory image
        imageViewFile.setBackgroundResource(R.drawable.directory);

        // Open directory
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FileExplorerActivity.class);
                intent.putExtra(context.getResources().getResourceName(R.string.directory), currentFile);
                context.startActivity(intent);
            }
        });
    }

    /**
     * Set the file image and the OnClickListener.
     * @param imageViewFile
     * @param convertView
     * @param currentFile
     */
    private void manageFile(ImageView imageViewFile, View convertView, final File currentFile) {
        final String fileAbsolutePath = currentFile.getAbsolutePath();

        // Can be null when this class was instantiate by DepFilesActivity
        if (mostRecentFiles != null) {
            // If the file already exist in the HashMap, we just increment the counter
            if (mostRecentFiles.containsKey(fileAbsolutePath)) {
                mostRecentFiles.put(fileAbsolutePath, mostRecentFiles.get(fileAbsolutePath) + new Integer(1));
            }
            // Else we add it in the HashMap
            else {
                mostRecentFiles.put(fileAbsolutePath, new Integer(1));
            }

            // Save the hashMap
            Utils.saveObject(context, FileExplorerActivity.PATH_MOST_RECENT_FILES, mostRecentFiles);
        }

        // Set file image
        imageViewFile.setBackgroundResource(R.drawable.file);

        // Open file
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Can be null when this class was instantiate by DepFilesActivity
                if (mostUsedFiles != null) {
                    // If the file already exist in the HashMap, we just increment the counter
                    if (mostUsedFiles.containsKey(fileAbsolutePath)) {
                        mostUsedFiles.put(fileAbsolutePath, mostUsedFiles.get(fileAbsolutePath) + 1);
                    }
                    // Else we add it in the HashMap
                    else {
                        mostUsedFiles.put(fileAbsolutePath, new Integer(1));
                    }

                    // Save the hashMap
                    Utils.saveObject(context, FileExplorerActivity.PATH_MOST_USED_FILES, mostUsedFiles);
                }

                openFileBasedOnExtension(currentFile);
            }
        });

        // Add into favorites
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // Can be null when this class was instantiate by DepFilesActivity
                if (favorites != null) {
                    // If the file is not is the favorites
                    if (!favorites.contains(fileAbsolutePath)) {
                        // Add to favorites if the limit is not reached
                        if (favorites.size() < FileExplorerActivity.LIMIT) {
                            favorites.add(fileAbsolutePath);
                            view.setBackgroundColor(Color.YELLOW);
                        }
                        // Alert the user
                        else {
                            alertFavoritesListIsFull();

                        }
                    }
                    // Remove from favorites
                    else {
                        favorites.remove(fileAbsolutePath);
                        view.setBackgroundColor(Color.WHITE);
                    }

                    Utils.saveObject(context, FileExplorerActivity.PATH_FAVORITES, favorites);
                }

                return true;
            }
        });
    }

    /**
     * Open the file with an application
     * @param currentFile
     */
    private void openFileBasedOnExtension(File currentFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String fileName = currentFile.toString().toLowerCase();
        Uri uri = Uri.fromFile(currentFile);

        // Word
        if (fileName.endsWith(".doc") || fileName.endsWith(".docx")) {
            intent.setDataAndType(uri, "application/msword");
        }
        // PDF
        else if(fileName.endsWith(".pdf")) {
            intent.setDataAndType(uri, "application/pdf");
        }
        // Powerpoint
        else if(fileName.endsWith(".ppt") || fileName.endsWith(".pptx")) {
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        }
        // Excel
        else if(fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        }
        // ZIP/RAR
        else if(fileName.endsWith(".zip") || fileName.endsWith(".rar"))  {
            intent.setDataAndType(uri, "application/zip");
        }
        // RTF
        else if(fileName.endsWith(".rtf")) {
            intent.setDataAndType(uri, "application/rtf");
        }
        // Audio
        else if(fileName.endsWith(".wav") || fileName.endsWith(".mp3")) {
            intent.setDataAndType(uri, "audio/x-wav");
        }
        // GIF
        else if(fileName.endsWith(".gif")) {
            intent.setDataAndType(uri, "image/gif");
        }
        // JPG
        else if(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png")) {
            intent.setDataAndType(uri, "image/jpeg");
        }
        // Text
        else if(fileName.endsWith(".txt")) {
            intent.setDataAndType(uri, "text/plain");
        }
        // Video
        else if(fileName.endsWith(".3gp") || fileName.endsWith(".mpg") || fileName.endsWith(".mpeg") || fileName.endsWith(".mpe") || fileName.endsWith(".mp4") || fileName.endsWith(".avi")) {
            intent.setDataAndType(uri, "video/*");
        }
        // Other
        else {
            intent.setDataAndType(uri, "*/*");
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Alert the user that favorites list is full
     */
    private void alertFavoritesListIsFull() {
        new AlertDialog.Builder(context)
                .setMessage(R.string.favorites_list_full)
                .setTitle(R.string.error)
                .setCancelable(true)
                .setNeutralButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton){}
                        })
                .show();
    }
}
