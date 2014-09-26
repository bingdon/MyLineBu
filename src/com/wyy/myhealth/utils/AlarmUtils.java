package com.wyy.myhealth.utils;

import java.util.Calendar;
import java.util.TimeZone;

import com.wyy.myhealth.contants.ConstantS;
import com.wyy.myhealth.receiver.PostFootReceiver;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * ���ӹ�����
 * 
 * @author lyl
 * 
 */
public class AlarmUtils {

	private static AlarmManager alarmManager;
	private static final long day=1000*60*60*24;

	public static boolean setPostWFoot(Context context) {
		if (isPosted(context)) {
			return true;
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		calendar.set(Calendar.HOUR_OF_DAY, 11);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		BingLog.i("����", "time:" + calendar.toString());
		alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, PostFootReceiver.class);
		intent.setAction(ConstantS.POST_FOOT_ACTION);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
				intent, 0);
		long day = 1000 * 6;
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(), day, pendingIntent);
		setSharePre(context);
		return true;
	}

	/**
	 * 7������
	 * 
	 * @param context
	 * @return
	 */
	public static boolean setPostWFoot7(Context context) {

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 7);
		
		alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, PostFootReceiver.class);
		intent.setAction(ConstantS.POST_FOOT_ACTION);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 100,
				intent, 0);
		
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				AlarmUtils.day, pendingIntent);
		return true;
	}

	/**
	 * 11������
	 * 
	 * @param context
	 * @return
	 */
	public static boolean setPostWFoot11(Context context) {
		if (isPosted(context)) {
			return true;
		}

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 11);

		alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, PostFootReceiver.class);
		intent.setAction(ConstantS.POST_FOOT_ACTION);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 101,
				intent, 0);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				AlarmUtils.day, pendingIntent);
		return true;
	}

	/**
	 * ����8������
	 * 
	 * @param context
	 * @return
	 */
	public static boolean setPostWFoot20(Context context) {
		if (isPosted(context)) {
			return true;
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		calendar.set(Calendar.HOUR_OF_DAY, 20);

		alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, PostFootReceiver.class);
		intent.setAction(ConstantS.POST_FOOT_ACTION);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 102,
				intent, 0);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				AlarmUtils.day, pendingIntent);
		return true;
	}

	/**
	 * ��������״̬
	 * 
	 * @param context
	 */
	private static void setSharePre(Context context) {

		SharedPreferences sharedPreferences = context.getSharedPreferences(
				ConstantS.POST_FOOT_ACTION, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("AlarmUtils", true);
		editor.commit();
	}

	public static void deleteAlramstate(Context context) {
		context.getSharedPreferences(ConstantS.POST_FOOT_ACTION,
				Context.MODE_PRIVATE).edit().clear().commit();
	}

	/**
	 * �ж��Ƿ��Ѿ���������
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isPosted(Context context) {

		SharedPreferences sharedPreferences = context.getSharedPreferences(
				ConstantS.POST_FOOT_ACTION, Context.MODE_PRIVATE);
		if (sharedPreferences.getBoolean("AlarmUtils", false)) {
			return true;
		}

		return false;
	}

	/**
	 * ɾ������
	 * 
	 * @param context
	 */
	public static void deleteAlram(Context context) {
		alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, PostFootReceiver.class);
		intent.setAction(ConstantS.POST_FOOT_ACTION);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
				intent, 0);
		alarmManager.cancel(pendingIntent);
	}

	/**
	 * ��������ʱ�������
	 * 
	 * @param context
	 *            ������
	 */
	public static boolean setAler(Context context) {
		if (isPosted(context)) {
			return true;
		}
		setPostWFoot7(context);
		setPostWFoot11(context);
		setPostWFoot20(context);

		setSharePre(context);
		return true;
	}

}
