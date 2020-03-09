package com.easj.capservice.entities;

public class SessionData {

    private int driverId;

    private String DriverName;

    private int pin;

    private String token;

    private String url;

    private String socketUrl;

    private DriverStatus driverStatus;

    public SessionData() {
        super();
    }

    public SessionData(int driverId, String DriverName, int pin, String token, String url, String socketUrl) {
        this.driverId = driverId;
        this.DriverName = DriverName;
        this.pin = pin;
        this.token = token;
        this.url = url;
        this.socketUrl = socketUrl;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return DriverName;
    }

    public void setDriverName(String driverName) {
        this.DriverName = driverName;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSocketUrl() {
        return socketUrl;
    }

    public void setSocketUrl(String socketUrl) {
        this.socketUrl = socketUrl;
    }

    public DriverStatus getDriverStatus() {
        return driverStatus;
    }

    public void setDriverStatus(DriverStatus driverStatus) {
        this.driverStatus = driverStatus;
    }

    public static class DriverStatus {
        private int driverStatusId;

        private String description;

        private int categoryId;

        private boolean isActive;

        public DriverStatus() {
            super();
        }

        public DriverStatus(int driverStatusId, String description, int categoryId, boolean isActive) {
            this.driverStatusId = driverStatusId;
            this.description = description;
            this.categoryId = categoryId;
            this.isActive = isActive;
        }

        public int getDriverStatusId() {
            return driverStatusId;
        }

        public void setDriverStatusId(int driverStatusId) {
            this.driverStatusId = driverStatusId;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(int categoryId) {
            this.categoryId = categoryId;
        }

        public boolean isActive() {
            return isActive;
        }

        public void setActive(boolean active) {
            isActive = active;
        }
    }

}
