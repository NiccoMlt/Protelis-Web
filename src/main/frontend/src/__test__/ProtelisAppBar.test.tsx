import React from 'react';
import ReactDOM from 'react-dom';
import { shallow } from 'enzyme';
import { Provider } from 'react-redux';
import configureMockStore from 'redux-mock-store';
import { RootState } from '../app/rootReducer';
import ProtelisAppBar from '../components/ProtelisAppBar';
import { initialState as editor } from '../features/editor/editorSlice';
import { initialState as exec } from '../features/render/execSlice';

const mockStore = configureMockStore<RootState>();

describe('Protelis AppBar component', () => {
  it('renders without crashing inside shallow', () => {
    const component = shallow(<ProtelisAppBar />);
    expect(component).toBeDefined();
  });

  it('renders without crashing', () => {
    const store = mockStore({
      editor,
      exec,
    });
    const div = document.createElement('div');
    ReactDOM.render(
      <Provider store={store}>
        <ProtelisAppBar />
      </Provider>,
      div,
    );
    ReactDOM.unmountComponentAtNode(div);
  });
});
