package to.dtech.rotadasaguas;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.like.LikeButton;
import com.like.OnLikeListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import at.blogc.android.views.ExpandableTextView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import to.dtech.rotadasaguas.adapter.CommentAdapter;
import to.dtech.rotadasaguas.domain.Comentario;
import to.dtech.rotadasaguas.domain.util.LibraryClass;
import to.dtech.rotadasaguas.fragment.MapFragmentLocal;
import to.dtech.rotadasaguas.util.HttpHandler;

public class LocalActivity extends AppCompatActivity  implements OnMapReadyCallback, BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener, ObservableScrollViewCallbacks {

    private SweetAlertDialog pDialog;

    public String nomeLocal;
    public String place_id;

    public Double coordenadasLog;
    public Double coordenadasLat;


    private GoogleMap map;
    private MapFragmentLocal mMapFragmentLocal;

    public String urlBusca;

    private SliderLayout mDemoSlider;

    public HashMap<String,String> imgGoogle = new HashMap<String, String>();
    public ArrayList<String> autores = new ArrayList<>();
    public ArrayList<String> datas = new ArrayList<>();
    public ArrayList<String> comentarios = new ArrayList<>();
    public ArrayList<String> estrelas = new ArrayList<>();

    public String phone;
    public String rating;
    public String linkMap = "";
    public String website = "";
    public String endPlace;
    public String openNow = "";
    public Boolean lHours = false;
    public ArrayList<String> hoursAux = new ArrayList<>();
    public HashMap<String,String> imgGoogleAux = new HashMap<String, String>();

    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Iconify.with(new FontAwesomeModule());
        setContentView(R.layout.activity_local);


        //RECEBE DADOS DA INTENT ANTERIOR E ADICIONA NA NOVA
        Intent intentOld = getIntent();
        nomeLocal = intentOld.getStringExtra("nome");
        place_id = intentOld.getStringExtra("endereco");
        Log.d("PLACE_ID", place_id);

        final ListView listView = (ListView) findViewById(R.id.comentariosLocal);


        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));

        String titulo = nomeLocal;
        SpannableString s = new SpannableString(titulo);
        s.setSpan(new ForegroundColorSpan(Color.parseColor("#333333")), 0, titulo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);

        ObservableScrollView scrollView = (ObservableScrollView) findViewById(R.id.scLocal);
        scrollView.setScrollViewCallbacks(this);
        ActionBar ab = getSupportActionBar();
        ab.hide();



        new GetLocal().execute();

        //MAIS INFORMAÇÕES
        final ExpandableTextView expandableTextView = (ExpandableTextView) this.findViewById(R.id.horariosFuncionamento);
        final Button buttonToggle = (Button) this.findViewById(R.id.button_toggle);
        final Button buttonComment = (Button) this.findViewById(R.id.btnComentario);


        //BTN ADICIONAIS
        final Button btnPhone = (Button) this.findViewById(R.id.btnLigar);
        final Button btnMap = (Button) this.findViewById(R.id.btnMapa);
        final Button btnSite = (Button) this.findViewById(R.id.btnSite);
        final LikeButton btnFav = (LikeButton) this.findViewById(R.id.gostei);

        // set animation duration via code, but preferable in your layout files by using the animation_duration attribute
        expandableTextView.setAnimationDuration(1000L);

        // set interpolators for both expanding and collapsing animations
        expandableTextView.setInterpolator(new OvershootInterpolator());

        btnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v)
            {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phone));
                    startActivity(intent);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),
                            "O local não possui telefone!",
                            Toast.LENGTH_LONG)
                            .show();
                }

            }
        } );

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v)
            {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(linkMap));
                    startActivity(intent);
                }
                catch (Exception e){

                }
            }
        } );

        btnSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v)
            {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(website));
                    startActivity(intent);;
                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(),
                            "O local não possui site!",
                            Toast.LENGTH_LONG)
                            .show();
                }

            }
        } );


        // toggle the ExpandableTextView
        buttonToggle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                expandableTextView.toggle();
                buttonToggle.setText(expandableTextView.isExpanded() ? "Mostrar Horários" : "Esconder Horários");
            }
        });


        buttonComment.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                if (listView.getVisibility() == View.VISIBLE){
                    listView.setVisibility(View.GONE);
                    buttonComment.setText("Mostrar Avaliações");
                }
                else{
                    listView.setVisibility(View.VISIBLE);
                    buttonComment.setText("Ocultar Avaliações");
                }


            }
        });

        //FIREBASE INIT
        mDatabase = LibraryClass.getFirebase();
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase.child("favoritos").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            Log.d("VALOR_DATA", data.getValue().toString());
                            if (data.getValue().equals(place_id)){
                                btnFav.setLiked(true);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("FIREBASE", "getUser:onCancelled", databaseError.toException());
                    }
                }
        );

        btnFav.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                salvarFavorito(place_id, FirebaseAuth.getInstance().getCurrentUser().getUid());
            }

            @Override
            public void unLiked(final LikeButton likeButton) {
                likeButton.setLiked(true);
                new SweetAlertDialog(LocalActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Deseja apagar dos favoritos?")
                        .setConfirmText("Sim, quero apagar!")
                        .setCancelText("Não")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                removerFavorito(place_id, FirebaseAuth.getInstance().getCurrentUser().getUid());
                                likeButton.setLiked(false);
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                likeButton.setLiked(true);
                                sDialog.cancel();
                            }
                        })
                        .show();
            }
        });





    }
    @Override
    public void onMapReady(GoogleMap map) {

        try {
            double l = coordenadasLat;
            double g = coordenadasLog;
            LatLng city = new LatLng(l,g);

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(city, 18));

            map.addMarker(new MarkerOptions()
                    .title(nomeLocal)
                    .snippet(endPlace)
                    .rotation(10)
                    .position(city));
        }catch(Exception e){
            Log.e("Bug: MapReady", e.toString());
        }

    }
    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

        ActionBar ab = getSupportActionBar();

        if (scrollY > 80){
            Window w = getWindow();
            ab.show();
            ab.setDisplayShowTitleEnabled(true);
            w.setStatusBarColor(Color.parseColor("#333333"));
        }
        else if(scrollY < 80){
            Window w = getWindow();
            ab.hide();
            w.setStatusBarColor(Color.parseColor("#333333"));
        }
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
       /* ActionBar ab = getSupportActionBar();
        if (ab == null) {
            return;
        }
        if (scrollState == ScrollState.UP) {
            if (ab.isShowing()) {
                ab.hide();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!ab.isShowing()) {
                ab.show();
            }
        }*/
    }
    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(this,slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
    }

    //BOTAO VOLTAR ACTIONBAR
    public boolean onOptionsItemSelected(MenuItem item){
        mDemoSlider.setCustomIndicator((PagerIndicator) findViewById(R.id.custom_indicator));
        onBackPressed();
        return true;
    }

    private class GetLocal extends AsyncTask<Void, Void, Void> {
        private String placeName = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new SweetAlertDialog(LocalActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#0066FF"));
            pDialog.setTitleText("Carregando");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            HttpHandler shPlace = new HttpHandler();

            if (place_id != null){
                    String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + place_id + "&key=AIzaSyCvLptUUleUij6Bu5wsUcgBN5punqYO1Wo&language=pt-BR";
                    Log.d("Verificar", "URL Place: " + url);
                    String jsonLocal = shPlace.makeServiceCall(url);
                    if (jsonLocal != null){
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(jsonLocal);
                            JSONObject result = jsonObject.getJSONObject("result");
                            placeName = result.getString("name");
                            endPlace = result.getString("vicinity");
                        }catch (final Exception e){
                            Log.e("Bug: ", e.toString());
                        }
                        if (jsonObject != null){
                            //COORDENADAS
                            carregarCoordenadas(jsonObject);

                            //COMENTARIOS
                            carregarComentarios(jsonObject);

                            //HORARIOS DE FUNCIONAMENTO
                            carregarHorarios(jsonObject);

                            //RATING
                            carregarRating(jsonObject);

                            //FOTOS DO LOCAL
                            carregarFotos(jsonObject);

                            //LINK MAP
                            carregarLinkMap(jsonObject);

                            //WEBSITE
                            carregarWebSite(jsonObject);

                            //TELEFONE
                            carregarTelefone(jsonObject);
                        }
                    }
                }else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Falha na aquisição dos dados!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            if (rating == null){
                rating = "0";
            }


            TextView textView = (TextView) findViewById(R.id.titulo_local);
            TextView openNowText = (TextView) findViewById(R.id.openNow);
            Button btnHorarios = (Button) findViewById(R.id.button_toggle);

            ExpandableTextView eTv = (ExpandableTextView) findViewById(R.id.horariosFuncionamento);
            RatingBar rb = (RatingBar) findViewById(R.id.ratingBar);

            rb.setRating(Float.parseFloat(rating));
            textView.setText(placeName);

            if (openNow.equalsIgnoreCase("true")){
                if (lHours == true) {
                    openNowText.setText("Aberto Agora");
                    openNowText.setBackgroundColor(Color.parseColor("#009900"));
                    eTv.setText("Horários de Funcionamento:" + "\n \n" + hoursAux.get(0).toString().substring(0, 1).toUpperCase() + hoursAux.get(0).toString().substring(1) + "\n" + hoursAux.get(1).toString().substring(0, 1).toUpperCase() + hoursAux.get(1).toString().substring(1) + "\n" + hoursAux.get(2).toString().substring(0, 1).toUpperCase() + hoursAux.get(2).toString().substring(1) + "\n" + hoursAux.get(3).toString().substring(0, 1).toUpperCase() + hoursAux.get(3).toString().substring(1) + "\n" + hoursAux.get(4).toString().substring(0, 1).toUpperCase() + hoursAux.get(4).toString().substring(1) + "\n" + hoursAux.get(5).toString().substring(0, 1).toUpperCase() + hoursAux.get(5).toString().substring(1) + "\n" + hoursAux.get(6).toString().substring(0, 1).toUpperCase() + hoursAux.get(6).toString().substring(1));
                }
                else {
                    openNowText.setText("Aberto Agora");
                    openNowText.setBackgroundColor(Color.parseColor("#009900"));
                    eTv.setVisibility(View.GONE);
                    btnHorarios.setVisibility(View.GONE);
                }
            }
            else{
                if (lHours == true) {
                    eTv.setText("Horários de Funcionamento:" + "\n \n" + hoursAux.get(0).toString().substring(0, 1).toUpperCase() + hoursAux.get(0).toString().substring(1) + "\n" + hoursAux.get(1).toString().substring(0, 1).toUpperCase() + hoursAux.get(1).toString().substring(1) + "\n" + hoursAux.get(2).toString().substring(0, 1).toUpperCase() + hoursAux.get(2).toString().substring(1) + "\n" + hoursAux.get(3).toString().substring(0, 1).toUpperCase() + hoursAux.get(3).toString().substring(1) + "\n" + hoursAux.get(4).toString().substring(0, 1).toUpperCase() + hoursAux.get(4).toString().substring(1) + "\n" + hoursAux.get(5).toString().substring(0, 1).toUpperCase() + hoursAux.get(5).toString().substring(1) + "\n" + hoursAux.get(6).toString().substring(0, 1).toUpperCase() + hoursAux.get(6).toString().substring(1));
                }
                else {
                    eTv.setVisibility(View.GONE);
                    btnHorarios.setVisibility(View.GONE);
                }
            }

            imgGoogle = imgGoogleAux;

            mDemoSlider = (SliderLayout)findViewById(R.id.slider);

            if (imgGoogle.size() == 0){
                mDemoSlider.setVisibility(View.GONE);
            }
            else{
                mDemoSlider.setVisibility(View.VISIBLE);
            }
            for(String name : imgGoogle.keySet()){
                DefaultSliderView textSliderView = new DefaultSliderView(LocalActivity.this);
                // initialize a SliderLayout
                textSliderView
                        .image(imgGoogle.get(name))
                        .setScaleType(BaseSliderView.ScaleType.Fit);

                mDemoSlider.addSlider(textSliderView);
            }
            mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
            mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            mDemoSlider.setCustomAnimation(new DescriptionAnimation());
            mDemoSlider.setDuration(6000);
            mDemoSlider.addOnPageChangeListener(LocalActivity.this);


            final List<Comentario> comentarios = getComentarios();

            final ListView listView = (ListView) findViewById(R.id.comentariosLocal);
            listView.setVisibility(View.GONE);
            listView.setFocusable(false);
            listView.setAdapter(new CommentAdapter(LocalActivity.this, comentarios));

                //GOOGLE MAPS
                mMapFragmentLocal = MapFragmentLocal.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.llMap, mMapFragmentLocal)
                        .commit();

                mMapFragmentLocal.getMapAsync(LocalActivity.this);

        }


    }

    public List<Comentario> getComentarios(){

        List<Comentario> listAux = new ArrayList<>();

        for(int i = 0; i < autores.size(); i++){
            Comentario c = new Comentario(autores.get(i).toString(), datas.get(i).toString(), comentarios.get(i).toString(), estrelas.get(i).toString());
            listAux.add(c);
        }
        return(listAux);
    }


    public void carregarComentarios(JSONObject jsonObject){
        try {
            JSONObject result = jsonObject.getJSONObject("result");
            JSONArray commentsGoogle = result.getJSONArray("reviews");

            for (int i = 0; i < commentsGoogle.length(); i++){
                if (!commentsGoogle.getJSONObject(i).getString("text").equalsIgnoreCase("")) {
                    autores.add(commentsGoogle.getJSONObject(i).getString("author_name"));
                    comentarios.add(commentsGoogle.getJSONObject(i).getString("relative_time_description"));
                    datas.add(commentsGoogle.getJSONObject(i).getString("text"));
                    estrelas.add(commentsGoogle.getJSONObject(i).getString("rating"));
                }
            }
        }catch (Exception e){
            Log.e("Bug: comentarios", e.toString());
        }
    }

    public void carregarHorarios(JSONObject jsonObject){
        try{
            JSONObject result = jsonObject.getJSONObject("result");
            openNow = result.getJSONObject("opening_hours").getString("open_now");

            JSONObject argOpenNow = result.getJSONObject("opening_hours");
            JSONArray openingHours = argOpenNow.getJSONArray("weekday_text");

            for (int i = 0; i < openingHours.length(); i++){
                hoursAux.add(openingHours.get(i).toString());
                lHours = true;
            }
        }catch(Exception e){
            Log.e("Bug: horarios", e.toString());
        }
    }

    public void carregarCoordenadas(JSONObject jsonObject){
        try {
            JSONObject result = jsonObject.getJSONObject("result");
            JSONObject n = result.getJSONObject("geometry").getJSONObject("location");
            coordenadasLat = Double.parseDouble(n.getString("lat").toString());
            coordenadasLog = Double.parseDouble(n.getString("lng").toString());
        }catch (Exception e){
            Log.e("Bug: coordenadas", e.toString());
        }

    }

    public void carregarRating(JSONObject jsonObject){
        try {
            JSONObject result = jsonObject.getJSONObject("result");
            rating = result.getString("rating");
        }catch (Exception e){
            Log.e("Bug: rating", e.toString());
        }
    }

    public void carregarFotos(JSONObject jsonObject){
        try {
            JSONObject result = jsonObject.getJSONObject("result");
            JSONArray photosGoogle = result.getJSONArray("photos");

            for (int i = 0; i < photosGoogle.length(); i++){
                String photoHash = photosGoogle.getJSONObject(i).getString("photo_reference");
                imgGoogleAux.put("Imagem "+i, "https://maps.googleapis.com/maps/api/place/photo?maxwidth=600&photoreference=" + photoHash + "&key=AIzaSyCvLptUUleUij6Bu5wsUcgBN5punqYO1Wo");
            }

        }catch (Exception e){
            Log.e("Bug: photosGoogle", e.toString());
        }
    }

    public void carregarLinkMap(JSONObject jsonObject){
        try {
            JSONObject result = jsonObject.getJSONObject("result");
            linkMap = result.getString("url");
        }catch (Exception e){
            Log.e("Bug: LinkMap", e.toString());
        }
    }

    public void carregarWebSite(JSONObject jsonObject){
        try {
            JSONObject result = jsonObject.getJSONObject("result");
            website = result.getString("website");
        }catch (Exception e){
            Log.e("Bug: website", e.toString());
        }
    }

    public void carregarTelefone(JSONObject jsonObject){
        try {
            JSONObject result = jsonObject.getJSONObject("result");
            phone = result.getString("international_phone_number").replace("-", "").replace(" ", "");
        }catch (Exception e){
            Log.e("Bug: phone", e.toString());
        }
    }

    private void salvarFavorito(String id, String userId){
        try {
            mDatabase.child("favoritos").child(userId).child(id).setValue(id);
            new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Local salvo!")
                    .setContentText("O local foi adicionado aos favoritos!")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .show();
        }catch (Exception e){
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Ops!")
                    .setContentText("Algo deu Errado!")
                    .show();
        }
    }

    private void removerFavorito(String id, String userId){
        try {
            mDatabase.child("favoritos").child(userId).child(id).removeValue();
        }catch (Exception e){
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Ops!")
                    .setContentText("Algo deu Errado!")
                    .show();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}


}
