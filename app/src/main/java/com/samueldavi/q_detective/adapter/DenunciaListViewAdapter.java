package com.samueldavi.q_detective.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.samueldavi.q_detective.R;
import com.samueldavi.q_detective.model.Denuncia;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by samue on 11/12/2017.
 */

public class DenunciaListViewAdapter extends BaseAdapter {
    private List<Denuncia> denuncias;
    private LayoutInflater inflater;
    private Context context;

    public DenunciaListViewAdapter(List<Denuncia> denuncias, Context context) {
        this.denuncias = denuncias;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return denuncias.size();
    }

    @Override
    public Object getItem(int i) {
        return denuncias.get(i);
    }

    @Override
    public long getItemId(int i) {
        return denuncias.get(i).getId();
    }

    public void updateDenunciasList(List<Denuncia> newlist) {
        denuncias.clear();
        denuncias.addAll(newlist);
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        convertView = (convertView != null) ? convertView : inflater.inflate(R.layout.listview_item_denuncia, null);

        TextView descriptionTxt = convertView.findViewById(R.id.descricao_denuncia);
        TextView catAndDate = convertView.findViewById(R.id.txtView_categoria_data_denuncia);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        descriptionTxt.setText(denuncias.get(i).getDescricao());
        catAndDate.setText(df.format(denuncias.get(i).getData()));

        switch (denuncias.get(i).getCategoria()){
            case 0:
                catAndDate.setCompoundDrawablesWithIntrinsicBounds(null, context.getResources().getDrawable(R.drawable.ic_road) , null, null);
                //catAndDate.setCompoundDrawablePadding(R.drawable.ic_road);
                break;

            case 1:
                catAndDate.setCompoundDrawablesWithIntrinsicBounds(null, context.getResources().getDrawable(R.drawable.ic_park) , null, null);
                break;

            case 2:
                catAndDate.setCompoundDrawablesWithIntrinsicBounds(null, context.getResources().getDrawable(R.drawable.ic_sewer) , null, null);
                break;
        }

        return convertView;
    }
}
