package com.ernesto.caesiumtest;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.ernesto.libcaesium.*;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;

public class CompProcTask extends AsyncTask<Void, Integer, Boolean> {
    private TextView tv;
    private List<String> files;
    private Context ctx;
    private int total;
    private long sumSize = 0, sumCompSize = 0;

    public CompProcTask(TextView tv, List<String> files, Context ctx) {
        this.tv = tv;
        this.files = files;
        this.ctx = ctx;
    }

    public String getNewFilePath(String oldPath) {
        File file = new File(oldPath);
        String filePath = file.getParent();
        String fileName = file.getName();
        int index = fileName.lastIndexOf('.');
        String name = fileName.substring(0, index);
        String extension = fileName.substring(index);
        String newFilePath = filePath + File.separator + name + "_comp" + extension;
        return newFilePath;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        int total = files.size();
        this.total = total;
        boolean failed = false;
        for (int i = 0; i < total; i++) {
            String file = files.get(i);
            try {
                File f = new File(file);
                if(f.exists()) sumSize += f.length();
                FastImageInfo fii = new FastImageInfo(f);
                int width = fii.getWidth();
                int height = fii.getHeight();
                CCSParameter csp = new CCSParameter(true, 90, 90, false,
                        90, 90, true, width, height);
                String nuevoPath = getNewFilePath(file);
                if(!CaesiumNative.compressPic(file, nuevoPath, csp)) {
                    if(!failed) failed = true;
                } else {
                    File f0 = new File(nuevoPath);
                    if(f0.exists()) sumCompSize += f0.length();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // process file here
            publishProgress((int) (((i+1) / (float) total) * 100));
        }
        return failed ? Boolean.FALSE : Boolean.TRUE;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        double d = (double) sumCompSize / (double) sumSize * 100.f;
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        String s = nf.format(d);
        new AlertDialog.Builder(ctx)
                .setTitle(result ? "Operation done" : "Operation failed")
                .setMessage(result ? ("Compressed " + this.total + " pictures successfully!\n" + "Average compression rate: " + s + "%") :
                        "Failed to compress one or more pictures, please check log for details")
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        tv.setText("Processing files... Progress: " + values[0] + "%");
    }
}
