package com.niks.utpvsec.activities;

import java.io.IOException;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.niks.utpvsec.classes.IDCard;
import com.utp.vsec.R;

public class ScanActivity extends Activity {

	private static final String TAG = "ScanActivity";
	public static final String NFC_DATA = "NFC Data";
	public static final String EXIT_AFTER_WRITE = "exitAfterWrite";
	public static final int WRITE_TAG = 0;

	private TextView msg = null;
	private boolean supposedToExit = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scan);
		msg = (TextView) findViewById(R.id.msg);

		TextView status = (TextView) findViewById(R.id.nfcstatus);
		NfcAdapter adapter = NfcAdapter
				.getDefaultAdapter(getApplicationContext());
		if (null != adapter && adapter.isEnabled()) {
			status.setText("NFC is enabled :)");
			status.setTextColor(getResources().getColor(
					android.R.color.holo_green_dark));
		} else {
			status.setText("NFC is not enabled :(");
			status.setTextColor(getResources().getColor(
					android.R.color.holo_red_dark));
		}

		Button b;
		b = (Button) findViewById(R.id.newcard);
		b.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						EditActivity.class);
				startActivityForResult(intent, EditActivity.EDIT_DETAILS);
			}
		});
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "started");
		Intent intent = getIntent();
		if (WRITE_TAG == intent.getIntExtra("requestCode", -1)) {
			// We are asked to write the tag
			supposedToExit = intent.getBooleanExtra(EXIT_AFTER_WRITE, true);
			
			Log.d(TAG, "Setting up Write");
			setupWrite(intent.getByteArrayExtra(ScanActivity.NFC_DATA));
			setIntent(new Intent());
		} else if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.getAction()) {
			Log.d(TAG, "Tag detected");
			byte[] data = readFromTag(intent);
			IDCard idcard = IDCard.deserialize(data);
			if (null == idcard) {
				String msg = "Wrong tag tapped\nPlease tap correct tag";
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG)
						.show();
			} else {
				Intent i = new Intent(getApplicationContext(),
						ShowActivity.class);
				i.putExtra(IDCard.IDCARD_DATA, idcard);
				startActivity(i);
				setIntent(new Intent());
				finish();
			}
		} else {
			msg.setText("Ready to read TAG!");
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		NfcAdapter nfcAdapter = NfcAdapter
				.getDefaultAdapter(getApplicationContext());
		nfcAdapter.disableForegroundDispatch(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (EditActivity.EDIT_DETAILS == requestCode) {
			if (RESULT_OK == resultCode) {
				IDCard idcard = (IDCard) data
						.getSerializableExtra(IDCard.IDCARD_DATA);
				Intent intent = new Intent(getApplicationContext(),
						ScanActivity.class);
				intent.putExtra(ScanActivity.NFC_DATA, IDCard.serialize(idcard));
				intent.putExtra("requestCode", ScanActivity.WRITE_TAG);
				intent.putExtra(ScanActivity.EXIT_AFTER_WRITE, false);
				startActivityForResult(intent, ScanActivity.WRITE_TAG);
			} else {
				Log.d(TAG, "Edit activity failed");
				Toast.makeText(getApplicationContext(), "Failed to save",
						Toast.LENGTH_SHORT).show();
			}
		} else if (ScanActivity.WRITE_TAG == requestCode) {
			if (RESULT_OK == resultCode) {
			} else {
				Log.d(TAG, "Write to tag failed");
				Toast.makeText(getApplicationContext(), "Failed to write",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private byte[] readFromTag(Intent intent) {
		Parcelable[] rawMsgs = intent
				.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

		if (null != rawMsgs) {
			NdefMessage msg = (NdefMessage) rawMsgs[0];
			NdefRecord recd = msg.getRecords()[0];

			byte[] payload = recd.getPayload();
			byte[] data = new byte[payload.length - 3];
			System.arraycopy(payload, 3, data, 0, data.length);
			Log.d(TAG, "data read = " + new String(data));
			return data;
		} else {
			return null;
		}
	}

	private void setupWrite(byte[] data) {
		Context context = getApplicationContext();
		NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(context);

		Intent nfcIntent = new Intent(context, ScanActivity.class)
				.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		nfcIntent.putExtra(ScanActivity.NFC_DATA, data);
		PendingIntent pi = PendingIntent.getActivity(context, 0, nfcIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		IntentFilter tagDetected = new IntentFilter(
				NfcAdapter.ACTION_TAG_DISCOVERED);

		nfcAdapter.enableForegroundDispatch(this, pi,
				new IntentFilter[] { tagDetected }, null);
		Toast.makeText(getApplicationContext(), "Ready to write",
				Toast.LENGTH_SHORT).show();
		msg.setText("Ready to write data to TAG!!");
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		byte[] nfcMessageData = intent.getByteArrayExtra(ScanActivity.NFC_DATA);
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		if (nfcMessageData != null) {
			writeToTag(this, tag, nfcMessageData);
			/*
			 * NfcAdapter nfcAdapter =
			 * NfcAdapter.getDefaultAdapter(getApplicationContext());
			 * nfcAdapter.disableForegroundDispatch(this);
			 */
		}
	}

	public boolean writeToTag(Context context, Tag tag, byte[] data) {
		/*
		 * NdefRecord appRecord = NdefRecord.createApplicationRecord(context
		 * .getPackageName());
		 */
		byte[] payload = new byte[3 + data.length];
		payload[0] = (byte) 2;
		payload[1] = 'e';
		payload[2] = 'n';
		System.arraycopy(data, 0, payload, 3, data.length);
		NdefRecord relayRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
				NdefRecord.RTD_TEXT, new byte[0], payload);

		NdefMessage message = new NdefMessage(new NdefRecord[] { relayRecord });

		try {
			// If the tag is already formatted, just write the message to it
			Ndef ndef = Ndef.get(tag);
			if (ndef != null) {
				ndef.connect();

				// Make sure the tag is writable
				if (!ndef.isWritable()) {
					Toast.makeText(context, "Read only :(", Toast.LENGTH_SHORT)
							.show();
					return false;
				}

				// Check if there's enough space on the tag for the message
				int size = message.toByteArray().length;
				if (ndef.getMaxSize() < size) {
					Toast.makeText(context, "Tooooooo big :( " + size,
							Toast.LENGTH_SHORT).show();
					return false;
				}

				try {
					// Write the data to the tag
					ndef.writeNdefMessage(message);

					Toast.makeText(context, "Data written to tag :)",
							Toast.LENGTH_SHORT).show();
					msg.setText("Data written successfully");
					if (supposedToExit) {
						finish();
					}
					return true;
				} catch (TagLostException tle) {
					Toast.makeText(context, "Tag lost suddenly :|",
							Toast.LENGTH_SHORT).show();
					return false;
				} catch (IOException ioe) {
					Toast.makeText(context, "Format error :(",
							Toast.LENGTH_SHORT).show();
					return false;
				} catch (FormatException fe) {
					Toast.makeText(context, "Format error :(",
							Toast.LENGTH_SHORT).show();
					return false;
				}
				// If the tag is not formatted, format it with the message
			} else {
				NdefFormatable format = NdefFormatable.get(tag);
				if (format != null) {
					try {
						format.connect();
						format.format(message);

						Toast.makeText(context,
								"Data written to Tag in NDEF format :)",
								Toast.LENGTH_SHORT).show();
						msg.setText("Data written successfully");
						if (supposedToExit) {
							finish();
						}
						return true;
					} catch (TagLostException tle) {
						Toast.makeText(context, "Tag lost suddenly :|",
								Toast.LENGTH_SHORT).show();
						return false;
					} catch (IOException ioe) {
						Toast.makeText(context, "Format error :(",
								Toast.LENGTH_SHORT).show();
						return false;
					} catch (FormatException fe) {
						Toast.makeText(context, "Format error :(",
								Toast.LENGTH_SHORT).show();
						return false;
					}
				} else {
					Toast.makeText(context, "NDEF not supported :(",
							Toast.LENGTH_SHORT).show();
					return false;
				}
			}
		} catch (Exception e) {
			Toast.makeText(context, "Even the error is not known LOLZ",
					Toast.LENGTH_SHORT).show();
		}

		return false;
	}
}