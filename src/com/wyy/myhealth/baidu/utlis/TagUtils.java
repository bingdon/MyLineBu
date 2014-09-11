package com.wyy.myhealth.baidu.utlis;

import java.util.List;

import android.content.Context;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;

/**
 * �ٶȱ�ǩ������
 * 
 * @author lyl
 * 
 */
public class TagUtils {

	/**
	 * ���ñ�ǩ
	 * 
	 * @param tag
	 */
	public static void setTag(String tag, Context context) {
		if (!Utils.hasBind(context)) {
			PushManager.startWork(context, PushConstants.LOGIN_TYPE_API_KEY,
					Utils.getMetaValue(context, "api_key"));
			// Push: �������ڵ���λ�����ͣ����Դ�֧�ֵ���λ�õ����͵Ŀ���
			PushManager.enableLbs(context);
		}
		List<String> tags = Utils.getTagsList(tag);
		PushManager.setTags(context, tags);

	}

}
