package com.wyy.myhealth.ui.personcenter.modify.utils;

/**
 * ������ع���
 * 
 * @author lyl
 * 
 */
public class HwUtlis {

	/**
	 * �ж������Ƿ�����
	 * 
	 * @param content
	 *            ����
	 * @return ����true ��ʾ��������������
	 */
	public static boolean isWeight(String content) {
		boolean isweight = false;

		if (isNum(content)) {
			double weight = Double.valueOf(content);
			if (weight > 0 && weight < 200) {
				isweight = true;
			}
		}

		return isweight;
	}

	/**
	 * �ж�����Ƿ�����
	 * 
	 * @param content
	 *            ���
	 * @return ����true ��ʾ��������������
	 */
	public static boolean isHeight(String content) {
		boolean isheight = false;

		if (isNum(content)) {
			double height = Double.valueOf(content);
			if (height > 0 && height < 300) {
				isheight = true;
			}
		}

		return isheight;
	}

	/**
	 * �ж��ַ����Ƿ�λ����
	 * 
	 * @param content
	 * @return
	 */
	public static boolean isNum(String content) {

		boolean isnum = false;
		try {
			Double.valueOf(content);
			isnum = true;
		} catch (Exception e) {
			// TODO: handle exception
		}

		return isnum;
	}

}
