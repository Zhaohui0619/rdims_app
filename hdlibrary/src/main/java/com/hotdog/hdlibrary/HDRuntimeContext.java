package com.hotdog.hdlibrary;

import android.content.Context;
import android.content.ContextWrapper;

import com.facebook.stetho.Stetho;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.hotdog.hdlibrary.utils.DeviceUtils;
import com.hotdog.hdlibrary.utils.FileUtils;
import com.hotdog.hdlibrary.utils.StorageUtils;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.Nullable;

import bolts.Task;
import bolts.UnobservedTaskException;

/**
 * 全局Context,仍然属于客户端,但是与客户端区分开,只作为公共库的初始化环境
 */

public final class HDRuntimeContext extends ContextWrapper {

    private static HDRuntimeContext INSTANCE;

    public static HDRuntimeContext get() {
        if (null == INSTANCE) {
            throw new IllegalStateException("HDRuntimeContext must be initialized first!");
        }
        return INSTANCE;
    }

    public static void init(Context app, boolean DEBUG) {
        if (app == null) {
            return;
        }
        INSTANCE = new HDRuntimeContext(app);
        INSTANCE.debug = DEBUG;
        INSTANCE.init();
    }

    private HDRuntimeContext(Context base) {
        super(base);
    }

    private boolean debug = true;

    public boolean isDebug() {
        return debug;
    }

    /**
     * 初始化公共库里面的类
     */
    private void init() {
	    if (debug)//调试相关库,只有在debug模式下才打开
	    {
		    Stetho.initializeWithDefaults(this);
	    }
	    {//logger相关配置
		    String pkgName = Iterables.getLast(Splitter.on(".").split(this.getPackageName()));
		    //logger相关配置
		    FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
				    //.showThreadInfo(true)   // (Optional) Whether to show thread info or not. Default true
				    //.methodCount(2)         // (Optional) How many method line to show. Default 2
				    //.methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
				    //.logStrategy(customLog) // (Optional) Changes the log strategy to print out. Default LogCat
				    .tag(pkgName)           // (Optional) Global tag for every log. Default PRETTY_LOGGER
				    .build();
		    Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
			    @Override
			    public boolean isLoggable(int priority, String tag) {
				    return debug;
			    }
		    });
		    DeviceUtils.printDeviceInfo(this);
	    }
	    //task相关配置,打印未处理的异常信息
	    Task.setUnobservedExceptionHandler(new Task.UnobservedExceptionHandler() {
		    @Override
		    public void unobservedException(Task<?> t, UnobservedTaskException e) {
			    Logger.t("Task").e(e, "UnobservedTaskException of task %s", t);
		    }
	    });
	    Task.callInBackground(new Callable<Object>() {
		    @Override
		    public Object call() throws Exception {
		    	File tempDir = new File(StorageUtils.getTempDirectory(HDRuntimeContext.this));
			    List<File> files2Delete = Lists.newArrayList(tempDir.listFiles(new FileFilter() {
				    @Override
				    public boolean accept(File pathname) {
					    return pathname.lastModified() + StorageUtils.TEMP_FILE_EXISTS_TIME < System.currentTimeMillis();
				    }
			    }));
			    return Iterables.filter(files2Delete, new Predicate<File>() {
				    @Override
				    public boolean apply(@Nullable File input) {
					    try {
						    FileUtils.forceDelete(input);
					    } catch (IOException e) {
						    e.printStackTrace();
					    }
					    return true;
				    }
			    });
		    }
	    });
    }

}
