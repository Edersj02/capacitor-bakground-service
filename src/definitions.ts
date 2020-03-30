import {} from '@capacitor/core';

declare module '@capacitor/core' {
  interface PluginRegistry {
    CapBackground: CapBackground;
  }
}

export interface CapBackground {
  stopBackgroundService(): Promise<{}>;
  startBackgroundService(options: {
    driverId: string;
    driverName: string;
    pin: string;
    token: string;
    tenant: string;
    url: string;
    socketUrl: string;
    socketActive: string;
  }): Promise<{}>;
  setDriverStatus(options: { driverstatus: any }): Promise<{}>;
  setTripsIds(options: { tripsids: any[] }): Promise<{}>;
}
