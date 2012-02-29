package dk.christer.malmofestivalen;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import dk.christer.malmofestivalen.fragments.CurrentlyShowingFragment;

public class CurrentlyShowingActivity extends FragmentActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.fragmentlayout);

        setTitle(R.string.startjustnu);
    }

}
