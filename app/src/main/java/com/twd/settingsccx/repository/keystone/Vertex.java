package com.twd.settingsccx.repository.keystone;

import android.util.Log;

public class Vertex {
    public int x = 0;
    public int y = 0;
    public int point = 0;
    public int maxXStep =50;
    public int maxYStep=50;
    private static final String TAG = "Vertex";

    public void setX(int value){
        x = value;
    }
    public void setY(int value){
        y = value;
    }
    public void setMaxX(int value){
        maxXStep = value;
    }
    public void setMaxY(int value){
        maxYStep = value;
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public int getPoint(){return point;}
    public Vertex(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public Vertex(int point,int x, int y) {
        this.point = point;
        this.x = x;
        this.y = y;
    }
    public Vertex(String strVertex) {
        String v[] = strVertex.trim().split(",");
        if (v != null) {
            //this.x = Integer.parseInt(v[0]);
            //this.y = Integer.parseInt(v[1]);
            this.x = (int)Float.parseFloat(v[0]);
            this.y = (int)Float.parseFloat(v[1]);
        }
    }
    public Vertex(int point,String strVertex) {
        this.point = point;
        String v[] = strVertex.trim().split(",");
        if (v != null) {
            //this.x = Integer.parseInt(v[0]);
            //this.y = Integer.parseInt(v[1]);
            this.x = (int)Float.parseFloat(v[0]);
            this.y = (int)Float.parseFloat(v[1]);
        }
    }

    @Override
    public String toString() {
        return x + "," + y;
    }

    public void doLeft(){
        switch (point){
            case 0:
                Log.d(TAG, "doLeft: case0");
                x = x - 1;
                if(x<0){
                    x = 0;
                }
                break;
            case 3:
                Log.d(TAG, "doLeft: case3");
                x = x - 1;
                if(x<0){
                    x = 0;
                }
                break;
            case 1:
                Log.d(TAG, "doLeft: case1");
                x = x + 1;
                if(x > maxXStep){
                    x = maxXStep;
                }
                break;
            case 2:
                Log.d(TAG, "doLeft: case2");
                x = x + 1;
                if(x > maxXStep){
                    x = maxXStep;
                }
                break;
            default:
                break;
        }
    }
    public void doRight(){
        switch (point){
            case 0:
                Log.d(TAG, "doRight: case0");
                Log.d(TAG, "doRight: case3");
                x = x + 1;
                if(x>maxXStep){
                    x = maxXStep;
                }
                break;
            case 3:
                Log.d(TAG, "doRight: case3");
                x = x + 1;
                if(x>maxXStep){
                    x = maxXStep;
                }
                break;
            case 1:
                Log.d(TAG, "doRight: case1");
                x = x - 1;
                if(x < 0){
                    x = 0;
                }
                break;
            case 2:
                Log.d(TAG, "doRight: case2");
                x = x - 1;
                if(x < 0){
                    x = 0;
                }
                break;
            default:
                break;
        }
    }
    public void doTop(){
        switch (point){
            case 0:
                Log.d(TAG, "doTop: case0");
                y = y - 1;
                if(y<0){
                    y = 0;
                }
                break;
            case 1:
                Log.d(TAG, "doTop: case1");
                y = y - 1;
                if(y<0){
                    y = 0;
                }
                break;
            case 2:
                Log.d(TAG, "doTop: case2");
                y = y + 1;
                if(y>maxYStep){
                    y = maxYStep;
                }
                break;
            case 3:
                Log.d(TAG, "doTop: case3");
                y = y + 1;
                if(y>maxYStep){
                    y = maxYStep;
                }
                break;
            default:
                break;
        }
    }
    public void doBottom(){
        switch (point){
            case 0:
                Log.d(TAG, "doBottom: case0");
                y = y + 1;
                if(y>maxYStep){
                    y = maxYStep;
                }
                break;
            case 1:
                Log.d(TAG, "doBottom: case1");
                y = y + 1;
                if(y>maxYStep){
                    y = maxYStep;
                }
                break;
            case 2:
                Log.d(TAG, "doBottom: case2");
                y = y - 1;
                if(y<0){
                    y = 0;
                }
                break;
            case 3:
                Log.d(TAG, "doBottom: case3");
                y = y - 1;
                if(y<0){
                    y = 0;
                }
                break;
            default:
                break;
        }
    }


}
