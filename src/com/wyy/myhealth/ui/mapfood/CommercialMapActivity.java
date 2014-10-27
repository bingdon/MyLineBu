package com.wyy.myhealth.ui.mapfood;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.MyLocationOverlay.LocationMode;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKGeocoderAddressComponent;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionInfo;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.wyy.myhealth.R;
import com.wyy.myhealth.analytics.UmenAnalyticsUtility;
import com.wyy.myhealth.app.PreferencesFoodsInfo;
import com.wyy.myhealth.app.WyyApplication;
import com.wyy.myhealth.bean.Foods;
import com.wyy.myhealth.bean.NearFoodBean;
import com.wyy.myhealth.ui.baseactivity.BaseActivity;
import com.wyy.myhealth.ui.fooddetails.FoodDetailsActivity;
import com.wyy.myhealth.ui.mapfood.MapFoodsActivity.MyLocationListenner;
import com.wyy.myhealth.ui.mapfood.MapFoodsActivity.MyOverlay;
import com.wyy.myhealth.ui.yaoyingyang.YaoyingyangFragment;
import com.wyy.myhealth.utils.FoodsUtil;
import com.wyy.myhealth.utils.ImageUtil;

public class CommercialMapActivity extends BaseActivity {

	
	private static final String TAG=CommercialMapActivity.class.getSimpleName();

	private static final double SCALE = 1000000.0;
	private static final int INIT_LEVEL = 17;
	private static final int SHOW_LEVEL = 15;
	private static final double HALF = 0.5;
	/**
	 * pu MapView �ǵ�ͼ���ؼ�
	 */
	private  MapView mMapView = null;
	/**
	 * ��MapController��ɵ�ͼ����
	 */
	static MyOverlay mOverlay = null;

	// ��λ���
	LocationClient mLocClient;
	LocationData locData = null;
	public MyLocationListenner myListener = new MyLocationListenner();
	boolean isRequest = false;// �Ƿ��ֶ���������λ
	boolean isFirstLoc = true;// �Ƿ��״ζ�λ
	HashMap<OverlayItem, Foods> imemFoods = new HashMap<OverlayItem, Foods>();
	public static ArrayList<Foods> foodsList;
	public static Foods foods;
	// ��λͼ��
	MyLocationOverlay myLocationOverlay = null;

	/**
	 * MKMapViewListener ���ڴ����ͼ�¼��ص�
	 */
	MKMapViewListener mMapListener = null;


	// public static EditText edit_search;
	private MKSearch mSearch = null; // ����ģ�飬Ҳ��ȥ����ͼģ�����ʹ��
	/**
	 * �����ؼ������봰��
	 */
	private ArrayAdapter<String> sugAdapter = null;
	private String city = null;
	
	private TextView commercialname;
	
	private TextView commercialtel;
	
	private TextView commercialaddress;
	
	
	@Override
	protected void onInitActionBar() {
		// TODO Auto-generated method stub
		super.onInitActionBar();
		getSupportActionBar().setTitle(R.string.commerialmap);
	}
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WyyApplication app = (WyyApplication) this.getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(this);
			/**
			 * ���BMapManagerû�г�ʼ�����ʼ��BMapManager
			 */
			app.mBMapManager.init(WyyApplication.BAIDU_KEY,
					new MKGeneralListener() {
						
						@Override
						public void onGetPermissionState(int arg0) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onGetNetworkState(int arg0) {
							// TODO Auto-generated method stub
							
						}
					});
		}
		setContentView(R.layout.activity_commeria);

		mMapView = (MapView) findViewById(R.id.foodsmapView);


		/**
		 * ��ʾ�������ſؼ�
		 */
		mMapView.setBuiltInZoomControls(false);

		mOverlay = new MyOverlay(getResources().getDrawable(
				R.drawable.icon_marka), mMapView, this);
		mMapView.getOverlays().add(mOverlay);
		createMapListener();


		/**
		 * ���õ�ͼ�Ƿ���Ӧ����¼� .
		 */
		mMapView.getController().setZoom(INIT_LEVEL);
		mMapView.getController().enableClick(true);
		//���ӽǶ�
		mMapView.getController().setOverlooking(-45);
		
		mMapView.refresh();

		// ��λ��ʼ��
		mLocClient = new LocationClient(this);
		locData = new LocationData();

		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// ��gps
		option.setCoorType("bd09ll"); // ������������
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		// mLocClient.start();

		// ��λͼ���ʼ��
		myLocationOverlay = new MyLocationOverlay(mMapView);
		myLocationOverlay.setLocationMode(LocationMode.NORMAL);
		// ���ö�λ����
		myLocationOverlay.setData(locData);
		// λ����ʽͼƬ
		Drawable drawable = getResources().getDrawable(R.drawable.zhuye_gprs);
		drawable.setBounds(10, 10, 50, 50);
		myLocationOverlay.setMarker(drawable);

		// ��Ӷ�λͼ��
		mMapView.getOverlays().add(myLocationOverlay);

		if (YaoyingyangFragment.isdingwei) {
			Intent intent = getIntent();
			double lat = 0;
			double lon = 0;
			
			commercialname=(TextView)findViewById(R.id.commericalname);
			commercialtel=(TextView)findViewById(R.id.commericaltel);
			commercialaddress=(TextView)findViewById(R.id.commericaladdress);
			
			NearFoodBean nearFoodBean=(NearFoodBean) intent.getSerializableExtra("foods");
			
			
			if (null!=nearFoodBean) {
				commercialname.setText(""+nearFoodBean.getCommercialName());
				commercialtel.setText(""+nearFoodBean.getCommercialPhone());
				commercialaddress.setText(""+nearFoodBean.getCommercialAddress());
				
				try {
					lat=Double.valueOf(nearFoodBean.getCommercialLat());
					lon=Double.valueOf(nearFoodBean.getCommercialLon());
					
					Log.i(TAG, "����:"+lat+"γ��:"+lon);
					
					GeoPoint point = new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
					mMapView.getController().setCenter(point);
					resetOverlay();
					createSearch();
					
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}
			
			
			YaoyingyangFragment.isdingwei = false;
			return;
		}

		mLocClient.start();

		createSearch();

	}
	
	
	// ������ͼ�¼�
		public void createMapListener() {
			/**
			 * MapView������������Activityͬ������activity����ʱ�����MapView.onPause()
			 */
			mMapListener = new MKMapViewListener() {
				@Override
				public void onMapMoveFinish() {
					/**
					 * �ڴ˴����ͼ�ƶ���ɻص� ���ţ�ƽ�ƵȲ�����ɺ󣬴˻ص�������
					 */
					resetOverlay();
				}

				@Override
				public void onClickMapPoi(MapPoi mapPoiInfo) {
					/**
					 * �ڴ˴����ͼpoi����¼� ��ʾ��ͼpoi���Ʋ��ƶ����õ� ���ù��� ʱ���˻ص����ܱ�����
					 */
					@SuppressWarnings("unused")
					String title = "";
					// if (mapPoiInfo != null){
					// title = mapPoiInfo.strText;
					// Toast.makeText(FoodsMapActivity.this,title,Toast.LENGTH_SHORT).show();
					// mMapController.animateTo(mapPoiInfo.geoPt);
					// }
				}

				@Override
				public void onGetCurrentMap(Bitmap b) {
					/**
					 * �����ù� mMapView.getCurrentMap()�󣬴˻ص��ᱻ���� ���ڴ˱����ͼ���洢�豸
					 */
				}

				@Override
				public void onMapAnimationFinish() {
					/**
					 * ��ͼ��ɴ������Ĳ�������: animationTo()���󣬴˻ص�������
					 */
				}

				/**
				 * �ڴ˴����ͼ������¼�
				 */
				@Override
				public void onMapLoadFinish() {
					Toast.makeText(CommercialMapActivity.this, "��ͼ�������", Toast.LENGTH_SHORT)
							.show();
					// requestLocClick();
					// resetOverlay();
				}
			};
			mMapView.regMapViewListener(WyyApplication.getInstance().mBMapManager,
					mMapListener);

		}


		/**
		 * �޸�λ��ͼ��
		 * 
		 * @param marker
		 */
		public void modifyLocationOverlayIcon(Drawable marker) {
			// ������markerΪnullʱ��ʹ��Ĭ��ͼ�����
			myLocationOverlay.setMarker(marker);
			// �޸�ͼ�㣬��Ҫˢ��MapView��Ч
			mMapView.refresh();
		}

		/**
		 * �������Overlay
		 * 
		 * @param view
		 */
		public void resetOverlay() {
			Toast.makeText(CommercialMapActivity.this, "������ʳ����", Toast.LENGTH_SHORT).show();
			new Thread() {
				@Override
				public void run() {// ��Ҫִ�еķ���

					if (mMapView.getZoomLevel() > SHOW_LEVEL) {
						GeoPoint gp = mMapView.getMapCenter();
						// Log.i("resetOverlay", "mMapView.getZoomLevel() ��"
						// + mMapView.getZoomLevel());
						// Log.i("resetOverlay", "���ȿ� ��" +
						// mMapView.getLongitudeSpan());
						
						
						try {
							double minx = (gp.getLongitudeE6() - mMapView
									.getLongitudeSpan() * HALF)
									/ SCALE;
							double miny = (gp.getLatitudeE6() - mMapView
									.getLatitudeSpan() * HALF)
									/ SCALE;
							double maxx = (gp.getLongitudeE6() + mMapView
									.getLongitudeSpan() * HALF)
									/ SCALE;
							double maxy = (gp.getLatitudeE6() + mMapView
									.getLatitudeSpan() * HALF)
									/ SCALE;
							Log.i("��ͼ", "minx:"+minx+"miny:"+miny+"maxx:"+maxx+"maxy:"+maxy);
							searchByBox(minx, miny, maxx, maxy);
						} catch (Exception e) {
							// TODO: handle exception
							Log.e("��ͼ", "��������:"+e.toString());
						}
						
						
					}
				}
			}.start();

		}
		
		
		
		
		/**
		 * �������Overlay
		 * 
		 * @param view
		 */
		public void search4Place() {
			Toast.makeText(CommercialMapActivity.this, "������ʳ����", Toast.LENGTH_SHORT).show();
			new Thread() {
				@Override
				public void run() {// ��Ҫִ�еķ���

					if (mMapView.getZoomLevel() > SHOW_LEVEL) {
						GeoPoint gp = mMapView.getMapCenter();
						
						
						try {
							double minx = (gp.getLongitudeE6() - mMapView
									.getLongitudeSpan() * HALF)
									/ SCALE;
							double miny = (gp.getLatitudeE6() - mMapView
									.getLatitudeSpan() * HALF)
									/ SCALE;
							double maxx = (gp.getLongitudeE6() + mMapView
									.getLongitudeSpan() * HALF)
									/ SCALE;
							double maxy = (gp.getLatitudeE6() + mMapView
									.getLatitudeSpan() * HALF)
									/ SCALE;
							Log.i("��ͼ", "minx:"+minx+"miny:"+miny+"maxx:"+maxx+"maxy:"+maxy);
							searchByBox(minx, miny, maxx, maxy);
						} catch (Exception e) {
							// TODO: handle exception
							Log.e("��ͼ", "��������:"+e.toString());
						}
						
						
					}
				}
			}.start();

		}
		

		/**
		 * �ֶ�����һ�ζ�λ����
		 */
		public void requestLocClick() {
			isRequest = true;
			mLocClient.requestLocation();
			Toast.makeText(CommercialMapActivity.this, "���ڶ�λ����", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onPause() {
			/**
			 * MapView������������Activityͬ������activity����ʱ�����MapView.onPause()
			 */
			mMapView.onPause();
			super.onPause();
			UmenAnalyticsUtility.onPause(this);
		}

		@Override
		protected void onResume() {
			/**
			 * MapView������������Activityͬ������activity�ָ�ʱ�����MapView.onResume()
			 */
			mMapView.onResume();
			super.onResume();
			UmenAnalyticsUtility.onResume(this);
		}

		@Override
		protected void onDestroy() {
			/**
			 * MapView������������Activityͬ������activity����ʱ�����MapView.destroy()
			 */
			mMapView.destroy();
			if (mLocClient != null)
				mLocClient.stop();
			mMapView.destroy();
			super.onDestroy();
		}
	
	
	/**
	 * ��λSDK��������
	 */
	public class MyLocationListenner implements BDLocationListener {

		double[] oldLocaltion = new double[2];
		final double maxDistance = 100;

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;

			

			locData.latitude = location.getLatitude();
			locData.longitude = location.getLongitude();
			// �������ʾ��λ����Ȧ����accuracy��ֵΪ0����
			locData.accuracy = location.getRadius();
			// �˴��������� locData�ķ�����Ϣ, �����λ SDK δ���ط�����Ϣ���û������Լ�ʵ�����̹�����ӷ�����Ϣ��
			locData.direction = location.getDerect();
			// ���¶�λ����
			myLocationOverlay.setData(locData);
			// ����ͼ������ִ��ˢ�º���Ч
			mMapView.refresh();
			// ���ֶ�����������״ζ�λʱ���ƶ�����λ��
			if (isRequest || isFirstLoc) {
				// �ƶ���ͼ����λ��
				// Log.d("LocationOverlay", "receive location, animate to it");
				mMapView.getController().animateTo(
						new GeoPoint((int) (locData.latitude * 1e6),
								(int) (locData.longitude * 1e6)));
				initCity();
				/**
				 * ���õ�ͼ���ż���
				 */
				isRequest = false;

			}

			if (Math.abs(oldLocaltion[0] - locData.longitude) > this.maxDistance
					|| Math.abs(oldLocaltion[1] - locData.latitude) > this.maxDistance) {
				if (!isFirstLoc)
					searchByCircle();
				oldLocaltion[0] = locData.longitude;
				oldLocaltion[1] = locData.latitude;

			}
			if (isFirstLoc)
				// �ٽ���Χ��λ
				searchByCircle();
			// �״ζ�λ���
			isFirstLoc = false;

		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}
	
	
	public void createSearch() {
		WyyApplication app = (WyyApplication) this.getApplication();
		// ��ʼ������ģ�飬ע�������¼�����
		mSearch = new MKSearch();
		mSearch.init(app.mBMapManager, new MKSearchListener() {
			// �ڴ˴�������ҳ���
			@Override
			public void onGetPoiDetailSearchResult(int type, int error) {
				if (error != 0) {
					Toast.makeText(CommercialMapActivity.this, "��Ǹ��δ�ҵ����",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(CommercialMapActivity.this, "�ɹ����鿴����ҳ��",
							Toast.LENGTH_SHORT).show();
				}
			}

			/**
			 * �ڴ˴���poi�������
			 */
			public void onGetPoiResult(MKPoiResult res, int type, int error) {
				// ����ſɲο�MKEvent�еĶ���
				if (error != 0 || res == null) {
					Toast.makeText(CommercialMapActivity.this, "��Ǹ��δ�ҵ����",
							Toast.LENGTH_LONG).show();
					return;
				}
				// ����ͼ�ƶ�����һ��POI���ĵ�
				if (res.getCurrentNumPois() > 0) {
					// ��poi�����ʾ����ͼ��
					// ��ePoiTypeΪ2��������·����4��������·��ʱ�� poi����Ϊ��
					for (MKPoiInfo info : res.getAllPoi()) {
						if (info.pt != null) {
							mMapView.getController().animateTo(
									new GeoPoint(info.pt.getLatitudeE6(),
											info.pt.getLongitudeE6()));
							return;
						}
					}
				} else if (res.getCityListNum() > 0) {
					// ������ؼ����ڱ���û���ҵ����������������ҵ�ʱ�����ذ����ùؼ�����Ϣ�ĳ����б�
					String strInfo = "��";
					for (int i = 0; i < res.getCityListNum(); i++) {
						strInfo += res.getCityListInfo(i).city;
						strInfo += ",";
					}
					strInfo += "�ҵ����";
					Toast.makeText(CommercialMapActivity.this, strInfo,
							Toast.LENGTH_LONG).show();
				}
			}

			public void onGetDrivingRouteResult(MKDrivingRouteResult res,
					int error) {
			}

			public void onGetTransitRouteResult(MKTransitRouteResult res,
					int error) {
			}

			public void onGetWalkingRouteResult(MKWalkingRouteResult res,
					int error) {
			}

			public void onGetAddrResult(MKAddrInfo res, int error) {
				// ����ſɲο�MKEvent�еĶ���
				if (error != 0 || res == null) {
					Toast.makeText(CommercialMapActivity.this, "��Ǹ��δ�ҵ����",
							Toast.LENGTH_LONG).show();
					return;
				}
				MKGeocoderAddressComponent kk = res.addressComponents;
				city = kk.city;
				Toast.makeText(CommercialMapActivity.this, city, Toast.LENGTH_LONG)
						.show();
			}

			public void onGetBusDetailResult(MKBusLineResult result, int iError) {
			}

			/**
			 * ���½����б�
			 */
			@Override
			public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
				if (res == null || res.getAllSuggestions() == null) {
					return;
				}
				sugAdapter.clear();
				for (MKSuggestionInfo info : res.getAllSuggestions()) {
					if (info.key != null)
						sugAdapter.add(info.key);
				}
				sugAdapter.notifyDataSetChanged();

			}

			@Override
			public void onGetShareUrlResult(MKShareUrlResult result, int type,
					int error) {
				// TODO Auto-generated method stub

			}
		});
	}
	
	
	/**
	 * ���ݷ�Χ��ѯ��ʳ
	 * 
	 * @param minx
	 * @param miny
	 * @param maxx
	 * @param maxy
	 */
	protected void searchByBox(double minx, double miny, double maxx,
			double maxy) {

		FoodsUtil foodsHands = new FoodsUtil();
		AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				System.out.println(response);
				foodsList = parseFoods(response);
				showFoods(foodsList);

			}

		};
		foodsHands.findByBox(minx, miny, maxx, maxy, res);
	}

	protected void searchByCircle() {
		Toast.makeText(CommercialMapActivity.this, "������ʳ����", Toast.LENGTH_SHORT).show();
		new Thread() {
			@Override
			public void run() {// ��Ҫִ�еķ���
				FoodsUtil foodsHands = new FoodsUtil();
				AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						System.out.println(response);
						foodsList = parseFoods(response);
						showFoods(foodsList);

					}
				};
				foodsHands.findByCircle(locData.longitude, locData.latitude,
						res);
			}
		}.start();
	}

	/**
	 * �������Ʋ�ѯ��ʳ
	 * 
	 * @param str
	 */
	protected void searchByName(String str) {
		FoodsUtil foodsHands = new FoodsUtil();
		AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				System.out.println(response);

				foodsList = parseFoods(response);
				showFoods(foodsList);

			}
		};
		foodsHands.findByText(str, res);

	}
	
	
	/**
	 * ����json��ʽ��ʳ����
	 * 
	 * @param str
	 * @return
	 */
	public ArrayList<Foods> parseFoods(String str) {
		ArrayList<Foods> list = new ArrayList<Foods>();
		try {

			JSONObject result = new JSONObject(str);
			if (result.isNull("foods"))
				return list;
			JSONArray json = result.getJSONArray("foods");

			int length = json.length();
			for (int i = 0; i < length; i++) {
				Foods food = new Foods();
				JSONObject obj = json.getJSONObject(i);
				food.setCommercialLat(obj.getString("commercialLat"));
				food.setCommercialLon(obj.getString("commercialLon"));
				food.setCommercialName(obj.getString("commercialName"));
				// food.setCommercialAddress(obj.getString("commercialAddress"));
				// food.setCommercialPhone(obj.getString("commercialPhone"));
				food.setTags(obj.getString("tags"));
				food.setId(obj.getString("id"));
				// food.setTastelevel(obj.getString("tastelevel"));
				food.setFoodpic(obj.getString("foodpic"));
				// food.setSummary(obj.getString("summary"));
				// food.setEnergy(obj.getString("energy"));
				// food.setType(obj.getString("type"));
				list.add(food);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * ��ʾ��ʳ
	 * 
	 * @param foods
	 */
	protected void showFoods(ArrayList<Foods> foods) {
		mOverlay.clearOutBundItem();
		mOverlay.addFoods(foods);
		System.out.print(mOverlay.size());
		mMapView.refresh();

	}

	public class MyOverlay extends ItemizedOverlay<OverlayItem> {
		private Context context;

		public MyOverlay(Drawable defaultMarker, MapView mapView,
				Context context) {
			super(defaultMarker, mapView);
			this.context = context;
		}

		@Override
		public boolean onTap(int index) {
			OverlayItem item = getItem(index);
			foods = imemFoods.get(item);

			Intent intent = new Intent();
			PreferencesFoodsInfo.setfoodId(CommercialMapActivity.this,
					"" + foods.getId());
			intent.setClass(context, FoodDetailsActivity.class);
			context.startActivity(intent);
			return true;
		}

		@Override
		public boolean onTap(GeoPoint pt, MapView mMapView) {
			// if (pop != null) {
			// pop.hidePop();
			// mMapView.removeView(button);
			// }
			return false;
		}

		public void clearOutBundItem() {

			GeoPoint gp = mMapView.getMapCenter();
			double minx = gp.getLongitudeE6() - mMapView.getLongitudeSpan()
					* HALF;
			double miny = gp.getLatitudeE6() - mMapView.getLatitudeSpan()
					* HALF;
			double maxx = gp.getLongitudeE6() + mMapView.getLongitudeSpan()
					* HALF;
			double maxy = gp.getLatitudeE6() + mMapView.getLatitudeSpan()
					* HALF;
			ArrayList<OverlayItem> items = this.getAllItem();

			for (OverlayItem item : items) {
				GeoPoint p = item.getPoint();
				if (p.getLongitudeE6() < minx || p.getLongitudeE6() > maxx
						|| p.getLatitudeE6() < miny || p.getLatitudeE6() > maxy) {
					this.removeItem(item);
					// Log.i("", "��� ������" + item);
				}
			}

		}

		public void addFoods(ArrayList<Foods> foods) {
			/**
			 * �����Զ���overlay
			 */

			for (Foods food : foods) {

				/**
				 * ׼��overlay ����
				 */
				GeoPoint p1 = new GeoPoint(
						(int) (Double.parseDouble(food.getCommercialLat()) * 1E6),
						(int) (Double.parseDouble(food.getCommercialLon()) * 1E6));
				// OverlayItem item = new OverlayItem(p1,
				// food.getCommercialName(), "");
				// item.setMarker(getResources().getDrawable(R.drawable.icon_gcoding));
				// item.setMarker(ImageUtil.loadImageFromUrl("http://tx.bdimg.com/sys/portrait/item/990e6271796a7a6c170c.jpg"));
				boolean flag = false;
				ArrayList<OverlayItem> items = this.getAllItem();
				for (OverlayItem item : items) {
					GeoPoint p = item.getPoint();
					if (p.getLongitudeE6() == p1.getLongitudeE6()
							&& p.getLatitudeE6() == p.getLatitudeE6()) {
						imemFoods.put(item, food);
						flag = true;
						break;
					}
				}

				if (!flag) {

					OverlayItem item = new OverlayItem(p1,
							food.getCommercialName(), "");

					imemFoods.put(item, food);
					item.setMarker(ImageUtil
							.loadImageFromUrl(FoodsUtil.urlMiniImage
									+ food.getId()));
					item.setAnchor(0.5f, 1f);

					/**
					 * ��item ��ӵ�overlay�� ע�⣺ ͬһ��itmeֻ��addһ��
					 */
					addItem(item);
				}

			}
		}

	}

	
	public void initCity() {

		mSearch.reverseGeocode(new GeoPoint((int) (locData.latitude * 1e6),
				(int) (locData.longitude * 1e6)));
	}
	
	
}
