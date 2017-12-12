package com.samueldavi.q_detective.adapter;

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

    public DenunciaListViewAdapter(List<Denuncia> denuncias, LayoutInflater inflater) {
        this.denuncias = denuncias;
        this.inflater = inflater;
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
                //catAndDate.setCompoundDrawablePadding(R.drawable.ic_);
                break;

            case 1:
                //catAndDate.setCompoundDrawablePadding(R.drawable.ic_);
                break;

            case 2:
                //catAndDate.setCompoundDrawablePadding(R.drawable.ic_);
                break;
        }

        return null;
    }
}
