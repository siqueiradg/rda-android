package to.dtech.rotadasaguas.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;
import com.like.LikeButton;

import java.util.List;

import to.dtech.rotadasaguas.R;
import to.dtech.rotadasaguas.domain.Comentario;
import to.dtech.rotadasaguas.domain.Tag;

public class CommentAdapter extends BaseAdapter {
    private Context context;
    private List<Comentario> comentarios;

    public CommentAdapter(Context context, List<Comentario> comentarios){
        this.context = context;
        this.comentarios = comentarios;

    }

    @Override
    public int getCount() {
        return comentarios.size();
    }

    @Override
    public Object getItem(int position) {
        return comentarios.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {

        final TextView autor;
        final TextView comentario;

        public ViewHolder(View view) {

            autor = (TextView) view.findViewById(R.id.autor);
            comentario = (TextView) view.findViewById(R.id.comentario);

        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;
        ViewHolder holder;

        if( convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_coment, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);

        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }


        Comentario comentario = comentarios.get(position);
        holder.autor.setText(comentario.getAutor());
        holder.comentario.setText(comentario.getComentario());

        return view;
    }

}

