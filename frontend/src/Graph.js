import React from 'react';
import { save } from 'save-file';
import ReactDOM from 'react-dom';
import Files from 'react-files';
import axios from "axios";
import {
  GraphView, // required
  // Edge, // optional
  // Node, // optional
  // BwdlTransformer, // optional, Example JSON transformer
  // GraphUtils // optional, useful utility functions
} from 'react-digraph';
import './Graph.css';
import { sample as GRAPH_SAMPLE } from "./Graph.sample";
import {
  makeGraphConfigObject,
  NODE_KEY,
  SOURCE_TYPE,
  HANDLER_TYPE,
  SINK_TYPE,
  STANDARD_EDGE_TYPE,
  nodeTypes
} from './Graph.configs';
import { Col, Row, Container } from "react-bootstrap";
import toposort from 'toposort';

export class Graph extends React.Component {

  constructor(props) {
    super(props);

    let sample = GRAPH_SAMPLE;
    let config = makeGraphConfigObject();
    this.state = {
      graph: sample,
      selected: {},
      totalNodes: sample.nodes.length,
      graphConfig: config,
      selectedOption: {},
      selectedOption2: {}
    }

    this.GraphView = React.createRef();
  }

  componentDidMount() {
    // TODO:
    // - fetch types of nodes for Sources/Handlers/Sinks
    // - construct a GraphConfig object
    // - update state with this.setState({...});
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
  onCreateNode(event) {
    const graph = this.state.graph;

    let id = this.state.totalNodes + 1;
    const type = this.state.selectedOption;

    let select = document.getElementById('secondOption');
    let title = select.options[select.selectedIndex].value;

    const viewNode = {
      id: 'a' + id,
      title,
      type,
      x: 10,
      y: 0
    };

    graph.nodes = [...graph.nodes, viewNode];
    this.setState({
      graph: graph,
      totalNodes: this.state.totalNodes + 1,
    });
  }

  // Deletes a node from the graph
  onDeleteNode(viewNode, nodeId, nodeAdr) {
    const graph = this.state.graph;
    // Delete any connected edges

    const newEdges = graph.edges.filter(edge => {
      return edge.source !== nodeId && edge.target !== nodeId;
    });

    graph.nodes = nodeAdr;
    graph.edges = newEdges;
    let totalNodes = this.state.totalNodes - 1;

    this.setState({
      graph,
      selected: null,
      totalNodes: totalNodes,
    });
  }

  // Creates a new node between two edges
  onCreateEdge(event) {

    let sourceSelect = document.getElementById('source');
    let sinkSelect = document.getElementById('sink');

    let sourceNode = sourceSelect.options[sourceSelect.selectedIndex].value;
    let sinkNode = sinkSelect.options[sinkSelect.selectedIndex].value;
    const type = STANDARD_EDGE_TYPE;

    const graph = this.state.graph;
    const viewEdge = {
      source: sourceNode,
      target: sinkNode,
      type
    };
    
    let sourceViewNode = this.getViewNode(sourceNode);
    let sinkViewNode =  this.getViewNode(sinkNode);

    // Check if Edge is valid
    // TODO check if edge is repeated
    if (sourceNode === sinkNode) {
      console.warn(`Trying to create an edge from node ${sourceNode} to itself`);
    } else if (sourceViewNode.type === SINK_TYPE) {
      console.warn(`Trying to create an output edge from the sink node ${sourceNode}`);
    } else if (sinkViewNode.type === SOURCE_TYPE) {
      console.warn(`Trying to create an input edge to the source node ${sinkNode}`);
    } else if (! isGraphAcyclic(this.state.graph)) {
      console.warn('Edge creation would create a cycle in the graph');
    } else { // Else, create the edge (it's valid)
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

  // makeItLarge() {
  //   const graph = this.state.graph;
  //   const generatedSample = generateSample(this.state.totalNodes);
  //   graph.nodes = generatedSample.nodes;
  //   graph.edges = generatedSample.edges;
  //   this.setState(this.state);
  // }

  // addStartNode() {
  //   const graph = this.state.graph;
  //   // using a new array like this creates a new memory reference
  //   // this will force a re-render
  //   graph.nodes = [
  //     {
  //       id: Date.now(),
  //       title: 'Node A',
  //       type: this.state.graphConfig.NodeTypes.special,
  //       x: 0,
  //       y: 0
  //     },
  //     ...this.state.graph.nodes
  //   ];
  //   this.setState({
  //     graph
  //   });
  // }

  // deleteStartNode() {
  //   const graph = this.state.graph;
  //   graph.nodes.splice(0, 1);
  //   // using a new array like this creates a new memory reference
  //   // this will force a re-render
  //   graph.nodes = [...this.state.graph.nodes];
  //   this.setState({
  //     graph
  //   });
  // }

  handleChange(event) {
    console.log('Handling Change onBlur');
    console.log(event);

    this.setState(
      {
        totalNodes: parseInt(event.target.value || '0', 10)
      },
      // this.makeItLarge.bind(this)
    );
  }

  onSelectPanNode(event) {
    if (this.GraphView) {
      this.GraphView.panToNode(event.target.value, true);
    }
  }

  onRunGraph(){

    axios.post(process.env.REACT_APP_API_URL + '/sendGraph', JSON.stringify(this.state.graph))
    .then(response => {
      console.log(response);
      if(response.data === true){
        this.sendRunRequest();
      } 
      else{
        console.warn('There was an error parsing the graph.');
      }
    })
    .catch(error => console.warn('Error on axios.post: ' + JSON.stringify(error)));
  }

  sendRunRequest(){
    axios.get(process.env.REACT_APP_API_URL + '/runGraph', JSON.stringify({}))
    .then(response => console.log(response))
    .catch(error => console.warn('Error on axios.get: ' + JSON.stringify(error)));
  }

  getNodeTypes() {
    let graphConfig = this.state.graphConfig;
    let nodeTypes = new Map();
    for (let prop in graphConfig.NodeTypes) {
      let parentTypeId = graphConfig.NodeTypes[prop].shapeId;
      let parentType = parentTypeId.split('#')[1];
      nodeTypes[parentType] = [];
    }

    for (let subType in graphConfig.NodeSubtypes) {
      let parentType = graphConfig.NodeSubtypes[subType].nodeType;
      nodeTypes[parentType].push(subType);
    }

    return nodeTypes;
  }


  handleChange1(){
    let select = document.getElementById('firstOption');
    let selectedOption = select.options[select.selectedIndex].value;
    this.setState({selectedOption});
  };

  onFilesChange(files) {

    let reader = new FileReader();
    reader.onload = () => {
      var data = reader.result;
      if(this.loadGraph(data))
      {
        console.log('Loaded graph successfully!');
      }
      else
      {
        console.log('The graph couldn`t be loaded. Please check if the submitted graph is in the right format!');
      }
    };
    reader.readAsText(files[0]);

  };

  saveGraph() {
    save(JSON.stringify(this.state.graph), 'graph.json');
  }

  loadGraph(graphObj) {

    if (graphObj == null)
        return false;

    let jsonGraph = JSON.parse(graphObj);

    let nodes = jsonGraph.nodes;
    let edges = jsonGraph.edges;

    if (nodes == null || edges == null)
        return false;

    const graph = this.state.graph;

    graph.nodes = nodes;
    graph.edges = edges;

    this.setState({
      graph,
      selected: null,
      totalNodes: nodes.length,
    });

    return true;
  }
 
  onFilesError(error, file) {
   console.log('error code ' + error.code + ': ' + error.message)
  };

  render() {
    function sequenceToOptions(seq) {
      return seq.map(el => Object.assign({}, { value: el, label: el }));
    }

    const nodes = this.state.graph.nodes;
    const edges = this.state.graph.edges;
    const selected = this.state.selected;

    const NodeTypes = this.state.graphConfig.NodeTypes;
    const NodeSubtypes = this.state.graphConfig.NodeSubtypes;
    const EdgeTypes = this.state.graphConfig.EdgeTypes;


    let nodeTypes = this.getNodeTypes();
    let firstOptions = Object.keys(nodeTypes);
    firstOptions = sequenceToOptions(firstOptions);

    let selectedOption1 = this.state.selectedOption;
    if (Object.keys(selectedOption1).length === 0) {
      selectedOption1 = firstOptions[0].value;
    }
    
    let secondOptions = nodeTypes[selectedOption1];
    secondOptions = sequenceToOptions(secondOptions);


    return (
      
    <Container id='graph'>
      {/* TODO Eventually move this header to a side panel to the right of the GraphView */}

      <Row>
        <Col id='graph-view' sm={8}>
          <GraphView
                ref={(el) => (this.GraphView = el)}
                nodeKey={NODE_KEY}
                nodes={nodes}
                edges={edges}
                selected={selected}
                nodeTypes={NodeTypes}
                nodeSubtypes={NodeSubtypes}
                edgeTypes={EdgeTypes}
                onSelectNode={this.onSelectNode.bind(this)}
                onCreateNode={this.onCreateNode.bind(this)}
                onUpdateNode={this.onUpdateNode.bind(this)}
                onDeleteNode={this.onDeleteNode.bind(this)}
                onSelectEdge={this.onSelectEdge.bind(this)}
                onSwapEdge={this.onSwapEdge.bind(this)}
                onDeleteEdge={this.onDeleteEdge.bind(this)}
                />
        </Col>

        <Col id='graph-settings' sm={4}>
          <div>
            <span id="number-nodes">Number of Nodes: {this.state.totalNodes.toString()}</span>
          </div>
          <div className="create-node">
            <span>Add Node: </span>
            
            <select id='firstOption' onChange={this.handleChange1.bind(this)}>
              {firstOptions.map(node => <option value={node.value}>{node.value}</option>)}
            </select>

            <select id='secondOption'>
              {secondOptions.map(node => <option value={node.value}>{node.value}</option>)}
            </select>

            <button onClick={this.onCreateNode.bind(this)}>Create</button>
          </div>
          <div className="create-edge">
            <span>Add Edge: </span>
            <select id="source">
              {nodes.map(node => <option key={node[NODE_KEY]} value={node[NODE_KEY]}>{node.title}</option>)}
            </select>
            <select id="sink">
              {nodes.map(node => <option key={node[NODE_KEY]} value={node[NODE_KEY]}>{node.title}</option>)}
            </select>
            <button onClick={this.onCreateEdge.bind(this)}>Create</button>
          </div>
          <div>
            <span>Pan to Node: </span>
            <select id="panToSelection" onChange={this.onSelectPanNode.bind(this)}>
              {nodes.map(node => <option key={node[NODE_KEY]} value={node[NODE_KEY]}>{node.title}</option>)}
            </select>
          </div>
          <div className="send-backend-run">
            <button onClick={this.onRunGraph.bind(this)}>Run</button>
          </div>
          <div>
            <span>Load graph:</span>
            <div className="files">
              <Files
                className='files-dropzone'
                onChange={this.onFilesChange.bind(this)}
                onError={this.onFilesError}
                accepts={['.json']}
                multiple
                maxFiles={3}
                maxFileSize={10000000}
                minFileSize={0}
                clickable
              >
                Drop files here or click to upload
              </Files>
            </div>
            <div className="files">
              <Files
                onChange={this.onFilesChange.bind(this)}
                onError={this.onFilesError}
                accepts={['.json']}
                maxFiles={1}
                maxFileSize={10000000}
                minFileSize={0}
                clickable
              >
                <button>Upload</button>
              </Files>
            </div>
          </div>
          <div>
            <button onClick={this.saveGraph.bind(this)}>Save</button>
          </div>
        </Col>

      </Row>
    </Container>
    );
  }

}

function isGraphAcyclic(graph) {
  let directedEdges = graph.edges.map(el => [el.source, el.target]);

  try { // Try to obtain topological sort
    toposort(directedEdges);
  } catch (err) {
    return false;
  }

  return true;
}

// function generateSample(totalNodes) {
//   const generatedSample = {
//     edges: [],
//     nodes: []
//   };
//   let y = 0;
//   let x = 0;

//   const numNodes = totalNodes ? totalNodes : 0;
//   // generate large array of nodes
//   // These loops are fast enough. 1000 nodes = .45ms + .34ms
//   // 2000 nodes = .86ms + .68ms
//   // implying a linear relationship with number of nodes.
//   for (let i = 1; i <= numNodes; i++) {
//     if (i % 20 === 0) {
//       y++;
//       x = 0;
//     } else {
//       x++;
//     }
//     generatedSample.nodes.push({
//       id: `a${i}`,
//       title: `Node ${i}`,
//       type: nodeTypes[Math.floor(nodeTypes.length * Math.random())],
//       x: 0 + 200 * x,
//       y: 0 + 200 * y
//     });
//   }
//   // link each node to another node
//   for (let i = 1; i < numNodes; i++) {
//     generatedSample.edges.push({
//       source: `a${i}`,
//       target: `a${i + 1}`,
//       type: edgeTypes[Math.floor(edgeTypes.length * Math.random())]
//     });
//   }
//   return generatedSample;
// }
