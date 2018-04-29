package monash.smarterclient;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ReportFragment extends Fragment {
    View vReport;
    private TextView mTextView;

    public ReportFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        vReport = inflater.inflate(R.layout.fragment_report, container, false);
        mTextView = (TextView) vReport.findViewById(R.id.test);
        mTextView.setText("Success");
        return vReport;
    }
}
