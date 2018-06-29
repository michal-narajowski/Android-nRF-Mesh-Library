package no.nordicsemi.android.meshprovisioner.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class SensorData implements Parcelable {
    public static class SensorPropertyData implements Parcelable {

        public int propertyID;
        public byte[] data;


        public SensorPropertyData(int propertyID, byte[] data) {
            this.propertyID = propertyID;
            this.data = data;
        }

        protected SensorPropertyData(Parcel in) {
            propertyID = in.readInt();
            data = in.createByteArray();
        }

        public static final Creator<SensorPropertyData> CREATOR = new Creator<SensorPropertyData>() {
            @Override
            public SensorPropertyData createFromParcel(Parcel in) {
                return new SensorPropertyData(in);
            }

            @Override
            public SensorPropertyData[] newArray(int size) {
                return new SensorPropertyData[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(propertyID);
            parcel.writeByteArray(data);
        }
    }

    public ArrayList<SensorPropertyData> propertyData;

    public SensorData(ByteBuffer buffer) {
        propertyData = new ArrayList<>();

        while(buffer.position() < buffer.limit()) {
            byte b = buffer.get();
            if ((b & 1) == 1) {
                int len = (b >> 1);
                byte b1 = buffer.get();
                byte b2 = buffer.get();

                int propertyID = (b1 | (b2 << 8));
                byte[] byteArray = new byte[len];

                for (int i = 0; i < len; ++i) {
                    byteArray[i] = buffer.get();
                }
                SensorPropertyData propData = new SensorPropertyData(propertyID, byteArray);
                propertyData.add(propData);
            }
        }
    }

    protected SensorData(Parcel in) {
        propertyData = in.createTypedArrayList(SensorPropertyData.CREATOR);
    }

    public static final Creator<SensorData> CREATOR = new Creator<SensorData>() {
        @Override
        public SensorData createFromParcel(Parcel in) {
            return new SensorData(in);
        }

        @Override
        public SensorData[] newArray(int size) {
            return new SensorData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(propertyData);
    }

}
