package com.hafizzaturrahim.tambang.geotag;


import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.media.ExifInterface;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hafizzaturrahim.tambang.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

public class GeotagActivity extends AppCompatActivity implements View.OnClickListener {


    private Button buttonChoose;
    private Button buttonUpload;
    private Button buttontakeImage;

    private ImageView imageView;

    private EditText edttitleGeotag;

    private Uri mCapturedImageURI;
    private Bitmap bitmap;

    private String KEY_IMAGE = "image";
    private String KEY_NAME = "name";
    private String mCurrentPhotoPath;
    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;

    private int PICK_IMAGE_REQUEST = 2;
    static final int REQUEST_IMAGE_CAPTURE = 1;


    TextView t;

    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "Tambang";

    private Uri fileUri; // file base_url to store image/video


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geotag);

        buttonUpload = (Button) findViewById(R.id.buttonUpload);
        buttontakeImage = (Button) findViewById(R.id.takeImage);
        edttitleGeotag = (EditText) findViewById(R.id.editText);

        imageView = (ImageView) findViewById(R.id.imgPhotoResult);

//        t = (TextView) findViewById(R.id.lat);
        buttonUpload.setOnClickListener(this);
        buttontakeImage.setOnClickListener(this);

        requestRuntimePermission();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            String fileName = "temp.jpg";
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, fileName);
            mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            Uri filePath = data.getData();
//            try {
//                //Getting the Bitmap from Gallery
//                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
//                //Setting the Bitmap to ImageView
//                imageView.setImageBitmap(bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view
                previewImage();
                Log.v("path uri take camera", fileUri.getPath());
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        } else {
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    //Display an error
                    Toast.makeText(this, "can't show image", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    fileUri = data.getData();
                    Log.v("path uri browse galery", fileUri.getPath());
                    previewImage();
//                        InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
//                        bitmap = BitmapFactory.decodeStream(inputStream);
//                        //scale bitmap to prevent too large resolution that cant rendered by OpenGL
//                        int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
//                        bitmap = Bitmap.createScaledBitmap(bitmap, 512, nh, true);

//                        Picasso.with(GeotagActivity.this).load(String.valueOf(bitmap)).fit().centerCrop().into(imageView);
//                        imageView.setImageBitmap(bitmap);
                }
            }
        }

//        if (resultCode == RESULT_OK) {
//            if (requestCode == 100) {
//                try {
//                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);
//                } catch (FileNotFoundException e1) {
//                    // TODO Auto-generated catch block
//                    e1.printStackTrace();
//                } catch (IOException e1) {
//                    // TODO Auto-generated catch block
//                    e1.printStackTrace();
//                }
//                imageView.setImageBitmap(bitmap);
//                sendBroadcast(new Intent(
//                        Intent.ACTION_MEDIA_MOUNTED,
//                        Uri.parse("file://" + Environment.getExternalStorageDirectory())));
//            }
//        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file base_url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    /*
     * Here we restore the fileUri again
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file base_url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    /*
     * Display image from a path to ImageView
     */
    private void previewImage() {

        try {
            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                    options);
            imageView.setImageBitmap(bitmap);

//            Log.v("real path uri",fileUri.getPath());
            getExifData();

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void getExifData() {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(fileUri.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String LATITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
        String LATITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
        String LONGITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        String LONGITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);

        Float Latitude = null, Longitude = null;

        if ((LATITUDE != null)
                && (LATITUDE_REF != null)
                && (LONGITUDE != null)
                && (LONGITUDE_REF != null)) {

            if (LATITUDE_REF.equals("N")) {
                Latitude = convertToDegree(LATITUDE);
            } else {
                Latitude = 0 - convertToDegree(LATITUDE);
            }

            if (LONGITUDE_REF.equals("E")) {
                Longitude = convertToDegree(LONGITUDE);
            } else {
                Longitude = 0 - convertToDegree(LONGITUDE);
            }

        }

        t.setText(String.valueOf(Latitude));
//            Log.d("lat gbr",latitude);
        Toast.makeText(this, "latitude " + String.valueOf(Latitude), Toast.LENGTH_SHORT).show();
    }

    private Float convertToDegree(String stringDMS) {
        Float result;
        String[] DMS = stringDMS.split(",", 3);

        String[] stringD = DMS[0].split("/", 2);
        Double D0 = new Double(stringD[0]);
        Double D1 = new Double(stringD[1]);
        Double FloatD = D0 / D1;

        String[] stringM = DMS[1].split("/", 2);
        Double M0 = new Double(stringM[0]);
        Double M1 = new Double(stringM[1]);
        Double FloatM = M0 / M1;

        String[] stringS = DMS[2].split("/", 2);
        Double S0 = new Double(stringS[0]);
        Double S1 = new Double(stringS[1]);
        Double FloatS = S0 / S1;

        result = new Float(FloatD + (FloatM / 60) + (FloatS / 3600));

        return result;


    }

    ;

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /*
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "Tambang_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }


    private void requestRuntimePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    private void uploadImage() {
        String UPLOAD_URL = "http://dolanbatu.com/side/upload.php";
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Toast.makeText(GeotagActivity.this, s, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(GeotagActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);

                //Getting Image Name
                String name = edttitleGeotag.getText().toString().trim();

                //Creating parameters
                Map<String, String> params = new Hashtable<>();

                //Adding parameters
                params.put(KEY_IMAGE, image);
                params.put(KEY_NAME, name);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    @Override
    public void onClick(View v) {
        if (v == buttonChoose) {
//            showFileChooser();
        } else if (v == buttonUpload) {
            if (imageView.getDrawable() == null) {
                Toast.makeText(this, "Foto tidak ditemukan", Toast.LENGTH_SHORT).show();
            }else if(edttitleGeotag.getText().toString().isEmpty()){
                edttitleGeotag.setError("Judul harus diisi");
            }
            else {
                uploadImage();
            }
        } else if (v == buttontakeImage) {
//            dispatchTakePictureIntent();
            captureImage();
        }
    }

}
