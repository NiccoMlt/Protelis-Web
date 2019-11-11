import { configure } from 'enzyme';
import Adapter from 'enzyme-adapter-react-16';

global.console.warn = (message) => {
  throw message;
};

global.console.error = (message) => {
  throw message;
};

configure({ adapter: new Adapter() });
