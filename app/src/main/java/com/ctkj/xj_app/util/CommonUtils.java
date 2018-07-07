package com.ctkj.xj_app.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.ctkj.xj_app.bean.InspectExceptionVoA;
import com.google.gson.Gson;
import com.hotdog.hdlibrary.encrypt.Digest;
import com.hotdog.hdlibrary.utils.StorageUtils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * created by zhaohui on 2018/5/8 15:00
 */

public class CommonUtils {

    public static String getJson(String fileName, Context context) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 得到巡检路径的模拟数据
     * @param fileName 文件名
     * @param context 上下文
     * @return
     */
    public static ArrayList<LatLng> getPositionList(String fileName, Context context){
        ArrayList<LatLng> positionList = new ArrayList<>(0);
        AssetManager assetManager = context.getAssets();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            String regex = "[\\d\\.,]+";
            Pattern pattern = Pattern.compile(regex);
            LatLng latLng = null;
            while ((line = in.readLine())!=null){
                Matcher m= pattern.matcher(line);
                while (m.find()){
                   String str = m.group();
                   String[] p = str.split(",");
                    Double latitude = Double.parseDouble(p[1]);
                    Double longitude = Double.parseDouble(p[0]);
                    latLng = new LatLng(latitude,longitude);
                   positionList.add(latLng);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return positionList;
    }

    public static ArrayList<String> getInspectPath(String fileName,Context context){
        ArrayList<String> inspectPathList = new ArrayList<>(0);
        AssetManager assetManager = context.getAssets();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            String regex = "[\\d\\.,]+";
            Pattern pattern = Pattern.compile(regex);
            while ((line = in.readLine())!=null){
                Matcher m= pattern.matcher(line);
                while (m.find()){
                    inspectPathList.add(m.group());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inspectPathList;
    }

    /**
     * 得到配置文件类
     * @param context 上下文
     * @param fileName  assets底下的文件名
     * @return
     */
    public static Properties getProperties(Context context,String fileName){
        Properties urlProps;
        Properties props = new Properties();

        try{
            InputStream in = context.getAssets().open(fileName);
            props.load(in);
        }catch (Exception e){
            e.printStackTrace();
        }

        urlProps = props;
        return urlProps;
    }

    /**
     * 巡检计划或巡检任务的时间生成 (yyyy-MM-dd HH:mm:ss - yyyy-MM-dd HH:mm:ss)
     * @param dateStr1 开始时间
     * @param dateStr2 结束时间
     * @return
     */
    public static String getTaskOrPlanTime(String dateStr1,String dateStr2 ){

        SimpleDateFormat sf1 = new SimpleDateFormat("EEE MMM dd hh:mm:ss z yyyy", Locale.ENGLISH);
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = sf1.parse(dateStr1);
            date2 = sf1.parse(dateStr2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String date = "时间："+sf2.format(date1)+" - "+sf2.format(date2);
        return date;

    }

    /**
     * 生成.json文件
     * @param mContext 上下文
     * @param exceptionVoA 异常Vo类
     */
    public static void createJsonFile(Context mContext, InspectExceptionVoA exceptionVoA){
        Gson gson = new Gson();
        String exception = gson.toJson(exceptionVoA);
        String name = Digest.md5Hex(UUID.randomUUID().toString().getBytes()).toUpperCase();
        File jsonFile = new File(StorageUtils.getFileDirectory(mContext), name + ".json");

        try {
            Writer writer = new OutputStreamWriter(new FileOutputStream(jsonFile),"UTF-8");
            writer.write(exception);
            writer.flush();
            writer.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 生成格式化日期
     * @return 格式化之后的日期 (yyyy-MM-dd HH:mm:ss)
     */
    public static Date getFormatDate() {
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat lf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTimeStr = lf.format(new Date());
        DateTime dateTime = format.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    public static String getFormatDateStr(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }
}
