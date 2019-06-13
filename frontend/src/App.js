import React from 'react';
import './App.css';
import { Graph } from "./Graph.js";

class App extends React.Component {

  render() {
    return (
      <div className="App">
        <Graph id="graph-render"/>
      </div>
    );
  }

}

export default App;
