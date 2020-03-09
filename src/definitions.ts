import {} from "@capacitor/core";

declare module "@capacitor/core" {
  interface PluginRegistry {
    CapBackground: CapBackground;
  }
}

export interface CapBackground {
  stopBackgroundService(): Promise<{}>;
  startBackgroundService(options: {
    driverId: string,
    driverName: string,
    pin: string,
    token: string,
    url: string,
    socketUrl: string
  }): Promise<{}>;
  setDriverStatus(options: {driverstatus: any}): Promise<{}>;
}
