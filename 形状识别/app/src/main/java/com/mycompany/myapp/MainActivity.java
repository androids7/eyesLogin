package com.mycompany.myapp;

import android.app.*;
import android.os.*;
import android.graphics.*;
import java.nio.*;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import android.widget.*;
import java.util.*;
import android.view.*;
import android.view.View.*;
import android.annotation.*;
import android.renderscript.*;
import org.opencv.imgcodecs.*;
import org.opencv.objdetect.*;
import org.opencv.core.*;
import java.io.*;


public class MainActivity extends Activity implements OnClickListener
{
	Bitmap b1,b2,b3,b4,b5,b6;
	
	double poi=-0.130;//相似度必须大于这个
	private ImageView mIv_ImageView1,mIv_ImageView2;
	private Button mBtn_compare;
	
	
	CascadeClassifier eyeDetector;
	
	private BaseLoaderCallback callback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			super.onManagerConnected(status);
			switch (status) {
				case BaseLoaderCallback.SUCCESS:

					try{
						InputStream is = getResources().openRawResource(R.raw.haarcascade_eye);
						//File cascadeDir = getDir("cache", Context.MODE_PRIVATE);
						mCascadeFile2 = new File("/sdcard/haarcascade_eye.xml");
						mCascadeFile2.createNewFile();
						FileOutputStream os = new FileOutputStream(mCascadeFile2);

						byte[] buffer = new byte[4096];
						int bytesRead;
						while ((bytesRead = is.read(buffer)) != -1) {
							os.write(buffer, 0, bytesRead);
						}
						is.close();
						os.close();


					}catch(Exception e){

						}
					
					
					
					
					eyeDetector = new CascadeClassifier(mCascadeFile2.getAbsolutePath());
                    if (eyeDetector.empty()) {
                      //  Log.e(TAG, "Failed to load cascade classifier");
                        eyeDetector = null;
                    } else {
                       // Log.e(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());
                    }

					
					
					break;

				default:
					break;
			}
		}
	};
	
	PrintStream ps;
	ByteArrayOutputStream bao;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		bao=new ByteArrayOutputStream();
		ps=new PrintStream(bao);
		System.setOut(ps);
		System.setErr(ps);
		
		b1=BitmapFactory.decodeFile("/sdcard/1/img/1.jpg");
		b2=BitmapFactory.decodeFile("/sdcard/1/img/2.jpg");
		
		b3=Bitmap.createScaledBitmap(b1,256,256,true);
		b4=Bitmap.createScaledBitmap(b2,256,256,true);
		b5=blur(b3,20);
		/*
		b5=blur(b5,25);
		b5=blur(b5,25);
		*/
		b6=blur(b4,25);
		//b6=blur(b6,25);
		/*
		b6=blur(b6,25);
		b6=blur(b6,25);
		b6=blur(b6,25);
		b6=blur(b6,25);
		*/
		//b6=blur(b6,25);
		
		init();
		
		}
		
		
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected Bitmap blur(Bitmap bitmap, int radius) {
        RenderScript rs = RenderScript.create(this);
        Allocation overlayAlloc = Allocation.createFromBitmap(
			rs, bitmap);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(
			rs, overlayAlloc.getElement());

        blur.setInput(overlayAlloc);
        blur.setRadius(radius);
        blur.forEach(overlayAlloc);

        overlayAlloc.copyTo(bitmap);
        rs.destroy();
        return bitmap;
    }
		
		
		
	public void init(){
		
		mIv_ImageView1 = (ImageView)findViewById(R.id.mainImageView1);
		mIv_ImageView2 = (ImageView)findViewById(R.id.mainImageView2);
		mBtn_compare = (Button)findViewById(R.id.btn_compare);
		mIv_ImageView1.setImageBitmap(b5);
		mIv_ImageView2.setImageBitmap(b6);
		mBtn_compare.setOnClickListener(this);
	}
		
	
	
	

	/**
     * opencv实现人眼识别
     * @param imagePath
     * @param outFile
     * @throws Exception
     */
	private File                   mCascadeFile2;

	//MatOfRect faceDetections;
    public  void detectEye(String imagePath,  String outFile) throws Exception {



		



        //CascadeClassifier eyeDetector = new CascadeClassifier(mCascadeFile2.getAbsolutePath().toString());

		//"D:\\opencv\\opencv\\sources\\data\\haarcascades\\haarcascade_eye.xml");




        Mat image =new Mat();
		Utils.bitmapToMat( BitmapFactory.decodeFile (imagePath),image); //读取图片

        // 在图片中检测人脸
        MatOfRect faceDetections = new MatOfRect();

        eyeDetector.detectMultiScale(image,faceDetections,20);
		//(image, faceDetections, 2.0,1,1,new Size(20,20),new Size(20,20));

        System.out.println(String.format("Detected %s eyes", faceDetections.toArray().length));
        Rect[] rects = faceDetections.toArray();
        if(rects != null && rects.length <2){
           // throw new RuntimeException("不是一双眼睛");
			Toast.makeText(this,"不是一双眼睛",0).show();
        }
        Rect eyea = rects[0];
        Rect eyeb = rects[1];
        System.out.println("a-中心坐标 " + eyea.x + " and " + eyea.y);
        System.out.println("b-中心坐标 " + eyeb.x + " and " + eyeb.y);
        //获取两个人眼的角度
        double dy=(eyeb.y-eyea.y);
        double dx=(eyeb.x-eyea.x);
        double len=Math.sqrt(dx*dx+dy*dy);
        System.out.println("dx is "+dx);
        System.out.println("dy is "+dy);
        System.out.println("len is "+len);
        double angle=Math.atan2(Math.abs(dy),Math.abs(dx))*180.0/Math.PI;
        System.out.println("angle is "+angle);
		
		Mat eyes[]=new Mat[2];
		Rect rer[]=new Rect[2];
		int i=0;
        for(Rect rect:faceDetections.toArray()) {
			eyes[i]=new Mat();
			rer[i]=new Rect(rect.x+120,rect.y+150,rect.width-250,rect.height-270);
            Imgproc.rectangle(image,new org.opencv.core.Point(rect.x, rect.y), new org.opencv.core.Point(rect.x+ rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
			
			eyes[i]=image.submat(rer[i]);
			i++;
        }
      //  Imgcodecs.imwrite(outFile, image);
		Bitmap bmp=Bitmap.createBitmap(eyes[0].width(),eyes[0].height(),Bitmap.Config.ARGB_8888);
		
		
		
		Utils.matToBitmap(eyes[0],bmp);
		File f = new File(outFile); 
		if (f.exists()) { 
			f.delete(); 
		} 
		try { 
		f.createNewFile();
			FileOutputStream out = new FileOutputStream(f); 
			bmp.compress(Bitmap.CompressFormat.PNG, 90, out); 
			out.flush(); 
			out.close(); 
		
		} catch (FileNotFoundException e) { 
// TODO Auto-generated catch block 
			e.printStackTrace(); 
		} catch (IOException e) { 
// TODO Auto-generated catch block 
			e.printStackTrace(); 
		}
		//bmp.compress(Bitmap.CompressFormat.JPEG,100,fo);

        System.out.println(String.format("人眼识别成功，人眼图片文件为： %s", outFile));
    }
	
	
	@Override
	public void onClick(View v) {
		Mat mat1 = new Mat();
		Mat mat2 = new Mat();
		Mat mat11 = new Mat();
		Mat mat22 = new Mat();
		Utils.bitmapToMat(b5, mat1);
		Utils.bitmapToMat(b6, mat2);
		Imgproc.cvtColor(mat1, mat11, Imgproc.COLOR_BGR2GRAY);
		Imgproc.cvtColor(mat2, mat22, Imgproc.COLOR_BGR2GRAY);
		comPareHist(mat11, mat22);
		
		
		try
		{
			detectEye("/sdcard/1/img/666.jpg", "/sdcard/1/outeye.jpg");
		}
		catch (Exception e)
		{}

	
	
		
	}

	private Mat                  mMat0;
	private MatOfInt             mChannels[];
	private MatOfInt             mHistSize;
	private int                  mHistSizeNum = 25;
	private MatOfFloat           mRanges;
	private Scalar               mColorsRGB[];
	private Point                mP1;
	private Point                mP2;
	private float                mBuff[];
	public Mat procSrc2GrayJni(Mat srcMat,int type) {
		Mat grayMat = new Mat();
		Imgproc.cvtColor(srcMat, grayMat, type);//转换为灰度图
		// Imgproc.HoughCircles(rgbMat, gray,Imgproc.CV_HOUGH_GRADIENT, 1, 18);
		// //霍夫变换找园
        mChannels = new MatOfInt[] { new MatOfInt(0), new MatOfInt(1), new MatOfInt(2) };
        mBuff = new float[mHistSizeNum];
        mHistSize = new MatOfInt(mHistSizeNum);
        mRanges = new MatOfFloat(0f, 256f);
        mMat0  = new Mat();
        mColorsRGB = new Scalar[] { new Scalar(200, 0, 0, 255), new Scalar(0, 200, 0, 255), new Scalar(0, 0, 200, 255) };
        mP1 = new Point();
        mP2 = new Point();



		Mat rgba = srcMat;
		Size sizeRgba = rgba.size();
		Mat hist = new Mat(); //转换直方图进行绘制
		int thikness = (int) (sizeRgba.width / (mHistSizeNum + 10) / 5);
		if(thikness > 5) thikness = 5;
		int offset = (int) ((sizeRgba.width - (5*mHistSizeNum + 4*10)*thikness)/2);
		// RGB
		for(int c=0; c<3; c++) {
			Imgproc.calcHist(Arrays.asList(rgba), mChannels[c], mMat0, hist, mHistSize, mRanges);
			Core.normalize(hist, hist, sizeRgba.height/2, 0, Core.NORM_INF);
			hist.get(0, 0, mBuff);
			for(int h=0; h<mHistSizeNum; h++) {
				mP1.x = mP2.x = offset + (c * (mHistSizeNum + 10) + h) * thikness;
				mP1.y = sizeRgba.height-1;
				mP2.y = mP1.y - 2 - (int)mBuff[h];
			//	Core.line(rgba, mP1, mP2, mColorsRGB[c], thikness);
			}
		}

		return rgba;
	}
	/**
	 * 比较来个矩阵的相似度
	 * @param srcMat
	 * @param desMat
	 */
	public void comPareHist(Mat srcMat,Mat desMat){

		srcMat.convertTo(srcMat, CvType.CV_32F);
		desMat.convertTo(desMat, CvType.CV_32F);
		double target = Imgproc.compareHist(srcMat, desMat, Imgproc.CV_COMP_CORREL);
	//	Log.e(TAG, "相似度 ：   ==" + target);
	
	if(target<=poi){
		AlertDialog.Builder ab=new AlertDialog.Builder(this);
		AlertDialog ad=ab.create();
		ab.setMessage("识别成功");
		ab.show();
	}
		Toast.makeText(this, "相似度 ：   ==" + target, 1000).show();
	}


	@Override
	protected void onResume() {
		super.onResume();
		// 通过OpenCV引擎服务加载并初始化OpenCV类库，所谓OpenCV引擎服务即是  
        // OpenCV_2.4.9.2_Manager_2.4_*.apk程序包，存在于OpenCV安装包的apk目录中  
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this,  
							   callback);  
	}

	@Override
	protected void onDestroy()
	{
		// TODO: Implement this method
		super.onDestroy();
		
		File f=new File("/sdcard/1/log.txt");
		if(f.exists()){
			f.delete();
		}
		else{
			try
			{
				f.createNewFile();
			}
			catch (IOException e)
			{}
		}
		try
	{
	FileOutputStream fo=new FileOutputStream(f);
	
	fo.write(bao.toByteArray());
	fo.close();
	}
catch (Exception e) {}
	}
	
	
}
