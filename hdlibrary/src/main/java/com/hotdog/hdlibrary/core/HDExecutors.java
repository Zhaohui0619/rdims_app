package com.hotdog.hdlibrary.core;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import bolts.Task;

public final class HDExecutors {

	private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

	/**
	 * 主线程执行
	 */
	public static final Executor UI = new Executor() {
		@Override
		public void execute(@NonNull Runnable command) {
			MAIN_HANDLER.post(command);
		}
	};

	/**
	 * 后台线程执行
	 */
	public static final Executor BACKGROUND = Task.BACKGROUND_EXECUTOR;

	/**
	 * 排队执行
	 */
	public static final Executor SCHEDULED = Executors.newSingleThreadScheduledExecutor();


	public static void runOnUIThread(Runnable runnable) {
		UI.execute(runnable);
	}

	public static void runOnUIThreadDelayed(final Runnable runnable, final long delay) {
		MAIN_HANDLER.postDelayed(runnable, delay);
	}

	public static void runOnBackgroundThread(Runnable runnable) {
		BACKGROUND.execute(runnable);
	}

	public static void runScheduled(Runnable runnable) {
		SCHEDULED.execute(runnable);
	}

}
