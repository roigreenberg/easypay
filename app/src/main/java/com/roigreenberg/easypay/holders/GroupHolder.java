package com.roigreenberg.easypay.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Roi on 16/09/2017.
 */

public class GroupHolder  extends RecyclerView.ViewHolder  {
    public final TextView nameField;

    public GroupHolder(View itemView) {
        super(itemView);
        nameField = (TextView) itemView.findViewById(R.id.group_name);
    }

    public void setName(String name) {
        nameField.setText(name);
    }

}
