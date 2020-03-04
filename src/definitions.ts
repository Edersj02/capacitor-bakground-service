import {} from "@capacitor/core";

declare module "@capacitor/core" {
  interface PluginRegistry {
    CapBackground: CapBackground;
  }
}

export interface CapBackground {
  startBackgroundService(options: {driverId: string, token: string}): Promise<{}>;
}
