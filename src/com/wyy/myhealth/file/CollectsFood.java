package com.wyy.myhealth.file;

/**
 * 
 * @author lyl
 * 
 */
public class CollectsFood {
	// �ղ�ʱ��
	private String time;
	// �ղ��·�
	private String month;
	// �ղ�����
	private String day;
	// �ղ�ʳ��id
	private String foodid;
	// �ղ�id
	private String collect_id;
	// ͼƬ��ַ
	private String foodpic;
	//ʳ���ǩ
	private String tag;
	//ʳ���ζ����
	private String tastelevel;
	//����
	private String energy;
	//�ղش���
	private String collectcount;
	//�޴���
	private String laudcount;
	
	private String commentcount;
	private String summary;
	
	public String getCommentcount() {
		return commentcount;
	}

	public void setCommentcount(String commentcount) {
		this.commentcount = commentcount;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getCollectcount() {
		return collectcount;
	}

	public void setCollectcount(String collectcount) {
		this.collectcount = collectcount;
	}

	public String getLaudcount() {
		return laudcount;
	}

	public void setLaudcount(String laudcount) {
		this.laudcount = laudcount;
	}

	public String getEnergy() {
		return energy;
	}

	public void setEnergy(String energy) {
		this.energy = energy;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getTastelevel() {
		return tastelevel;
	}

	public void setTastelevel(String tastelevel) {
		this.tastelevel = tastelevel;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	//ʳ������
	private String type;

	public String getFoodpic() {
		return foodpic;
	}

	public void setFoodpic(String foodpic) {
		this.foodpic = foodpic;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getFoodid() {
		return foodid;
	}

	public void setFoodid(String foodid) {
		this.foodid = foodid;
	}

	public String getCollect_id() {
		return collect_id;
	}

	public void setCollect_id(String collect_id) {
		this.collect_id = collect_id;
	}
}
