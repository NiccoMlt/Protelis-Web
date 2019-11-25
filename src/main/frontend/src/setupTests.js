/* eslint-disable @typescript-eslint/explicit-function-return-type */ // it's a JS file
/* eslint-disable @typescript-eslint/unbound-method */ // Jest wants it like that

import { configure } from 'enzyme';
import Adapter from 'enzyme-adapter-react-16';

global.console.warn = (message) => {
  throw message;
};

global.console.error = (message) => {
  throw message;
};

configure({ adapter: new Adapter() });
