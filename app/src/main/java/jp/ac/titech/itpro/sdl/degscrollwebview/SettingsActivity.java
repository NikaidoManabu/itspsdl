package jp.ac.titech.itpro.sdl.degscrollwebview;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
    }


    @Override
    protected boolean isValidFragment(String fragmentName) {
        // 使用できる Fragment か確認する
//        if (FragmentA.class.getName().equals(fragmentName) ||
//                FragmentB.class.getName().equals(fragmentName) ||
//                FragmentC.class.getName().equals(fragmentName)) {
//            return true;
//        }
//        return false;
        return true;
    }
}
