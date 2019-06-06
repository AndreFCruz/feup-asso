import React from 'react';
import {
  GraphView, // required
  // Edge, // optional
  // type IEdge, // optional
  // Node, // optional
  // type INode, // optional
  // type LayoutEngineType, // required to change the layoutEngineType, otherwise optional
  // BwdlTransformer, // optional, Example JSON transformer
  // GraphUtils // optional, useful utility functions
} from 'react-digraph';
import './Graph.css';
import { GraphConfig as DEFAULT_GRAPH_CONFIG } from "./Graph.configs";
import { sample1 as DEFAULT_GRAPH_SAMPLE } from "./Graph.sample";

const NODE_KEY = "id"       // Allows D3 to correctly update DOM

export class Graph extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      graph: DEFAULT_GRAPH_SAMPLE,
      selected: {},
      graphConfig: DEFAULT_GRAPH_CONFIG,
    }
  }

  componentDidMount() {
    // TODO:
    // - fetch types of nodes for Sources/Handlers/Sinks
    // - construct a GraphConfig object
    // - update state with this.setState({...});
  }

  /* Define custom graph editing methods here */

  onSelectNode(event) {
    console.log('** onSelectNode **');
    console.log(event);
  }

  onCreateNode(event) {
    console.log('** onCreateNode **');
    console.log(event);
  }

  onUpdateNode(event) {
    console.log('** onUpdateNode **');
    console.log(event);
  }

  onDeleteNode(event) {
    console.log('** onDeleteNode **');
    console.log(event);
  }

  onSelectEdge(event) {
    console.log('** onSelectEdge **');
    console.log(event);
  }

  onCreateEdge(event) {
    console.log('** onCreateEdge **');
    console.log(event);
  }

  onSwapEdge(event) {
    console.log('** onSwapEdge **');
    console.log(event);
  }

  onDeleteEdge(event) {
    console.log('** onDeleteEdge **');
    console.log(event);
  }

  render() {
    const nodes = this.state.graph.nodes;
    const edges = this.state.graph.edges;
    const selected = this.state.selected;

    const NodeTypes = this.state.graphConfig.NodeTypes;
    const NodeSubtypes = this.state.graphConfig.NodeSubtypes;
    const EdgeTypes = this.state.graphConfig.EdgeTypes;

    return (
      <div id='graph'>

        <GraphView  ref='GraphView'
                    nodeKey={NODE_KEY}
                    nodes={nodes}
                    edges={edges}
                    selected={selected}
                    nodeTypes={NodeTypes}
                    nodeSubtypes={NodeSubtypes}
                    edgeTypes={EdgeTypes}
                    onSelectNode={this.onSelectNode}
                    onCreateNode={this.onCreateNode}
                    onUpdateNode={this.onUpdateNode}
                    onDeleteNode={this.onDeleteNode}
                    onSelectEdge={this.onSelectEdge}
                    onCreateEdge={this.onCreateEdge}
                    onSwapEdge={this.onSwapEdge}
                    onDeleteEdge={this.onDeleteEdge}/>

      </div>
    );
  }

}