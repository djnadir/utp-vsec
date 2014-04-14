package com.niks.utpvsec.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.niks.utpvsec.classes.IDCard;
import com.niks.utpvsec.classes.Ownership;
import com.niks.utpvsec.classes.User;
import com.utp.vsec.R;

public class EditActivity extends Activity {

	private static final String TAG = "EditActivity";
	public static final int EDIT_DETAILS = 1;
	protected static final int PICK_PHOTO = 2;
	private IDCard editCard;
	private boolean resumingAfterCapture = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit);
		ImageView iv = (ImageView) findViewById(R.id.photo);
		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent cameraIntent = new Intent(
						MediaStore.ACTION_IMAGE_CAPTURE);
				cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

				Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
				galleryIntent.setType("image/*");

				Intent pickintent = Intent.createChooser(cameraIntent,
						"Pick a photo");
				pickintent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
						new Intent[] { galleryIntent });
				startActivityForResult(pickintent, EditActivity.PICK_PHOTO);
			}
		});
		RadioGroup rg = (RadioGroup) findViewById(R.id.ownership);
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				LinearLayout ll = (LinearLayout) findViewById(R.id.owneronly);
				if (checkedId == R.id.owner) {
					ll.setVisibility(View.VISIBLE);
				} else {
					ll.setVisibility(View.GONE);
				}
			}
		});
		Button b = (Button) findViewById(R.id.done);
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(TAG, "clicked");
				Intent intent = new Intent();
				IDCard idcard = getData();
				intent.putExtra(IDCard.IDCARD_DATA, idcard);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "started");
		if (resumingAfterCapture) {
			resumingAfterCapture = false;
			return;
		}
		Intent intent = getIntent();
		IDCard prevCard = (IDCard) intent
				.getSerializableExtra(IDCard.IDCARD_DATA);
		if (null == prevCard) {
			editCard = new IDCard();
			clearData();
		} else {
			editCard = prevCard;
			setData(editCard);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case EditActivity.PICK_PHOTO:
			if (RESULT_OK == resultCode) {
				Bitmap photo = (Bitmap) data.getExtras().get("data");
				photo = Bitmap.createScaledBitmap(photo, 300, 500, true);
				Toast.makeText(getApplicationContext(), "Captured ",
						Toast.LENGTH_SHORT).show();

				ImageView iv = (ImageView) findViewById(R.id.photo);
				iv.setImageBitmap(photo);
				resumingAfterCapture = true;
			}
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
			break;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	private void setData(IDCard idcard) {
		EditText et;
		RadioGroup rg;
		ImageView iv;
		LinearLayout ll;

		iv = (ImageView) findViewById(R.id.photo);
		idcard.setPhoto(iv);

		if (Ownership.OWNER == idcard.ownership) {

			rg = (RadioGroup) findViewById(R.id.ownership);
			rg.check(R.id.owner);

			et = (EditText) findViewById(R.id.id);
			et.setText(idcard.id);

			et = (EditText) findViewById(R.id.name0);
			et.setText(idcard.allowed[0].name);

			et = (EditText) findViewById(R.id.id0);
			et.setText(idcard.allowed[0].id);

			et = (EditText) findViewById(R.id.name1);
			et.setText(idcard.allowed[1].name);

			et = (EditText) findViewById(R.id.id1);
			et.setText(idcard.allowed[1].id);

			et = (EditText) findViewById(R.id.name2);
			et.setText(idcard.allowed[2].name);

			et = (EditText) findViewById(R.id.id2);
			et.setText(idcard.allowed[2].id);

			ll = (LinearLayout) findViewById(R.id.owneronly);
			ll.setVisibility(View.VISIBLE);

		} else if (Ownership.STAFF == idcard.ownership) {

			rg = (RadioGroup) findViewById(R.id.ownership);
			rg.check(R.id.staff);

			ll = (LinearLayout) findViewById(R.id.owneronly);
			ll.setVisibility(View.INVISIBLE);

		} else {
			invalidCall("Invalid data");
		}

		et = (EditText) findViewById(R.id.name);
		et.setText(idcard.name);

		et = (EditText) findViewById(R.id.carreg);
		et.setText(idcard.carreg);

		et = (EditText) findViewById(R.id.carmodel);
		et.setText(idcard.carModel);

		et = (EditText) findViewById(R.id.phone);
		et.setText(idcard.phone);
	}

	private IDCard getData() {
		IDCard idcard = new IDCard();
		EditText et;
		RadioGroup rg;
		ImageView iv;

		iv = (ImageView) findViewById(R.id.photo);
		idcard.getPhoto(iv);

		et = (EditText) findViewById(R.id.name);
		idcard.name = et.getText().toString();

		et = (EditText) findViewById(R.id.carreg);
		idcard.carreg = et.getText().toString();

		et = (EditText) findViewById(R.id.carmodel);
		idcard.carModel = et.getText().toString();

		et = (EditText) findViewById(R.id.phone);
		idcard.phone = et.getText().toString();

		rg = (RadioGroup) findViewById(R.id.ownership);
		int selected = rg.getCheckedRadioButtonId();
		if (selected == R.id.owner) {
			idcard.ownership = Ownership.OWNER;

			et = (EditText) findViewById(R.id.id);
			idcard.id = et.getText().toString();

			EditText name, number;

			name = (EditText) findViewById(R.id.name0);
			number = (EditText) findViewById(R.id.id0);
			idcard.allowed[0] = new User(name.getText().toString(), number
					.getText().toString());

			name = (EditText) findViewById(R.id.name1);
			number = (EditText) findViewById(R.id.id1);
			idcard.allowed[1] = new User(name.getText().toString(), number
					.getText().toString());

			name = (EditText) findViewById(R.id.name2);
			number = (EditText) findViewById(R.id.id2);
			idcard.allowed[2] = new User(name.getText().toString(), number
					.getText().toString());
		} else if (selected == R.id.staff) {
			idcard.ownership = Ownership.STAFF;
		} else {
			Log.d(TAG, "BUG in code! Invalid ownership");
			Toast.makeText(getApplicationContext(), "Invalid Ownership",
					Toast.LENGTH_LONG).show();
		}

		return idcard;
	}

	private void clearData() {
		EditText et;
		ImageView iv;
		RadioGroup rg;

		iv = (ImageView) findViewById(R.id.photo);
		iv.setImageResource(R.drawable.default_contact);

		et = (EditText) findViewById(R.id.name);
		et.setText("");

		et = (EditText) findViewById(R.id.id);
		et.setText("");

		et = (EditText) findViewById(R.id.carreg);
		et.setText("");

		et = (EditText) findViewById(R.id.carmodel);
		et.setText("");

		et = (EditText) findViewById(R.id.phone);
		et.setText("");

		et = (EditText) findViewById(R.id.name0);
		et.setText("");

		et = (EditText) findViewById(R.id.id0);
		et.setText("");

		et = (EditText) findViewById(R.id.name1);
		et.setText("");

		et = (EditText) findViewById(R.id.id1);
		et.setText("");

		et = (EditText) findViewById(R.id.name2);
		et.setText("");

		et = (EditText) findViewById(R.id.id2);
		et.setText("");

		rg = (RadioGroup) findViewById(R.id.ownership);
		rg.check(R.id.owner);
	}

	private void invalidCall(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
		finish();
	}

}
