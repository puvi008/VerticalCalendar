package br.com.jpttrindade.calendarview.bottomsheet;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
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
import br.com.jpttrindade.calendarview.listener.onMonthChangedListener;

/**
 * Created by puviarasu on 13/3/19.
 */


public class MonthBottomSheet extends BottomSheetDialogFragment {
    Context mContext;
    onMonthChangedListener onDateChangedListene;
    ImageView ivClose;
    ArrayList<String> months=new ArrayList<String>();
    ListView listView;

    public MonthBottomSheet newInstance(Bundle bundle) {
        MonthBottomSheet yearBottomSheet = new MonthBottomSheet();
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
        View view = inflater.inflate(R.layout.month_bottomsheet, container, false);
        ivClose = view.findViewById(R.id.ivClose);
        listView = view.findViewById(R.id.lvList);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        setAdapter(this.months);
        return view;
    }

    public void setMonths(ArrayList<String> months) {
        this.months.clear();
        this.months.addAll(months);

    }

    private void setAdapter(final ArrayList<String> years) {

        ArrayAdapter adapter = new ArrayAdapter<String>(mContext,
                R.layout.list_item,R.id.label, years);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                onDateChangedListene.onMonthChanged(years.get(position));
                getDialog().dismiss();
            }
        });
    }


    public void setListener(onMonthChangedListener onmonthChangedListener) {
        this.onDateChangedListene = onmonthChangedListener;
    }
}


