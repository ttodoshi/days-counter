package org.todoshis.dayscounter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

class ShowMessage {
    public static void showMessage(Context context, String text){
        AsyncShow as = new AsyncShow(context, text);
        as.execute();
    }

    private static class AsyncShow extends AsyncTask {
        @SuppressLint("StaticFieldLeak")
        Context context;
        String text;
        AsyncShow (Context context, String text){
            this.context = context;
            this.text = text;
        }
        @Override
        protected Void doInBackground(Object[] objects) {
            Handler handler =  new Handler(context.getMainLooper());
            handler.post( new Runnable(){
                public void run(){
                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                }
            });
            return null;
        }
    }
}