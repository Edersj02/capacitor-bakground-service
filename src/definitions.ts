import {} from "@capacitor/core";

declare module "@capacitor/core" {
  interface PluginRegistry {
    CapBackground: CapBackground;
  }
}

export interface CapBackground {
  stopBackgroundService(): Promise<{}>;
  startBackgroundService(options: {driverId: string, token: string, url: string}): Promise<{}>;
}
