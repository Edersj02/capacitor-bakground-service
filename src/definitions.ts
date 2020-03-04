import {} from "@capacitor/core";

declare module "@capacitor/core" {
  interface PluginRegistry {
    CapBackground: CapBackground;
  }
}

export interface CapBackground {
  startBackgroundService(options: SessionData): Promise<{}>;
}

export interface SessionData {
  driverId: number;
  token: String;
}
