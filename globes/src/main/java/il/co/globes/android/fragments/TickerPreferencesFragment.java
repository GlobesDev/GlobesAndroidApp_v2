package il.co.globes.android.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import il.co.globes.android.MainSlidingActivity;
import il.co.globes.android.R;
import il.co.globes.android.TickerItemsAdapter;
import il.co.globes.android.interfaces.GlobesListener;
import il.co.globes.android.objects.Ticker;

public class TickerPreferencesFragment extends Fragment {
    private boolean isEditMode = false;
    private AlertDialog alertDialog;
    public Button btnEditMode;
    ListView listView;
    Ticker looper;
    TickerItemsAdapter adapter;

    // header
    private String header;

    // callback
    private GlobesListener mCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (GlobesListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " + GlobesListener.class.getSimpleName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainSlidingActivity.getMainSlidingActivity().onSetDefaultActionBar();
        looper = Ticker.getInstance(getActivity());
        View TickerView = inflater.inflate(R.layout.activity_looper_preferences, null);
        btnEditMode = (Button) TickerView.findViewById(R.id.button_edit_end_edit_looper_items_list);
        btnEditMode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEditMode && looper.getLooperItemsCount() == 3) {
                    showDialogCannotRemoveItem();
                } else if (isEditMode && looper.getLooperItemsCount() == 3) {
                    btnEditMode.setBackgroundResource(R.drawable.selector_button_end_edit_looper_items);
                } else {
                    isEditMode = !isEditMode;
                    if (isEditMode)
                        btnEditMode.setBackgroundResource(R.drawable.selector_button_end_edit_looper_items);
                    else btnEditMode.setBackgroundResource(R.drawable.selector_button_edit_looper_items);
                    adapter.notifyDataSetChanged();
                }

            }
        });
        listView = (ListView) TickerView.findViewById(R.id.listView_looper_items_looper_preferences_activity);
        // TODO eli
        return TickerView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (adapter == null) {
            adapter = new TickerItemsAdapter(looper.getItems(), getActivity(), this, mCallback);
        }
        listView.setAdapter(adapter);
    }

    // old

    public boolean isEditMode() {
        return isEditMode;
    }

    public void setEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
    }

    private void showDialogCannotRemoveItem() {
        AlertDialog.Builder ad = new AlertDialog.Builder((Activity) mCallback);
        ad.setCancelable(false);
        ad.setTitle(R.string.title_update_looper_dialog);
        StringBuilder msg = new StringBuilder();
        msg.append("לא ניתן לערוך פריטים בפס מדדים ");
        msg.append("מספר ניירות ערך מינימלי בפס מדדים הוא שלושה  ");
        ad.setMessage(msg.toString());

        ad.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
                return;
            }
        });
        alertDialog = ad.create();
        alertDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
//        EasyTracker tracker = EasyTracker.getInstance(getActivity());
//        tracker.set(Fields.SCREEN_NAME, getActivity().getResources().getString(R.string.il_co_globes_android_fragments_TickerPreferencesFragment));
//        tracker.send(MapBuilder.createAppView().build());
    }
}
