package com.jiacyer.attackclient.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.ImageReader;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Toast;

import com.jiacyer.attackclient.tools.AttackContralTools;

import java.util.Arrays;

/**
 *  Created by Jiacy-PC on 2018/1/31.
 */

public class CameraUtil {
    private CameraManager cameraManager;
    private CameraDevice cameraDevice;
    private CaptureRequest.Builder previewRequestBuilder;
    private CameraCaptureSession cameraCaptureSession;

    private String frontCameraID;

//    private CDStateCallback cdStateCallback;
    private Handler cameraHandler;
    private ImageReader imageReader;

    private String[] listOfCamera;

    private Context context;

    private final CameraDevice.StateCallback stateCallbackOfCD = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            initImageReader();
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            if (cameraCaptureSession != null) {
                try {
                    cameraCaptureSession.stopRepeating();
//                    cameraCaptureSession.close();
//                    cameraCaptureSession = null;
                } catch (CameraAccessException e) {
                    e.printStackTrace();
//                    cameraCaptureSession = null;
                }
            }
            if (camera != null) {
                camera.close();
            }
            cameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int i) {
//            if (cameraCaptureSession != null) {
//                try {
//                    cameraCaptureSession.stopRepeating();
//                } catch (CameraAccessException e) {
//                    e.printStackTrace();
//                }
//            }
            if (camera != null) {
                camera.close();
            }
            cameraDevice = null;
            if (imageReader != null) {
                imageReader.close();
            }
            imageReader = null;
            Toast.makeText(context, "摄像头开启失败", Toast.LENGTH_SHORT).show();
        }
    };

    private final CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {

    };

    private final CameraCaptureSession.StateCallback stateCallbackOfCCS = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession ccs) {
            if (null == cameraDevice) return;
            previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
//            previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.control);
            CaptureRequest captureRequest = previewRequestBuilder.build();
            try {
                cameraCaptureSession = ccs;
                ccs.setRepeatingRequest(captureRequest, null, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
            Toast.makeText(context, "配置失败", Toast.LENGTH_SHORT).show();
        }
    };


    public CameraUtil(Context context) throws CameraAccessException {
        Log.i("Camera", "Camera init begin! Context:" + context.toString());
        this.context = context;
        cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        Log.i("Camera", "Before initCamera()");
        initCamera();
        Log.i("Camera", "After initCamera()");
//        takePicture();
        Log.i("Camera", "Camera init End!");
    }

    public void stopCamera() {
        if (cameraDevice != null) {
            cameraDevice.close();
        }
        cameraDevice = null;
        if (cameraCaptureSession != null) {
            cameraCaptureSession.close();
        }
        cameraCaptureSession = null;
        if (imageReader != null) {
            imageReader.close();
        }
        imageReader = null;
    }

    @SuppressLint("MissingPermission")
    private void initCamera() throws CameraAccessException {
        frontCameraID = getFrontCameraID();
        cameraHandler = new Handler(context.getMainLooper());
        Log.i("Camera", "Before initImageReader()");
//        initImageReader();
        Log.i("Camera", "After initImageReader()");
        cameraManager.openCamera(frontCameraID, stateCallbackOfCD, cameraHandler);
    }

    private void initImageReader() {
//        SurfaceView surfaceView = MainContralTools.getSurfaceView();
        Log.i("Camera", "Step1 in initImageReader()");
//        TextureView textureView = MainContralTools.getTextureView();
        TextureView textureView = AttackContralTools.getTextureView();
        if (textureView == null)
            System.out.println("textureView is null");
        else
            System.out.println("textureView is not null! Width:"+textureView.getWidth()+";Height:"+textureView.getHeight());

        Log.i("Camera", "Step2 in initImageReader()");
        imageReader = ImageReader.newInstance(textureView.getWidth(),textureView.getHeight(), ImageFormat.JPEG,2);
        Log.i("Camera", "Step3 in initImageReader()");
//        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
//            @Override
//            public void onImageAvailable(ImageReader imageReader) {
//                // 拿到拍照照片数据
//                Image image = imageReader.acquireLatestImage();
//
//                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
//                byte[] bytes = new byte[buffer.remaining()];
//                buffer.get(bytes);// 由缓冲区存入字节数组
//                final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                if (bitmap != null) {
//                    Log.i("Bitmap", "Width="+bitmap.getWidth());
//                    Log.i("Bitmap", "Height="+bitmap.getHeight());
////                    MainContralTools.setImageBitmap(bitmap);
//                }
//                image.close();
//            }
//        }, cameraHandler);
        Log.i("Camera", "Step4 in initImageReader()");
    }

    private String getFrontCameraID() throws CameraAccessException {
        listOfCamera = cameraManager.getCameraIdList();

        for (String cameraID : listOfCamera) {
            // 获取相机的相关参数
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraID);
            // 检查是否为前置摄像头的ID
            Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
            if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                return cameraID;
            }
        }
        return null;
    }


    private void createCameraPreviewSession() {
        try {
            previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
//            SurfaceView surfaceView = MainContralTools.getSurfaceView();
            TextureView textureView = AttackContralTools.getTextureView();

            if (textureView != null) {
//                Surface surface = surfaceView.getHolder().getSurface();
                Surface surface = new Surface(textureView.getSurfaceTexture());

                previewRequestBuilder.addTarget(surface);
                cameraDevice.createCaptureSession(Arrays.asList(surface, imageReader.getSurface()),
                        stateCallbackOfCCS, null);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

//    public void takePicture() {
//        if (cameraDevice == null)
//            return;
//        try {
//            CaptureRequest.Builder captureRequestBuilder =
//                    cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
////            ImageView imageView = MainContralTools.getImageView();
////            if (imageView != null) {
//            captureRequestBuilder.addTarget(imageReader.getSurface());
//            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
//            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_MODE_AUTO);
////            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.);
//            CaptureRequest captureRequest = captureRequestBuilder.build();
//            cameraCaptureSession.capture(captureRequest, null, null);
////            }
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//    }


}
