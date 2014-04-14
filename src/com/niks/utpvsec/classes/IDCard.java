package com.niks.utpvsec.classes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.utp.vsec.R;

public class IDCard implements Serializable {
	private static final long serialVersionUID = 8470732927570815679L;
	private static final String TAG = "IDCard";
	public static String IDCARD_DATA = "ID Card Data";

	private byte[] photo;
	public String name;
	public String id;
	public Ownership ownership;
	public String carreg;
	public String carModel;
	public String phone;
	public User allowed[] = new User[3];

	public IDCard(String name, String id, Ownership ownership, String carreg,
			String carModel, String phone) {
		super();
		this.photo = null;
		this.name = name;
		this.id = id;
		this.ownership = ownership;
		this.carreg = carreg;
		this.carModel = carModel;
		this.phone = phone;

		for (int i = 0; i < allowed.length; i++) {
			allowed[i] = new User();
		}
	}

	public IDCard() {
		this.photo = null;
		this.name = "Mr Person Name";
		this.id = "12345";
		this.ownership = Ownership.OWNER;
		this.carreg = "ABC 123";
		this.carModel = "Mercedez Benz";
		this.phone = "123-1234567";

		for (int i = 0; i < allowed.length; i++) {
			allowed[i] = new User();
		}
	}

	public static byte[] serialize(IDCard obj) {
		try {
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			ObjectOutputStream o;
			o = new ObjectOutputStream(b);
			o.writeObject(obj);
//			//TODO Workaround for testing w/o NFC
//			DummyTag.persistentData = b.toByteArray();
//			return obj.name.getBytes();
			return b.toByteArray();
		} catch (Exception e) {
			Log.e(TAG, "Serialization failed");
			e.printStackTrace();
		}
		return null;
	}

	public static IDCard deserialize(byte[] bytes) {
		if (null == bytes) {
			return null;
		}
		try {
//			//TODO Workaround for testing w/o NFC
//			bytes = DummyTag.persistentData;
			ByteArrayInputStream b = new ByteArrayInputStream(bytes);
			ObjectInputStream o;
			o = new ObjectInputStream(b);
			return (IDCard) o.readObject();
		} catch (Exception e) {
			Log.e(TAG, "Deserialization failed");
			e.printStackTrace();
		}
		return null;
	}

	public void setPhoto(ImageView iv) {
		if (null == this.photo) {
			iv.setImageResource(R.drawable.default_contact);
		} else {
			Bitmap bitmap = BitmapFactory.decodeByteArray(this.photo, 0,
					this.photo.length);
			iv.setImageBitmap(bitmap);
		}
	}

	public void getPhoto(ImageView iv) {
		iv.buildDrawingCache();
		Bitmap bitmap = iv.getDrawingCache();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 25, stream);
		this.photo = stream.toByteArray();
		Log.d(TAG, "bytes " + bitmap.getByteCount() + " compressed "
				+ this.photo.length);
	}

}