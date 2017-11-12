package com.security.weather.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.security.weather.R;
import com.security.weather.activity.MainActivity;
import com.security.weather.activity.WeatherActivaity;
import com.security.weather.bean.City;
import com.security.weather.bean.Country;
import com.security.weather.bean.Province;
import com.security.weather.bean.Weather;
import com.security.weather.http.HandleDataUtil;
import com.security.weather.http.HttpUtil;
import com.security.weather.util.ConstantUtil;
import com.security.weather.util.UIUtil;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 选择地址
 */
public class ChooseAreaFragment extends Fragment {

    private Context context;
    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTRY=2;

    private List<String> dataList=new ArrayList<>();

    /**
     * 进度展示条;
     */
    private ProgressDialog progressDialog;

    /**
     * 选中的省份;
     */
    private Province selectedProvince;

    /**
     * 省份集合
     */
    private List<Province> provinceList;

    /**
     * 选中的城市;
     */
    private City selectedCity;

    /**
     * 选中的县城;
     */
    private Country selectedCountry;

    /**
     * 当前选中的级别;
     */
    private int currentLevel;

    private TextView tv_title;
    private Button btn_back;
    private ListView listview;

    /**
     *ListView的适配器;
     */
    private ArrayAdapter<String> adapter;
    private List<City> cityList;
    private List<Country> countryList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_choosearea,container,false);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        btn_back = (Button) view.findViewById(R.id.btn_back);
        listview = (ListView) view.findViewById(R.id.listview);
        adapter = new ArrayAdapter(context,android.R.layout.simple_list_item_1,dataList);
        listview.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel==LEVEL_PROVINCE){
                    selectedProvince=provinceList.get(position);
                    queryCities();
                }else if(currentLevel==LEVEL_CITY){
                    selectedCity=cityList.get(position);
                    queryCountries();
                }else if(currentLevel==LEVEL_COUNTRY){
                    String weatherId=countryList.get(position).getWeatherId();
                    if(getActivity() instanceof MainActivity){
                        Intent intent=new Intent(getActivity(), WeatherActivaity.class);
                        intent.putExtra("weather_id",weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    }else if(getActivity() instanceof WeatherActivaity){
                        WeatherActivaity activity= (WeatherActivaity) getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefresh.setRefreshing(true);
                        activity.requestWeather(weatherId);
                        activity.setWeatherId(weatherId);
                    }
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentLevel==LEVEL_COUNTRY){
                    queryCities();
                }else if(currentLevel==LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    /**
     * 查询省份数据;
     */
    private void queryProvinces() {
        tv_title.setText("中国");
        btn_back.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if(provinceList.size()>0){
            dataList.clear();
            for(Province province: provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listview.setSelection(0);
            currentLevel=LEVEL_PROVINCE;
        }else{
            queryFromServer(ConstantUtil.AREA_URL,"province");
        }
    }

    /**
     * 查询选中省份的所有城市;
     */
    private void queryCities(){
        tv_title.setText(selectedProvince.getProvinceName());
        btn_back.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceId=?", String.valueOf(selectedProvince.getId())).find(City.class);
        if(cityList.size()>0){
            dataList.clear();
            for(City city: cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listview.setSelection(0);
            currentLevel=LEVEL_CITY;
        }else{
            queryFromServer(ConstantUtil.AREA_URL+selectedProvince.getProvinceCode(),"city");
        }
    }

    /**
     * 查询选中城市的所有县城 ;
     */
    private void queryCountries(){
        tv_title.setText(selectedCity.getCityName());
        btn_back.setVisibility(View.VISIBLE);
        countryList = DataSupport.where("cityId=?", String.valueOf(selectedCity.getId())).find(Country.class);
        if(countryList.size()>0){
            dataList.clear();
            for(Country country:countryList){
                dataList.add(country.getCountryName());
            }
            adapter.notifyDataSetChanged();
            listview.setSelection(0);
            currentLevel=LEVEL_COUNTRY;
        }else{
            queryFromServer(ConstantUtil.AREA_URL+selectedProvince.getProvinceCode()+"/"+selectedCity.getCityCode(),"country");
        }

    }

    /**
     * 根据传入的地址和类型从服务器上获取数据;
     * @param url
     * @param type
     */
    private void queryFromServer(String url, final String type){
        //展示ProgressDialog;
        showProgressDialog();
        HttpUtil.getInstance().sendOKHttpRequest(url, new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData=response.body().string();
                boolean result=false;
                if("province".equals(type)){
                    result= HandleDataUtil.handleProviceData(responseData);
                }else if("city".equals(type)){
                    result=HandleDataUtil.handleCityData(responseData,selectedProvince.getId());
                }else if("country".equals(type)){
                    result=HandleDataUtil.handleCountryData(responseData,selectedCity.getId());
                }
                if(result){
                    UIUtil.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                               queryProvinces();
                            }else if("city".equals(type)){
                               queryCities();
                            }else if("country".equals(type)){
                                queryCountries();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                UIUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(context, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 显示进度对话框;
     */
    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     *关闭进度对话框;
     */
    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }
}
