package com.wyy.myhealth.bean;

/**
 * ɹһɹ����ʳ��
 * 
 * @author lyl
 * 
 */
public class ShaiFoods extends Foods {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// ͷ���ַ
	private String headimage;
	// ����
	private String comment_content;
	// ������ID
	private String com_userid;
	// �û���
	private String username;
	//����������
	private String comment_user_name;
	
	public String getComment_user_name() {
		return comment_user_name;
	}

	public void setComment_user_name(String comment_user_name) {
		this.comment_user_name = comment_user_name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getHeadimage() {
		return headimage;
	}

	public void setHeadimage(String headimage) {
		this.headimage = headimage;
	}

	public String getComment_content() {
		return comment_content;
	}

	public void setComment_content(String comment_content) {
		this.comment_content = comment_content;
	}

	public String getCom_userid() {
		return com_userid;
	}

	public void setCom_userid(String com_userid) {
		this.com_userid = com_userid;
	}

}
