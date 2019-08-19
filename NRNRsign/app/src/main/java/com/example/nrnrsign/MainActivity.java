package com.example.nrnrsign;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Build;
import android.widget.Toast;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.support.v4.content.FileProvider;
import android.net.Uri;
import android.content.ContentValues;


import java.io.*;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.android.Utils;


public class MainActivity extends AppCompatActivity {
    private String imageFilePath;
    private String mCurrentPhotoPath;

    private static final int REQUEST_IMAGE_CAPTURE = 672;
    private Uri  photoUri;
    private Uri  photoUri2;

    private Mat matInput;
    private Mat matResult;

    public native void thresholding(long matAddrInput, long matAddrResult);

    static{
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            if(!hasPermissions(PERMISSIONS)){
//                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
//            }
//        }

        findViewById(R.id.take).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTakePhotoIntent();
            }
        });

        findViewById(R.id.convert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cv_thresholding();
            }
        });

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePhoto();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            ((ImageView)findViewById(R.id.photo)).setImageURI(photoUri);

        }
    }

    private void sendTakePhotoIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null){
            File photoFile = null;
            try{
                photoFile = createImageFile();
            }catch (IOException ex){
            }

            if(photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void savePhoto() {
        Bitmap finalBitmap = ((BitmapDrawable)((ImageView)findViewById(R.id.photo)).getDrawable()).getBitmap();

        if(finalBitmap != null) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageName = "TEST_" + timeStamp + "_";

            File sdPath = Environment.getExternalStorageDirectory();

            String fname = "Image-" + imageName + ".png";
            File mfile = new File(sdPath.getAbsolutePath()+"/DCIM/", fname);

            if (mfile.exists()) mfile.delete();

            try {
                FileOutputStream out = new FileOutputStream(mfile);
                finalBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, mfile.getAbsolutePath());
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg"); // or image/png
                getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                Toast.makeText(this, "사진이 저장되었습니다", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "설정에서 권한 승인이 필요함", Toast.LENGTH_SHORT).show();
            }
        }else{Toast.makeText(this, "이미지 불러오기 실패", Toast.LENGTH_SHORT).show();}
        //Toast.makeText(this, "사진이 저장되었습니다", Toast.LENGTH_SHORT).show();
    }

    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,
                ".png",
                storageDir
        );
        imageFilePath = image.getAbsolutePath();

        return image;
    }

    public void cv_thresholding() {
        Bitmap myBitmap = ((BitmapDrawable)((ImageView)findViewById(R.id.photo)).getDrawable()).getBitmap();
        Mat matInput = new Mat();
        Utils.bitmapToMat(myBitmap, matInput);

        if(matResult == null) matResult = new Mat(matInput.rows(), matInput.cols(), matInput.type());
        thresholding(matInput.getNativeObjAddr(), matResult.getNativeObjAddr());

        Bitmap output = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(matResult, output);
        ((ImageView)findViewById(R.id.photo)).setImageBitmap(output);
    }

}
























