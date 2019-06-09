import React from 'react';
import axios from "axios";
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
// import { GraphConfig as DEFAULT_GRAPH_CONFIG } from "./Graph.configs";
import { sample2 as DEFAULT_GRAPH_SAMPLE } from "./Graph.sample";
import GraphConfig, {
  edgeTypes,
  EMPTY_EDGE_TYPE,
  EMPTY_TYPE,
  NODE_KEY,
  nodeTypes,
  // POLY_TYPE,
  // SPECIAL_CHILD_SUBTYPE,
  SPECIAL_EDGE_TYPE,
  SPECIAL_TYPE,
  // SKINNY_TYPE
} from './graph-config'; // Configures node/edge types

export class Graph extends React.Component {

  constructor(props) {
    super(props);

    let sample = DEFAULT_GRAPH_SAMPLE;
    let config = GraphConfig;

    this.state = {
      graph: sample,
      selected: {},
      totalNodes: sample.nodes.length,
      graphConfig: config,
    }
  }

  componentDidMount() {
    // TODO:
    // - fetch types of nodes for Sources/Handlers/Sinks
    // - construct a GraphConfig object
    // - update state with this.setState({...});

    // Testing axios
    axios.get('https://api.github.com/users/AndreFCruz')
      .then(response => console.log(response));
  }

  /*
   * Handlers/Interaction
   */

  // Called by 'drag' handler, etc..
  // to sync updates from D3 with the graph
  onUpdateNode(viewNode) {
    const graph = this.state.graph;
    const i = this.getNodeIndex(viewNode);

    graph.nodes[i] = viewNode;
    this.setState({ graph });
  }

  // Node 'mouseUp' handler
  onSelectNode(viewNode) {
    // Deselect events will send Null viewNode
    this.setState({ selected: viewNode });
  }

  // Edge 'mouseUp' handler
  onSelectEdge(viewEdge) {
    this.setState({ selected: viewEdge });
  }

  // Updates the graph with a new node
  onCreateNode(x, y) {
    const graph = this.state.graph;

    // This is just an example - any sort of logic
    // could be used here to determine node type
    // There is also support for subtypes. (see 'sample' above)
    // The subtype geometry will underlay the 'type' geometry for a node
    const type = Math.random() < 0.25 ? SPECIAL_TYPE : EMPTY_TYPE;

    const viewNode = {
      id: Date.now(),
      title: '',
      type,
      x,
      y
    };

    graph.nodes = [...graph.nodes, viewNode];
    this.setState({ graph });
  }

  // Deletes a node from the graph
  onDeleteNode(viewNode, nodeId, nodeArr) {
    const graph = this.state.graph;
    // Delete any connected edges
    const newEdges = graph.edges.filter((edge, i) => {
      return edge.source !== viewNode[NODE_KEY] && edge.target !== viewNode[NODE_KEY];
    });
    graph.nodes = nodeArr;
    graph.edges = newEdges;

    this.setState({ graph, selected: null });
  }

  // Creates a new node between two edges
  onCreateEdge(sourceViewNode, targetViewNode) {
    const graph = this.state.graph;
    // This is just an example - any sort of logic
    // could be used here to determine edge type
    const type = sourceViewNode.type === SPECIAL_TYPE ? SPECIAL_EDGE_TYPE : EMPTY_EDGE_TYPE;

    const viewEdge = {
      source: sourceViewNode[NODE_KEY],
      target: targetViewNode[NODE_KEY],
      type
    };

    // Only add the edge when the source node is not the same as the target
    if (viewEdge.source !== viewEdge.target) {
      graph.edges = [...graph.edges, viewEdge];
      this.setState({
        graph,
        selected: viewEdge
      });
    }
  }

  // Called when an edge is reattached to a different target.
  onSwapEdge(sourceViewNode, targetViewNode, viewEdge) {
    const graph = this.state.graph;
    const i = this.getEdgeIndex(viewEdge);
    const edge = JSON.parse(JSON.stringify(graph.edges[i]));

    edge.source = sourceViewNode[NODE_KEY];
    edge.target = targetViewNode[NODE_KEY];
    graph.edges[i] = edge;
    // reassign the array reference if you want the graph to re-render a swapped edge
    graph.edges = [...graph.edges];

    this.setState({
      graph,
      selected: edge
    });
  }

  // Called when an edge is deleted
  onDeleteEdge(viewEdge, edges) {
    const graph = this.state.graph;
    graph.edges = edges;
    this.setState({
      graph,
      selected: null
    });
  }

  // Helper to find the index of a given node
  getNodeIndex(searchNode) {
    return this.state.graph.nodes.findIndex((node) => {
      return node[NODE_KEY] === searchNode[NODE_KEY];
    });
  }

  // Helper to find the index of a given edge
  getEdgeIndex(searchEdge) {
    return this.state.graph.edges.findIndex((edge) => {
      return edge.source === searchEdge.source && edge.target === searchEdge.target;
    });
  }

  // Given a nodeKey, return the corresponding node
  getViewNode(nodeKey) {
    const searchNode = {};
    searchNode[NODE_KEY] = nodeKey;
    const i = this.getNodeIndex(searchNode);
    return this.state.graph.nodes[i];
  }

  makeItLarge() {
    const graph = this.state.graph;
    const generatedSample = generateSample(this.state.totalNodes);
    graph.nodes = generatedSample.nodes;
    graph.edges = generatedSample.edges;
    this.setState(this.state);
  }

  addStartNode() {
    const graph = this.state.graph;
    // using a new array like this creates a new memory reference
    // this will force a re-render
    graph.nodes = [
      {
        id: Date.now(),
        title: 'Node A',
        type: SPECIAL_TYPE,
        x: 0,
        y: 0
      },
      ...this.state.graph.nodes
    ];
    this.setState({
      graph
    });
  }

  deleteStartNode() {
    const graph = this.state.graph;
    graph.nodes.splice(0, 1);
    // using a new array like this creates a new memory reference
    // this will force a re-render
    graph.nodes = [...this.state.graph.nodes];
    this.setState({
      graph
    });
  }

  handleChange(event) {
    this.setState(
      {
        totalNodes: parseInt(event.target.value || '0', 10)
      },
      this.makeItLarge.bind(this)
    );
  }

  onSelectPanNode(event) {
    if (this.GraphView) {
      this.GraphView.panToNode(event.target.value, true);
    }
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

        <div className="graph-header">
          <button onClick={this.addStartNode.bind(this)}>Add Node</button>
          <button onClick={this.deleteStartNode.bind(this)}>Delete Node</button>
          <input
            className="total-nodes"
            type="number"
            onBlur={this.handleChange.bind(this)}
            placeholder={this.state.totalNodes.toString()}
          />
          <div className="pan-list">
          <span>Pan To:</span>
            <select onChange={this.onSelectPanNode.bind(this)}>
              {nodes.map(node => <option key={node[NODE_KEY]} value={node[NODE_KEY]}>{node.title}</option>)}
            </select>
          </div>
        </div>

        <GraphView  ref='GraphView'
                    nodeKey={NODE_KEY}
                    nodes={nodes}
                    edges={edges}
                    // selected={selected}
                    nodeTypes={NodeTypes}
                    nodeSubtypes={NodeSubtypes}
                    edgeTypes={EdgeTypes}
                    // onSelectNode={this.onSelectNode.bind(this)}
                    // onCreateNode={this.onCreateNode.bind(this)}
                    // onUpdateNode={this.onUpdateNode.bind(this)}
                    // onDeleteNode={this.onDeleteNode.bind(this)}
                    // onSelectEdge={this.onSelectEdge.bind(this)}
                    // onCreateEdge={this.onCreateEdge.bind(this)}
                    // onSwapEdge={this.onSwapEdge.bind(this)}
                    // onDeleteEdge={this.onDeleteEdge.bind(this)}
                    />

      </div>
    );
  }

}

function generateSample(totalNodes) {
  const generatedSample = {
    edges: [],
    nodes: []
  };
  let y = 0;
  let x = 0;

  const numNodes = totalNodes ? totalNodes : 0;
  // generate large array of nodes
  // These loops are fast enough. 1000 nodes = .45ms + .34ms
  // 2000 nodes = .86ms + .68ms
  // implying a linear relationship with number of nodes.
  for (let i = 1; i <= numNodes; i++) {
    if (i % 20 === 0) {
      y++;
      x = 0;
    } else {
      x++;
    }
    generatedSample.nodes.push({
      id: `a${i}`,
      title: `Node ${i}`,
      type: nodeTypes[Math.floor(nodeTypes.length * Math.random())],
      x: 0 + 200 * x,
      y: 0 + 200 * y
    });
  }
  // link each node to another node
  for (let i = 1; i < numNodes; i++) {
    generatedSample.edges.push({
      source: `a${i}`,
      target: `a${i + 1}`,
      type: edgeTypes[Math.floor(edgeTypes.length * Math.random())]
    });
  }
  return generatedSample;
}
