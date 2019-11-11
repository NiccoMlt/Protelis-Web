import React from 'react';
import ReactDOM from 'react-dom';
import { shallow } from 'enzyme';
import Konva from 'konva';
import App from '../app/App';

describe('App component', () => {
  it('renders without crashing inside shallow', () => {
    // Enzyme does not need to set Konva isBrowser undocumented property
    const component = shallow(<App />);
    expect(component).toBeDefined();
  });

  it('renders without crashing', () => {
    (Konva as any).isBrowser = false; // Needed by React default node environment
    // https://github.com/konvajs/react-konva/issues/270#issuecomment-431114761
    const div = document.createElement('div');
    ReactDOM.render(<App />, div);
    ReactDOM.unmountComponentAtNode(div);
  });
});
