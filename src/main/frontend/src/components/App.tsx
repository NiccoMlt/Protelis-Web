import React from 'react';
import { BrowserRouter as Router } from 'react-router-dom';
import ProtelisAppBar from './AppBar';
import FileTreeView from './FileTreeView';

const App: React.FC = () => (
  <Router>
    <div className="App">
      <header className="App-header">
        <ProtelisAppBar />
      </header>
      <FileTreeView />
    </div>
  </Router>
);

export default App;
