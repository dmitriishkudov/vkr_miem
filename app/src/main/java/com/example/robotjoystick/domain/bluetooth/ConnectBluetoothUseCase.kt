package com.example.robotjoystick.domain.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.example.robotjoystick.data.bluetooth.connector.BluetoothConnector
import com.example.robotjoystick.data.bluetooth.connector.factory.BluetoothConnectorFactory
import com.example.robotjoystick.data.bluetooth.permissions.BluetoothPermissionsManager
import com.example.robotjoystick.data.bluetooth.permissions.withConnectPermissions
import com.example.robotjoystick.data.bluetooth.device.BluetoothDeviceData
import javax.inject.Inject

@SuppressLint("MissingPermission")
class ConnectBluetoothUseCase @Inject constructor(
    private val scanBluetoothDevices: ScanBluetoothDevicesUseCase,
    private val getBluetoothDevice: GetBluetoothDeviceUseCase,
    private val bluetoothConnectorFactory: BluetoothConnectorFactory,
    private val permissionsManager: BluetoothPermissionsManager,
    private val createBond: CreateBondUseCase,
) {
    suspend operator fun invoke(bluetoothDeviceData: BluetoothDeviceData): BluetoothConnector {
        scanBluetoothDevices.stop()
        val device = getBluetoothDevice(bluetoothDeviceData)
        permissionsManager.withConnectPermissions {
            if (device.bondState != BluetoothDevice.BOND_BONDED) {
                createBond(device)
            }
        }
        return bluetoothConnectorFactory.create(device)
    }
}