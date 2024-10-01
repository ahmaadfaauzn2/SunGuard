package com.example.bletesting;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/*
 * This is an adapter that populates the list of BLE devices found in the sweep. This list is then
 * used to populate the UI the user sees so they can select the device they are working with.
 *
 * */
public class BleRecyclerAdapter extends RecyclerView.Adapter<BleRecyclerAdapter.BleViewHolder> {

    private ArrayList<BluetoothDevice> bleDevices;


    private LayoutInflater inflater;
//    private BluetoothLeService bluetoothLeService;
    private Context context;

    private ItemClickListener clickListener;

    // data is passed into the constructor
    BleRecyclerAdapter(Context context) {
        this.bleDevices = new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
    }

    // inflates a row containing a device's info as needed
    @Override
    public BleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_device, parent, false);
        return new BleViewHolder(view);
    }


    @SuppressLint("MissingPermission")
    // binds the data to the TextViews in each row
    @Override
    public void onBindViewHolder(BleViewHolder holder, int position) {
        String name = "";
        if (bleDevices.get(position).getName() == null) {
            name = "Unknown Device";
        } else {
            name = bleDevices.get(position).getName();
        }
        String address = bleDevices.get(position).getAddress();

        holder.deviceAddressView.setText(address);
        holder.deviceNameView.setText(name);
    }
    @Override
    public int getItemCount() {
        return bleDevices.size();
    }

    public BluetoothDevice getSelectedDevice() {
        return bleDevices.get(0);
    }

    public class BleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView deviceAddressView;
        private TextView deviceNameView;
        public BleViewHolder(View deviceView) {
            super(deviceView);
            deviceAddressView = deviceView.findViewById(R.id.device_address);
            deviceNameView = deviceView.findViewById(R.id.device_name);
            deviceView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public BluetoothDevice getDevice(int id ) {
        return bleDevices.get(id);
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    // add a new device to the list
    public void addDevice(BluetoothDevice device) {
        if (!bleDevices.contains(device)) {
            bleDevices.add(device);
        }
    }

    // clear the list
    public void clear() {
        bleDevices.clear();
    }

}
