package com.example.sotiria.sightsar;

import android.content.Context;
import android.content.res.Configuration;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.vuforia.Device;
import com.vuforia.Matrix44F;
import com.vuforia.MultiTargetResult;
import com.vuforia.State;
import com.vuforia.Tool;
import com.vuforia.Trackable;
import com.vuforia.TrackableResult;
import com.vuforia.Vuforia;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

//import com.ateith_farmar_web_intelligence.app.Constants;
//import com.ateith_farmar_web_intelligence.app.utils.Identify3DModel;

class ImageTargetRenderer implements GLSurfaceView.Renderer, CameraRendererControl {

    private static final String TAG = ImageTargetRenderer.class.getSimpleName();


    private CameraSession vuforiaAppSession;
    private CameraActivity mActivity;
    private AboutTarget aboutTargetActivity;
    private CameraRenderer mIdentifyRenderer;
    private boolean mIsActive = false;
    private boolean mModelIsLoaded = false;
    private OnTargetDetectListener mListener;
    private onOrientationChange monOrientationChange;
    LoadingDialogHandler loadingDialogHandler;
    public View mLoadingDialogContainer;



    ImageTargetRenderer(CameraActivity activity, CameraSession session) {
        mActivity = activity;
        vuforiaAppSession = session;
        // IdentifyRenderer used to encapsulate the use of RenderingPrimitives setting
        // the device mode AR/VR and stereo mode
        mIdentifyRenderer = new CameraRenderer(this, mActivity, Device.MODE.MODE_AR, false, 0.01f, 5f);

    }




    // Called to draw the current frame.
    @Override
    public void onDrawFrame(GL10 gl) {
        if (!mIsActive)
            return;

        // Call our function to render content from IdentifyRenderer class
        mIdentifyRenderer.render();
    }


    public void setActive(boolean active) {
        mIsActive = active;

        if (mIsActive)
            mIdentifyRenderer.configureVideoBackground();
    }


    // Called when the surface is created or recreated.
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(TAG, "GLRenderer.onSurfaceCreated");

        // Call Vuforia function to (re)initialize rendering after first use
        // or after OpenGL ES context was lost (e.g. after onPause/onResume):
        vuforiaAppSession.onSurfaceCreated();

        mIdentifyRenderer.onSurfaceCreated();
    }


    // Called when the surface changed size.
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(TAG, "GLRenderer.onSurfaceChanged");

        // Call Vuforia function to handle render surface size changes:
        vuforiaAppSession.onSurfaceChanged(width, height);

        // RenderingPrimitives to be updated when some rendering change is done
        mIdentifyRenderer.onConfigurationChanged(mIsActive);

       initRendering();
    }


    /**
     * Function for initializing the renderer.
     */
    private void initRendering() {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, Vuforia.requiresAlpha() ? 0.0f
               : 1.0f);

        if (!mModelIsLoaded) {
            try {
                Identify3DModel buildingsModel = new Identify3DModel();
                buildingsModel.loadModel(mActivity.getResources().getAssets(),
                        "CameraActivity/Buildings.txt");
                mModelIsLoaded = true;

            } catch (IOException e) {
                Log.e(TAG, "Unable to load buildings");
            }

            // Hide the Loading Dialog

       mActivity.loadingDialogHandler.sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);



        }
    }


    public void updateConfiguration() {
        mIdentifyRenderer.onConfigurationChanged(mIsActive);
    }


    // The render function called from SampleAppRendering by using RenderingPrimitives views.
    // The state is owned by IdentifyRenderer which is controlling it's lifecycle.
    // State should not be cached outside this method.
    public void renderFrame(State state, float[] projectionMatrix) {

        // Renders video background replacing Renderer.DrawVideoBackground()
        mIdentifyRenderer.renderVideoBackground();

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // handle face culling, we need to detect if we are using reflection
        // to determine the direction of the culling
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);

        // If we find any trackables this frame than get first result
        if (state.getNumTrackableResults() > 0) {
            TrackableResult result = state.getTrackableResult(0);
            Trackable trackable = result.getTrackable();
            if (mListener != null) {
                mListener.onTargetDetected(trackable.getName());
                mListener.onTargetFound(trackable.getId());
                Log.e(TAG, "CurrentClass" + trackable.getClass());
                Log.e(TAG, "CurrentName" + trackable.getName());
                Log.e(TAG, "CurrentID" + trackable.getId());

            }
            } else {
            if (mListener != null) {
                mListener.onTargetLose();
            }
        }
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
    }



    void setTargetDetectListener(OnTargetDetectListener listener) {
        mListener = listener;
    }


    public interface OnTargetDetectListener {

        /**
         * Callback for Target. When is detected
         *
         * @param targetName
         */
        void onTargetDetected(String targetName);
        void onTargetFound(int id);

        /**
         * Callback for Target. When is lose
         */
        void onTargetLose();
    }



    private void printUserData(Trackable trackable)
    {
        String userData = (String) trackable.getUserData();
        Log.d(TAG, "UserData:Retreived User Data	\"" + userData + "\"");
    }

    void setonOrientationChanger(onOrientationChange orientationChange) {
        monOrientationChange = orientationChange;
    }

    public interface onOrientationChange{
         void onConfigurationChanged(int orientation);
    }
}
