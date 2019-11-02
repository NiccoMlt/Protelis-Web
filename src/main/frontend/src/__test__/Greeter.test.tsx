import React from 'react';
import ReactDOM from 'react-dom';
import Greeter from '../components/Greeter';

it('renders without crashing', () => {
  const div = document.createElement('div');
  ReactDOM.render(<Greeter />, div);
  ReactDOM.unmountComponentAtNode(div);
});
