package org.tensorflow.demo;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.tensorflow.demo.env.ImageUtils;
import org.tensorflow.demo.env.Logger;
import org.tensorflow.demo.tracking.MultiBoxTracker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * 仅测试
 */
public class tryActivity extends Activity implements View.OnClickListener {

    private static final int TF_OD_API_INPUT_SIZE = 300;//必须和输入照片的宽度值一样，否则识别率会很低
    private static final String TF_OD_API_MODEL_FILE = "file:///android_asset/ssd_mobilenet_v1_by_ddn.pb";
    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/coco_labels_list_by_ddn.txt";
    private Classifier detectorClassifier;
    private long lastProcessingTimeMs;
    private Bitmap croppedBitmap = null;
    private Bitmap cropCopyBitmap = null;
    private ImageView imageView2;
    private Button btn_Toast1,btn_Toast2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_try);
        imageView2=(ImageView)findViewById(R.id.im_try2);
        initToastTest();
        initImage();
        init();
    }

    private void initToastTest() {
        btn_Toast1=(Button)findViewById(R.id.btn_toast1);
        btn_Toast1.setOnClickListener(this);

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_toast1:
                midToast("ceshi");
                break;
                default:
                    Log.i(TAG, "onClick: ");

                    break;
        }
    }
    private void midToast(String str)
    {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.view_toast_custom,
                (ViewGroup) findViewById(R.id.timo));
        ImageView img_logo = (ImageView) view.findViewById(R.id.img_logo);
        TextView tv_msg = (TextView) view.findViewById(R.id.tv_msg);
        tv_msg.setText(str);
        Toast toast = new Toast(this);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }

    private void initImage() {
        croppedBitmap=readBitmapFromFileDescriptor(Environment.getExternalStorageDirectory().getPath()+ File.separator+"t11.jpg",320,240);
    }
    private void init() {
        try {
            detectorClassifier = TensorFlowObjectDetectionAPIModel.create(getAssets(), TF_OD_API_MODEL_FILE, TF_OD_API_LABELS_FILE, TF_OD_API_INPUT_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
            final long startTime = SystemClock.uptimeMillis();
            final List<Classifier.Recognition> results = detectorClassifier.recognizeImage(croppedBitmap);
            Log.i(TAG, "init: ");
            lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;
            Log.i(TAG, "lastProcessingTimeMs: "+lastProcessingTimeMs);
            cropCopyBitmap = croppedBitmap.copy(Bitmap.Config.ARGB_8888, true);//复制一张用来显示绘制框
            final Canvas canvas = new Canvas(cropCopyBitmap);
            final Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(3.0f);
            paint.setTextSkewX(20);

               Paint textPaint = new Paint();          // 创建画笔
               textPaint.setColor(Color.GREEN);        // 设置颜色
               textPaint.setStyle(Paint.Style.FILL);   // 设置样式
               textPaint.setTextSize(15);
        for (final Classifier.Recognition result : results) {
                if(result.getConfidence()>=0.01){
                    final RectF location = result.getLocation();
                    Log.i(TAG, "result.getTitle--------------------:"+result.getTitle().toString());
                    Log.i(TAG, "result.getConfidence--------------------:"+result.getConfidence().toString());
                    canvas.drawRect(location, paint);
                    canvas.drawText("herpes："+result.getConfidence().toString(),0,12,location.left,location.top,textPaint);
                    imageView2.setImageBitmap(cropCopyBitmap);
                }
            }
    }
    /**
     * 获取本地图片
     * @param filePath
     * @param width
     * @param height
     * @return
     */
    public static Bitmap readBitmapFromFileDescriptor(String filePath, int width, int height) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(fis.getFD(), null, options);
            float srcWidth = options.outWidth;
            float srcHeight = options.outHeight;
            int inSampleSize = 1;

            if (srcHeight > height || srcWidth > width) {
                if (srcWidth > srcHeight) {
                    inSampleSize = Math.round(srcHeight / height);
                } else {
                    inSampleSize = Math.round(srcWidth / width);
                }
            }

            options.inJustDecodeBounds = false;
            options.inSampleSize = inSampleSize;

            return BitmapFactory.decodeFileDescriptor(fis.getFD(), null, options);
        } catch (Exception ex) {
        }
        return null;
    }



}
