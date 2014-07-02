package ch.leafit.webfauna.test;

import android.test.ActivityInstrumentationTestCase2;
import ch.leafit.webfauna.views.HelloAndroidActivity;

public class HelloAndroidActivityTest extends ActivityInstrumentationTestCase2<HelloAndroidActivity> {

    public HelloAndroidActivityTest() {
        super(HelloAndroidActivity.class); 
    }

    public void testActivity() {
        HelloAndroidActivity activity = getActivity();
        assertNotNull(activity);
    }
}

