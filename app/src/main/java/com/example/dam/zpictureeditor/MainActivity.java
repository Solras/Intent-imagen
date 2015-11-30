package com.example.dam.zpictureeditor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ZoomControls;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    private ImageView ivFoto;
    private String ruta;
    private Uri uri;
    private Bitmap original;
    public static final int REQUEST_IMAGE_GET = 1;
    private ZoomControls zoomControls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.zoomControls = (ZoomControls) findViewById(R.id.zoomControls);
        this.ivFoto = (ImageView) findViewById(R.id.ivFoto);
        init();
    }

    public void init(){
        Uri data = getIntent().getData();

        if(getIntent().getType()!=null)
            ivFoto.setImageURI(data);

        original = ((BitmapDrawable) ivFoto.getDrawable()).getBitmap();

        zoomControl();
    }

    public void guardar(View v){
        try {
            Bitmap bmp = ((BitmapDrawable) ivFoto.getDrawable()).getBitmap();
            File f = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            String path = f.getPath() + "/" + new GregorianCalendar().getTime().toString().trim() + ".jpg";
            FileOutputStream out = new FileOutputStream(path);
            if(bmp.compress(Bitmap.CompressFormat.PNG, 100, out))
                Toast.makeText(this,"Imagen guardada en " + f.getPath(),Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this,"No se ha podido guardar la imagen",Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void zoomControl(){
        zoomControls=(ZoomControls)findViewById(R.id.zoomControls);
        zoomControls.setOnZoomInClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                float x = ivFoto.getScaleX();
                float y = ivFoto.getScaleY();

                ivFoto.setScaleX(x + 1);
                ivFoto.setScaleY(y + 1);
            }
        });

        zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
            @Override            public void onClick(View v) {

                float x = ivFoto.getScaleX();
                float y = ivFoto.getScaleY();

                ivFoto.setScaleX(x - 1);
                ivFoto.setScaleY(y - 1);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            uri = data.getData();
            ruta = uri.toString();
            if (uri != null) {
                ivFoto.setImageURI(uri);
                original = ((BitmapDrawable) ivFoto.getDrawable()).getBitmap();
            }
        }
    }

    public void imgFoto(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET);
        }
    }


    public void original(View v) {
        ivFoto.setImageBitmap(original);
    }


    public void btBlancoNegro(View v) {
        BitmapDrawable bmpDraw = (BitmapDrawable) ivFoto.getDrawable();
        Bitmap bitmap = bmpDraw.getBitmap();
        ivFoto.setImageBitmap(toEscalaDeGris(bitmap));
    }

    public static Bitmap toEscalaDeGris(Bitmap bmpOriginal) {
        Bitmap bmpGris = Bitmap.createBitmap(bmpOriginal.getWidth(), bmpOriginal.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas lienzo = new Canvas(bmpGris);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter cmcf = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(cmcf);
        lienzo.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGris;
    }


    public void btRotarIzq(View v) {
        BitmapDrawable bmpDraw = (BitmapDrawable) ivFoto.getDrawable();
        Bitmap bitmap = bmpDraw.getBitmap();
        ivFoto.setImageBitmap(rotarBitmap(bitmap, 90));
    }

    public void btRotarDer(View v) {
        BitmapDrawable bmpDraw = (BitmapDrawable) ivFoto.getDrawable();
        Bitmap bitmap = bmpDraw.getBitmap();
        ivFoto.setImageBitmap(rotarBitmap(bitmap, -90));
    }

    public static Bitmap rotarBitmap(Bitmap bmpOriginal, float angulo) {
        Matrix matriz = new Matrix();
        matriz.postRotate(angulo);
        return Bitmap.createBitmap(bmpOriginal, 0, 0, bmpOriginal.getWidth(), bmpOriginal.getHeight(), matriz, true);
    }


    public void voltearHorizontal(View v) {
        BitmapDrawable bmpDraw = (BitmapDrawable) ivFoto.getDrawable();
        Bitmap bitmap = bmpDraw.getBitmap();

        Bitmap bmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        int pixel, red, green, blue, alpha;
        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                pixel = bitmap.getPixel(i, j);
                red = Color.red(pixel);
                green = Color.green(pixel);
                blue = Color.blue(pixel);
                alpha = Color.alpha(pixel);
                bmp.setPixel(bitmap.getWidth()-i-1, j, Color.argb(alpha, red, green, blue));
            }
        }
        ivFoto.setImageBitmap(bmp);
    }
    public void voltearVertical(View v) {
        BitmapDrawable bmpDraw = (BitmapDrawable) ivFoto.getDrawable();
        Bitmap bitmap = bmpDraw.getBitmap();

        Bitmap bmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        int pixel, red, green, blue, alpha;
        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                pixel = bitmap.getPixel(i, j);
                red = Color.red(pixel);
                green = Color.green(pixel);
                blue = Color.blue(pixel);
                alpha = Color.alpha(pixel);
                bmp.setPixel(i, bitmap.getHeight()-j-1, Color.argb(alpha, red, green, blue));
            }
        }
        ivFoto.setImageBitmap(bmp);
    }


    public void filtroRojo(View v) {
        Bitmap bmp = Bitmap.createBitmap(original.getWidth(), original.getHeight(), original.getConfig());
        int pixel, green, blue, alpha;
        for (int i = 0; i < original.getWidth(); i++) {
            for (int j = 0; j < original.getHeight(); j++) {
                pixel = original.getPixel(i, j);
                green = Color.green(pixel);
                blue = Color.blue(pixel);
                alpha = Color.alpha(pixel);
                bmp.setPixel(i, j, Color.argb(alpha, 250, green, blue));
            }
        }
        ivFoto.setImageBitmap(bmp);
    }
    public void filtroVerde(View v) {
        Bitmap bmp = Bitmap.createBitmap(original.getWidth(), original.getHeight(), original.getConfig());
        int pixel, red, blue, alpha;
        for (int i = 0; i < original.getWidth(); i++) {
            for (int j = 0; j < original.getHeight(); j++) {
                pixel = original.getPixel(i, j);
                red = Color.red(pixel);
                blue = Color.blue(pixel);
                alpha = Color.alpha(pixel);
                bmp.setPixel(i, j, Color.argb(alpha, red, 250, blue));
            }
        }
        ivFoto.setImageBitmap(bmp);
    }
    public void filtroAzul(View v) {
        Bitmap bmp = Bitmap.createBitmap(original.getWidth(), original.getHeight(), original.getConfig());
        int pixel, green, red, alpha;
        for (int i = 0; i < original.getWidth(); i++) {
            for (int j = 0; j < original.getHeight(); j++) {
                pixel = original.getPixel(i, j);
                red = Color.red(pixel);
                green = Color.green(pixel);
                alpha = Color.alpha(pixel);
                bmp.setPixel(i, j, Color.argb(alpha, red, green, 250));
            }
        }
        ivFoto.setImageBitmap(bmp);
    }
}
