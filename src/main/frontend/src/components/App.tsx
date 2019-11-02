import React from 'react';
import { BrowserRouter as Router } from 'react-router-dom';
import Greeter from './Greeter';

const App: React.FC = () => (
  <Router>
    <div className="App">
      <header className="App-header">
        <Greeter />
      </header>
    </div>
  </Router>
);

export default App;
