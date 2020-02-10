import configureMockStore from 'redux-mock-store';
import React from 'react';
import ReactDOM from 'react-dom';
import { shallow } from 'enzyme';
import Konva from 'konva';
import { Provider } from 'react-redux';
import App from '../app/App';
import { RootState } from '../app/rootReducer';
import { initialState as editor } from '../features/editor/editorSlice';
import { initialState as exec } from '../features/render/execSlice';

const mockStore = configureMockStore<RootState>();

describe('App component', () => {
  it('renders without crashing inside shallow', () => {
    // Enzyme does not need to set Konva isBrowser undocumented property
    const component = shallow(<App />);
    expect(component).toBeDefined();
  });

  it('renders without crashing', () => {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    (Konva as any).isBrowser = false; // Needed by React default node environment
    // https://github.com/konvajs/react-konva/issues/270#issuecomment-431114761

    const div = document.createElement('div');
    const store = mockStore({
      editor,
      exec,
    });

    ReactDOM.render(
      <Provider store={store}>
        <App />
      </Provider>,
      div,
    );
    ReactDOM.unmountComponentAtNode(div);
  });
});
