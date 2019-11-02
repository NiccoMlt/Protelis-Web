import React from 'react';
import { BrowserRouter as Router } from 'react-router-dom';
import ProtelisAppBar from './AppBar';
import FileTreeView from './FileTreeView';
import ProtelisEditor from './ProtelisEditor';

const App: React.FC = () => (
  <Router>
    <div className="App">
      <header className="App-header">
        <ProtelisAppBar />
      </header>
      <FileTreeView />
      <ProtelisEditor />
    </div>
  </Router>
);

export default App;
