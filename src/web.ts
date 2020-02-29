import { WebPlugin } from '@capacitor/core';
import { CapBackgroundPlugin } from './definitions';

export class CapBackgroundWeb extends WebPlugin implements CapBackgroundPlugin {
  constructor() {
    super({
      name: 'CapBackground',
      platforms: ['web']
    });
  }

  async echo(options: { value: string }): Promise<{value: string}> {
    console.log('ECHO', options);
    return options;
  }
}

const CapBackground = new CapBackgroundWeb();

export { CapBackground };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(CapBackground);
