package halla.icsw.d_day.customView;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.ActionBar;
import halla.icsw.d_day.R;

public class CustomActionBar {

    private Activity activity;
    private ActionBar actionBar;

    public CustomActionBar(Activity _activity, ActionBar _actionBar) {
        this.activity = _activity;
        this.actionBar = _actionBar;
    }

    public void setActionBar() {

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);

        View mCustomView = LayoutInflater.from(activity).inflate(R.layout.activity_actionbar, null);
        actionBar.setCustomView(mCustomView);

        Toolbar parent = (Toolbar) mCustomView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(mCustomView, params);
    }
}

