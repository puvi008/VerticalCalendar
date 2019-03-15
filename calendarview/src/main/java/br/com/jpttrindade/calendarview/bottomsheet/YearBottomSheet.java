package br.com.jpttrindade.calendarview.bottomsheet;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import br.com.jpttrindade.calendarview.R;
import br.com.jpttrindade.calendarview.listener.onDateChangedListener;

/**
 * Created by puviarasu on 13/3/19.
 */


public class YearBottomSheet extends BottomSheetDialogFragment {
    Context mContext;
    onDateChangedListener onDateChangedListene;
    ImageView ivClose;
    ArrayList<String> years=new ArrayList<String>();
    ListView listView;

    public YearBottomSheet newInstance(Bundle bundle) {
        YearBottomSheet yearBottomSheet = new YearBottomSheet();
        yearBottomSheet.setArguments(bundle);
        return yearBottomSheet;
    }



    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            View bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
            int height = getResources().getDimensionPixelSize(R.dimen._320sdp);
            bottomSheet.getLayoutParams().height = height;
        }
        final View view = getView();
        view.post(new Runnable() {
            @Override
            public void run() {
                View parent = (View) view.getParent();
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) parent.getLayoutParams();
                CoordinatorLayout.Behavior behavior = params.getBehavior();
                BottomSheetBehavior<View> bottomSheetBehavior = (BottomSheetBehavior<View>) behavior;
                bottomSheetBehavior.setPeekHeight(view.getMeasuredHeight());
            }
        });

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = (Context) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.year_bottomsheet, container, false);
        ivClose = view.findViewById(R.id.ivClose);
        listView = view.findViewById(R.id.lvList);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        setAdapter(this.years);
        return view;
    }

    public void setYears(ArrayList<String> years) {
        this.years.clear();
        this.years.addAll(years);

    }

    private void setAdapter(final ArrayList<String> years) {

        ArrayAdapter adapter = new ArrayAdapter<String>(mContext,
                R.layout.list_item,R.id.label, years);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                onDateChangedListene.onDateChanged(years.get(position));
                getDialog().dismiss();
            }
        });
    }


    public void setListener(onDateChangedListener ondateChangedListener) {
        this.onDateChangedListene = ondateChangedListener;
    }
}


