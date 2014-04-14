package com.niks.utpvsec.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.niks.utpvsec.classes.IDCard;
import com.niks.utpvsec.classes.Ownership;
import com.utp.vsec.R;

public class ShowActivity extends Activity {

	private static final String TAG = "ShowActivity";
	private IDCard idcard;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = getIntent();
		TextView tv;
		Log.d(TAG, "started");

		this.idcard = (IDCard) intent.getSerializableExtra(IDCard.IDCARD_DATA);
		if (null == idcard) {
			invalidCall("No data given");
			return;
		}
		if (Ownership.OWNER == idcard.ownership) {
			setContentView(R.layout.owner);
			tv = (TextView) findViewById(R.id.ownership);
			tv.setText("Owner");

			tv = (TextView) findViewById(R.id.id);
			tv.setText(idcard.id);

			tv = (TextView) findViewById(R.id.name0);
			tv.setText(idcard.allowed[0].name);
			tv = (TextView) findViewById(R.id.id0);
			tv.setText(idcard.allowed[0].id);

			tv = (TextView) findViewById(R.id.name1);
			tv.setText(idcard.allowed[1].name);
			tv = (TextView) findViewById(R.id.id1);
			tv.setText(idcard.allowed[1].id);

			tv = (TextView) findViewById(R.id.name2);
			tv.setText(idcard.allowed[2].name);
			tv = (TextView) findViewById(R.id.id2);
			tv.setText(idcard.allowed[2].id);

		} else if (Ownership.STAFF == idcard.ownership) {
			setContentView(R.layout.staff);
			tv = (TextView) findViewById(R.id.ownership);
			tv.setText("Staff");
		} else {
			invalidCall("Invalid data");
			return;
		}
		
		ImageView iv = (ImageView) findViewById(R.id.photo);
		idcard.setPhoto(iv);
		
		tv = (TextView) findViewById(R.id.name);
		tv.setText(idcard.name);

		tv = (TextView) findViewById(R.id.carreg);
		tv.setText(idcard.carreg);

		tv = (TextView) findViewById(R.id.carmodel);
		tv.setText(idcard.carModel);

		tv = (TextView) findViewById(R.id.phone);
		tv.setText(idcard.phone);
	}

	private void invalidCall(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_showactivity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.edit:
			Intent intent = new Intent(getApplicationContext(),
					EditActivity.class);
			intent.putExtra(IDCard.IDCARD_DATA, idcard);
			startActivityForResult(intent, EditActivity.EDIT_DETAILS);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (EditActivity.EDIT_DETAILS == requestCode) {
			if (RESULT_OK == resultCode) {
				IDCard idcard = (IDCard) data.getSerializableExtra(IDCard.IDCARD_DATA);
				Intent intent = new Intent(getApplicationContext(), ScanActivity.class);
				intent.putExtra(ScanActivity.NFC_DATA, IDCard.serialize(idcard));
				intent.putExtra("requestCode", ScanActivity.WRITE_TAG);
				startActivityForResult(intent, ScanActivity.WRITE_TAG);
				finish();
			} else {
				Log.d(TAG, "Edit activity failed");
				Toast.makeText(getApplicationContext(), "Failed to save",
						Toast.LENGTH_SHORT).show();
			}
		} else if (ScanActivity.WRITE_TAG == requestCode) {
			if (RESULT_OK == resultCode) {
				finish();
			} else {
				Log.d(TAG, "Write to tag failed");
				Toast.makeText(getApplicationContext(), "Failed to write",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

}
