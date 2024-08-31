package com.thucfb.qw.android.lawnchairdoubleclickrecentfix;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import java.util.ArrayList;
import java.util.WeakHashMap;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

@SuppressWarnings({"unused"})
public class HookLoadPackage implements IXposedHookLoadPackage {
	@Override
	public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
		class RecentsViewData {
			public int onRecentsAnimationCompleteRunning = 0;
			public int tvIdBeforeAnimation = -1;
			public final ArrayList<Integer> tvIdBeforeGetNextTask = new ArrayList<>();
		}
		WeakHashMap<Object, RecentsViewData> recentsViewDataMap = new WeakHashMap<>();

		findAndHookMethod("com.android.quickstep.views.RecentsView", lpparam.classLoader, "onRecentsAnimationComplete", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) {
				RecentsViewData data = recentsViewDataMap.computeIfAbsent(param.thisObject, k -> new RecentsViewData());
				data.onRecentsAnimationCompleteRunning++;
				data.tvIdBeforeAnimation = XposedHelpers.getIntField(param.thisObject, "mRunningTaskViewId");
			}

			@Override
			protected void afterHookedMethod(MethodHookParam param) {
				RecentsViewData data = recentsViewDataMap.computeIfAbsent(param.thisObject, k -> new RecentsViewData());
				data.onRecentsAnimationCompleteRunning--;
			}
		});

		findAndHookMethod("com.android.quickstep.views.RecentsView", lpparam.classLoader, "setCurrentTask", int.class, new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) {
				RecentsViewData data = recentsViewDataMap.computeIfAbsent(param.thisObject, k -> new RecentsViewData());
				if (data.onRecentsAnimationCompleteRunning <= 0)
					data.tvIdBeforeAnimation = -1;
			}
		});

		findAndHookMethod("com.android.quickstep.OverviewCommandHelper", lpparam.classLoader, "getNextTask", "com.android.quickstep.views.RecentsView", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) {
				RecentsViewData data = recentsViewDataMap.computeIfAbsent(param.args[0], k -> new RecentsViewData());
				if (data.tvIdBeforeAnimation != -1) {
					data.tvIdBeforeGetNextTask.add(XposedHelpers.getIntField(param.args[0], "mRunningTaskViewId"));
					XposedHelpers.setIntField(param.args[0], "mRunningTaskViewId", data.tvIdBeforeAnimation);
				} else {
					data.tvIdBeforeGetNextTask.add(null);
				}
			}

			@Override
			protected void afterHookedMethod(MethodHookParam param) {
				RecentsViewData data = recentsViewDataMap.computeIfAbsent(param.args[0], k -> new RecentsViewData());
				if (!data.tvIdBeforeGetNextTask.isEmpty()) {
					Integer v = data.tvIdBeforeGetNextTask.get(data.tvIdBeforeGetNextTask.size() - 1);
					if (v != null)
						XposedHelpers.setIntField(param.args[0], "mRunningTaskViewId", v);
					data.tvIdBeforeGetNextTask.remove(data.tvIdBeforeGetNextTask.size() - 1);
				}
			}
		});
	}
}
