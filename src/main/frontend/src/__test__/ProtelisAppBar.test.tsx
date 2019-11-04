import React from 'react';
import ReactDOM from 'react-dom';
import { shallow } from 'enzyme';
import ProtelisAppBar from '../components/ProtelisAppBar';

describe('Protelis AppBar component', () => {
  it('renders without crashing inside shallow', () => {
    const component = shallow(<ProtelisAppBar />);
    expect(component).toBeDefined();
  });

  it('renders without crashing', () => {
    const div = document.createElement('div');
    ReactDOM.render(<ProtelisAppBar />, div);
    ReactDOM.unmountComponentAtNode(div);
  });
});
