package fr.polytech.coffeemachineapp.utils

enum class DeviceStatus {
    OFFLINE {
        override fun toString(): String {
            return "Offline"
        }
        override fun getDrawable(): Int {
            return 0 // TODO
        }
    },
    ONLINE {
        override fun toString(): String {
            return "Online"
        }
        override fun getDrawable(): Int {
            return 0 // TODO
        }
    },
    RESET {
        override fun toString(): String {
            return "Reset"
        }
        override fun getDrawable(): Int {
            return 0 // TODO
        }
    },
    RESET_WIFI {
        override fun toString(): String {
            return "Reset Wifi"
        }
        override fun getDrawable(): Int {
            return 0 // TODO
        }
    },
    UNKNOWN {
        override fun toString(): String {
            return "Unknown"
        }
        override fun getDrawable(): Int {
            return 0 // TODO
        }
    };
    abstract fun getDrawable(): Int
    companion object {
        fun isOnline(status: DeviceStatus): Boolean {
            return status == ONLINE
        }
    }
}

enum class RequestStatus {
    WAITING {
        override fun toString(): String {
            return "Waiting"
        }
        override fun getDrawable(): Int {
            return 0 // TODO
        }
    },
    INITIALIZING {
        override fun toString(): String {
            return "Initializing"
        }
        override fun getDrawable(): Int {
            return 0 // TODO
        }
    },
    WARMING {
        override fun toString(): String {
            return "Warming"
        }
        override fun getDrawable(): Int {
            return 0 // TODO
        }
    },
    FILLING {
        override fun toString(): String {
            return "Filling"
        }
        override fun getDrawable(): Int {
            return 0 // TODO
        }
    },
    COLLECTING {
        override fun toString(): String {
            return "Collecting"
        }
        override fun getDrawable(): Int {
            return 0 // TODO
        }
    },
    UNKNOWN {
        override fun toString(): String {
            return "Unknown"
        }
        override fun getDrawable(): Int {
            return 0 // TODO
        }
    };
    abstract fun getDrawable(): Int
}

enum class LogStatus {
    SUCCESS {
        override fun toString(): String {
            return "Success"
        }
        override fun getDrawable(): Int {
            return 0 // TODO
        }
    },
    ERROR_WATER {
        override fun toString(): String {
            return "Error: No Water"
        }
        override fun getDrawable(): Int {
            return 0 // TODO
        }
    },
    ERROR_CUP {
        override fun toString(): String {
            return "Error: No Cup"
        }
        override fun getDrawable(): Int {
            return 0 // TODO
        }
    },
    ERROR_TIMEOUT {
        override fun toString(): String {
            return "Error: Timeout"
        }
        override fun getDrawable(): Int {
            return 0 // TODO
        }
    },
    ERROR_OFFLINE {
        override fun toString(): String {
            return "Error: Device Offline"
        }
        override fun getDrawable(): Int {
            return 0 // TODO
        }
    },
    ERROR_WARMING {
        override fun toString(): String {
            return "Error: Cannot warm the water"
        }
        override fun getDrawable(): Int {
            return 0 // TODO
        }
    },
    ERROR_FILLING {
        override fun toString(): String {
            return "Error: Cannot fill the cup"
        }
        override fun getDrawable(): Int {
            return 0 // TODO
        }
    },
    UNKNOWN {
        override fun toString(): String {
            return "Unknown"
        }
        override fun getDrawable(): Int {
            return 0 // TODO
        }
    };
    abstract fun getDrawable(): Int
    companion object {
        fun isError(status: LogStatus): Boolean {
            return status != SUCCESS
        }
    }
}