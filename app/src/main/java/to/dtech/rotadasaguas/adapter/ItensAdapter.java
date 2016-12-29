package to.dtech.rotadasaguas.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;

import java.util.List;

import to.dtech.rotadasaguas.LocalActivity;
import to.dtech.rotadasaguas.R;
import to.dtech.rotadasaguas.domain.ItemLocal;
import to.dtech.rotadasaguas.interfaces.RecyclerViewOnClickListenerHack;

public class ItensAdapter extends RecyclerView.Adapter<ItensAdapter.MyViewHolder> {
    private List<ItemLocal> mList;
    private LayoutInflater mLayoutInflater;
    private RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;


    public ItensAdapter(Context c, List<ItemLocal> l){
        mList = l;
        mLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Log.i("LOG", "onCreateViewHolder()");
        View v = mLayoutInflater.inflate(R.layout.item_minha_rota, viewGroup, false);
        MyViewHolder mvh = new MyViewHolder(v);
        return mvh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int position) {
        Log.i("LOG", "onBindViewHolder()");
        myViewHolder.icone.setText( "{fa-map-marker}" );
        myViewHolder.nomeLocal.setText(mList.get(position).getNome() );
        myViewHolder.descLocal.setText( mList.get(position).getDescricao() );
        myViewHolder.endLocal = mList.get(position).getEndereco();
        myViewHolder.descricao = mList.get(position).getDescricao();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public void setRecyclerViewOnClickListenerHack(RecyclerViewOnClickListenerHack r){
        mRecyclerViewOnClickListenerHack = r;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public IconTextView icone;
        public TextView nomeLocal;
        public TextView descLocal;
        public String descricao;
        public String endLocal;

        public MyViewHolder(View itemView) {
            super(itemView);

            icone = (IconTextView) itemView.findViewById(R.id.iv_car);
            nomeLocal = (TextView) itemView.findViewById(R.id.tv_model);
            descLocal = (TextView) itemView.findViewById(R.id.tv_brand);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mRecyclerViewOnClickListenerHack != null){
                mRecyclerViewOnClickListenerHack.onClickListener(v, getPosition());
            }

            nomeLocal = (TextView) itemView.findViewById(R.id.tv_model);
            String n = nomeLocal.getText().toString();

            Intent intent = new Intent(v.getContext(), LocalActivity.class);

            intent.putExtra("endereco", endLocal);
            intent.putExtra("descricao", descricao);
            intent.putExtra("nome", n);

            v.getContext().startActivity(intent);
        }
    }

}
