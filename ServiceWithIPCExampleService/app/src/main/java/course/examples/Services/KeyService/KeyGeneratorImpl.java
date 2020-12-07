package course.examples.Services.KeyService;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import course.examples.Services.KeyCommon.KeyGenerator;

public class KeyGeneratorImpl extends Service {

	// Set of already assigned IDs
	// Note: These keys are not guaranteed to be unique if the Service is killed 
	// and restarted.

	private static final int NOTIFICATION_ID = 1;
	private MediaPlayer mPlayer;
	public ArrayList<Bitmap> arrayList;
	private static String CHANNEL_ID = "Music player style";
	public int currentTrack = -1;

	@Override
	public void onCreate() {

		super.onCreate();

		// Instantiate objects
		arrayList = new ArrayList<>();

		// Add to ArrayList (https://stackoverflow.com/questions/3035692/how-to-convert-a-drawable-to-a-bitmap)
		arrayList.add(BitmapFactory.decodeResource(this.getResources(), R.drawable.pikachu));
		arrayList.add(BitmapFactory.decodeResource(this.getResources(), R.drawable.eevee));
		arrayList.add(BitmapFactory.decodeResource(this.getResources(), R.drawable.squirtle));
	}

	// Implement the Stub for this Object
	private final KeyGenerator.Stub mBinder = new KeyGenerator.Stub() {

		@Override
		public Bitmap getPicture(int id) throws RemoteException {

			// Acquire lock to ensure exclusive access to mIDs
			// Then examine and modify mIDs

			Bitmap picture;

			synchronized (arrayList) {
				picture = arrayList.get(id);
			}

			return picture;
		}

		@Override
		public void play(int id) throws RemoteException {
			onStart(id);
		}

		@Override
		public void pause(int id) throws RemoteException {
			onPause(id);
		}

		@Override
		public void resume(int id) throws RemoteException {
			onResume(id);
		}

		@Override
		public void stop(int id) throws RemoteException {
			onStop(id);
		}

	};

	public void onPause(int id) {
		if (mPlayer.isPlaying()) {
			mPlayer.pause();
		}
	}

	public void onResume(int id) {
		if (!mPlayer.isPlaying()) {
			mPlayer.start();
		}
	}

	public void onStop(int id) {
		mPlayer.stop();
	}

	public void onStart(int id) {

		if (mPlayer == null) {

			// Get specific track
			if (id == 1) {
				currentTrack = 1;
				mPlayer = MediaPlayer.create(this, R.raw.monica);
			} else if (id == 2) {
				currentTrack = 2;
				mPlayer = MediaPlayer.create(this, R.raw.surf);
			} else if (id == 3) {
				currentTrack = 3;
				mPlayer = MediaPlayer.create(this, R.raw.young);
			}

			// Set player settings
			mPlayer.setLooping(false);
			mPlayer.setVolume(50, 50);

			// Start playing song
			mPlayer.start();

			// Create a notification area notification so the user
			// can get back to the MusicServiceClient
			final Intent notificationIntent = new Intent();
			final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
			final Notification notification = new Notification.Builder(
					getApplicationContext())
					.setSmallIcon(android.R.drawable.ic_media_play)
					.setOngoing(true).setContentTitle("Music Playing")
					.setContentText("Click to Access Music Player")
					.setContentIntent(pendingIntent)
					.build();

			// Put this Service in a foreground state, so it won't
			// readily be killed by the system
			startForeground(NOTIFICATION_ID, notification);
		} else {

			// Check if track is the same one
			if (currentTrack == id) {
				// Rewind to beginning of song
				mPlayer.seekTo(0);
			} else { // Stop track and get new one in
				mPlayer.stop();

				// Get specific track
				if (id == 1) {
					currentTrack = 1;
					mPlayer = MediaPlayer.create(this, R.raw.monica);
				} else if (id == 2) {
					currentTrack = 2;
					mPlayer = MediaPlayer.create(this, R.raw.surf);
				} else if (id == 3) {
					currentTrack = 3;
					mPlayer = MediaPlayer.create(this, R.raw.young);
				}

				// Set player settings
				mPlayer.setLooping(false);
				mPlayer.setVolume(50, 50);

				// Start playing song
				mPlayer.start();

				// Create a notification area notification so the user
				// can get back to the MusicServiceClient
				final Intent notificationIntent = new Intent();
				final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
				final Notification notification = new Notification.Builder(
						getApplicationContext())
						.setSmallIcon(android.R.drawable.ic_media_play)
						.setOngoing(true).setContentTitle("Music Playing")
						.setContentText("Click to Access Music Player")
						.setContentIntent(pendingIntent)
						.build();

				// Put this Service in a foreground state, so it won't
				// readily be killed by the system
				startForeground(NOTIFICATION_ID, notification);
			}
		}
	}


	// Return the Stub defined above
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onDestroy() {

		if (mPlayer != null) {
			mPlayer.stop();
			mPlayer.release();
		}

		stopSelf();
	}
}
