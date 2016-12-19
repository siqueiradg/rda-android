package to.dtech.rotadasaguas.fragment.city;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import to.dtech.rotadasaguas.CidadesActivity;
import to.dtech.rotadasaguas.LocalActivity;
import to.dtech.rotadasaguas.R;
import to.dtech.rotadasaguas.util.HttpHandler;


public class SocorroFragment extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    private SliderLayout mDemoSlider;
    private String placeName;
    private ProgressDialog pDialog;
    public HashMap<String,String> imgGoogle = new HashMap<String, String>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_socorro, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.nomeCidade);
        textView.setText("Socorro");
        mDemoSlider = (SliderLayout) rootView.findViewById(R.id.slider);

        new GetCity().execute();


        return rootView;
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class GetCity extends AsyncTask<Void, Void, Void> {
        private String placeName = "";
        private HashMap<String,String> imgGoogleAux = new HashMap<String, String>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Carregando...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HashMap<String,String> url_maps = new HashMap<String, String>();
            HttpHandler sh = new HttpHandler();

            String place_id = "ChIJE56PyYQ_yZQRGwx2bSRZxF4";

            if (place_id != null){
                String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + place_id + "&key=AIzaSyAqPP51HO6FJIw2ZuSaHfxKqqNPtPXkMVA";
                String jsonLocal = sh.makeServiceCall(url);
                if (jsonLocal != null){
                    try {
                        JSONObject jsonObject = new JSONObject(jsonLocal);
                        JSONObject result = jsonObject.getJSONObject("result");
                        JSONArray photosGoogle = result.getJSONArray("photos");
                        for (int i = 0; i < photosGoogle.length(); i++){
                            String photoHash = photosGoogle.getJSONObject(i).getString("photo_reference");
                            imgGoogleAux.put("Imagem "+i, "https://maps.googleapis.com/maps/api/place/photo?maxwidth=600&photoreference=" + photoHash + "&key=AIzaSyAqPP51HO6FJIw2ZuSaHfxKqqNPtPXkMVA");
                        }

                    }catch (final Exception e){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(),"Falha desconhecida!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),"Sem conexão de dados!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }


            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            imgGoogle = imgGoogleAux;

            mDemoSlider = (SliderLayout) getActivity().findViewById(R.id.slider);

            for(String name : imgGoogle.keySet()){
                DefaultSliderView textSliderView = new DefaultSliderView(getContext());
                // initialize a SliderLayout
                textSliderView
                        .image(imgGoogle.get(name))
                        .setScaleType(BaseSliderView.ScaleType.Fit)
                        .setOnSliderClickListener(SocorroFragment.this);

                mDemoSlider.addSlider(textSliderView);
            }
            mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
            mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            mDemoSlider.setCustomAnimation(new DescriptionAnimation());
            mDemoSlider.setDuration(6000);
            mDemoSlider.addOnPageChangeListener(SocorroFragment.this);
        }


    }
}
