import React from 'react';
import './App.css';
import { Graph } from "./Graph.js";

function App() {
  return (
    <div className="App">
      <header className="App-header">
        {/* <img src={logo} className="App-logo" alt="logo" /> */}
        <p>
          ASSO DAG EDITOR
        </p>
      </header>
      
      <Graph id="graph-render"/>
    </div>
  );
}

export default App;
