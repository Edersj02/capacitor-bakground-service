import {} from "@capacitor/core";

declare module "@capacitor/core" {
  interface PluginRegistry {
    CapBackground: CapBackground;
  }
}

export interface CapBackground {
  echo(options: { value: string }): Promise<{value: string}>;
  
  startBackgroundService(): Promise<{}>;
}
