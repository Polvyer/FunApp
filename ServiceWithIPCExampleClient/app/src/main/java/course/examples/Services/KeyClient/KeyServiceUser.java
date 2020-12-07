package course.examples.Services.KeyClient;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import course.examples.Services.KeyCommon.KeyGenerator;

public class KeyServiceUser extends Activity {

	public EditText pictureNumberInput, clipNumberInput;
	public Button getPictureButton, getClipButton, playButton, pauseButton, resumeButton, stopButton;
	public ImageView pictureView;
	private KeyGenerator mKeyGeneratorService;
	private boolean mIsBound = false;
	protected static final int PERMISSION_REQUEST = 0;
	protected static final String TAG = "KeyServiceUser";

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.main);

		// Get XML References
		pictureNumberInput = (EditText) findViewById(R.id.pictureNumberInput);
		clipNumberInput = (EditText) findViewById(R.id.clipNumberInput);
		getPictureButton = (Button) findViewById(R.id.getPictureButton);
		getClipButton = (Button) findViewById(R.id.getClipButton);
		playButton = (Button) findViewById(R.id.playButton);
		pauseButton = (Button) findViewById(R.id.pauseButton);
		resumeButton = (Button) findViewById(R.id.resumeButton);
		stopButton = (Button) findViewById(R.id.stopButton);
		pictureView = (ImageView) findViewById(R.id.pictureView);

		// Add to ImageView
		pictureView.setImageResource(R.drawable.totodile);
	}

	// Bind to KeyGenerator Service
	@Override
	protected void onResume() {

		super.onResume();

		if (checkSelfPermission("course.examples.Services.KeyService.GEN_ID")
			!= PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this,
					new String[]{"course.examples.Services.KeyService.GEN_ID"},
					PERMISSION_REQUEST);
		}
		else {
			checkBindingAndBind();
		}
	}

	public void getPicture(View view) {

		// Check if number is an int
		try {
			int number = Integer.parseInt(pictureNumberInput.getText().toString());

			// Check if number is valid
			if (number == 1 || number == 2 || number == 3) {
				// Get picture
				pictureView.setImageBitmap(mKeyGeneratorService.getPicture(number - 1));
			} else {
				Toast.makeText(this, "Please enter a valid number (1-3)", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			System.out.println(e);
			Toast.makeText(this, "Errr...something went wrong", Toast.LENGTH_SHORT).show();
		}
	}

	public void play(View view) {

		// Check if number is an int
		try {
			int number = Integer.parseInt(clipNumberInput.getText().toString());

			// Check if number is valid
			if (number == 1 || number == 2 || number == 3) {
				// Play clip
				mKeyGeneratorService.play(number);
			} else {
				Toast.makeText(this, "Please enter a valid number (1-3)", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			System.out.println(e);
			Toast.makeText(this, "Errr...something went wrong", Toast.LENGTH_SHORT).show();
		}
	}

	public void pause(View view) {
		// Check if number is an int
		try {
			int number = Integer.parseInt(clipNumberInput.getText().toString());

			// Check if number is valid
			if (number == 1 || number == 2 || number == 3) {
				// Play clip
				mKeyGeneratorService.pause(number);
			} else {
				Toast.makeText(this, "Please enter a valid number (1-3)", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			System.out.println(e);
			Toast.makeText(this, "Errr...something went wrong", Toast.LENGTH_SHORT).show();
		}
	}

	public void resume(View view) {
		// Check if number is an int
		try {
			int number = Integer.parseInt(clipNumberInput.getText().toString());

			// Check if number is valid
			if (number == 1 || number == 2 || number == 3) {
				// Play clip
				mKeyGeneratorService.resume(number);
			} else {
				Toast.makeText(this, "Please enter a valid number (1-3)", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			System.out.println(e);
			Toast.makeText(this, "Errr...something went wrong", Toast.LENGTH_SHORT).show();
		}
	}

	public void stop(View view) {
		// Check if number is an int
		try {
			int number = Integer.parseInt(clipNumberInput.getText().toString());

			// Check if number is valid
			if (number == 1 || number == 2 || number == 3) {
				// Play clip
				mKeyGeneratorService.stop(number);
			} else {
				Toast.makeText(this, "Please enter a valid number (1-3)", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			System.out.println(e);
			Toast.makeText(this, "Errr...something went wrong", Toast.LENGTH_SHORT).show();
		}
	}

	protected void checkBindingAndBind() {

		if (!mIsBound) {

			boolean b = false;
			Intent i = new Intent(KeyGenerator.class.getName());

			// UB:  Stoooopid Android API-21 no longer supports implicit intents
			// to bind to a service #@%^!@..&**!@
			// Must make intent explicit or lower target API level to 20.
			ResolveInfo info = getPackageManager().resolveService(i, 0);
			i.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));

			b = bindService(i, this.mConnection, Context.BIND_AUTO_CREATE);
			if (b) {
				Log.i(TAG, "Ugo says bindService() succeeded!");
			} else {
				Log.i(TAG, "Ugo says bindService() failed!");
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

		switch (requestCode) {
			case PERMISSION_REQUEST: {

				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// Permission granted, go ahead and display map
					checkBindingAndBind();
				}
				else {
					Toast.makeText(this, "BUMMER: No Permission :-(", Toast.LENGTH_LONG).show() ;
				}
			}
			default: {
				// do nothing
			}
		}
	}

	// Unbind from KeyGenerator Service
	@Override
	protected void onStop() {

		super.onStop();

		if (mIsBound) {
			unbindService(this.mConnection);
		}
	}

	private final ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder iservice) {

			mKeyGeneratorService = KeyGenerator.Stub.asInterface(iservice);
			mIsBound = true;
		}

		public void onServiceDisconnected(ComponentName className) {

			mKeyGeneratorService = null;
			mIsBound = false;
		}
	};

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {

		unbindService(mConnection);
		super.onDestroy();
	}

	// Re-configure soft key
	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
	}
}
