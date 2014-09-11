package com.wyy.myhealth.db.utils;

import java.util.ArrayList;
import java.util.List;

import com.wyy.myhealth.bean.CollectData;
import com.wyy.myhealth.bean.ListDataBead;
import com.wyy.myhealth.db.WyyDatebase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class CollectDatabaseUtils implements DatebaseInterface {

	public CollectDatabaseUtils(Context context){
		WyyDatebase.getMyDatabase(context);
	}
	
	@Override
	public Object update(String json, int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long update(String json, int postion, int id) {
		// TODO Auto-generated method stub
		long updatepostion=0;
    	try {
	    	//ʹ��execSQL��������в�������
	    	//db.execSQL("INSERT INTO USER(NAME) VALUES('"+name+"')");
	
	    	//ʹ��insert��������в�������
	    	//����ContentValues����洢"����-��ֵ"ӳ��
	    	ContentValues values = new ContentValues();	
	    	values.put(CollectData.JSONDATA, json);
	    	values.put(CollectData.COLLECT_POSTION, postion);
	    	String where =  CollectData._ID+" = ?";
		    String[] whereValue = { String.valueOf(id) };
	    	//���÷�����������
	    	updatepostion=WyyDatebase.wyyDatabase.update(CollectData.TABLE_NAME, values, where, whereValue);
    	} catch (Exception e) {
			e.printStackTrace();
		}
		return updatepostion;
	}

	@Override
	public Object delete(long id) {
		// TODO Auto-generated method stub
		int del=-1;
		 try{
	    	 //Object[] bindArgs = {id};
	    	 //db.execSQL("DELETE FROM USER WHERE _ID = ? ",bindArgs);
			 String where =  CollectData._ID+" = ?";
	    	 String[] whereArgs = {String.valueOf(id)};
	    	 del=WyyDatebase.wyyDatabase.delete(CollectData.TABLE_NAME, where, whereArgs);
	    }catch (Exception e){
	      e.printStackTrace();
	    }
		return del;
	}

	@Override
	public long insert(String json, int postion) {
		// TODO Auto-generated method stub
		long id = -1;
    	try {
	    	//ʹ��execSQL��������в�������
	    	//db.execSQL("INSERT INTO USER(NAME) VALUES('"+name+"')");
	
	    	//ʹ��insert��������в�������
	    	//����ContentValues����洢"����-��ֵ"ӳ��
	    	ContentValues values = new ContentValues();	
	    	values.put(CollectData.JSONDATA, json);
	    	values.put(CollectData.COLLECT_POSTION, postion);
	    	//���÷�����������
	    	id = WyyDatebase.wyyDatabase.insert(CollectData.TABLE_NAME, null, values);	
    	} catch (Exception e) {
			e.printStackTrace();
		}
		return id;
	}

	@Override
	public Object queryData() {
		// TODO Auto-generated method stub
		List<ListDataBead> list=new ArrayList<ListDataBead>();
		Cursor cursor = null;
    	try {
			//������ݿ����,������ݿⲻ�����򴴽�
			//��ѯ��������,��ȡ�α�
			cursor = WyyDatebase.wyyDatabase.query(CollectData.TABLE_NAME,null,null,null,null,null,"_ID desc");
//			//��ȡname�е�����
			int idIndex = cursor.getColumnIndex(CollectData._ID);	    
			 //��ȡjson�е�����
			int jsonIndex = cursor.getColumnIndex(CollectData.JSONDATA);	
			
			int postionIndex=cursor.getColumnIndex(CollectData.COLLECT_POSTION);
      
		    //������ѯ���������������ȡ����
		    for(cursor.moveToFirst();!(cursor.isAfterLast());cursor.moveToNext()){	
					  ListDataBead listDataBead=new ListDataBead();
					  listDataBead.setJsondata(cursor.getString(jsonIndex));
					  listDataBead.setPostion(cursor.getInt(postionIndex));
					  listDataBead.set_id(cursor.getInt(idIndex));
					  list.add(listDataBead);
		    }
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(cursor!=null){
				//�ر��α�
				cursor.close();		
			}
			
		}
    	
    	return list;
	}

	@Override
	public Object queryDataId(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		if(WyyDatebase.wyyDatabase!=null && WyyDatebase.wyyDatabase.isOpen()){
			//�ر����ݿ����
			WyyDatebase.wyyDatabase.close();		
			WyyDatebase.wyyDatabase=null;
		}
	}

	@Override
	public long deleteAll() {
		// TODO Auto-generated method stub
		
		long del=-1;
		
		try {
//			 String where =  CollectData._ID+" = ?";
			del=WyyDatebase.wyyDatabase.delete(CollectData.TABLE_NAME, null, null);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return del;
	}

	@Override
	public long insert(String json, String time) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long delete() {
		// TODO Auto-generated method stub
		return 0;
	}


}
