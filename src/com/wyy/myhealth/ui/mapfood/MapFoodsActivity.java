package com.wyy.myhealth.ui.mapfood;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import com.baidu.mapapi.map.MyLocationOverlay.LocationMode;
import com.baidu.mapapi.map.OverlayItem;
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
import com.wyy.myhealth.http.utils.JsonUtils;
import com.wyy.myhealth.ui.fooddetails.FoodDetailsActivity;
import com.wyy.myhealth.ui.yaoyingyang.YaoyingyangFragment;
import com.wyy.myhealth.utils.BingLog;
import com.wyy.myhealth.utils.FoodsUtil;
import com.wyy.myhealth.utils.ImageUtil;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class MapFoodsActivity extends ActionBarActivity {

	private static final String TAG = MapFoodsActivity.class.getSimpleName();

	private static final double SCALE = 1000000.0;
	private static final int INIT_LEVEL = 17;
	private static final int SHOW_LEVEL = 15;
	private static final double HALF = 0.5;
	/**
	 * pu MapView �ǵ�ͼ���ؼ�
	 */
	private MapView mMapView = null;
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

	private SearchView searchView;

	private GeoPoint point;

	private boolean isone = false;

	private String foodid = "";

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initActionBar();
		// ��ֹnetworkonmainthreadexception�쳣
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());

		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
				.build());
		// initFirst();
		/**
		 * ʹ�õ�ͼsdkǰ���ȳ�ʼ��BMapManager. BMapManager��ȫ�ֵģ���Ϊ���MapView���ã�����Ҫ��ͼģ�鴴��ǰ������
		 * ���ڵ�ͼ��ͼģ�����ٺ����٣�ֻҪ���е�ͼģ����ʹ�ã�BMapManager�Ͳ�Ӧ������
		 */
		WyyApplication app = (WyyApplication) this.getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(this);
			/**
			 * ���BMapManagerû�г�ʼ�����ʼ��BMapManager
			 */
			app.mBMapManager.init(WyyApplication.strKey,
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
		/**
		 * ����MapView��setContentView()�г�ʼ��,��������Ҫ��BMapManager��ʼ��֮��
		 */
		setContentView(R.layout.activity_map);

		mMapView = (MapView) findViewById(R.id.foodsmapView);

		if (!FoodsUtil.checkDataBase()) {
			System.out.println("���ݿ�ls.db������");
		}

		/**
		 * ��ʾ�������ſؼ�
		 */
		mMapView.setBuiltInZoomControls(false);

		mOverlay = new MyOverlay(getResources().getDrawable(
				R.drawable.icon_marka), mMapView, this);
		mMapView.getOverlays().add(mOverlay);
		createMapListener();

		// createSearch();

		/**
		 * ����һ��popupoverlay
		 */
		// createPop();
		// ��ͼ��ʼ��

		/**
		 * ���õ�ͼ�Ƿ���Ӧ����¼� .
		 */
		mMapView.getController().setZoom(INIT_LEVEL);
		mMapView.getController().enableClick(true);
		// ���ӽǶ�
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
			foodid = PreferencesFoodsInfo.getfoodId(MapFoodsActivity.this);
			Intent intent = getIntent();
			double lat = intent.getDoubleExtra("lat", 0);
			double lon = intent.getDoubleExtra("lon", 0);

			Log.i(TAG, "����:" + lat + "γ��:" + lon);

			point = new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
			mMapView.getController().setCenter(point);
			resetOverlay();
			createSearch();
			YaoyingyangFragment.isdingwei = false;
			return;
		}

		mLocClient.start();

		createSearch();

	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		// TODO Auto-generated method stub

		if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
				try {
					Method m = menu.getClass().getDeclaredMethod(
							"setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);

				} catch (Exception e) {
				}
			}
		}

		return super.onMenuOpened(featureId, menu);
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.actionbar_g_bg));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);

		searchView = new SearchView(this);
		settingSearchView();
		actionBar.setCustomView(searchView, new ActionBar.LayoutParams(
				Gravity.RIGHT));
		actionBar.setDisplayShowCustomEnabled(true);
		searchView.setOnQueryTextListener(searchListener);

	}

	private OnQueryTextListener searchListener = new OnQueryTextListener() {

		@Override
		public boolean onQueryTextSubmit(String arg0) {
			// TODO Auto-generated method stub
			mSearch.poiSearchInCity(city, arg0);
			return true;
		}

		@Override
		public boolean onQueryTextChange(String arg0) {
			// TODO Auto-generated method stub
			return false;
		}
	};

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
				Toast.makeText(MapFoodsActivity.this, "��ͼ�������",
						Toast.LENGTH_SHORT).show();
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
		Toast.makeText(MapFoodsActivity.this, "������ʳ����", Toast.LENGTH_SHORT)
				.show();
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
						BingLog.i("��ͼ", "minx:" + minx + "miny:" + miny
								+ "maxx:" + maxx + "maxy:" + maxy);
						searchByBox(minx, miny, maxx, maxy);
					} catch (Exception e) {
						// TODO: handle exception
						BingLog.e("��ͼ", "��������:" + e.toString());
						newSearch();
					}

				} else {
					newSearch();
				}
			}
		}.start();

	}

	private void newSearch() {
		try {
			double minx = (point.getLongitudeE6() - 1) / SCALE;
			double miny = (point.getLatitudeE6() - 1) / SCALE;
			double maxx = (point.getLongitudeE6() + 1) / SCALE;
			double maxy = (point.getLatitudeE6() + 1) / SCALE;
			BingLog.i("��ͼ", "minx:" + minx + "miny:" + miny + "maxx:" + maxx
					+ "maxy:" + maxy);
			searchByBox(minx, miny, maxx, maxy);
		} catch (Exception e) {
			// TODO: handle exception
			BingLog.e("��ͼ2", "��������:" + e.toString());
		}
	}

	/**
	 * �������Overlay
	 * 
	 * @param view
	 */
	public void search4Place() {
		Toast.makeText(MapFoodsActivity.this, "������ʳ����", Toast.LENGTH_SHORT)
				.show();
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
						Log.i("��ͼ", "minx:" + minx + "miny:" + miny + "maxx:"
								+ maxx + "maxy:" + maxy);
						searchByBox(minx, miny, maxx, maxy);
					} catch (Exception e) {
						// TODO: handle exception
						Log.e("��ͼ", "��������:" + e.toString());
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
		Toast.makeText(MapFoodsActivity.this, "���ڶ�λ����", Toast.LENGTH_SHORT)
				.show();
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

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mMapView.onRestoreInstanceState(savedInstanceState);
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
				BingLog.i(TAG, "��������:" + response);
				foodsList = parseFoods(response);
				showFoods(foodsList);

			}

		};
		foodsHands.findByBox(minx, miny, maxx, maxy, res);
	}

	protected void searchByCircle() {
		Toast.makeText(MapFoodsActivity.this, "������ʳ����", Toast.LENGTH_SHORT)
				.show();
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
				// Foods food=new Foods();
				JSONObject obj = json.getJSONObject(i);
				Foods food = JsonUtils.getfoods(obj);
				// food.setCommercialLat(obj.getString("commercialLat"));
				// food.setCommercialLon(obj.getString("commercialLon"));
				// food.setCommercialName(obj.getString("commercialName"));
				// food.setCommercialAddress(obj.getString("commercialAddress"));
				// food.setCommercialPhone(obj.getString("commercialPhone"));
				// food.setTags(obj.getString("tags"));
				// food.setId(obj.getString("id"));
				// food.setTastelevel(obj.getString("tastelevel"));
				// food.setFoodpic(obj.getString("foodpic"));
				// food.setSummary(obj.getString("summary"));
				// food.setEnergy(obj.getString("energy"));
				// food.setType(obj.getString("type"));
				if (!isone) {
					if (foodid.equals(food.getId())) {
						list.add(food);
					}
				} else {
					list.add(food);
				}

			}
			isone = true;
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
			PreferencesFoodsInfo.setfoodId(MapFoodsActivity.this,
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

	// private View.OnClickListener listener = new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// switch (v.getId()) {
	//
	// case R.id.left_button:
	// EditText keyWorldsView = (EditText) findViewById(R.id.search_input);
	//
	// mSearch.poiSearchInCity(city, keyWorldsView.getText()
	// .toString());
	// break;
	// case R.id.map_right_button:
	//
	// // Toast.makeText(MainActivity.this, "�����ҳ",
	// // Toast.LENGTH_LONG).show();
	// // Intent intent3 = new Intent(MainActivity.this,
	// // HomeActivity.class);
	// // startActivity(intent3);
	// // finish();
	//
	// break;
	// case R.id.Lie_Layout:
	//
	// /*
	// * Intent intent2 = new
	// * Intent(MainActivity.this,SearchActivity.class);
	// * intent2.putExtra("lon", (int) (locData.longitude * 1e6));
	// * intent2.putExtra("lat", (int) (locData.latitude * 1e6));
	// */
	//
	// Intent intent2 = new Intent(MainActivity.this,
	// LieActivity.class);
	//
	// startActivity(intent2);
	// finish();
	// break;
	// case R.id.Map_Layout:
	//
	// break;
	// default:
	// break;
	// }
	// }
	//
	// };

	public void createSearch() {
		WyyApplication app = (WyyApplication) this.getApplication();
		// ��ʼ������ģ�飬ע�������¼�����
		mSearch = new MKSearch();
		mSearch.init(app.mBMapManager, new MKSearchListener() {
			// �ڴ˴�������ҳ���
			@Override
			public void onGetPoiDetailSearchResult(int type, int error) {
				if (error != 0) {
					Toast.makeText(MapFoodsActivity.this, "��Ǹ��δ�ҵ����",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(MapFoodsActivity.this, "�ɹ����鿴����ҳ��",
							Toast.LENGTH_SHORT).show();
				}
			}

			/**
			 * �ڴ˴���poi�������
			 */
			public void onGetPoiResult(MKPoiResult res, int type, int error) {
				// ����ſɲο�MKEvent�еĶ���
				if (error != 0 || res == null) {
					Toast.makeText(MapFoodsActivity.this, "��Ǹ��δ�ҵ����",
							Toast.LENGTH_LONG).show();
					return;
				}
				// ����ͼ�ƶ�����һ��POI���ĵ�
				if (res.getCurrentNumPois() > 0) {
					// ��poi�����ʾ����ͼ��
					// MyPoiOverlay poiOverlay = new
					// MyPoiOverlay(SearchActivity.this, mMapView, mSearch);
					// poiOverlay.setData(res.getAllPoi());
					// mMapView.getOverlays().clear();
					// mMapView.getOverlays().add(poiOverlay);
					// mMapView.refresh();
					// ��ePoiTypeΪ2��������·����4��������·��ʱ�� poi����Ϊ��
					for (MKPoiInfo info : res.getAllPoi()) {
						if (info.pt != null) {
							// mMapView.getController().animateTo(info.pt);
							// Log.i(MainActivity.class.getName(),
							// info.pt.getLatitudeE6() + "��γ�� "
							// + info.pt.getLongitudeE6());
							// Intent intent = new Intent();
							// intent.setClass(MainActivity.this,
							// MainActivity.class);
							// intent.putExtra("lon", info.pt.getLongitudeE6());
							// intent.putExtra("lat", info.pt.getLatitudeE6());
							// startActivity(intent);
							// new
							// GeoPoint(info.pt.getLatitudeE6(),info.pt.getLongitudeE6());
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
					Toast.makeText(MapFoodsActivity.this, strInfo,
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
					Toast.makeText(MapFoodsActivity.this, "��Ǹ��δ�ҵ����",
							Toast.LENGTH_LONG).show();
					return;
				}
				MKGeocoderAddressComponent kk = res.addressComponents;
				city = kk.city;
				Toast.makeText(MapFoodsActivity.this, city, Toast.LENGTH_LONG)
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

		// keyWorldsView = (AutoCompleteTextView)
		// findViewById(R.id.search_input);
		// sugAdapter = new ArrayAdapter<String>(this,
		// android.R.layout.simple_dropdown_item_1line);
		// keyWorldsView.setAdapter(sugAdapter);
		//
		// /**
		// * ������ؼ��ֱ仯ʱ����̬���½����б�
		// */
		// keyWorldsView.addTextChangedListener(new TextWatcher() {
		//
		// @Override
		// public void afterTextChanged(Editable arg0) {
		//
		// }
		//
		// @Override
		// public void beforeTextChanged(CharSequence arg0, int arg1,
		// int arg2, int arg3) {
		//
		// }
		//
		// @Override
		// public void onTextChanged(CharSequence cs, int arg1, int arg2,
		// int arg3) {
		// if (cs.length() <= 0) {
		// return;
		// }
		// // String city =
		// // ((EditText)findViewById(R.id.city)).getText().toString();
		// /**
		// * ʹ�ý������������ȡ�����б������onSuggestionResult()�и���
		// */
		// mSearch.suggestionSearch(cs.toString(), "");
		// }
		// });
		//
		// keyWorldsView.setOnKeyListener(new OnKeyListener() {//
		// ������󰴼����ϵ����������س�����Ϊ����������
		//
		// @Override
		// public boolean onKey(View v, int keyCode, KeyEvent event) {
		//
		// if (city == null) {
		// Toast.makeText(MapFoodsActivity.this, "û�ж�λ�����У��޷���ѯ",
		// Toast.LENGTH_LONG).show();
		// return false;
		// }
		//
		// // TODO Auto-generated method stub
		// if (keyCode == KeyEvent.KEYCODE_ENTER) {// �޸Ļس�������
		// // �����ؼ���
		// ((InputMethodManager) keyWorldsView.getContext()
		// .getSystemService(
		// Context.INPUT_METHOD_SERVICE))
		// .hideSoftInputFromWindow(
		// MapFoodsActivity.this.getCurrentFocus()
		// .getWindowToken(),
		// InputMethodManager.HIDE_NOT_ALWAYS);
		//
		// EditText keyWorldsView = (EditText) findViewById(R.id.search_input);
		//
		// mSearch.poiSearchInCity(city, keyWorldsView
		// .getText().toString());
		//
		// return true;
		// }
		// return false;
		// }
		//
		// });

	}

	public void initCity() {

		mSearch.reverseGeocode(new GeoPoint((int) (locData.latitude * 1e6),
				(int) (locData.longitude * 1e6)));
	}

	// private void initFirst() {
	// // TODO Auto-generated method stub
	// if (!LoginActivity.firstYao) {
	// Intent intent = new Intent(MainActivity.this, Prompt.class);
	// intent.putExtra("first_map", "bing");
	// startActivity(intent);
	// LoginActivity.firstYao = true;
	//
	// }
	//
	// }

	private void settingSearchView() {
		ImageView icon = (ImageView) searchView
				.findViewById(android.support.v7.appcompat.R.id.search_button);
		icon.setImageResource(R.drawable.ic_search);
	}

}
