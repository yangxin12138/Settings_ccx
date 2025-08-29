package com.twd.settingsccx.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcel;
import android.util.Log;

import com.twd.settingsccx.repository.BaseViewModel;
import com.twd.settingsccx.repository.SysEquipmentRepository;
import com.twd.settingsccx.repository.keystone.Lcd;
import com.twd.settingsccx.repository.keystone.Vertex;
import com.twd.settingsccx.utils.SystemPropertiesUtils;

import java.lang.reflect.Method;

public class KeystoneViewModel extends BaseViewModel<SysEquipmentRepository> {

    public static final int ITEM_TWO_POINT = 1;
    public static final int ITEM_SINGLE_POINT = 2;
    public static final int ITEM_SIZE = 3;
    public static final int ITEM_PROJECTION= 4;
    public static final String PROP_LT_ORIGIN = "ro.sys.keystone.lt";
    public static final String PROP_RT_ORIGIN = "ro.sys.keystone.rt";
    public static final String PROP_RB_ORIGIN = "ro.sys.keystone.rb";
    public static final String PROP_LB_ORIGIN = "ro.sys.keystone.lb";
    public static final String ORIGIN_NULL = "0,0";

    //yangxin
    public static final String PROP_LTX = "persist.display.keystone_ltx";
    public static final String PROP_LTY = "persist.display.keystone_lty";
    public static final String PROP_RTX = "persist.display.keystone_rtx";
    public static final String PROP_RTY = "persist.display.keystone_rty";
    public static final String PROP_LBX = "persist.display.keystone_lbx";
    public static final String PROP_LBY = "persist.display.keystone_lby";
    public static final String PROP_RBX = "persist.display.keystone_rbx";
    public static final String PROP_RBY = "persist.display.keystone_rby";
    public static final String PROP_KEYSTONE_UPDATE = "persist.sys.keystone.update";


    public static Lcd lcd;

    public static Vertex vTopLeftOrigin;
    public static Vertex vTopRightOrigin;
    public static Vertex vBottomLeftOrigin;
    public static Vertex vBottomRightOrigin;
    public static Vertex vTopLeft;
    public static Vertex vTopRight;
    public static Vertex vBottomLeft;
    public static Vertex vBottomRight;

    protected static int lcdValidWidth_top ;
    protected static int lcdValidWidth_bottom ;
    protected static int lcdValidHeight_left;
    protected static int lcdValidHeight_right;
    protected static int lcdWidth;
    protected static int lcdHeight;
    private static final String TAG = "ProjectorViewModel";

    //   private int progress = 0;
    protected static float vZoom;
    protected static float mode;
    private boolean is_Vertical = false;
    protected static SharedPreferences prefs;
    protected static SharedPreferences.Editor editor;
    public final int MODE_ONEPOINT = 1;
    public final int MODE_TWOPOINT = 0;
    public final int MODE_UNKOWN = -1;
    //yangxin
    private IBinder mSurfaceFlinger;
    private float mLeftBottomX = 0;
    private float mLeftBottomY = 0;
    private float mRightBottomX = 0;
    private float mRightBottomY = 0;
    private float mLeftTopX = 0;
    private float mLeftTopY = 0;
    private float mRightTopX = 0;
    private float mRightTopY = 0;
    private String projectorMode;

    private static final int MIN_ZOOM = 50;   // 50%
    private static final int MAX_ZOOM = 100;  // 100%
    private static final int STEP   = 5;      // 每步 5%

    // 当前缩放百分比，默认 100
    private int currentZoom;

    public KeystoneViewModel(Application paramApplication) {
        super(paramApplication);
        lcd = new Lcd(getApplication());
        getKeystoneOrigin();
        getInitKeystone(getApplication());
        projectorMode = SystemPropertiesUtils.getProperty("persist.sys.projection","0");
    }

    /**
     * 根据百分比计算并应用四点坐标
     * zoom 50~100 -> scale 0~1
     */
    public void applyZoom(int zoom) {
        // 限制范围
        zoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoom));
        currentZoom = zoom;
        Log.d(TAG, "applyZoom: zoom = "+zoom);

        // 把百分比映射到 0~1（0 表示最小，1 表示最大）
        float scale = (zoom - MIN_ZOOM) * 1.0f / (MAX_ZOOM - MIN_ZOOM);
        Log.d(TAG, "applyZoom: scale = "+scale);
        // 反向：scale=1 时不动；scale=0 时移动到中心
        float disX = (1 - scale) * maxXStep;   // 横向总步长
        float disY = (1 - scale) * maxYStep;   // 纵向总步长
        Log.d(TAG, "applyZoom: disX = "+disX+",disY = " + disY);

        // 同步四个点
        vTopLeft.setX((int) disX);
        vTopLeft.setY((int) disY);

        vTopRight.setX((int) disX);
        vTopRight.setY((int) disY);

        vBottomLeft.setX((int) disX);
        vBottomLeft.setY((int) disY);

        vBottomRight.setX((int) disX);
        vBottomRight.setY((int) disY);
        // 下发到 SurfaceFlinger
        updatePoint(4);
        savePoint(4);
        update();
        prefs.edit().putInt("zoom_percent", currentZoom).apply();
    }

    /**
     * 供外部调用：+1 步
     */
    public void zoomIn() {
        currentZoom = prefs.getInt("zoom_percent",100);
        Log.d(TAG, "zoomIn: 往右 currentZoom = "+currentZoom);
        applyZoom(currentZoom + STEP);
    }

    /**
     * 供外部调用：-1 步
     */
    public void zoomOut() {
        currentZoom = prefs.getInt("zoom_percent",100);
        Log.d(TAG, "zoomIn: 往左 currentZoom = "+currentZoom);
        applyZoom(currentZoom - STEP);
    }

    public void resetZoom(){
        currentZoom = 100;
        prefs.edit().putInt("zoom_percent",currentZoom).apply();
        int secZoom = prefs.getInt("zoom_percent",350);
        Log.d(TAG, "resetZoom: 重置之后的secZoom = " + secZoom);
        //updateZoomUI();
    }
    public void getKeystoneOrigin(){
        String originLT = SystemPropertiesUtils.getProperty(PROP_LT_ORIGIN,ORIGIN_NULL);
        String originRT = SystemPropertiesUtils.getProperty(PROP_RT_ORIGIN,ORIGIN_NULL);
        String originLB = SystemPropertiesUtils.getProperty(PROP_LB_ORIGIN,ORIGIN_NULL);
        String originRB = SystemPropertiesUtils.getProperty(PROP_RB_ORIGIN,ORIGIN_NULL);

        vTopLeftOrigin = new Vertex(originLT);
        vTopRightOrigin = new Vertex(originRT);
        vBottomLeftOrigin = new Vertex(originLB);
        vBottomRightOrigin = new Vertex(originRB);
        Log.d(TAG, "getInitKeystone: origin_lt("+originLT+"),origin_rt("+originRT+"),origin_lb("+originLB+"),origin_rb("+originRB+")");
    }
    public static int loadZoomPercent(Context ctx) {
        return ctx.getSharedPreferences("ty_keystone", Context.MODE_PRIVATE)
                .getInt("zoom_percent", 100);
    }
    public void getInitKeystone(Context context){
        prefs = context.getSharedPreferences("ty_keystone", Context.MODE_PRIVATE);
        editor = prefs.edit();
        String strTopLeft = prefs.getString("top_left","0,0");
        String strTopRight = prefs.getString("top_right","0,0");
        String strBottomLeft = prefs.getString("bottom_left","0,0");
        String strBottomRight = prefs.getString("bottom_right","0,0");

        zoom_x = prefs.getInt("zoom_x",0);
        zoom_y = prefs.getInt("zoom_y",0);

        vZoom = prefs.getInt("zoom",0);
        mode = prefs.getInt("mode",MODE_UNKOWN);

        vTopLeft = new Vertex(0,strTopLeft);
        vTopRight = new Vertex(1,strTopRight);
        vBottomLeft = new Vertex(3,strBottomLeft);
        vBottomRight = new Vertex(2,strBottomRight);
        /*if(mode == MODE_ONEPOINT){
            vTopLeft.setMaxX(100);vTopLeft.setMaxY(100);
            vTopRight.setMaxX(100);vTopRight.setMaxY(100);
            vBottomLeft.setMaxX(100);vBottomLeft.setMaxY(100);
            vBottomRight.setMaxX(100);vBottomRight.setMaxY(100);

            zoom_x = 0;
            zoom_y = 0;
            saveZoom();
        } else if (mode == MODE_TWOPOINT) {
            vTopLeft.setMaxX(50);vTopLeft.setMaxY(50);
            vTopRight.setMaxX(50);vTopRight.setMaxY(50);
            vBottomLeft.setMaxX(50);vBottomLeft.setMaxY(50);
            vBottomRight.setMaxX(50);vBottomRight.setMaxY(50);
        }*/
        Log.d(TAG, "getInitKeystone: lt("+strTopLeft+"),rt("+strTopRight+"),lb("+strBottomLeft+"),rb("+vBottomRight+")");
        Log.d(TAG, "getInitKeystone: zoom:"+vZoom);

        String vertical = SystemPropertiesUtils.getProperty("ro.keystone.vertical","0");
        if(vertical.equals("1")){
            is_Vertical = true;
            lcdWidth = lcd.getLcdHeight();//   dm.widthPixels;
            lcdHeight = lcd.getLcdWidth();//dm.heightPixels;
        }else {
            lcdWidth = lcd.getLcdWidth();//   dm.widthPixels;
            lcdHeight = lcd.getLcdHeight();//dm.heightPixels;
        }
        lcdValidWidth_top = lcdWidth - vTopLeftOrigin.getX() + vTopRightOrigin.getX();
        lcdValidWidth_bottom = lcdWidth - vBottomLeftOrigin.getX() + vBottomRightOrigin.getX();
        lcdValidHeight_left = lcdHeight + vTopLeftOrigin.getY() - vBottomLeftOrigin.getY();
        lcdValidHeight_right = lcdHeight + vTopRightOrigin.getY() - vBottomRightOrigin.getY();
        currentZoom = prefs.getInt("zoom_percent", 100);
    }
    public void setKeystoneMode(int _mode){
        if(_mode == MODE_ONEPOINT){
            vTopLeft.setMaxX(50);vTopLeft.setMaxY(50);
            vTopRight.setMaxX(50);vTopRight.setMaxY(50);
            vBottomLeft.setMaxX(50);vBottomLeft.setMaxY(50);
            vBottomRight.setMaxX(50);vBottomRight.setMaxY(50);

            zoom_x = 0;
            zoom_y = 0;
            saveZoom();
        } else if (_mode == MODE_TWOPOINT) {
            vTopLeft.setMaxX(50);vTopLeft.setMaxY(50);
            vTopRight.setMaxX(50);vTopRight.setMaxY(50);
            vBottomLeft.setMaxX(50);vBottomLeft.setMaxY(50);
            vBottomRight.setMaxX(50);vBottomRight.setMaxY(50);
        }
    }

    public void savePoint(int point){
        switch (point){
            case 0:
                editor.putString("top_left",vTopLeft.toString());
                editor.apply();
                break;
            case 1:
                editor.putString("top_right",vTopRight.toString());
                editor.apply();
                break;
            case 3:
                editor.putString("bottom_left",vBottomLeft.toString());
                editor.apply();
                break;
            case 2:
                editor.putString("bottom_right",vBottomRight.toString());
                editor.apply();
                break;
            default:
                editor.putString("top_left",vTopLeft.toString());
                editor.putString("top_right",vTopRight.toString());
                editor.putString("bottom_left",vBottomLeft.toString());
                editor.putString("bottom_right",vBottomRight.toString());
                editor.apply();
                break;
        }
    }

    public void updatePoint(int point){
        switch (point){
            case 0:
                updateTopLeft();
                break;
            case 1:
                updateTopRight();
                break;
            case 3:
                updateBottomLeft();
                break;
            case 2:
                updateBottomRight();
                break;
            default:
                updateTopLeft();
                updateTopRight();
                updateBottomLeft();
                updateBottomRight();
                break;
        }
        updateALL();
    }

    public void restoreKeystone(){
        vTopLeft.setX(0);vTopLeft.setY(0);
        vTopRight.setX(0);vTopRight.setY(0);
        vBottomLeft.setX(0);vBottomLeft.setY(0);
        vBottomRight.setX(0);vBottomRight.setY(0);
        savePoint(4);//save all
        updatePoint(4);//update all
        update();
    }
    public void updateTopLeft(){
        float zoomx = lcdValidWidth_top*vZoom/10/2/2;
        float movex = vTopLeft.getX()*lcd.getStepX()*(20-vZoom)/20;
        Log.d(TAG,"updateTopLeft: zoomx = "+zoomx+",movex = "+movex+",vTopLeftOrigin.getX() = "+vTopLeftOrigin.getX()+",lcd.getStepX() = "+lcd.getStepX()+",vZoom = "+vZoom);
        float x = vTopLeftOrigin.getX() + (zoomx + movex);
        if (x<0){x = Math.abs(x);}
        mLeftTopX = x;
        String xString = Float.toString(x);
        float zoomy = lcdValidHeight_left*vZoom/10/2/2;
        float movey = vTopLeft.getY()*lcd.getStepY()*(20-vZoom)/20;
        Log.d(TAG, "updateTopLeft: zoomy = "+zoomy+",movey = "+movey+",vTopLeftOrigin.getY() = "+vTopLeftOrigin.getY()+",lcd.getStepY() = "+lcd.getStepY());
        float y = vTopLeftOrigin.getY() - (zoomy + movey);
        if (y<0){y = Math.abs(y);}
        y = (float) (y * 1.75);
        mLeftTopY = y;
        String yString = Float.toString(y);
        //SystemPropertiesUtils.setProperty(PROP_LT,x+","+y);
        SystemPropertiesUtils.setProperty(PROP_LTX,xString);
        SystemPropertiesUtils.setProperty(PROP_LTY,yString);
    }


    private void updateALL(){
        try {
            try {
                Class<?> serviceManagerClass = Class.forName("android.os.ServiceManager");
                Method getServiceMethod = serviceManagerClass.getMethod("getService", String.class);

                // 调用getService方法
                mSurfaceFlinger = (IBinder) getServiceMethod.invoke(null, "SurfaceFlinger");

                // 使用IBinder对象进行后续操作
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mSurfaceFlinger != null) {
                Log.i(TAG, "ParcelToFlinger prepare for data!");
                Parcel data = Parcel.obtain();
                data.writeInterfaceToken("android.ui.ISurfaceComposer");
                Log.d(TAG, "updateALL: mLeftBottomX = " + mLeftBottomX + ",mLeftBottomY = " + mLeftBottomY + ",mLeftTopX = " +
                        mLeftTopX + ",mLeftTopY = "+ mLeftTopY+",mRightTopX="+mRightTopX+",mRightTopY="+mRightTopY+",mRightBottomX="+
                        mRightBottomX+",mRightBottomY="+mRightBottomY);
                data.writeFloat((float)((double)mLeftBottomX * 0.001));
                data.writeFloat((float)((double)mLeftBottomY * 0.001));
                data.writeFloat((float)((double)mLeftTopX * 0.001));
                data.writeFloat((float)((double)mLeftTopY * 0.001));
                data.writeFloat((float)((double)mRightTopX * 0.001));
                data.writeFloat((float)((double)mRightTopY * 0.001));
                data.writeFloat((float)((double)mRightBottomX * 0.001));
                data.writeFloat((float)((double)mRightBottomY * 0.001));
                mSurfaceFlinger.transact(1050, data, null, 0);
                data.recycle();
            } else {
                Log.i(TAG,"error get surfaceflinger service");
            }
        } catch (Exception ex) {
            Log.i(TAG,"error talk with surfaceflinger service");
        }
    }

    public void updateTopRight(){
        float zoomx = lcdValidWidth_top*vZoom/10/2/2;
        float movex = vTopRight.getX()*lcd.getStepX()*(20-vZoom)/20;
        float x = vTopRightOrigin.getX() - (zoomx + movex);
        if (x<0){x = Math.abs(x);}
        mRightTopX = x;
        String xString = Float.toString(x);
        float zoomy = lcdValidHeight_right*vZoom/10/2/2;
        float movey = vTopRight.getY()*lcd.getStepY()*(20-vZoom)/20;
        float y = vTopRightOrigin.getY() - (zoomy + movey);
        Log.d(TAG, "updateTopRight: zoomy = "+zoomy+",movey = "+movey+",vTopRightOrigin.getY() = "+vTopRightOrigin.getY()+",lcd.getStepY() = "+lcd.getStepY());
        if (y<0){y = Math.abs(y);}
        y = (float) (y * 1.75);
        mRightTopY = y;
        String yString = Float.toString(y);
        Log.d(TAG, "setTopRight: "+x+","+y+",zoom:"+vZoom+"origin("+vTopRightOrigin.getX()+","+vTopRightOrigin.getY()
                +"),zoom("+zoomx+","+zoomy+"),move("+movex+","+movey+")");
        //SystemPropertiesUtils.setProperty(PROP_RT,x+","+y);
        SystemPropertiesUtils.setProperty(PROP_RTX,xString);
        SystemPropertiesUtils.setProperty(PROP_RTY,yString);
    }

    public void updateBottomLeft(){
        float zoomx = lcdValidWidth_bottom*vZoom/10/2/2;
        float movex = vBottomLeft.getX()*lcd.getStepX()*(20-vZoom)/20;
        float mirrorx = (vTopLeft.getX()-vBottomLeft.getX())*lcd.getStepX()*(20-vZoom)*434/20/600;
        float x = 0;
        //float x = vBottomLeftOrigin.getX() + (zoomx + movex);
        float zoomy = lcdValidHeight_left*vZoom/10/2/2;
        float movey = vBottomLeft.getY()*lcd.getStepY()*(20-vZoom)/20;
        float mirrory = vTopLeft.getY()*lcd.getStepY()*(20-vZoom)*434/20/600;
        float y =0;
        if(Build.HARDWARE.equals("mt6735")){
            x = vBottomLeftOrigin.getX() + (zoomx + movex) -mirrorx;
            y = vBottomLeftOrigin.getY() + (zoomy + movey)*1034/600 +mirrory;
        }else{
            x = vBottomLeftOrigin.getX() + (zoomx + movex);
            y = vBottomLeftOrigin.getY() + (zoomy + movey);
        }
        y = (float) (y * 1.75);
        mLeftBottomX = x;
        mLeftBottomY = y;
        String xString = Float.toString(x);
        String yString = Float.toString(y);

        Log.d(TAG, "setBottomLeft: "+x+","+y+",zoom:"+vZoom+"origin("+vBottomLeftOrigin.getX()+","+vBottomLeftOrigin.getY()
                +"),zoom("+zoomx+","+zoomy+"),move("+movex+","+movey+")");
        //SystemPropertiesUtils.setProperty(PROP_LB,x+","+y);
        SystemPropertiesUtils.setProperty(PROP_LBX,xString);
        SystemPropertiesUtils.setProperty(PROP_LBY,yString);
    }

    public void updateBottomRight(){
        float zoomx = lcdValidWidth_bottom*vZoom/10/2/2;
        float movex = vBottomRight.getX()*lcd.getStepX()*(20-vZoom)/20;
        float mirrorx = (vTopRight.getX()-vBottomRight.getX())*lcd.getStepX()*(20-vZoom)*434/20/600;
        float x = 0;
        //float x = vBottomRightOrigin.getX() - (zoomx + movex);
        float zoomy = lcdValidHeight_right*vZoom/10/2/2;
        float movey = vBottomRight.getY()*lcd.getStepY()*(20-vZoom)/20;
        float mirrory = vTopRight.getY()*lcd.getStepY()*(20-vZoom)*434/20/600;
        float y =0;
        if(Build.HARDWARE.equals("mt6735")){
            x = vBottomRightOrigin.getX() - (zoomx + movex) + mirrorx;
            y = vBottomRightOrigin.getY() + (zoomy + movey)*1034/600 + mirrory;
        }else{
            x = vBottomRightOrigin.getX() - (zoomx + movex);
            y = vBottomRightOrigin.getY() + (zoomy + movey);
        }
        if (x < 0){x = Math.abs(x);}
        if (y < 0){y = Math.abs(y);}
        y = (float) (y * 1.75);
        mRightBottomX = x;
        mRightBottomY = y;
        String xString = Float.toString(x);
        String yString = Float.toString(y);

        Log.d(TAG, "setBottomRight: "+x+","+y+",zoom:"+vZoom+"origin("+vBottomRightOrigin.getX()+","+vBottomRightOrigin.getY()
                +"),zoom("+zoomx+","+zoomy+"),move("+movex+","+movey+")");
        //SystemPropertiesUtils.setProperty(PROP_RB,x+","+y);
        SystemPropertiesUtils.setProperty(PROP_RBX,xString);
        SystemPropertiesUtils.setProperty(PROP_RBY,yString);
    }

   /* public void updateTopLeft(String value){
        SystemPropertiesUtils.setProperty(PROP_LT,value);
    }
    public void updateTopRight(String value){
        SystemPropertiesUtils.setProperty(PROP_RT,value);
    }
    public void updateBottomLeft(String value){
        SystemPropertiesUtils.setProperty(PROP_LB,value);
    }
    public void updateBottomRight(String value){
        SystemPropertiesUtils.setProperty(PROP_RB,value);
    }*/
    public void update(){
        SystemPropertiesUtils.setProperty(PROP_KEYSTONE_UPDATE,"1");
    }

    public boolean isVertical(){
        return  is_Vertical;
    }

    public String getOnePointInfo(int point){
        switch (point){
            case 0:
                if (projectorMode.equals("0")){return vTopLeft.toString();}
                else if (projectorMode.equals("1")) {return vTopRight.toString();}
                else if (projectorMode.equals("2")) {return vBottomRight.toString();}
                else if (projectorMode.equals("3")) {return vBottomLeft.toString();}
            case 1:
                if (projectorMode.equals("0")){return vTopRight.toString();}
                else if (projectorMode.equals("1")) {return vTopLeft.toString();}
                else if (projectorMode.equals("2")) {return vBottomLeft.toString();}
                else if (projectorMode.equals("3")) {return vBottomRight.toString();}
            case 3:
                if (projectorMode.equals("0")){return vBottomLeft.toString();}
                else if (projectorMode.equals("1")) {return vBottomRight.toString();}
                else if (projectorMode.equals("2")) {return vTopRight.toString();}
                else if (projectorMode.equals("3")) {return vTopLeft.toString();}
            case 2:
                if (projectorMode.equals("0")){return vBottomRight.toString();}
                else if (projectorMode.equals("1")) {return vBottomLeft.toString();}
                else if (projectorMode.equals("2")) {return vTopLeft.toString();}
                else if (projectorMode.equals("3")) {return vTopRight.toString();}
            default:
                break;
        }
        return null;
    }
    public void oneLeft(int point){
        switch (point){
            case 0:
                vTopLeft.doLeft();
                break;
            case 1:
                vTopRight.doLeft();
                break;
            case 3:
                vBottomLeft.doLeft();
                break;
            case 2:
                vBottomRight.doLeft();
                break;
            default:
                break;
        }
        updatePoint(4);//updatePoint(point);
        savePoint(point);
        update();
    }
    public void oneRight(int point){
        switch (point){
            case 0:
                vTopLeft.doRight();
                break;
            case 1:
                vTopRight.doRight();
                break;
            case 3:
                vBottomLeft.doRight();
                break;
            case 2:
                vBottomRight.doRight();
                break;
            default:
                break;
        }
        updatePoint(4);//updatePoint(point);
        savePoint(point);
        update();
    }
    public void oneTop(int point){
        switch (point){
            case 0:
                vTopLeft.doTop();
                break;
            case 1:
                vTopRight.doTop();
                break;
            case 3:
                vBottomLeft.doTop();
                break;
            case 2:
                vBottomRight.doTop();
                break;
            default:
                break;
        }
        updatePoint(4);//updatePoint(point);
        savePoint(point);
        update();
    }
    public void oneBottom(int point){
        switch (point){
            case 0:
                vTopLeft.doBottom();
                break;
            case 1:
                vTopRight.doBottom();
                break;
            case 3:
                vBottomLeft.doBottom();
                break;
            case 2:
                vBottomRight.doBottom();
                break;
            default:
                break;
        }
        updatePoint(4);//updatePoint(point);
        savePoint(point);
        update();
    }
    protected static final int maxXStep = 50;
    protected static final int maxYStep = 50;

    private static int zoom_x=0;
    private static int zoom_y=0;

    public void twoLeft(){
        if(zoom_x<=0) {
            leftZoomOut();
        }else{
            rightZoomIn();
        }
        zoom_x--;
        zoom_x= zoom_x<=(-maxXStep)?(-maxXStep):zoom_x;
        savePoint(4);
        saveZoom();
        update();
    }
    public void twoRight(){
        if(zoom_x>=0){
            rightZoomOut();
        }else{
            leftZoomIn();
        }
        zoom_x++;
        zoom_x= zoom_x>=maxXStep?maxXStep:zoom_x;
        savePoint(4);
        saveZoom();
        update();
    }
    public void twoTop(){
        if(zoom_y>= 0){
            topZoomOut();
        }else{
            bottomZoomIn();
        }
        zoom_y++;
        zoom_y= zoom_y>=maxYStep?maxYStep:zoom_y;
        savePoint(4);
        saveZoom();
        update();
    }
    public void twoBottom(){
        if(zoom_y<=0){
            bottomZoomOut();
        }else{
            topZoomIn();
        }
        zoom_y--;
        zoom_y= zoom_y<=(-maxYStep)?(-maxYStep):zoom_y;
        savePoint(4);
        saveZoom();
        update();
    }
    public void leftZoomOut(){
        vTopLeft.doBottom();
        vTopLeft.doRight();
        vBottomLeft.doTop();
        vBottomLeft.doRight();

        updateTopLeft();
        updateBottomLeft();
        updateALL();
    }
    public void leftZoomIn(){
        vTopLeft.doTop();
        vTopLeft.doLeft();
        vBottomLeft.doBottom();
        vBottomLeft.doLeft();

        updateTopLeft();
        updateBottomLeft();
        updateALL();
    }
    public void rightZoomOut(){
        vTopRight.doBottom();
        vTopRight.doLeft();
        vBottomRight.doTop();
        vBottomRight.doLeft();

        updateTopRight();
        updateBottomRight();
        updateALL();
    }
    public void rightZoomIn(){
        vTopRight.doTop();
        vTopRight.doRight();
        vBottomRight.doBottom();
        vBottomRight.doRight();

        updateTopRight();
        updateBottomRight();
        updateALL();
    }
    public void topZoomOut(){
        vTopLeft.doRight();
        vTopLeft.doBottom();
        vTopRight.doLeft();
        vTopRight.doBottom();

        updateTopLeft();
        updateTopRight();
        if(Build.HARDWARE.equals("mt6735")){
            //add for 6735
            updateBottomLeft();
            updateBottomRight();
        }
        updateALL();
    }
    public void topZoomIn(){
        vTopLeft.doLeft();
        vTopLeft.doTop();
        vTopRight.doRight();
        vTopRight.doTop();

        updateTopLeft();
        updateTopRight();
        if(Build.HARDWARE.equals("mt6735")){
            //add for 6735
            updateBottomLeft();
            updateBottomRight();
        }
        updateALL();
    }
    public void bottomZoomOut(){
        vBottomRight.doLeft();
        vBottomRight.doTop();
        vBottomLeft.doRight();
        vBottomLeft.doTop();

        updateBottomLeft();
        updateBottomRight();
        updateALL();
    }
    public void bottomZoomIn(){
        vBottomRight.doRight();
        vBottomRight.doBottom();
        vBottomLeft.doLeft();
        vBottomLeft.doBottom();

        updateBottomLeft();
        updateBottomRight();
        updateALL();
    }
    public void saveZoom(){
        editor.putInt("zoom_x",zoom_x);
        editor.putInt("zoom_y",zoom_y);
        editor.apply();
    }
    public String getTwoPointXString(){
        return "("+getTwoPointXInfo()+")";
    }
    public String getTwoPointYString(){
        return "("+getTwoPointYInfo()+")";
    }
    public int getTwoPointXInfo(){
        if(is_Vertical){
            return zoom_y;
        }else {
            return zoom_x;
        }
    }
    public int getTwoPointYInfo(){
        if(is_Vertical){
            return zoom_x;
        }else {
            return zoom_y;
        }
    }
    public void resetKeystone(){
        zoom_x=0;
        zoom_y=0;
        restoreKeystone();
    }
    public void saveZoom(int progress){
        vZoom = progress;
        editor.putInt("zoom",progress);
        editor.apply();
    }
    public void setZoom(int progress){
        saveZoom(progress);

        updatePoint(4);//update all
        update();
    }
    public int getZoom(){
        Log.d(TAG,"getZoom:"+vZoom);
        return (int)vZoom;
    }
    public String getProgress(){
        return "Level: "+(int)vZoom;
    }
}
