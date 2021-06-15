package com.inhatc.mobile_project;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadFilesTask extends  AsyncTask<String,Void, Bitmap>{

    ImageView imgView;

    public DownloadFilesTask(ImageView imgView) {
        this.imgView = imgView;
    }

    public DownloadFilesTask(){

    }

    public ImageView getImgView() {
        return imgView;
    }

    public void setImgView(ImageView imgView) {
        this.imgView = imgView;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        Bitmap bmp = null;
        try {
            String img_url = strings[0]; //url of the image
            URL url = new URL(img_url);
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmp;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected void onPostExecute(Bitmap result) {
        // doInBackground 에서 받아온 total 값 사용 장소
        imgView.setImageBitmap(result);
    }

}
