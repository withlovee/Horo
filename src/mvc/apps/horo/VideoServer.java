package mvc.apps.horo;

import java.io.ByteArrayInputStream;

import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class VideoServer extends Activity implements SurfaceHolder.Callback{
    TextView testView;

    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    PictureCallback rawCallback;
    ShutterCallback shutterCallback;
    PictureCallback jpegCallback;
    private final String tag = "VideoServer";
    private boolean cameraWorking = false;

    Button start, stop, capture;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); 

        start = (Button)findViewById(R.id.btn_start);
        //stop = (Button)findViewById(R.id.btn_stop);
        capture = (Button) findViewById(R.id.capture);
        start.setOnClickListener(new Button.OnClickListener(){
			@Override
            public void onClick(View arg0) {
                start_camera();
            }
        });
        /*
        stop.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View arg0) {
                stop_camera();
            }
        });
        */
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });
        
        surfaceView = (SurfaceView) findViewById(R.id.camera_preview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        rawCallback = new PictureCallback() {
			@Override
            public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
                Log.d("Log", "onPictureTaken - raw");
            }

        };

        /** Handles data for jpeg picture */
        shutterCallback = new ShutterCallback() {
            public void onShutter() {
                Log.i("Log", "onShutter'd");
            }
        };
        jpegCallback = new PictureCallback() {
			@Override
			public void onPictureTaken(byte[] data,
					android.hardware.Camera camera) {
				upload(data);
                stop_camera();
			}
        };
    }

    private void captureImage() {
    	if(cameraWorking){
    		camera.takePicture(shutterCallback, rawCallback, jpegCallback);
    	}
    }

    private void start_camera(){
    	if(!cameraWorking){
    		SurfaceView cameraPreview = (SurfaceView) findViewById(R.id.camera_preview);
    		cameraPreview.setBackgroundResource(0);
	        try{
	            camera = Camera.open();
	        }catch(RuntimeException e){
	            Log.e(tag, "init_camera: " + e);
	            return;
	        }
	        Camera.Parameters param;
	        camera.setDisplayOrientation(90);
	        param = camera.getParameters();
	        param.setPreviewFrameRate(20);
	        param.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
	        param.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
	        param.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
	        param.setPictureSize(866, 650);
	        camera.setParameters(param);
	        try {
	            camera.setPreviewDisplay(surfaceHolder);
	            camera.startPreview();
	            cameraWorking = true;
	        } catch (Exception e) {
	            Log.e(tag, "init_camera: " + e);
	            return;
	        }
    	}
    }

    private void stop_camera(){
    	if(cameraWorking){
	        camera.stopPreview();
	        camera.release();
	        cameraWorking = false;
    	}
    }
    
    private void upload(byte[] data){
    	String domain = "http://192.168.16.2";
    	String url = domain + "/horo.php";
    	AsyncHttpClient client = new AsyncHttpClient();
    	RequestParams params = new RequestParams();
    	params.put("uploaded_file", new ByteArrayInputStream(data), "img.jpg");
    	client.setTimeout(10000000);
    	client.post(url, params, new AsyncHttpResponseHandler() {
    		Context context = getApplicationContext();
    	    @Override
    	    public void onStart() {
    	        // called before request is started
    	    	Toast.makeText(context, "Uploading...", Toast.LENGTH_LONG).show();
    	    	Log.i("UPLOAD", "START");
    	    }

    	    @Override
    	    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
    	    	Toast.makeText(context, "Success!", Toast.LENGTH_LONG).show();
    	    	Log.i("UPLOAD", "SUCCESS");
    	    	Intent intent = new Intent(getApplication(), BrowserActivity.class);
    	    	String message = new String(response);
    	    	Log.i("RESPONSE", message);
    	    	intent.putExtra("DOMAIN", "http://192.168.16.2");
    	        intent.putExtra("FILENAME", message);
    	        startActivity(intent);
    	        Log.i("UPLOAD", "Start New Activity");
    	    }

    	    @Override
    	    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
    	    	Log.e("UPLOAD", "FAILURE");
    	    	Toast.makeText(context, "Failed, Please try again.", Toast.LENGTH_LONG).show();
    	    }

    	    @Override
    	    public void onRetry(int retryNo) {
    	    	Log.i("UPLOAD", "RETRY");
    	    	Toast.makeText(context, "Retrying...", Toast.LENGTH_LONG).show();
    		}
    	});
    	
    }
    
    
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		
	}

}
