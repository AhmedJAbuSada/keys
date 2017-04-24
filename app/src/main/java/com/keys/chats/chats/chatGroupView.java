package com.keys.chats.chats;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.keys.R;
import com.keys.chats.model.User;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.add_contact_to_group_item)
public class chatGroupView extends FrameLayout {

    @ViewById(R.id.root)
    LinearLayout root;
    @ViewById(R.id.name)
    TextView name;
    @ViewById(R.id.add)
    CheckBox add;

    Context context;

    public chatGroupView(Context context) {
        super(context);
        this.context = context;
    }

    public void bind(User s) {
        name.setText(s.getFullName());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width =  LinearLayout.LayoutParams.MATCH_PARENT;
    }
}
