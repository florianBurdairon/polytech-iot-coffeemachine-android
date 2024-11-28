package fr.polytech.coffeemachineapp.utils

enum class DeviceStatus {
    OFFLINE {
        override fun toString(): String {
            return "Offline"
        }
    },
    ONLINE {
        override fun toString(): String {
            return "Online"
        }
    },
    RESET {
        override fun toString(): String {
            return "Reset"
        }
    },
    RESET_WIFI {
        override fun toString(): String {
            return "Reset Wifi"
        }
    },
    UNKNOWN {
        override fun toString(): String {
            return "Unknown"
        }
    };
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
    },
    INITIALIZING {
        override fun toString(): String {
            return "Initializing"
        }
    },
    WARMING {
        override fun toString(): String {
            return "Warming"
        }
    },
    FILLING {
        override fun toString(): String {
            return "Filling"
        }
    },
    COLLECTING {
        override fun toString(): String {
            return "Collecting"
        }
    },
    UNKNOWN {
        override fun toString(): String {
            return "Unknown"
        }
    };
}

enum class LogStatus {
    SUCCESS {
        override fun toString(): String {
            return "Success"
        }
    },
    ERROR_WATER {
        override fun toString(): String {
            return "No Water"
        }
    },
    ERROR_CUP {
        override fun toString(): String {
            return "No Cup"
        }
    },
    ERROR_TIMEOUT {
        override fun toString(): String {
            return "Timeout"
        }
    },
    ERROR_OFFLINE {
        override fun toString(): String {
            return "Device Offline"
        }
    },
    ERROR_WARMING {
        override fun toString(): String {
            return "Cannot warm the water"
        }
    },
    ERROR_FILLING {
        override fun toString(): String {
            return "Cannot fill the cup"
        }
    },
    UNKNOWN {
        override fun toString(): String {
            return "Unknown"
        }
    };
    companion object {
        fun isError(status: LogStatus): Boolean {
            return status != SUCCESS
        }
    }
}