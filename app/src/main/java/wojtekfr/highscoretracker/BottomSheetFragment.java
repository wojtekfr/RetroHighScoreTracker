package wojtekfr.highscoretracker;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetFragment extends BottomSheetDialogFragment {



    public int controlCode;
    public ChangeSortingListener changeSortingListener;
    private Button okButton;
    private RadioGroup radioGroup;

    public void setControlCode(int controlCode) {
        this.controlCode = controlCode;
    }


    public BottomSheetFragment() {
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            changeSortingListener = (ChangeSortingListener) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet, container, false);
        okButton = view.findViewById(R.id.buttonBottomOk);
        radioGroup = view.findViewById(R.id.radioGroup);
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Log.d("xxx","tworzę bottom");
        super.onViewCreated(view, savedInstanceState);


        Log.d("xxx", "kod w bottom " + controlCode);
        switch (controlCode){
            case 0:
                ((RadioButton) radioGroup.getChildAt(0)).setChecked(true);
                break;
            case 2:
                ((RadioButton) radioGroup.getChildAt(1)).setChecked(true);
                break;
            case 3:
                ((RadioButton) radioGroup.getChildAt(2)).setChecked(true);
                break;
        }


        okButton.setOnClickListener(view1 -> dismiss());
        radioGroup.setOnCheckedChangeListener((radioGroup, i) -> changeSortingListener.applySorting(i));


    }





    public interface ChangeSortingListener {
        void applySorting(int selectedSortingOption);
    }
}
