package fr.polytech.coffeemachineapp.utils

class Constant {
    companion object {
        // BLE GATT UUIDs
        const val SERVICE_UUID = "e29834b1-fdbe-4780-84f2-f3a87d0e97fb"
        const val WIFI_CREDENTIAL_UUID = "dddbf1dc-63f8-4146-bbee-523b47143be8"
        const val DEVICE_SETUP_UUID = "f5609179-c8f5-49b9-8f88-f4b86ed493e4"

        // Firebase paths
        const val REQUEST_LOGS_PATH = "logs"
        const val REQUESTS_PATH = "requests"
        const val DEVICES_PATH = "devices"
        const val USERS_PATH = "users"
        const val SENSORS_PATH = "sensors"
        const val OWNERSHIPS_PATH = "ownership"
    }
}